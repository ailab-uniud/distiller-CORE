/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 *
 * 	Licensed under the Apache License, Version 2.0 (the "License");
 * 	you may not use this file except in compliance with the License.
 * 	You may obtain a copy of the License at
 *
 * 	     http://www.apache.org/licenses/LICENSE-2.0
 *
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 */
package it.uniud.ailab.dcore.annotation.annotators;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Annotates grams with the Maximality feature. Maximality gives a hint of
 * how much an n-gram is a concept of its own right ngrams with low
 * maximality tend to appear in the text just as subsets of longer phrases,
 * therefore are less interesting. Maximality is explained in detail in the
 * following paper http://ceur-ws.org/Vol-1384/paper2.pdf
 * 
 * WARNING : this annotator requires some other annotator to compute the 
 * frequency of n-grams in the blackboard.
 * 
 * @author Dario De Nart
 */
public class PhraseMaximalityAnnotator implements Annotator {

    /**
     * The phrase maximality in the document
     */
    public static final String MAXIMALITY = "Maximality";

    /**
     * Annotates grams with the Maximality feature. Maximality gives a hint of
     * how much an n-gram is a concept of its own right ngrams with low
     * maximality tend to appear in the text just as subsets of longer phrases,
     * therefore are less interesting. Maximality is explained in detail in the
     * following paper http://ceur-ws.org/Vol-1384/paper2.pdf
     * 
     * WARNING : this annotator requires some other annotator to compute the 
     * frequency of n-grams in the blackboard.
     *
     * @param component the component to annotate. Only the n-grams contained in
     * such component will be annotated. Warning: Maximality is a document-wide
     * feature, even if just a section will be passed and annotated, the
     * maximality indexes will be evaluated document-wise
     *
     */
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        HashMap<String, Gram> surfaces = new HashMap<>();
        HashMap<Gram, String> gram2surface = new HashMap<>();
        List<Sentence> sentences = DocumentUtils.getSentences(component);
        for (Gram g:blackboard.getGrams()){
            String stemmedSurface = "";
            for (Token t: g.getTokens()){
                stemmedSurface += t.getStem() + " ";
            }
            surfaces.put(stemmedSurface.trim(), g);
            gram2surface.put(g, stemmedSurface.trim());
        }
        
        for (Sentence s : sentences) {
            
            for (Gram g : s.getGrams()) {
                // annotate grams only once 
                 if (!g.hasFeature(MAXIMALITY)){
                     HashSet<Gram> superterms = new HashSet<>();
                     String surface = gram2surface.get(g);
                     for(String candidate: surfaces.keySet()){
                         if(candidate.contains(surface)){
                             superterms.add(surfaces.get(candidate));
                         }
                     }
                     // now we have a HashSet stuffed up with the superterms
                     // i.e. the n-grams that contain the current n-gram
                     Double maximality = 0.0;
                     for(Gram g2: superterms){
                         maximality = Math.max(
                                 g2.getFeature(StatisticalAnnotator.FREQUENCY_SENTENCE)/
                                         g.getFeature(StatisticalAnnotator.FREQUENCY_SENTENCE)
                                 , maximality);
                     }
                     g.putFeature(MAXIMALITY, 1.0-maximality);
                 }
                
            }
        }
    }

}
