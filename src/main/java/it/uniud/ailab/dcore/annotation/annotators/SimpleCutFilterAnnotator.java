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
 * keeping only the best N.
 * 
 * @author Marco Basaldella
 */
public class SimpleCutFilterAnnotator implements Annotator {
    
    /**
     * The number of keyphrases to keep.
     */
    private int cut = 10;

    /**
     * Set how many keyphrase should the filter keep.
     * 
     * @param cut the keyphrases to keep.
     */
    public void setCut(int cut) {
        this.cut = cut;
    }

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
        // we keep the best n grams (where n = cut) and remove the others.
        
        // keep the first 5%
        for (int i = 0; i < cut; i++) {
            gramsToTrash.remove(0);
        }
        
        // now remove the remaining grams from the blackboard.        
        for (Map.Entry<Gram, Double> e : gramsToTrash) 
            blackboard.removeGram(e.getKey());        
    }
    
}
