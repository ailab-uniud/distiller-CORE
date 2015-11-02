/*
 * Copyright (C) 2015 Artificial Intelligence
 * Laboratory @ University of Udine.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package it.uniud.ailab.dcore.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Utilities for querying Wikipedia.
 * 
 * @author Marco Basaldella
 */
public class WikipediaUtils {
    
    /**
     * Generates a Wikipedia page URI from a string.
     * 
     * @param target the page title
     * @param locale the language of the Wikipedia you need (for example, 
     * use Locale.ENGLISH for en.wikipedia)
     * @return the URI that points to that Wikipedia page. Note that the page
     * may not actually exists
     */
    public static URI generateWikiUri(String target, Locale locale) {
        URI uri = null;
        
        try {
            uri = new URI("https://" + locale.getLanguage() + ".wikipedia.org/");
        } catch (URISyntaxException ex) {
            throw new RuntimeException("Absurd failure: cannot create default Wikipedia URI.");
        }
        
        try {
            String urlencoded = URLEncoder.encode(target, "utf-8").replace("+", "%20");
            uri = new URI("https://" + locale.getLanguage() + ".wikipedia.org/wiki/" + urlencoded);
        } catch (Exception e) {
            System.err.println("Error while encoding Wikipedia URL " + uri.toASCIIString());
            
        }
        
        return uri;
    }
    
}
