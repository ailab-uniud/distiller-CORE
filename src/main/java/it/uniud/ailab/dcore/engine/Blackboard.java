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

package it.uniud.ailab.dcore.engine;

import com.rits.cloning.Cloner;
import java.util.HashMap;
import java.util.Map;
import it.uniud.ailab.dcore.annotation.TextAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.DocumentComposite;
import it.uniud.ailab.dcore.annotation.GenericAnnotation;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Token;
import java.util.Collection;
import java.util.List;

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
    
//    // <editor-fold desc="Singleton Pattern">
//    /**
//     * Instance of the singleton.
//     */ 
//    private final static Blackboard INSTANCE = new Blackboard();
//    
    /**
     * Instantiates an empty blackboard.
     */
    public Blackboard() { 
        gramContainer = new HashMap<>();
    }
        
//    /**
//     * The singleton of the class. 
//     * @return the document model.
//     */
//    public static Blackboard Instance() {
//        return INSTANCE;
//    }   
//    // </editor-fold>
      
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
    private List<GenericAnnotation> annotation;
        
    /**
     * Initializes the blackboard with a new document. This will destroy any
     * information previously held by the blackboard.
     * 
     * @param rawText the text of the new document.
     */    
    public void createDocument(String rawText)
    {
        this.rawText = rawText;
        this.document = new DocumentComposite(rawText);
        this.gramContainer = new HashMap<>();
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
        
        Gram gram = gramContainer.get(newGram.getSignature());
        
        // Deep clone the object instead of referencing the found one.
        // this way, we're free to modify it by adding annotations without
        // modifying the old object.
        if (gram == null) {
            Gram cloned = (new Cloner()).deepClone(newGram);
            gramContainer.put(cloned.getSignature(), cloned);
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
    public Collection<Gram> getGrams() {
        return gramContainer.values();
    }

}