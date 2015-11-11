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

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Keyphrase;
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
        HashMap<String, Keyphrase> surfaces = new HashMap<>();
        HashMap<Keyphrase, String> gram2surface = new HashMap<>();
        List<Sentence> sentences = DocumentUtils.getSentences(component);
        for (Keyphrase g:blackboard.getGrams()){
            String stemmedSurface = "";
            for (Token t: g.getTokens()){
                stemmedSurface += t.getStem() + " ";
            }
            surfaces.put(stemmedSurface.trim(), g);
            gram2surface.put(g, stemmedSurface.trim());
        }
        
        for (Sentence s : sentences) {
            
            for (Keyphrase g : s.getGrams()) {
                // annotate grams only once 
                 if (!g.hasFeature(MAXIMALITY)){
                     HashSet<Keyphrase> superterms = new HashSet<>();
                     String surface = gram2surface.get(g);
                     for(String candidate: surfaces.keySet()){
                         if(candidate.contains(surface)){
                             superterms.add(surfaces.get(candidate));
                         }
                     }
                     // now we have a HashSet stuffed up with the superterms
                     // i.e. the n-grams that contain the current n-gram
                     Double maximality = 0.0;
                     for(Keyphrase g2: superterms){
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
