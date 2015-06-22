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
 * The annotator that calculates the importance of an n-gram in a document, 
 * defined as keyphraseness, using a simple linear combination of all the 
 * gram features found by previous annotator.
 * 
 * @author Marco Basaldella
 */
public class LinearEvaluatorAnnotator implements Annotator {
        
    /**
     * The weights of the linear combination that generates the scores.
     */
    private Map<String,Double> weights = new HashMap<>();
    
    /**
     * Sets the weight of the the linear combination that will generate the 
     * scores.
     * 
     * @param weights the weights of the features
     */
    public void setWeights(Map<String,Double> weights) {
        this.weights = weights;
    }
    
    /**
     * Adds a feature to the linear combination.
     * 
     * @param feature the feature to add.
     * @param weight the weight of the feature to add.
     */
    public void addWeight(String feature, double weight) {
        weights.put(feature, weight);
    }    
    
    /**
     * The method which performs the actual scoring of the grams.
     * 
     * @param b the blackboard to annotate
     * @param c the component to annotate
     */
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
