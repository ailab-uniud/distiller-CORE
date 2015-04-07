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
 */
public class Annotation {
    private String type;
    private String label;
    private String text;
    
    private List<ConceptUnit> appearsIn; 

    /**
     * An annotation over a specified text. The text can be annotated with every
     * kind of information; for example, an entity detector can annotate a string
     * with its entity. A practical example may be an annotation of type 
     * "Wikipedia-en", the text "Software Engineering", and as label the link
     * at the Software Engineering page on the English Wikipedia.
     * 
     * @param type the type of the annotation.
     * @param text the annotated text.
     * @param label the label of the annotation.
     */
    public Annotation(String type,String text,String label) {
        this.type = type;
        this.text = text;
        this.label = label;
        appearsIn = new ArrayList<ConceptUnit>();
    }
    
    public String getText() {
        return text;
    }

    public String getLabel() {
        return label;
    }

    public String getWord() {
        return type;
    }
    
    public void addAppaerance(ConceptUnit unit) {
        appearsIn.add(unit);
    }
    
    public List<ConceptUnit> getAppaerances() {
        return appearsIn;
    }
    
    
    
}
