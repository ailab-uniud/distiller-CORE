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
package it.uniud.ailab.dcore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rits.cloning.Cloner;
import java.util.HashMap;
import java.util.Map;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.DocumentComposite;
import it.uniud.ailab.dcore.annotation.Annotation;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The BlackBoard that holds the document and all its annotations. In every part
 * of the extraction pipeline, every annotator will receive a piece of the
 * document contained in the blackboard and will write new information on it.
 *
 * The Blackboard has two main parts: one is the document root, which allows to
 * navigate the entire document; the other is the gram container, which contains
 * all the grams found in the document and their locations.
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public class Blackboard {

    /**
     * The default document identifier.
     */
    private static final String DEFAULT_DOCUMENT_ID = "DocumentRoot";

    /**
     * The full raw text of the document.
     */
    private String rawText;

    /**
     * The root block of the document.
     */
    private DocumentComponent document;

    /**
     * Container for the n-grams of the document. Every n-gram is part of a
     * specific list identifying the type of n-gram. The types of n-grams are
     * the key for searching in the main n-gram list.
     */
    private Map<String, Map<String, Gram>> generalNGramsContainer;

    /**
     * Document-wide annotations. This space can be used to add annotations that
     * belong to the whole document, as for example extracted concepts, or
     * tagset used by a POS-tagger, or the overall sentiment.
     */
    private List<Annotation> annotations;

    /**
     * Instantiates an empty blackboard.
     */
    public Blackboard() {
        createDocument("");
    }

    /**
     * Initializes the blackboard with a new document. This will destroy any
     * information previously held by the blackboard.
     *
     * @param rawText the text of the new document.
     * @param documentId the output-friendly identifier of the document
     */
    public final void createDocument(String rawText, String documentId) {
        this.rawText = rawText;
        this.document = new DocumentComposite(rawText, documentId);
        this.generalNGramsContainer = new HashMap<>();
        this.annotations = new ArrayList<>();
    }

    /**
     * Initializes the blackboard with a new document. This will destroy any
     * information previously held by the blackboard.
     *
     * @param rawText the text of the new document.
     */
    public final void createDocument(String rawText) {
        this.rawText = rawText;
        this.document = new DocumentComposite(rawText, DEFAULT_DOCUMENT_ID);
        this.generalNGramsContainer = new HashMap<>();
        this.annotations = new ArrayList<>();
    }

    /**
     * Gets the root of the document.
     *
     * @return the {@link it.uniud.ailab.dcore.persistence.DocumentComponent}
     * root object.
     */
    @JsonIgnore
    public DocumentComponent getStructure() {
        return document;
    }

    /**
     * Gets the raw text (i.e. unprocessed) of the document.
     *
     * @return the original document string.
     */
    public String getText() {
        return rawText;
    }

    /**
     * Adds a Gram in the Gram Container, merging grams with the same
     * identifier. If the gram is already present, the method updates it adding
     * the new occurrence. Annotations of the new gram are <b>not</b> merged
     * into the old gram. This is because it's good practice to annotate grams
     * only when they've <b>all</b> been added into the blackboard.
     *
     * @param unit the concept unit where the gram appears
     * @param newGram the gram to add
     */
    public void addGram(DocumentComponent unit, Gram newGram) {

        Map<String, Gram> grams = generalNGramsContainer.get(newGram.getType());
        if (grams == null) {
            grams = new HashMap<>();
        }
        Gram gram = grams.get(newGram.getIdentifier());

        // Deep clone the object instead of referencing the found one.
        // this way, we're free to modify it by adding annotations without
        // modifying the old object.
        if (gram == null) {
            Gram cloned = (new Cloner()).deepClone(newGram);
            grams.put(cloned.getIdentifier(), cloned);
            gram = cloned;
        } else {
            // add the surface of the new gram
            gram.addSurfaces(newGram.getSurfaces(), newGram.getTokenLists());
        }

        gram.addAppaerance(unit);
        unit.addGram(gram);
        generalNGramsContainer.put(newGram.getType(), grams);
    }

    public void addGram(Gram newGram) {

        Map<String, Gram> grams = generalNGramsContainer.get(newGram.getType());
        if (grams == null) {
            grams = new HashMap<>();
        }
        Gram gram = grams.get(newGram.getIdentifier());

        // Deep clone the object instead of referencing the found one.
        // this way, we're free to modify it by adding annotations without
        // modifying the old object.
        if (gram == null) {
            Gram cloned = (new Cloner()).deepClone(newGram);
            grams.put(cloned.getIdentifier(), cloned);
        } else {
            // add the surface of the new gram
            gram.addSurfaces(newGram.getSurfaces(), newGram.getTokenLists());
        }

        generalNGramsContainer.put(newGram.getType(), grams);
    }
    
    /**
     * Get the all the different kind of grams found in the document. This
     * grams are divided by type, stored in a Map using their identifier as 
     * key.
     * 
     * @return all the maps found in the document.
     */
    public Map<String,Map<String,Gram>> getGrams() {
        return generalNGramsContainer;
    }

    /**
     * Get all the grams of a given type found in the blackboard.
     * 
     * @param <T> the type of the grams to achieve
     * @param gramType the identifier of the gram type
     * @return a collection with all the grams with match type and identifier
     */
    public <T> Collection<T> getGramsByType(String gramType) {
        return (Collection<T>)generalNGramsContainer.get(gramType).values();
    }

    /**
     * Retrieves the keyphrases found in the document.
     *
     * @return a collection of
     * {@link it.uniud.ailab.dcore.persistence.Keyphrase}s.
     */
    @Deprecated
    @JsonIgnore
    public List<Gram> getKeyphrases() {

        Map<String, Gram> kps = generalNGramsContainer.get(Keyphrase.KEYPHRASE);
        return kps != null ? new ArrayList(kps.values()) : new ArrayList();
    }

    /**
     * Removes a keyphrase from the document because it's no more relevant, or
     * useful, or for whatever reason an annotator thinks so.
     *
     * @param g the gram to remove.
     */
    @Deprecated
    public void removeKeyphrase(Keyphrase g) {
        removeGram(Keyphrase.KEYPHRASE,g);
    }
    
    /**
     * Removes a gram from the document because it's no more relevant, or
     * useful, or for whatever reason an annotator thinks so.
     *
     * @param type the type of the gram to remove
     * @param g the gram to remove.
     */
    public void removeGram(String type,Gram g) {
        generalNGramsContainer.get(type)
                .remove(g.getIdentifier());

        for (Sentence s : DocumentUtils.getSentences(document)) {
            s.removeGram(g);
        }
    }

    /**
     * Adds an annotation in the blackboard.
     *
     * @param a the annotation to add
     */
    public void addAnnotation(Annotation a) {
        annotations.add(a);
    }

    /**
     * Get all the annotations.
     *
     * @return the annotations stored in the blackboard
     */
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * Gets the annotations produced by a specific annotator.
     *
     * @param annotator the annotator identifier
     * @return the annotations produced by the specified annotator
     */
    public List<Annotation> getAnnotations(String annotator) {
        return annotations.stream().filter((a)
                -> (a.getAnnotator().equals(annotator))).
                collect(Collectors.toList());
    }

    /**
     * Remove an annotation from the blackboard.
     * 
     * @param ann the annotation to remove.
     */
    public void removeAnnotation(Annotation ann) {
        annotations.remove(ann);
    }
    
    /**
     * Get the language of the document root.
     * 
     * @return the language of the document root.
     */
    public String getDocumentLanguage() {
        return getStructure().getLanguage().getLanguage();
    }

}
