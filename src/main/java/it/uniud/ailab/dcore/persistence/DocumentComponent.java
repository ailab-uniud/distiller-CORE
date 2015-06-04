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
package it.uniud.ailab.dcore.persistence;

import java.util.List;
import java.util.Locale;

/**
 * An abstract conceptual unit of the document. This can be a sentence, a chapter, 
 * a paragraph, and so on. In the Composite pattern, this is the root of 
 * the structure.
 * 
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public abstract class DocumentComponent {  
    
    private String text;
    private Locale language;
    
    public DocumentComponent(String text,Locale language) {
        this.text = text;
        this.language = language;
    }
    
    
    public DocumentComponent(String text) {
        this(text,null);
    }
    
    // <editor-fold desc="getters and setters">
    

    
    public String getText() {
        return text;
    }
    
    public void setLanguage(Locale language) {
        this.language = language;
        if (hasComponents()) {
            for (DocumentComponent c : getComponents())
                c.setLanguage(language);
        }
    }
    
    
    public Locale getLanguage() {
        return this.language;
    }
    
    
    public boolean hasComponents() {
        List<DocumentComponent> comps = getComponents();
        return !(comps == null);
    }
    
    // </editor-fold>
        
    // <editor-fold desc="abstract methods">

    /**
     * Returns the children of the document component, or null if the current 
     * concept unit has no children (a sentence, the leaf of the tree).
     *
     * @return the children of the document component, or null if the current 
     * concept unit has no children.
     */
    public abstract List<DocumentComponent> getComponents();
        
    /**
     * Returns all the annotations associated on a component.
     * 
     * @return the list of annotations.
     */
    public abstract List<Annotation> getAnnotations() ;
    
    public abstract void addGram(Gram g);
    
    public abstract List<Gram> getGrams();
    
    
    // </editor-fold>
    
    @Override
    public String toString() {
        return text;
    }

}
