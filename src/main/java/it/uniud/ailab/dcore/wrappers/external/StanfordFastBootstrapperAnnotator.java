/*
 * Copyright (C) 2015 Artificial Intelligence
 * Laboratory @ University of Udine.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package it.uniud.ailab.dcore.wrappers.external;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
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
 * Stanford Core NLP library. This annotator splits the document, tokenizes it
 * and performs PoS tagging and Named Entity Recognition togethere with parsing
 * to detect coreferences in the document. This annotator supports only the
 * English language.
 *
 * @author Giorgia Chiaradia
 */
public class StanfordFastBootstrapperAnnotator implements Annotator {

    /**
     * The Stanford NLP pipeline. The field is marked static to be optimized for
     * re-use, so that subsequent calls of annotate() don't have to reload
     * definitions every time, even for different instances of the annotator.
     */
    private static StanfordCoreNLP pipeline = null;

    /**
     * A counter that keeps track of the number of sentences identified by the
     * Annotator, used as identifier for the generated Sentences.
     */
    private int sentenceCounter = 0;

    public static final String COREFERENCE = "Coreference";

    /**
     * Annotate the document by splitting the document, tokenizing it,
     * performing PoS tagging and Named Entity Recognition using the Stanford
     * Core NLP tools.
     *
     * @param component the component to annotate.
     */
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        if (pipeline == null) {
            // creates a StanfordCoreNLP object, with POS tagging, lemmatization, 
            //NER, parsing, and coreference resolution 
            Properties props = new Properties();
            props.put("annotators", "tokenize, ssplit, pos, lemma");
            pipeline = new StanfordCoreNLP(props);

        }

        // read some text in the text variable
        String text = component.getText();

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and 
        //has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        for (CoreMap stanfordSentence : sentences) {

            Sentence distilledSentence
                    = new Sentence(stanfordSentence.toString(), "" + sentenceCounter++);

            distilledSentence.setLanguage(Locale.ENGLISH);

            // traversing the words in the current sentence
            // for each token in the text, we create a new token annotate it 
            // with the word representing it, its pos tag and its lemma
            for (CoreLabel token : stanfordSentence.get(TokensAnnotation.class)) {

                // this is the text of the token
                Token t = new Token(token.originalText());

                // this is the POS tag of the token                
                t.setPoS(token.tag());

                // this is the lemma of the ttoken
                t.setLemma(token.lemma());

                //add the token to the sentence
                distilledSentence.addToken(t);
            }

            //add the sentence to document
            ((DocumentComposite) component).addComponent(distilledSentence);
        }
    }

}
