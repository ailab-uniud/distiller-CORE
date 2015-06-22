/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 *
 * 	Licensed under the Apache License, Version 2.0 (the "License");
 * 	you may not use this file except in compliance with the License.
 * 	You may obtain a copy of the License at
 *
 * 	     http://www.apache.org/licenses/LICENSE-2.0
 *
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 */
package it.uniud.ailab.dcore.persistence;

import it.uniud.ailab.dcore.annotation.annotations.TextAnnotation;
import java.util.ArrayList;
import java.util.List;

/**
 * The smallest object of the Distiller, which represents a single word.
 */
public class Token {

    private final String text;
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
    
    public List<TextAnnotation> getAnnotations(String annotator) {
        List<TextAnnotation> ret = new ArrayList<>();
        for (TextAnnotation ann : this.getAnnotations())
        {
            if (ann.getAnnotator().equals(annotator))  {
                ret.add(ann);
            }
        }
        return ret;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Token other = (Token) obj;
        if (!text.equals(other.text)) {
            return false;
        }
        if (!stem.equals(other.stem)) {
            return false;
        }
        if (!PoS.equals(other.PoS)) {
            return false;
        }
        return true;
    }
    

    public TextAnnotation hasAnnotation(String annotator) {
        TextAnnotation a = null;
        for (TextAnnotation b : this.getAnnotations())
        {
            if (b.getAnnotator().equals(annotator))  {
                a = b;
                break;
            }
        }
        return a;
    }
}
