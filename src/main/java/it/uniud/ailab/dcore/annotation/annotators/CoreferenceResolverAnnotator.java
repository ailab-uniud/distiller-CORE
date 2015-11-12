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
package it.uniud.ailab.dcore.annotation.annotators;

import java.util.List;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import it.uniud.ailab.dcore.persistence.Mention;
import it.uniud.ailab.dcore.persistence.Mention.Reference;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Annotates grams with linguistic features concerning anaphora and coreference
 * resolution. Using information about pronominal anaphoras we are able to
 * capture also the repetition of grams that are referenced with pronouns,
 * thanks to this information we can annotate grams with Number_Of_Reference
 * feature which is defined as: total # of reference for given gram / ( total #
 * of phrases )
 *
 * Using information about nominal (NNP phrases) or proper anaphoras we can find
 * out if a candidate gram appears in them and annotate it with the In_Anaphora
 * feature, that is defined as: total # of appearance of gram in A / total # of
 * appearance of gram in D, where A is the set of anaphoras and D the document.
 *
 * @author Giorgia Chiaradia
 */
public class CoreferenceResolverAnnotator implements Annotator {

    // We use final fields to avoid spelling errors in feature naming.
    // Plus, is more handy to refer to a feature by ClassName.FeatureName, 
    // so that the code is much more readable.
    /**
     * Document number of reference of a gram, defined as ( # of pronominal
     * anaphor for gram / total # of phrases ).
     */
    public static final String NUMBER_OF_REFERENCE = "Number_Of_Reference";

    /**
     * Document in anaphora presence of a gram, defined as ( # of appearance of
     * gram in A / total # of appearance of gram in D ), where A is the set of
     * proper and nominal anaphoras and D id the document.
     */
    public static final String IN_ANAPHORA = "In_Anaphora";

    /**
     * Annotates grams with linguistic information.
     * <p>
     * Grams are annotated with information such as their frequency, their width
     * and their depth in the
     * {@link it.uniud.ailab.dcore.persistence.DocumentComponent} passed as
     * input.
     * <p>
     * Sentences are annotated with their length, expressed both in number of
     * words and number of characters (including whitespaces).
     *
     *
     * @param component the component to analyze.
     */
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        List<Sentence> sentences = DocumentUtils.getSentences(component);

        double numberOfPhrases = DocumentUtils
                .getNumberOfPhrasesInDocument(component);

        List<String> notPronominalAnaphora = new ArrayList<>();
        Map<String, Double> pronominalAnaphoraCounter = new HashMap<>();

        // Annotate grams with their statistical features.
        // The implementation is quite straightforward:
        // for the definitions of depth, height and frequency, just
        // see the variable declarations above.
//        //preprocess graph and getting numberofref for each anaphor
//        for(Annotation an : blackboard.getAnnotations(
//                StanfordBootstrapperAnnotator.COREFERENCE)){
//            
//            //get the annotation from blackboard for coreferences annotation 
//            CoreferenceChainAnnotation stanfordAnnotation = (CoreferenceChainAnnotation)an;
//            
//            //each annotation is an anaphor  
//            String anaphor = stanfordAnnotation.getAnnotation();
//            
//            //we count all the times the metions for the current anaphor are 
//            //pronominal anaphoras
//            double pronCount = stanfordAnnotation.getNumberOfPronominalReferences();
//            
//            //insert anaphor and respective pronominal anphora count in the 
//            //general map
//            pronominalAnaphoraCounter.put(anaphor, pronCount);
//            
//            //get all the non-pronominal mentions for the current anaphora
//            List<String> mentions = stanfordAnnotation.getNotPronominalMentions();
//            
//            //add the mentions list to the global one
//            notPronominalAnaphora.addAll(mentions);
//        }
        Map<String, Gram> mentions = blackboard.getGramsByType(Mention.MENTION);
        for (String anaphor : mentions.keySet()) {
            double numberOfRef = 0;
            Mention ment = (Mention) mentions.get(anaphor);
            for (Reference ref : ment.getReferences()) {
                if (ref.getType().equals("PRONOMINAL")) {
                    numberOfRef++;
                } else {
                    String rootedRef = "";
                    for (Token t : ref.getTokens()) {
                        if (t.getStem() == null) {
                            rootedRef += t.getLemma() + " ";
                        } else {
                            rootedRef += t.getStem() + " ";
                        }
                    }
                    rootedRef = rootedRef.trim();
                    notPronominalAnaphora.add(rootedRef);
                }
            }
            String rootedAnaphor = "";
            for (Token t : ment.getAnaphorToken()) {
                if (t.getStem() == null) {
                    rootedAnaphor += t.getLemma() + " ";
                } else {
                    rootedAnaphor += t.getStem() + " ";
                }
            }
            rootedAnaphor = rootedAnaphor.trim();
            pronominalAnaphoraCounter.put(rootedAnaphor, numberOfRef);
        }

        //for each sentence in the document
        for (Sentence s : sentences) {
            double score = 0; //initialize variable for NOR feature score

            //for each n gram control if the anaphor is present in the n gram or
            //vice-versa
            for (Gram g : s.getGrams()) {
                Keyphrase k = (Keyphrase) g;
                String key = "";
                for(Token t : k.getTokens()){
                    if (t.getStem() == null) {
                        key += t.getLemma() + " ";
                    } else {
                        key += t.getStem() + " ";
                    }
                }
                key = key.trim();
                
                for (String anaphor : pronominalAnaphoraCounter.keySet()) {
                    if (anaphor.toLowerCase().toLowerCase()
                            .matches(".*\\b" + key.toLowerCase() + "\\b.*")
                            || key.toLowerCase()
                            .matches(".*\\b" + anaphor.toLowerCase() + "\\b.*")) {

                        double score1 = score;
                        double score2 = pronominalAnaphoraCounter.get(anaphor);
                        score = Math.max(score1, score2); //get the maximal score 
                    }

                }

                if (score > 0) {//if n-gram is an anaphor
                    //normalize score for NOR by total # of phrases 
                    k.putFeature(NUMBER_OF_REFERENCE, (score / numberOfPhrases));
                } else {
                    k.putFeature(NUMBER_OF_REFERENCE, 0.0);
                }

                //check if an candidate is or not present in a mention
                double inAnaphoraScore = 0;
                double gramFreq = k.getFeature(StatisticalAnnotator.FREQUENCY);
                for(String reference : notPronominalAnaphora){
                    if(reference.toLowerCase().matches(".*\\b" + key.toLowerCase() + "\\b.*")){
                        inAnaphoraScore++;
                    }
                   
                }
                
                //assuring there aren't repetition or self references 
                if(inAnaphoraScore >= gramFreq)
                    inAnaphoraScore = 0;
                
                //set the InAnaphora feature for the candidate
                k.putFeature(IN_ANAPHORA, inAnaphoraScore/gramFreq);
            }
        }
    }
}
