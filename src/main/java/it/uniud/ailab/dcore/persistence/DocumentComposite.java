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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A part of a document which is composed by other sub-parts. For example, a
 * Section may be divided in Chapters, a Chapter in Paragraphs, and so on. In
 * the Composite pattern, this is the "composite" class of the structure.
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public class DocumentComposite extends DocumentComponent {

    /**
     * The children of the current instance.
     */
    private List<DocumentComponent> components;

    /**
     * Create a document composite.
     * 
     * @param text the text of the composite.
     * @param language the language of the composite.
     * @param identifier the output friendly identifier of the composite
     */
    public DocumentComposite(String text, Locale language,String identifier) {
        super(text, language,identifier);
        components = new ArrayList<>();
    }
    
    /**
     * Create a composite with no language associated. This requires
     * a call to setLanguage before many of the annotators can actually work.
     * 
     * @param text the text of the composite.
     * @param id the output friendly identifier of the composite
     */
    public DocumentComposite(String text,String id) {
        this(text, null,id);
    }

    
//    /**
//     * Create a composite with no language associated. This requires
//     * a call to setLanguage before many of the annotators can actually work.
//     * 
//     * @param text the text of the composite.
//     */
//    public DocumentComposite(String text) {
//        this(text, null,text.substring(0, 17) + "...");
//    }

    //<editor-fold desc="Getters and setters">
     /**
     * Gets the sub-components of the composite.
     * 
     * @return the sub-components
     */
    @Override
    public List<DocumentComponent> getComponents() {
        return components;
    }

    /**
     * Add a component to the composite.
     * 
     * @param component the component to add.
     */
    public void addComponent(DocumentComponent component) {
        this.components.add(component);
    }

    /**
     * Sets the components of the composite, deleting the previous set ones
     * (if any).
     * 
     * @param components the new components of the composite.
     */
    public void setComponents(List<DocumentComponent> components) {
        this.components = components;
    }
    //</editor-fold>

    /**
     * You can't add a gram on a composite, since a Gram which is not associated
     * with a particular sentence has no sense. In other words, if I associate
     * a gram with a composite, how can I know what sub-component he belongs to?
     * 
     * @param g the gram to add
     */
    @Override
    public void addGram(Gram g) {
        throw new UnsupportedOperationException("You can't add Grams on a Composite object."); 
    }

    /**
     * Get all the grams of the component; more specifically, get all the grams
     * of all its sub-components.
     * 
     * @return the gram that appear in the component.
     */
    @Override
    public List<Gram> getGrams() {
        List<Gram> ret = new ArrayList<>();
        for (DocumentComponent c : getComponents())
        {
            ret.addAll(c.getGrams());
        }
        
        return ret;
    }
}
