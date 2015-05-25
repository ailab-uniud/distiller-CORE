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

import java.util.Locale;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.*;

/**
 * Simple wrapper class for the Snowball Stemmer.
 * 
 * @author Marco Basaldella
 */
public class SnowballStemmerSelector {

    /**
     * Creates a Snowball Stemmer for the provided locale, or returns null
     * if there isn't any.
     * 
     * @param loc a locale
     * @return the stemmer for the provided locale.
     */
    public static SnowballStemmer getStemmerForLanguage(Locale loc) {
        
        switch (loc.getLanguage()) {
            case "da":
                return new danishStemmer();
            case "nl":
                return new dutchStemmer();
            case "en":
                return new englishStemmer();
            case "fi":
                return new finnishStemmer();
            case "fr":
                return new frenchStemmer();
            case "de":
                return new germanStemmer();
            case "hu":
                return new hungarianStemmer();
            case "it":
                return new italianStemmer();
            case "no":
                return new norwegianStemmer();
            case "pt":
                return new portugueseStemmer();
            case "ro":
                return new romanianStemmer();
            case "ru": 
                return new russianStemmer();
            case "es":
                return new spanishStemmer();
            case "sv":
                return new swedishStemmer();
            case "tr":
                return new turkishStemmer();
            default:
                return null;
                
        }
        
    }
    
}
