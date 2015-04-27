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

package org.uniud.dcore.engine;

import java.util.AbstractMap;
import org.uniud.dcore.persistence.DocumentComponent;
import org.uniud.dcore.persistence.DocumentComposite;
import org.uniud.dcore.persistence.Feature;
import org.uniud.dcore.persistence.Gram;

/**
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public class BlackBoard {
    
    // <editor-fold desc="Singleton Pattern">
    /**
     * Instance of the singleton.
     */ 
    private final static BlackBoard INSTANCE = new BlackBoard();
    
    /**
     * Private constructor for the singleton design pattern.
     */
    private BlackBoard() { }
        
    /**
     * The singleton of the class. 
     * @return the document model.
     */
    public static BlackBoard Instance() {
        return INSTANCE;
    }   
    // </editor-fold>
      
    /**
     * The full raw text of the document.
     */
    private String rawText;
    
    /**
     * The root block of the document. 
     */
    private DocumentComponent document;
    
    /**
     * Container for the annotations of the document.
     */
    private AbstractMap<String,Feature> AnnotationContainer;
    /**
     * Container for the n-grams of the document.
     */
    private AbstractMap<String,Gram> GramContainer;
        
    
    public void createDocument(String rawText)
    {
        this.rawText = rawText;
        this.document = new DocumentComposite(rawText);
    }

    public DocumentComponent getStructure() {
        return document;
    }    
    public String getText() {
        return rawText;
    }
    
    /**
     * Adds a Gram in the Gram Container. If the gram is already present, 
     * the method updates it adding the new occurrence.
     * 
     * @param unit the concept unit where the gram appears
     * @param newGram the gram to add
     */
    public void addGram(DocumentComponent unit,Gram newGram) {        
        
        Gram gram = GramContainer.get(newGram.getSignature());
        
        if (gram == null) {
            gram = GramContainer.put(newGram.getSignature(), newGram);
        }
        
        gram.addAppaerance(unit);
        unit.addGram(gram.getSignature());
    }
    
    /**
     * Retrieves the grams found in the document.
     * 
     * @return an array of {@link org.uniud.dcore.persistence.Gram}s.
     */
    public Gram[] getGrams() {
        return GramContainer.values().toArray(new Gram[GramContainer.size()]);
    }

}
