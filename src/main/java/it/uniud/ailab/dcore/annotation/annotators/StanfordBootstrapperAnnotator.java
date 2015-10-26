/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 *
 * 	This program is free software; you can redistribute it and/or
 * 	modify it under the terms of the GNU General Public License
 * 	as published by the Free Software Foundation; either version 2
 * 	of the License, or (at your option) any later version.
 *
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program; if not, write to the Free Software
 * 	Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package it.uniud.ailab.dcore.annotation.annotators;


import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
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
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.DocumentComposite;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;

/**
 * A bootstrapper annotator for the English language developed using the 
 * Stanford Core NLP library. The annotator splits the document, performs PoS
 * tagging and Named Entity Recognition. This annotator supports only the 
 * English language.
 *
 * @author Marco Basaldella
 */
public class StanfordBootstrapperAnnotator implements Annotator {
    
    /**
     * The Stanford NLP pipeline. The field is marked static to be optimized
     * for re-use, so that subsequent calls of annotate() don't have to reload
     * definitions every time, even for different instances of the annotator.
     */
    private static StanfordCoreNLP pipeline = null;
    
    private int index = 0;

    /**
     * Annotate the document by splitting the document, performing PoS tagging  
     * and Named Entity Recognition using the Stanford Core NLP tools.
     * 
     * @param component the component to annotate.
     */
    @Override
    public void annotate(Blackboard blackboard,DocumentComponent component) {
        
        index = 0;
        
        if (pipeline == null) {
            // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma"); 
            // add "ner" for named entity recognition
            // Add "parse, dcoref" to annotators for coreference resolution
            pipeline = new StanfordCoreNLP(props);
        };

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

            Sentence distilledSentence = 
                    new Sentence(stanfordSentence.toString(),""+index++); 
            
            distilledSentence.setLanguage(Locale.ENGLISH);
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : stanfordSentence.get(TokensAnnotation.class)) {
                
                
                // this is the text of the token
                String word = token.get(TextAnnotation.class);
                
                Token t = new Token(word);                
                
                // this is the POS tag of the token                
                t.setPoS(token.get(PartOfSpeechAnnotation.class));
                
                // this is the Stem
                t.setStem(token.get(LemmaAnnotation.class));
                                
//                // this is the NER label of the token
//                String ne = token.get(NamedEntityTagAnnotation.class);
//                t.addAnnotation(
//                    new org.uniud.dcore.persistence.Annotation("StanfordNER",word,ne));

                // coreference resolution guideline
//                Map corefChain = document.get(CorefChainAnnotation.class); 
//                
//                for (Iterator it = corefChain.values().iterator(); it.hasNext();) {
//                    CorefChain e = (CorefChain)it.next();
                      // TODO: implement coreference resolution annotation here
//                }

//                t.addAnnotation(
//                        new org.uniud.dcore.persistence.Annotation("StanfordCoref", text, ne) );
                
                distilledSentence.addToken(t);
            }

            ((DocumentComposite) component).addComponent(distilledSentence);
        }
    }

}
