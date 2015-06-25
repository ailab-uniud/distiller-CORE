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
import it.uniud.ailab.dcore.annotation.Annotation;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.annotation.annotations.ScoredAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Filters an annotation on the blackboard keeping only the n best instances
 * of that annotation.
 *
 * @author Marco Basaldella
 */
public class SimpleAnnotationFilterAnnotator implements Annotator {
    
    /**
     * The annotation to filter.
     */
    private String annotation;
    
    /**
     * The range of the filter.
     */
    private int range;

    /**
     * Set the annotation to filter.
     * 
     * @param annotation the annotation to filter.
     */
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    /**
     * Set the range of the best annotations to keep.
     * 
     * @param range the best annotations to keep.
     */
    public void setRange(int range) {
        this.range = range;
    }
    
    /**
     * Keeps only the n best instances of the specified annotation
     * on the blackboard, removing the others.
     * 
     * @param blackboard the blackboard
     * @param component the component to annotate (not used)
     */
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        
        // get the grams and order them by score
        Collection<Annotation> annotations = blackboard.getAnnotations(annotation);
        Map<ScoredAnnotation, Double> scoredAnnotations = new HashMap<>();

        for (Annotation ann : annotations) {
            
            ScoredAnnotation scoredAnn = (ScoredAnnotation)ann;            
            scoredAnnotations.put(scoredAnn, scoredAnn.getScore());
        }

        List<Map.Entry<ScoredAnnotation, Double>> annotationsToTrash
                = scoredAnnotations.entrySet().stream().sorted(
                        Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toList());
        
        for (int i = 0; i < range && i < annotationsToTrash.size(); i++)
            annotationsToTrash.remove(0);
        
        for (Map.Entry<ScoredAnnotation, Double> entry : annotationsToTrash) 
            blackboard.removeAnnotation((Annotation)entry.getKey());
        
    }
    
}
