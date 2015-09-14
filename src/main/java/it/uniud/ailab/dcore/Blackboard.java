/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 * 
 * 	This file is part of the Distiller-CORE library.
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

package it.uniud.ailab.dcore;

import com.rits.cloning.Cloner;
import java.util.HashMap;
import java.util.Map;
import it.uniud.ailab.dcore.annotation.annotations.TextAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.DocumentComposite;
import it.uniud.ailab.dcore.annotation.Annotation;
import it.uniud.ailab.dcore.annotation.annotations.ScoredAnnotation;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The BlackBoard that holds the document and all its annotations. In every part  
 * of the extraction pipeline, every annotator will receive a piece of the document
 * contained in the blackboard and will write new information on it.
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
     * Instantiates an empty blackboard.
     */
    public Blackboard() { 
        createDocument("");
    }
    /**
     * The full raw text of the document.
     */
    private String rawText;
    
    /**
     * The root block of the document. 
     */
    private DocumentComponent document;

    /**
     * Container for the n-grams of the document.
     */
    private Map<String,Gram> gramContainer;
    
    /**
     * Document-wide annotations. This space can be used to add annotations that
     * belong to the whole document, as for example extracted concepts, 
     * or tagset used by a POS-tagger, or the overall sentiment.
     */
    private List<Annotation> annotations;
        
    /**
     * Initializes the blackboard with a new document. This will destroy any
     * information previously held by the blackboard.
     * 
     * @param rawText the text of the new document.
     */    
    public final void createDocument(String rawText)
    {
        this.rawText = rawText;
        this.document = new DocumentComposite(rawText);
        this.gramContainer = new HashMap<>();
        this.annotations = new ArrayList<>();
    }

    /**
     * Gets the root of the document.
     * 
     * @return the {@link it.uniud.ailab.dcore.persistence.DocumentComponent} root
     * object.
     */
    public DocumentComponent getStructure() {
        return document;
    }    
    
    /**
     * Gets the raw text (i.e. unprocessed) of the document.
     * @return the original document string.
     */
    public String getText() {
        return rawText;
    }
    
    /**
     * Adds a Gram in the Gram Container. If the gram is already present, 
     * the method updates it adding the new occurrence; moreover, it will write
     * the annotations of the new gram on the stored one.
     * 
     * @param unit the concept unit where the gram appears
     * @param newGram the gram to add
     */
    public void addGram(DocumentComponent unit,Gram newGram) {        
        
        Gram gram = gramContainer.get(newGram.getIdentifier());
        
        // Deep clone the object instead of referencing the found one.
        // this way, we're free to modify it by adding annotations without
        // modifying the old object.
        if (gram == null) {
            Gram cloned = (new Cloner()).deepClone(newGram);
            gramContainer.put(cloned.getIdentifier(), cloned);
            gram = cloned;
        } else {
            // copy the annotations in the stored gram
            for (int i = 0; i < newGram.getTokens().size(); i++) {
                Token newToken = newGram.getTokens().get(i);
                for (TextAnnotation a : newToken.getAnnotations()) {
                    gram.getTokens().get(i).addAnnotation(a);
                }
            }
        }
        
        gram.addAppaerance(unit);
        unit.addGram(gram);
    }
    
    /**
     * Retrieves the grams found in the document.
     * 
     * @return a collection of {@link it.uniud.ailab.dcore.persistence.Gram}s.
     */
    public List<Gram> getGrams() {
        return new ArrayList(gramContainer.values());
    }
    
    /**
     * Removes a gram from the document because it's no more relevant, 
     * or useful, or for whatever reason an annotator thinks so.
     * 
     * @param g the gram to remove.
     */
    public void removeGram(Gram g) {
        gramContainer.remove(g.getIdentifier());
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
        return annotations.stream().filter((a) -> 
                (a.getAnnotator().equals(annotator))).
                collect(Collectors.toList());
    }

    public void removeAnnotation(Annotation ann) {
        annotations.remove(ann);
    }

}
