/*
 * Copyright (C) 2015 Artificial Intelligence
 * Laboratory @ University of Udine.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package it.uniud.ailab.dcore.persistence;

import it.uniud.ailab.dcore.annotation.Annotable;
import java.util.List;
import java.util.Locale;

/**
 * An abstract conceptual unit of the document. This can be a sentence, a
 * chapter, a paragraph, and so on. In the Composite pattern, this is the root
 * of the structure.
 *
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 * 
 * Add a map containing the coreference graph, indixed by anaphors
 * (I insert the map here in respect to the composite pattern, this is a 
 * feature not a component of the document, like sentences, ngrams and so on)
 * 
 * @modify Giorgia Chiaradia
 */
public abstract class DocumentComponent extends Annotable {

    private final String text;
    private String preprocessedText;
    private Locale language;

    /**
     * Creates a document component.
     *
     * @param text the text of the component
     * @param language the language of the component
     * @param identifier the unique identifier for the component
     */
    public DocumentComponent(String text, Locale language, String identifier) {
        super(identifier);
        this.text = text;
        this.language = language;
        this.preprocessedText = text;
    }

    // <editor-fold desc="getters and setters">
    /**
     * Returns the text of the component
     *
     * @return the text of the component.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the language of the component.
     *
     * @param language the language of the component
     */
    public void setLanguage(Locale language) {
        this.language = language;
        if (hasComponents()) {
            for (DocumentComponent c : getComponents()) {
                c.setLanguage(language);
            }
        }
    }

    /**
     * Returns the language of the component.
     *
     * @return the language of the component.
     * @see <a href="http://tools.ietf.org/html/rfc5646">RFC5646</a>
     * specification to know about language tags used by Java.
     */
    public Locale getLanguage() {
        return this.language;
    }

    /**
     * Check if the component is a leaf ( so it can be a
     * {@link it.uniud.ailab.dcore.persistence.Sentence}), or if it has
     * children, so it's surely a
     * {@link it.uniud.ailab.dcore.persistence.DocumentComposite}.
     *
     * @return true if the component has children; otherwise false.
     */
    public boolean hasComponents() {
        List<DocumentComponent> comps = getComponents();
        return (comps != null) && (comps.size() > 0);
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
     * Adds a gram to the component.
     *
     * @param g the gram to add
     */
    public abstract void addGram(Gram g);

    /**
     * Returns the gram associated with the component.
     *
     * @return the gram associated with the component.
     */
    public abstract List<Gram> getGrams();
    
    /**
     * Remote a gram from the component.
     * 
     * @param g the gram tor remove
     */
    public abstract void removeGram(Gram g);

    // </editor-fold>

    /**
     * Get the string representation of the component.
     *
     * @return the string that represent the component (which has been set in
     * the constructor).
     */
    @Override
    public String toString() {
        return text;
    }

    public void setPreprocessedText(String ppText) {
        this.preprocessedText = ppText;
    }

    public String getPreprocessedText(){
        return preprocessedText;
    }
}
