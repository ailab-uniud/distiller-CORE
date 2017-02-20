/*
 * Copyright (C) 2016 Artificial Intelligence
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

/**
 * Encapsulates all Arabic string constants which will be used during text processing
 * 
 * @author Muhammad Helmy
 */
public class ArabicConstants {
    /**
     * AL represents the Arabic definite article (Alef Lam in UTF-8) (ال)
     */
    private static String AL = "\u0627\u0644";
    /**
     * stopWords holds the Arabic stopwords separated by spaces
     */
    private static String stopWords;
    static{
        readStopWords();
    }
    /**
     * reads and prepare the stopwords string {@link #stopWords}
     */
    private static void readStopWords() {
        String filePath = ArabicConstants.class.getClassLoader().getResource("ailab.stopwords/ar-KPMinerPlusStopwords.txt").getFile();
        stopWords = ArabicDocProcessing.readDocumentText(filePath);
        stopWords = ArabicDocProcessing.preprocessDoc(stopWords);
        stopWords = " " + stopWords.replace("\r","").replace("\n"," ") + " ";
        stopWords = ArabicDocProcessing.normalizeAlefAndYa(stopWords);
    }

    /**
     * Get {@see #AL}
     * @return the AL
     */
    public static String getAL() {
        return AL;
    }

    /**
     * Get {@see #stopWords}
     * @return the stopWords
     */
    public static String getStopWords() {
        return stopWords;
    }
}