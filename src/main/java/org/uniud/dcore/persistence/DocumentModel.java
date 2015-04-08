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

package org.uniud.dcore.persistence;

import java.util.AbstractMap;
import java.util.List;

/**
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public class DocumentModel {
    
    // <editor-fold desc="Singleton Pattern">
    /**
     * Instance of the singleton.
     */ 
    private final static DocumentModel INSTANCE = new DocumentModel();
    
    /**
     * Private constructor for the singleton design pattern.
     */
    private DocumentModel() { }
        
    /**
     * The singleton of the class. 
     * @return the document model.
     */
    public static DocumentModel Instance() {
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
    private ConceptUnit document;
    
    /**
     * Container for the annotations of the document.
     */
    private AbstractMap<String,Feature> AnnotationContainer;
    /**
     * Container for the n-grams of the document.
     */
    private AbstractMap<String,Gram> GramContainer;
        
    
    public void createDocument(String RawText, ConceptUnit document)
    {
        this.rawText = RawText;
        this.document = document;
    }

    public ConceptUnit getStructure() {
        return document;
    }
    
    public String getText() {
        return rawText;
    }
    
    
        
    
    /**
     * Adds an annotation in the appropriate container.
     * 
     * @param unit
     * @param featureValue
     * @param gram
     * @param annotationContent 
     */
    public void addGram(ConceptUnit unit,Gram newGram) {        
        
        Gram gram = GramContainer.get(newGram.getSignature());
        
        if (gram == null) {
            gram = GramContainer.put(newGram.getSignature(), newGram);
        }
        
        gram.addAppaerance(unit);
        unit.addGram(gram.getSignature());
    }
    
        
    public Gram[] getGrams() {
        return GramContainer.values().toArray(new Gram[GramContainer.size()]);
    }
    
}
