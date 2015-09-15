/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 * 
 * 	This file is part of the Distiller-CORE library.
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

/**
 * An abstract annotation. It forces child annotators to identify themselves
 * by calling the one-argument constructor, where they have to pass their 
 * identifier as a string. 
 * 
 * Identifiers should be set as public static final fields in each Annotator
 * class, such as in 
 * {@link it.uniud.ailab.dcore.annotation.annotators.GenericEvaluatorAnnotator}.
 * 
 * @author Marco Basaldella
 */
public abstract class Annotation {
    protected final String annotator;
    
    protected Annotation(String annotator) {
        this.annotator = annotator;
    }
    
    /**
     * Gets the identifier of the annotator that produced the annotation.
     * 
     * @return the identifier of the annotator.
     */
    public String getAnnotator() {
        return annotator;
    }
    
    /**
     * By default, the toString method returns the name of the annotation.
     * When an annotation is specialized, it should be good practice to override
     * this method.
     * 
     * @return the name of the annotation
     */
    @Override
    public String toString() {
        return annotator;
    }
    
}
