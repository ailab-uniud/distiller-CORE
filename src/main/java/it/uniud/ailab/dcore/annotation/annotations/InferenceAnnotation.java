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
import java.net.URI;

/**
 *
 * @author Marco Basaldella
 */
public class InferenceAnnotation extends Annotation {
    
    /**
     * The concept inferred.
     */
    private final String concept;

    /**
     * The score associated to the concept.
     */
    private final double score;
    
    /**
     * The URI associated with the concept.
     */
    private final URI uri;
    
    /**
     * Instantiates the annotation.
     * 
     * @param annotator the annotator that generated the annotation.
     * @param concept the annotated concept.
     * @param score the score associated to the concept by the annotator.
     * @param uri the URI associated to the concept
     */
    public InferenceAnnotation(String annotator,String concept,
            double score,URI uri) {
        super(annotator);
        this.concept = concept;
        this.score = score;
        this.uri = uri;
    }
    
    /**
     * Gets the concept inferred by the annotator.
     * 
     * @return the concept.
     */
    public String getConcept() {
        return concept;
    }

    /**
     * Gets the score associated to the concept inferred.
     * 
     * @return the score generated by the annotator.
     */
    public double getScore() {
        return score;
    }
    
    /**
     * Gers the URI associated with the concept.
     * 
     * @return the URI associated with the concept.
     */
    public URI getUri() {
        return uri;
    }
}