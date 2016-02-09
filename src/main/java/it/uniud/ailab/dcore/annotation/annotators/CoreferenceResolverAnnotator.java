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
import java.util.regex.Pattern;

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

        //get all the sentences in the document
        List<Sentence> sentences = DocumentUtils.getSentences(component);

        //get the total number of phrases in the document
        double numberOfPhrases = DocumentUtils
                .getNumberOfPhrasesInDocument(component);

        //prepare the list containing the references which are not
        //pronominal
        List<String> notPronominalAnaphora = new ArrayList<>();
        
        //prepare the map containing the anaphor and the number of pronominal 
        //references
        Map<String, Double> pronominalAnaphoraCounter = new HashMap<>();
        
        //get the map of grams which type is mention 
        Map<String, Gram> mentions = blackboard.getGramsByType(Mention.MENTION);
        
        //preprocess the metion grams so to distiguish the ones that are 
        //pronominal and count them, from the ones that are proper name or 
        //nominal references. All the data structure for mentions will be full
        //up with the stemmed or lemmatize version of the gram, so to facilitate
        //the comparison with the candidate keyphrases.
        for (String anaphor : mentions.keySet()) { //for every anaphor...
            double numberOfRef = 0; //set number of reference primarly to zero
            Mention ment = (Mention) mentions.get(anaphor); // assuring the gram 
                                                            //is a mention
            //for every reference to anaphor
            for (Reference ref : ment.getReferences()) { 
                if (ref.getType().equals("PRONOMINAL")) { //if is a pronoun
                    numberOfRef++; //increment # of reference for that anaphor
                } else {
                    //if is proper name or nominal, create a string of the 
                    //stemmed form of the reference
                    StringBuilder strBuilder = new StringBuilder();
                    for (Token t : ref.getTokens()) {
                        if (t.getStem() == null) {
                            strBuilder.append(t.getLemma());
                        } else {
                            strBuilder.append(t.getStem());
                        }
                        strBuilder.append(" ");
                    }
                    String rootedRef = strBuilder.toString().trim();
                    
                    //add the non pronominal reference(stemmed) to the global list 
                    notPronominalAnaphora.add(rootedRef);
                }
            }
            
            //create a stemmed string for the anaphor n-gram
            String rootedAnaphor = "";
            for (Token t : ment.getAnaphorToken()) {
                if (t.getStem() == null) {
                    rootedAnaphor += t.getLemma() + " ";
                } else {
                    rootedAnaphor += t.getStem() + " ";
                }
            }
            rootedAnaphor = rootedAnaphor.trim();
            
            //add the total # of references for the current anaphor to the map
            pronominalAnaphoraCounter.put(rootedAnaphor, numberOfRef);
        }
        
        //for each sentence in the document
        for (Sentence s : sentences) {
            double score = 0; //initialize variable for NOR feature score

            //for each candidate keyphrase control if the anaphor is present 
            //in the n gram or vice-versa
            for (Gram g : s.getGrams()) {
                //assuming the gram is really a keyphrase
                Keyphrase k = (Keyphrase) g;
                
                //create a stemmed form for the entire keyphrase
                String key = "";
                for(Token t : k.getTokens()){
                    if (t.getStem() == null) {
                        key += t.getLemma() + " ";
                    } else {
                        key += t.getStem() + " ";
                    }
                }
                key = key.trim();
                
                //iterate on all the anaphors
                for (String anaphor : pronominalAnaphoraCounter.keySet()) {
                    //check if the anaphor contains or not the keyphrase
                    //and vice-versa
                    String escapedKey = Pattern.quote(key.toLowerCase());
                    String escapedAnaphora = Pattern.quote(anaphor.toLowerCase());
                    
                    if (anaphor.toLowerCase()
                            .matches(".*\\b" + escapedKey + "\\b.*")
                            || key.toLowerCase()
                            .matches(".*\\b" + escapedAnaphora + "\\b.*")) {
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
                
                
                double inAnaphoraScore = 0; //initialize inAnaphora score to zero
                //get the term frequency 
                double gramFreq = k.getFeature(StatisticalAnnotator.FREQUENCY);
                
                //for each reference to the current anaphor
                for(String reference : notPronominalAnaphora){
                    //check if an candidate is or not present in a reference
                    if(reference.toLowerCase()
                            .matches(".*\\b" + key.toLowerCase() + "\\b.*")){
                        inAnaphoraScore++;//if it is increment inAnaphora score
                    }
                   
                }
                
                //assuring there aren't repetition or self references 
                if(inAnaphoraScore >= gramFreq)
                    inAnaphoraScore = 0;
                
                //set the InAnaphora feature for the candidate, normalizing the 
                //score to the candidate frequency
                k.putFeature(IN_ANAPHORA, inAnaphoraScore/gramFreq);
            }
        }
    }
}
