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

    private List<DocumentComponent> components;

    public DocumentComposite(String text, Locale language) {
        super(text, language);
        components = new ArrayList<DocumentComponent>();
    }

    public DocumentComposite(String text) {
        this(text, null);
    }

    //<editor-fold desc="Getters and setters">
    @Override
    public List<DocumentComponent> getComponents() {
        return components;
    }

    @Override
    public List<TextAnnotation> getAnnotations() {
        List<TextAnnotation> ret = new ArrayList<>();
        for (DocumentComponent c : getComponents())
        {
            ret.addAll(c.getAnnotations());
        }
        
        return ret;
    }

    public void addComponent(DocumentComponent component) {
        this.components.add(component);
    }

    public void setComponents(List<DocumentComponent> components) {
        this.components = components;
    }
    //</editor-fold>

    @Override
    public void addGram(Gram g) {
        throw new UnsupportedOperationException("You can't add Grams on a Composite object."); 
    }

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
