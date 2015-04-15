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
 *
 */
public class Token {

    private String word;
    private String stem;
    private String PoS;
    private List<Annotation> annotations;

    public Token(String word){
        annotations = new ArrayList<Annotation>();
        this.word = word;
    }
    
    // <editor-fold desc="Id, stem and PoS">
    public void setStem(String stem) {
        this.stem = stem;
    }
    
    public void setPoS(String PoS) {
        this.PoS = PoS;
    }   

    public String getWord() {
        return word;
    }

    public String getStem() {
        return stem;
    }
    
    public String getPoS() {
        return PoS;
    }
    
    // </editor-fold>
    
    // <editor-fold desc="Annotations">
    
    /**
     * Adds an annotation to the token.
     * 
     * @param annotation the annotation to add.
     */
    public void setAnnotation(Annotation annotation) {
        annotations.add(annotation);
    }
    
    /**
     * Gets all the annotations associated with the token.
     * 
     * @return the annotations associated to the token.
     */
    public Annotation[] getAnnotations() {
        return annotations.toArray(new Annotation[annotations.size()]);
    } 

    // </editor-fold>
            
}
