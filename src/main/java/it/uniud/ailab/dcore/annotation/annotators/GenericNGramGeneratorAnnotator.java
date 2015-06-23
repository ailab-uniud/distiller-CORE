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

import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;

/**
 * This module generates the n-grams of the document, using the 
 * {@link it.uniud.ailab.dcore.persistence.Annotation}s produced by the
 * previous Annotators and assigning them
 * {@link it.uniud.ailab.dcore.annotation.Feature}s accordingly. 
 * 
 * To correctly evaluate annotations, the module must know their syntax.
 * See the single Annotators to check how they output their result. 
 * 
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public interface GenericNGramGeneratorAnnotator extends Annotator {
    
    /**
     * An annotation which counts the number of nouns in a n-gram.
     */
    public static final String NOUNVALUE = "NounValue";
        
    /**
     * Generates the n-grams of a {@link it.uniud.ailab.dcore.persistence.DocumentComponent}
     * by using the POS patterns and other annotations produced by the Annotators.
     * 
     * @param blackboard the blackboard to analyze
     * @param component the component of the blackboard to analyze
     */
    @Override
    public abstract void annotate(
            Blackboard blackboard,DocumentComponent component);
      
}
