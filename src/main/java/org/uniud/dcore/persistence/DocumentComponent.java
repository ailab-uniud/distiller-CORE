/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 * 
 * 	This file is part of the Distiller-CORE library.
 * 
 * 	Distiller-CORE is free software; you can redistribute it and/or
 * 	modify it under the terms of the GNU Lesser General Public
 * 	License as published by the Free Software Foundation; either
 * 	version 2.1 of the License, or (at your option) any later version.
 *
 * 	Distiller-CORE is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * 	Lesser General Public License for more details.
 *
 * 	You should have received a copy of the GNU Lesser General Public
 * 	License along with this library; if not, write to the Free Software
 * 	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * 	MA 02110-1301  USA or see <http://www.gnu.org/licenses/>.
 */
package org.uniud.dcore.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract conceptual unit of the document. This can be a sentence, a chapter, 
 * a paragraph, and so on. In the Composite pattern, this is the root of 
 * the structure-
 * 
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public abstract class DocumentComponent {  

    private List<String> GramIDs;
    
    public DocumentComponent() {
        GramIDs = new ArrayList<String>();
    }
    
    public void addGram(String feature) {
        GramIDs.add(feature);
    }
    
    public List<String> getGrams() {
        return GramIDs;
    }
    
    // <editor-fold desc="abstract methods">

    /**
     * Returns the children of the document component, or null if the current 
     * concept unit has no children (a sentence, the leaf of the tree).
     *
     * @return the children of the document component, or null if the current 
     * concept unit has no children.
     */
    public abstract List<DocumentComponent> getComponents();
        
    public abstract String getRawText() ;
    // </editor-fold>
    
}
