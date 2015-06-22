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
