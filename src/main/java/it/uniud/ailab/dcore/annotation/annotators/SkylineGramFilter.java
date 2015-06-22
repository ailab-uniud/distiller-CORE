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

import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
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
public class SkylineGramFilter implements Annotator {

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        
        // get the grams and order them by score
        Collection<Gram> grams = blackboard.getGrams();
        Map<Gram, Double> scoredGrams = new HashMap<>();

        for (Gram g : grams) {
            scoredGrams.put(g, g.getFeature(GenericEvaluatorAnnotator.SCORE));
        }

        List<Map.Entry<Gram, Double>> gramsToTrash
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
        for (Map.Entry<Gram, Double> e : gramsToTrash) 
            blackboard.removeGram(e.getKey());        
    }
    
}
