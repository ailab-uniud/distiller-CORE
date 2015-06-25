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
package it.uniud.ailab.dcore.annotation.annotations;

import it.uniud.ailab.dcore.annotation.Annotation;

/**
 * A feature is a value generated by an annotator that expresses some
 * property of the annotated object. For example, a sentiment annotator
 * may assign a high value over a gram for positive sentiment, and negative 
 * otherwise.
 * 
 * @author Marco Basaldella
 */
public class FeatureAnnotation 
        extends Annotation
        implements ScoredAnnotation {
    
    private final double score;
    
    /**
     * A feature.
     * 
     * @param annotator the annotator that generated the feature.
     * @param score the value of the feature.
     */
    public FeatureAnnotation(String annotator,double score) {
        super(annotator);
        this.score = score;
    }
    
    /**
     * Get the value of the feature.
     * 
     * @return the value of the feature
     */
    @Override
    public double getScore() {
        return score;
    }  
}
