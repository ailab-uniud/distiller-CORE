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
 * A part of a document which is composed by other sub-parts. For example, a Section
 * may be divided in Chapters, a Chapter in Paragraphs, and so on. In the Composite 
 * pattern, this is the "composite" class of the structure.
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public class DocumentComposite extends DocumentComponent {
    
    private DocumentComponent[] subUnits;
    //<editor-fold desc="Getters and setters">
    @Override
     public DocumentComponent[] getComponents() {
        return subUnits;
    }
     
     public void setComponents(DocumentComposite[] subBlocks) throws IllegalStateException {
         if (this.subUnits == null)
             this.subUnits = subBlocks;
         else
             throw new IllegalStateException("Trying to set SubBlocks of a ConceptBlock more than once.");                     
     }
    //</editor-fold>
     
     //<editor-fold desc="overrides">     
     @Override
    public String getRawText() {
        
        String output = "";
        
        for (DocumentComponent cb:subUnits) {
            output = output.concat(cb.getRawText());
        }
        
        return output;    
    }
    //</editor-fold>
    
}
