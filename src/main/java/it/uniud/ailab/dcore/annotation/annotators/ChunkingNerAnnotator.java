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
import it.uniud.ailab.dcore.annotation.DefaultAnnotations;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;

/**
 * 
 *
 * @author Giorgia Chiaradia
 */
public class ChunkingNerAnnotator implements Annotator {

    // We use final fields to avoid spelling errors in feature naming.
    // Plus, is more handy to refer to a feature by ClassName.FeatureName, 
    // so that the code is much more readable.
    /**
     * Document number of reference of a gram, defined as ( # of pronominal
     * anaphor for gram / total # of phrases ).
     */
    public static final String IS_CHUNK = "IsAChunk";

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

        //for each sentence in the document
        for (Sentence s : sentences) {
            

            //for each candidate keyphrase control if the anaphor is present 
            //in the n gram or vice-versa
            for (Gram g : s.getGrams()) {
                double score = 0; //initialize variable for NER feature score
                //assuming the gram is really a keyphrase
                Keyphrase k = (Keyphrase) g;
                
                List<Token> tokens = k.getTokens();
                
                for(Token t : tokens){
                    if(t.hasAnnotation(DefaultAnnotations.IS_CHUNK)){
                        
                        //eventually we can distinguish the score basing on ner tag
                        //using the NERAnnotation's string, that contains the 
                        //assigned NER category
//                        double nerScore;
//                        String ner = ((NERAnnotation)t.getAnnotation
//                                        (DefaultAnnotations.IS_CHUNK)).getNerTag();
//                        if(ner.equalsIgnoreCase("LOCATION")){
//                            nerScore = 0.2;
//                        } else if(ner.equalsIgnoreCase("PERSON")){
//                            nerScore = 0.7;
//                        } else { //organization
//                            nerScore = 0.5;
//                        }
//                        score += nerScore;
                        score++;
                    }
                }
                
                if (score > 0) {//if n-gram is an anaphor
                    //normalize score for NOR by total # of phrases 
                    k.putFeature(IS_CHUNK, (score / tokens.size()));
                } else {
                    k.putFeature(IS_CHUNK, 0.0);
                }
                
                System.out.println("ner for " + k.getSurface() + " : " + score/tokens.size());
                
            }
        }
    }
}
