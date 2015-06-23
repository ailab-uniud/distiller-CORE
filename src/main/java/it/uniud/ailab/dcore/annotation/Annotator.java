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

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;


/**
 * The interface that should be used by any class that reads 
 * the whole text (or parts of it) and produces annotations over it.
 * 
 * Part of Speech taggers and Named Entity Recognition modules are examples
 * of annotators.
 * 
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public interface Annotator {
    
    /**
     * The abstract annotation method. All classes that perform some kind of 
     * annotation (splitting, PoS tagging, entity linking...) must inherit from
     * Annotator. They annotate the blackboard given as first parameter or a 
     * component of the blackboard, given as second parameter. 
     * 
     * @param blackboard blackboard to annotate
     * @param component component to annotate.
     */
    public void annotate(Blackboard blackboard, DocumentComponent component);
    
    
}
