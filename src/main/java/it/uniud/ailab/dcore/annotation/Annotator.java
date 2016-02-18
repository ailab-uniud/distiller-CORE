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

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.Stage;
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
public interface Annotator extends Stage {
    
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
    
    
    @Override
    default void run(Blackboard b) {
        this.annotate(b, b.getStructure());
    }    
    
}
