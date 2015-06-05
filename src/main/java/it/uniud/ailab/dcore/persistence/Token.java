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

import it.uniud.ailab.dcore.annotation.TextAnnotation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class Token implements Cloneable {

    private String text;
    private String stem;
    private String PoS;
    private List<TextAnnotation> annotations;

    public Token(String text){
        annotations = new ArrayList<TextAnnotation>();
        this.text = text;
    }
    
    // <editor-fold desc="Id, stem and PoS">
    public void setStem(String stem) {
        this.stem = stem;
    }
    
    public void setPoS(String PoS) {
        this.PoS = PoS;
    }   

    public String getText() {
        return text;
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
     * Adds an annotation to the token. Avoid duplicates by checking if 
     * an annotation has already been added before. Note that two annotations are
     * equal only if an annotator annotates the same text with the same annotation.
     * 
     * See {@link it.uniud.ailab.dcore.persistence.TextAnnotation} implementation of
     * the equals() method if it's not clear.
     * 
     * @param annotation the annotation to add.
     */
    public void addAnnotation(TextAnnotation annotation) {
        if (!annotations.contains(annotation))
                annotations.add(annotation);
    }
    
    /**
     * Gets all the annotations associated with the token.
     * 
     * @return the annotations associated to the token.
     */
    public List<TextAnnotation> getAnnotations() {
        return annotations;
    } 

    // </editor-fold>
    
    @Override
    public String toString() {
        String ret = text + " {(POS:" + getPoS() + ")" ;
        for (TextAnnotation a : getAnnotations()) {
            ret = ret + ", (" + a.getAnnotator() + ":" + a.getAnnotation() + ")" ;
        }
        return ret + "}";
    }            

    public TextAnnotation getAnnotation(String annotation) {
        TextAnnotation a = null;
        for (TextAnnotation b : this.getAnnotations())
        {
            if (b.getAnnotator().equals(annotation))  {
                a = b;
                break;
            }
        }
        return a;
    }
}
