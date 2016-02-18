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
import it.uniud.ailab.dcore.annotation.Annotation;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.annotation.annotations.ScoredAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
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
