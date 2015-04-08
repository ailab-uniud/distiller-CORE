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

import org.uniud.dcore.persistence.*;

/**
 * The entry point of a text in the Distiller. This class splits the text into a
 * tree of {@link org.uniud.dcore.persistence.DocumentComponent}, which mimics 
 * the structure of a document. For example, when a document is divided in 
 * two chapters, every chapter is a DocumentComponent. If a chapter has two 
 * subsections, each subsection is a DocumentComponent, and the chapter should 
 * reference them; and so on. The basic component is the {@link org.uniud.dcore.persistence.Sentence}; 
 * any other composite part of the document is a {@link org.uniud.dcore.persistence.DocumentComposite}.
 * In this example, chapters and subsections should actually be DocumentComposite.
 * 
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public abstract class Splitter {
    
    protected abstract DocumentComponent Split(String rawText);
    
    public void buildModel(String rawText) throws IllegalStateException {
        
        DocumentComponent splitted = Split(rawText);
        
        // TODO: Check this coherence check             
        if (splitted.getRawText().equals(rawText)) {
            throw new IllegalStateException("Document built after splitting is different from original text.");            
        }
        else
            BlackBoard.Instance().createDocument(rawText, splitted);
    }
    
}