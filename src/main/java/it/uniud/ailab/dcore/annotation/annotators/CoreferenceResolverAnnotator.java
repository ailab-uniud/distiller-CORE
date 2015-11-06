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

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.util.IntPair;
import java.util.List;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.DefaultAnnotations;
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

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
        double numberOfPhrases = DocumentUtils.getNumberOfPhrasesInDocument(component);
        Map<String, Collection<Set<CorefChain.CorefMention>>> coreferenceGraph
                = ((DocumentComponent) component).getCoreferenceMap();
        Map<String, Double> pronominalReferencesMap = new HashMap<>();
        List<String> notPronominalAnaphoras = new ArrayList<>();
        
        // Annotate grams with their statistical features.
        // The implementation is quite straightforward:
        // for the definitions of depth, height and frequency, just
        // see the variable declarations above.

        //preprocess graph and getting numberofref for each anaphor
        for (String anaphor : coreferenceGraph.keySet()) {
            Collection<Set<CorefChain.CorefMention>> mentions
                    = coreferenceGraph.get(anaphor);

            double number_of_reference = 0.0;

            for (Set<CorefChain.CorefMention> mSet : mentions) {

                for (CorefChain.CorefMention ment : mSet) {
                    
                    String reference = ment.mentionSpan;
                    //eleiminate self references
                    if (!reference.equalsIgnoreCase(anaphor)) {
                        
                        //find out number of pronominal anaphora, which 
                        //would be lost in default parsing,
                        //that eliminates all the pronoms in the pre processing
                        if (ment.mentionType == Dictionaries.MentionType.PRONOMINAL) {
                            number_of_reference++;
                        } else {
                            notPronominalAnaphoras.add(reference);
                        }
                    }
                }
            }
            
            pronominalReferencesMap.put(anaphor, number_of_reference);
        }

        for (Sentence s : sentences) {
            double score = 0;
            for (Gram g : s.getGrams()) {
                String surf = g.getSurface();
                for (String anaphor : pronominalReferencesMap.keySet()) {
                    if (anaphor.toLowerCase().contains(surf.toLowerCase())
                            || surf.toLowerCase().contains(anaphor.toLowerCase())) {
                        
                        double score1 = score;
                        double score2 = pronominalReferencesMap.get(anaphor);
                        score = Math.max(score1, score2);
                        g.putFeature(NUMBER_OF_REFERENCE, score);

                    }
                    
                }
                
                if(score > 0){
                    g.putFeature(NUMBER_OF_REFERENCE, (score/numberOfPhrases));
                } else {
                    g.putFeature(NUMBER_OF_REFERENCE, 0.0);
                }
                
                double inAnaphoraScore = 0;
                for(String references : notPronominalAnaphoras){
                    if(references.toLowerCase().contains(surf.toLowerCase())
                            || surf.toLowerCase().contains(references.toLowerCase())){
                        inAnaphoraScore++;
                    }
                   
                }
                g.putFeature(IN_ANAPHORA, (inAnaphoraScore/g.getFeature(StatisticalAnnotator.FREQUENCY)));
            }
//            for (Gram g : s.getGrams()) {
//                String anaphor = g.getSurface();
//                if (coreferenceGraph.containsKey(anaphor)) {
//
//                    Collection<Set<CorefChain.CorefMention>> mentions
//                            = coreferenceGraph.get(anaphor);
//
//                    double number_of_reference = 0.0;
//
//                    for (Set<CorefChain.CorefMention> mSet : mentions) {
//
//                        for (CorefChain.CorefMention ment : mSet) {
//
//                            //eleiminate self references
//                            if (!ment.mentionSpan.equalsIgnoreCase(anaphor)) {
//                                System.out.println(ment.mentionSpan);
//                                //find out number of pronominal anaphora, which 
//                                //would be lost in default parsing,
//                                //that eliminates all the pronoms in the pre processing
//                                if (ment.mentionType == Dictionaries.MentionType.PRONOMINAL) {
//
//                                    number_of_reference++;
//                                    
//                                }
//                            }
//                        }
//                    }
//                    System.out.println("numberOfRef "+ number_of_reference);
//                    System.out.println("numberOfRef "+ (number_of_reference / numberOfPhrases));
//                    g.putFeature(NUMBER_OF_REFERENCE, (number_of_reference / numberOfPhrases));
//                    
//                } else {
//                    g.putFeature(NUMBER_OF_REFERENCE, 0.0);
//                }
//
//            }
        }
    }
}
