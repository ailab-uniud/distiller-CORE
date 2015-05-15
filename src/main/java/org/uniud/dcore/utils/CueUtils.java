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

import cue.lang.SentenceIterator;
import cue.lang.WordIterator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Wraps the cue.language library by Jonathan Feinberg to provide sentence
 * splitting and tokenization utilities.
 *
 * @author Marco Basaldella
 * @author Dante Degl'Innocenti
 * @see https://github.com/vcl-xx/cue.language
 */
public class CueUtils {

    /**
     * Splits a text in sentences using the cue.language library.
     * 
     * @param text the text to split
     * @param lang the language of the text
     * @return the text, splitted in sentences.
     */
    public static List<String> splitSentence(String text, Locale lang) {
        List<String> sentences = new LinkedList<>();

        for (final String s : new SentenceIterator(text, lang)) {
            sentences.add(s);
        }

        return sentences;
    }

    /**
     * Tokenizes a sentence using the cue.language library. For the Italian
     * language, another tokenizer step is performed by splitting tokens 
     * with apostrophes.
     * 
     * @param text the text to split
     * @param lang the language of the text
     * @return the tokenized input text
     */
    public static List<String> tokenizeSentence(String text, Locale lang) {
        List<String> tokens = new LinkedList<>();

        for (final String word : new WordIterator(text)) {
            tokens.add(word);
        }

        // a fix for Italian, since the tokenization with CUE doesnt' work
        // well on apostrophes.
        if (lang.getLanguage().equals("it")) {
            List<String> fixedTokens = new LinkedList<>();

            for (String word : tokens) {
                String[] all = word.split("'|’");
                fixedTokens.addAll(Arrays.asList(all));
            }
            
            tokens = fixedTokens;
        }

        return tokens;
    }

}
