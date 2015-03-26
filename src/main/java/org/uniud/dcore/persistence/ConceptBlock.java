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

/**
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public class ConceptBlock extends ConceptUnit {
    
    private ConceptBlock[] subBlocks;
    //<editor-fold desc="Getters and setters">
    @Override
     public ConceptBlock[] getSubBlocks() throws EndOfTreeException{
        return subBlocks;
    }
     
     public void setSubBlocks(ConceptBlock[] subBlocks) throws IllegalStateException {
         if (this.subBlocks == null)
             this.subBlocks = subBlocks;
         else
             throw new IllegalStateException("Trying to set SubBlocks of a ConceptBlock more than once.");                     
     }
    //</editor-fold>
     
     //<editor-fold desc="overrides">     
     @Override
    public String getRawText() {
        
        String output = "";
        
        for (ConceptBlock cb:subBlocks) {
            output = output.concat(cb.getRawText());
        }
        
        return output;    
    }
    //</editor-fold>
    
}
