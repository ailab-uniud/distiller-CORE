/*
 *  This file is part of Distiller-CORE.

    Distiller-CORE is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Distiller-CORE is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Distiller-CORE.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.uniud.dcore.engine;

import org.uniud.dcore.persistence.DocumentComponent;


/**
 * The Annotator interface should be implemented by any class that reads 
 * the whole text (or parts of it) and produces annotations over it.
 * 
 * Part-Of-Speech taggers and Named-Entity-Recognition modules are examples
 * of annotators.
 * 
 * @author Marco Basaldella, Dario De Nart
 */
public interface Annotator {
    
    /**
     * The abstract annotation class. All classes that perform some kind of 
     * annotation (splitting, PoS tagging, entity linking...) must inherit from
     * Annotator. They annotate the component that is passed as parameter and
     * then return the annotated object to the caller, that writes it on the 
     * {@link org.uniud.dcore.engine.BlackBoard}.
     * 
     * @param component component to annotate.
     */
    public void annotate(DocumentComponent component);
    
    
}
