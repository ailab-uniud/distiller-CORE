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
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public abstract class ConceptUnit {  

    private List<String> Annotations;
    
    public ConceptUnit() {
        Annotations = new ArrayList<String>();
    }
    
    public void addAnnotation(String annotation) {
        Annotations.add(annotation);
    }
    
    public List<String> getAnnotations() {
        return Annotations;
    }
    
    // <editor-fold desc="abstract methods">

    /**
     *
     * @return
     * @throws org.uniud.dcore.persistence.EndOfTreeException
     */
    public abstract ConceptBlock[] getSubBlocks() throws EndOfTreeException;
        
    public abstract String getRawText() ;
    // </editor-fold>
    
}
