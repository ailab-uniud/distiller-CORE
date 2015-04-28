

/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 * 
 * 	This file is part of the Distiller-CORE library.
 * 
 * 	Distiller-CORE is free software; you can redistribute it and/or
 * 	modify it under the terms of the GNU Lesser General Public
 * 	License as published by the Free Software Foundation; either
 * 	version 2.1 of the License, or (at your option) any later version.
 *
 * 	Distiller-CORE is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * 	Lesser General Public License for more details.
 *
 * 	You should have received a copy of the GNU Lesser General Public
 * 	License along with this library; if not, write to the Free Software
 * 	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * 	MA 02110-1301  USA or see <http://www.gnu.org/licenses/>.
 */

package org.uniud.dcore.wrappers.stanford;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.uniud.dcore.engine.Annotator;
import org.uniud.dcore.persistence.DocumentComponent;
import org.uniud.dcore.persistence.DocumentComposite;
import org.uniud.dcore.persistence.Sentence;
import org.uniud.dcore.persistence.Token;

/**
 *
 * @author Marco Basaldella
 */
public class StanfordBootstrapper implements Annotator {

    @Override
    public void annotate(DocumentComponent component) {

        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // read some text in the text variable
        String text = component.getText();

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        for (CoreMap stanfordSentence : sentences) {

            Sentence distilledSentence = new Sentence(stanfordSentence.toString()); 
            
            distilledSentence.setLanguage(Locale.ENGLISH);
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : stanfordSentence.get(TokensAnnotation.class)) {
                
                
                // this is the text of the token
                String word = token.get(TextAnnotation.class);
                
                Token t = new Token(word);                
                
                // this is the POS tag of the token
                String pos = token.get(PartOfSpeechAnnotation.class);
                
                t.setPoS(pos);
                                
                // this is the NER label of the token
                String ne = token.get(NamedEntityTagAnnotation.class);
                
                
                t.addAnnotation(
                    new org.uniud.dcore.persistence.Annotation("StanfordNER",word,ne));
                
                distilledSentence.addToken(t);
            }

            ((DocumentComposite) component).addComponent(distilledSentence);
        }

    }

}
