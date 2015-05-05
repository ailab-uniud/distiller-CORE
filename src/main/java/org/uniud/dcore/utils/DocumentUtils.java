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
package org.uniud.dcore.utils;

import java.util.ArrayList;
import java.util.List;
import org.uniud.dcore.persistence.DocumentComponent;
import org.uniud.dcore.persistence.Sentence;
import org.uniud.dcore.persistence.Token;

/**
 * Utilies for the {@link org.uniud.dcore.persistence.DocumentComponent} 
 * interface and its composite pattern classes 
 * {@link org.uniud.dcore.persistence.DocumentComposite} and
 * {@link org.uniud.dcore.persistence.Sentence}, used to perform common
 * operations on a document (or parts of it).
 * 
 * @author Marco Basaldella
 */
public class DocumentUtils {
            
    public static List<Sentence> getSentences(DocumentComponent component) {
        List<Sentence> ret = new ArrayList<>();
        
        for (DocumentComponent c : component.getComponents()) {
            if (c.getComponents() == null)
                ret.add((Sentence)c);
            else
                ret.addAll(getSentences(c));
        }
        
        return ret;
    }   
    
    public static String getAnnotatedComponent(DocumentComponent c) {
        
        String ret = "";
        
        if (c.hasComponents()) {
            
            ret = ret + "Component\n";
            
            for (DocumentComponent comp : c.getComponents()) {
                ret = ret + getAnnotatedComponent(comp);
            }
            
        } else {
            for (Token t : ((Sentence)c).getTokens()) {
                ret = ret + (ret.length() == 0 ? "" : " ") + t;
            }
            ret = ret + "\n";
        }
        
        return ret;
    }
    
    
    
}
