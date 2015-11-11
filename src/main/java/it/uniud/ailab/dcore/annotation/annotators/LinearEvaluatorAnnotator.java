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

import java.util.HashMap;
import java.util.Map;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.persistence.Keyphrase;

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
        for (Keyphrase g : b.getGrams())
        {
            double score = 0;
            for (FeatureAnnotation f : g.getFeatures()) {
                if (weights.containsKey(f.getAnnotator())) {
                    score += f.getScore() * weights.get(f.getAnnotator());
                }
            }            
            g.putFeature(it.uniud.ailab.dcore.annotation.annotators.GenericEvaluatorAnnotator.SCORE, score);
        }
        
    }    
}
