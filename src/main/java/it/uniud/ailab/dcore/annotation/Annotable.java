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
package it.uniud.ailab.dcore.annotation;

import java.util.HashMap;
import java.util.Map;

/**
 * A generic object that can be annotated. 
 * 
 *
 * @author Marco Basaldella
 */
public abstract class Annotable {
    
    /**
     * The list of annotation.
     */
    private final Map<String,Annotation> annotations = new HashMap<>();
    
    /**
     * The identifier or the annotated object.
     */
    private final String identifier;
    
    /**
     * Generate an Annotable object.
     * 
     * @param identifier the identifier of the object.
     */
    protected Annotable(String identifier) {
        this.identifier = identifier;
    }
    
    /**
     * Get the identifier of the object.
     * 
     * @return the identifier of the object.
     */
    public String getIdentifier() {
        return identifier;
    }
    
    /**
     * Get the original text of the annotable object.
     * 
     * @return the original text of the object.
     */
    public abstract String getText();
    
    /**
     * Adds an annotation to the object. If there's an annotation with the same
     * identifier, it will be overwritten.
     * 
     * @param ann the annotation to add.
     */
    public void addAnnotation(Annotation ann) {
        annotations.put(ann.getAnnotator(),ann);
    }
    
    /**
     * Get the annotation specified with the specified annotator.
     * 
     * @param annotator the annotator to search
     * @return the annotation requested, {@code null} if not found.
     */
    public Annotation getAnnotation(String annotator) {
        return annotations.get(annotator);
    }
    
    /**
     * Get all the annotations stored in the object.
     * 
     * @return all the annotations.
     */
    public Annotation[] getAnnotations() {
        return annotations.values().toArray(new Annotation[annotations.size()]);
    }
    
    /**
     * Check if the Annotable contains the specified annotation.
     * 
     * @param annotator the identifier of the annotation to search
     * @return true if the annotator has been annotated by the 
     * annotator provided as input, false otherwise.
     */
    public boolean hasAnnotation(String annotator) {
        return annotations.containsKey(annotator);
    }
    
    @Override
    public String toString() {
        return getIdentifier();
    }
}
