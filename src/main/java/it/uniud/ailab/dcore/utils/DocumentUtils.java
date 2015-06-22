/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
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
package it.uniud.ailab.dcore.utils;

import java.util.ArrayList;
import java.util.List;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;

/**
 * Utilies for the {@link it.uniud.ailab.dcore.persistence.DocumentComponent} 
 * interface and its composite pattern classes 
 * {@link it.uniud.ailab.dcore.persistence.DocumentComposite} and
 * {@link it.uniud.ailab.dcore.persistence.Sentence}, used to perform common
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
