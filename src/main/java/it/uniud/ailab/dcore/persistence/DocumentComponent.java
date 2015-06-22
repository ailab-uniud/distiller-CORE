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
    public abstract List<TextAnnotation> getAnnotations() ;
    
    public abstract void addGram(Gram g);
    
    public abstract List<Gram> getGrams();
    
    
    // </editor-fold>
    
    @Override
    public String toString() {
        return text;
    }

}
