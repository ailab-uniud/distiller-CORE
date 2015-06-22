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

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Required;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.persistence.Gram;

/**
 *
 * @author Marco Basaldella
 */
public class LinearEvaluatorAnnotator implements Annotator {
        
    private Map<String,Double> weights;
    
    @Required
    public void setWeights(HashMap<String,Double> weights) {
        this.weights = weights;
    }

    @Override
    public void annotate(Blackboard b,DocumentComponent c) {        
        for (Gram g : b.getGrams())
        {
            double score = 0;
            for (FeatureAnnotation f : g.getFeatures()) {
                if (weights.containsKey(f.getAnnotator())) {
                    score += f.getValue() * weights.get(f.getAnnotator());
                }
            }            
            g.putFeature(it.uniud.ailab.dcore.annotation.annotators.GenericEvaluatorAnnotator.SCORE, score);
        }
              
    }    
}
