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
}
