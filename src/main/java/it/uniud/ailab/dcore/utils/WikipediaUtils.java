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

import java.net.URI;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Utilities for querying Wikipedia.
 * 
 * @author Marco Basaldella
 */
public class WikipediaUtils {
    
    public static URI generateWikiUri(String target, Locale locale) {
        URI uri = null;
        try {
            String urlencoded = URLEncoder.encode(target, "utf-8").replace("+", "%20");
            uri = new URI("https://en.wikipedia.org/wiki/" + urlencoded);
            
            
        } catch (Exception e) {
            System.err.println("Error while encoding Wikipedia URL " + uri.toASCIIString());
        }
        
        return null;
    }
    
}
