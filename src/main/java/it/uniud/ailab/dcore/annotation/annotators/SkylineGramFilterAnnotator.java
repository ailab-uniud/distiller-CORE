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

import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This annotator removes all the non-relevant grams from the blackboard, 
 * according to their keyphraseness score.
 * 
 * @author Marco Basaldella
 */
public class SkylineGramFilterAnnotator implements Annotator {

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        
        // get the grams and order them by score
        Collection<Gram> grams = blackboard.getKeyphrases();
        Map<Keyphrase, Double> scoredGrams = new HashMap<>();

        for (Gram g : grams) {
            Keyphrase k = (Keyphrase)g;
            scoredGrams.put(k, k.getFeature(GenericEvaluatorAnnotator.SCORE));
        }

        List<Map.Entry<Keyphrase, Double>> gramsToTrash
                = scoredGrams.entrySet().stream().sorted(
                        Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toList());
        
        // now the have the gram ordered by score. 
        // we want to keep at least the first 5%; then, we look for the maximum
        // steep in score in the following 20%, and we discard everything after
        // that remains.
        
        double bestRange = Math.ceil((gramsToTrash.size() * 5.0) / 100.0);
        double steepRange = Math.ceil((gramsToTrash.size() * 25.0) / 100.0);
        
        // keep the first 5%
        for (int i = 0; i < bestRange; i++) {
            gramsToTrash.remove(0);
        }
        
        double maxSteep = Double.MIN_VALUE;
        int maxSteepIndex = Integer.MIN_VALUE;
        
        // search for the maximum steep in the next 15%
        for (int i = 0; i < steepRange - 1; i++) {
            
            double steep = (gramsToTrash.get(i).getValue() - 
                    gramsToTrash.get(i+1).getValue());
            
            if (steep > maxSteep) {
                maxSteep = steep;
                maxSteepIndex = i;                
            }
        }
        
        // keep the grams before the steep
        for (int i = 0; i < maxSteepIndex; i++) {
            gramsToTrash.remove(0);
        }
        
        // now remove the remaining grams from the blackboard.        
        for (Map.Entry<Keyphrase, Double> e : gramsToTrash) 
            blackboard.removeKeyphrase(e.getKey());        
    }
    
}
