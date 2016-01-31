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

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.DefaultAnnotations;
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.annotation.annotations.NERAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.DocumentComposite;
import it.uniud.ailab.dcore.persistence.Mention;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A bootstrapper annotator for the English language developed using the
 * Stanford Core NLP library. This annotator splits the document, tokenizes it
 * and performs PoS tagging and Named Entity Recognition togethere with parsing
 * to detect coreferences in the document. This annotator supports only the
 * English language.
 *
 * @author Giorgia Chiaradia
 */
public class StanfordBootstrapperAnnotator implements Annotator {

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
            props.put("annotators", "tokenize, ssplit, pos, parse, lemma, ner, dcoref");
            pipeline = new StanfordCoreNLP(props);

        }

        // read some text in the text variable
        String text = component.getText();

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);

        //get the graph for coreference resolution
        Map<Integer, CorefChain> graph
                = document.get(CorefCoreAnnotations.CorefChainAnnotation.class);

        //prepare the map for coreference graph of document
        Map<String, Collection<Set<CorefChain.CorefMention>>> coreferenceGraph
                = new HashMap<>();

        for (CorefChain corefChain : graph.values()) {

            //get the representative mention, that is the word recall in other sentences
            CorefChain.CorefMention cm = corefChain.getRepresentativeMention();

            //eliminate auto-references
            if (corefChain.getMentionMap().size() <= 1) {
                continue;
            }

            //get the stemmed form of the references, so the comparison with 
            //grams will be easier
            List<CoreLabel> tks = document.get(SentencesAnnotation.class)
                    .get(cm.sentNum - 1).get(TokensAnnotation.class);
                    //list of tokens which compose the anaphor

            List<Token> anaphorsTokens = new ArrayList<>();
            for (int i = cm.startIndex - 1; i < cm.endIndex - 1; i++) {
                CoreLabel current = tks.get(i);
                Token t = new Token(current.word());
                t.setPoS(current.tag());
                t.setLemma(current.lemma());
                anaphorsTokens.add(t);
            }
            
            //the mention n-gram which is formed by the anaphor and a 
            //list of references
            Mention mention
                    = new Mention(cm.mentionSpan, anaphorsTokens, cm.mentionSpan);

            //get map of the references to the corefchain obj
            Collection<Set<CorefChain.CorefMention>> mentionMap
                    = corefChain.getMentionMap().values();
            for (Set<CorefChain.CorefMention> mentions : mentionMap) {
                
                for (CorefChain.CorefMention reference : mentions) {
                    //eliminate self-references
                    if(reference.mentionSpan.equalsIgnoreCase(cm.mentionSpan)){
                        continue;
                    }
                    List<CoreLabel> tokens = document.get(SentencesAnnotation.class)
                            .get(reference.sentNum - 1).get(TokensAnnotation.class);
                    
                    //list of tokens which compose the mention
                    List<Token> mentionTokens = new ArrayList<>();
                    for (int i = reference.startIndex - 1; i < reference.endIndex - 1; i++) {
                        CoreLabel current = tokens.get(i);
                        //set token features 
                        Token t = new Token(current.word());
                        t.setPoS(current.tag());
                        t.setLemma(current.lemma());
                        mentionTokens.add(t);
                    }
                    //add to mention a new reference
                    mention.addReference(
                            reference.mentionSpan,
                            mentionTokens,
                            reference.mentionType.toString());
                }
            }

            //assign to the document a new corenference obj
            //containing the anaphor and its mentions 
            blackboard.addGram(mention);
        }

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and 
        //has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        //A counter that keeps track of the number of phrases in a sentences
        int phraseCounter = 0;

        for (CoreMap stanfordSentence : sentences) {
            
            Sentence distilledSentence
                    = new Sentence(stanfordSentence.toString(), "" + sentenceCounter++);

            distilledSentence.setLanguage(Locale.ENGLISH);

            //getting the dependency graph of the document so to count the number of phrases 
            //ROOT sentences are the first level children in the parse tree; every ROOT sentence
            //is constitute by a group of clauses which can be the principal (main clauses) or not
            //(coordinate and subordinate). We use ROOT sentences as a starting point to find out all
            //the phrases present in the sentences themselves, checking out for the tag "S".
            Tree sentenceTree = stanfordSentence.get(TreeCoreAnnotations.TreeAnnotation.class);

            for (Tree sub : sentenceTree.subTreeList()) {
                if (sub.label().value().equals("S")) {
                    phraseCounter++;
                }
            }

            //annotate the sentence with a new feature counting all the phrases
            //cointained in the sentence    
            distilledSentence.addAnnotation(new FeatureAnnotation(
                    DefaultAnnotations.PHRASES_COUNT, phraseCounter));

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

                String ner = token.get(NamedEntityTagAnnotation.class);
                if(!ner.equalsIgnoreCase("O")){
                    t.addAnnotation(new NERAnnotation(DefaultAnnotations.IS_NER, 
                        ner));
                } 
                //add the token to the sentence
                distilledSentence.addToken(t);
            }

            //add the sentence to document
            ((DocumentComposite) component).addComponent(distilledSentence);
        }
    }

}
