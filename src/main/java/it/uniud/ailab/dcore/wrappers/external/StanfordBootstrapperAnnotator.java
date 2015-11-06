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
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
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
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.DocumentComposite;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A bootstrapper annotator for the English language developed using the 
 * Stanford Core NLP library. This annotator splits the document, tokenizes it and performs PoS
 * tagging and Named Entity Recognition togethere with parsing to detect coreferences in the document. 
 * This annotator supports only the English language.
 *
 * @author Giorgia Chiaradia
 */
public class StanfordBootstrapperAnnotator implements Annotator {
    
    /**
     * The Stanford NLP pipeline. The field is marked static to be optimized
     * for re-use, so that subsequent calls of annotate() don't have to reload
     * definitions every time, even for different instances of the annotator.
     */
    private static StanfordCoreNLP pipeline = null;
    
    /**
     * A counter that keeps track of the number of sentences identified by the
     * Annotator, used as identifier for the generated Sentences.
     */
    private int sentenceCounter = 0;
   
    /**
     * Annotate the document by splitting the document, tokenizing it, performing PoS tagging  
     * and Named Entity Recognition using the Stanford Core NLP tools. 
     * 
     * @param component the component to annotate.
     */
    @Override
    public void annotate(Blackboard blackboard,DocumentComponent component) {
        
        
        if (pipeline == null) {
            // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
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
        Map<Integer, CorefChain> graph = 
                document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
        
        //prepare the map for coreference graph of document
        Map<String,Collection<Set<CorefChain.CorefMention>>> coreferenceGraph 
                = new HashMap<>();
        
        for(CorefChain corefChain : graph.values() ){
            
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
            
            StringBuilder stringBuiler = new StringBuilder();
            for(int i = cm.startIndex-1; i < cm.endIndex-1; i++){
                stringBuiler.append(tks.get(i).get(LemmaAnnotation.class));
                stringBuiler.append(" ");
            }
            
            String anaphor = stringBuiler.toString().trim();
            
            //get map of the references to the corefchain obj
            Collection<Set<CorefChain.CorefMention>> mentionMap = corefChain.getMentionMap().values();
            
            //set the string representing corefchain obj as key
            //set the references map as value
            coreferenceGraph.put(anaphor, mentionMap);
        }
        
        //assing to document a map containing the anaphora as key and the 
        //coreference graph as value
        component.setCoreferenceMap(coreferenceGraph);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and 
        //has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        
        //A counter that keeps track of the number of phrases in a sentences
        int phraseCounter = 0; 
        
        
        for (CoreMap stanfordSentence : sentences) {

            Sentence distilledSentence = 
                    new Sentence(stanfordSentence.toString(),""+sentenceCounter++); 
            
            
            distilledSentence.setLanguage(Locale.ENGLISH);
            
            //getting the dependency graph of the document so to count the number of phrases 
            //ROOT sentences are the first level children in the parse tree; every ROOT sentence
            //is constitute by a group of clauses which can be the principal (main clauses) or not
            //(coordinate and subordinate). We use ROOT sentences as a starting point to find out all
            //the phrases present in the sentences themselves, checking out for the tag "S".
            Tree sentenceTree = stanfordSentence.get(TreeCoreAnnotations.TreeAnnotation.class);
           
                for(Tree sub : sentenceTree.subTreeList()){
                    if(sub.label().value().equals("S")){ 
                        phraseCounter++;
                    }
                }                
         
            distilledSentence.setPhraseNumber(phraseCounter);
            
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
                
                distilledSentence.addToken(t);
            }

            ((DocumentComposite) component).addComponent(distilledSentence);
        }
    }

}
