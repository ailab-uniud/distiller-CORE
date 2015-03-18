/*
 *     This file is part of Distiller-CORE.
 * 
 *     Distiller-CORE is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Distiller-CORE is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Distiller-CORE.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.uniud.dcore.persistence;

import java.util.List;

/**
 *
 * @author Marco Basaldella, Dario De Nart
 */
public class DocumentModel {
    
    // <editor-fold desc="Singleton Pattern">
    /**
     * Instance of the singleton.
     */ 
    private final static DocumentModel INSTANCE = new DocumentModel();
    
    /**
     * Private constuctor for the singleton design pattern.
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
      
    private List<Sentence> sentences;
    
}
