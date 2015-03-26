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

import java.util.HashMap;

/**
 *
 *
 */
public class Token {
   
    private String identifier;
    private String stem;
    private HashMap<String, String> annotations;
    
    public Token(){
        annotations = new HashMap<>();
    }
    
    // single annotation handling
    public void setAnnotation(String name, String value) {
        annotations.put(name, value);
    }
    
        public String getAnnotation(String name) {
        return annotations.get(name);
    }

    public void setAnnotations(HashMap<String, String> annotations) {
        this.annotations = annotations;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public HashMap<String, String> getAnnotations() {
        return annotations;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getStem() {
        return stem;
    }
    
    
            
}
