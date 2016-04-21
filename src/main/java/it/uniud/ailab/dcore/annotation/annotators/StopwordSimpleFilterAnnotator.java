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
package it.uniud.ailab.dcore.annotation.annotators;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.json.simple.parser.ParseException;

/**
 * Removes n-grams which surface is equal to a stopword or which first token
 * surface is equal with a stopword.
 *
 * @author Marco Basaldella
 */
public class StopwordSimpleFilterAnnotator implements Annotator {

    /**
     * The languages that the n-gram generator will process and their POS
     * pattern database paths.
     */
    private Map<Locale, String> stopwordsPath;

    private Set<String> stopwords;

    /**
     * A stopword filter annotator, that removes Grams from the blackboard that
     * start with forbidden words.
     */
    public StopwordSimpleFilterAnnotator() {
        stopwordsPath = new HashMap<>();
        stopwords = new HashSet<>();

        stopwordsPath.put(Locale.ENGLISH,
                getClass().getClassLoader().
                getResource("ailab/stopwords/en-tartarus-improved.txt").getFile());
        stopwordsPath.put(Locale.ITALIAN,
                getClass().getClassLoader().
                getResource("ailab/stopwords/generic.txt").getFile());
    }

    /**
     * Sets the database paths of the stopword.
     *
     * @param stopwordsPath the database paths
     */
    public void setStopwordsPath(Map<Locale, String> stopwordsPath) {
        this.stopwordsPath = stopwordsPath;
    }

    /**
     * Adds to the database paths of the POS patterns a file path for a
     * specified language..
     *
     * @param locale the language of the new path
     * @param path the path of the POS pattern file for the language
     */
    public void addStopwordsPath(Locale locale, String path) {
        stopwordsPath.put(locale, path);
    }

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        // load the database, then
        // TODO: handle exceptions better
        try {
            loadDatabase(component.getLanguage());
        } catch (IOException | ParseException ex) {
            Logger.getLogger(SimpleNGramGeneratorAnnotator.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        for (Gram g : blackboard.getKeyphrases()) {
            Keyphrase k = (Keyphrase) g;
            if (stopwords.contains(k.getSurface().toLowerCase())
                    || stopwords.contains(k.getTokens().get(0).getText().toLowerCase())) {
                blackboard.removeKeyphrase(k);
            }
        }

    }

    /**
     * Loads the stopword database according to the path and language specified
     * in the constructor.
     *
     * @param lang the language to search in the database
     * @throws IOException if the database file is nonexistent or non accessible
     * @throws ParseException if the database file is malformed
     * @throws NullPointerException if the language requested is not in the
     * database
     */
    private void loadDatabase(Locale lang) throws IOException, ParseException {
        // Get the POS pattern file and parse it.

        InputStreamReader is;

        // running from command-line and loading inside the JAR
        if (stopwordsPath.get(lang).contains("!")) {
            is = new InputStreamReader(
                    getClass().getResourceAsStream(
                            stopwordsPath.get(lang).substring(
                                    stopwordsPath.get(lang).lastIndexOf("!") + 1)),
                    StandardCharsets.UTF_8);
        } else {
            // normal operation
            is = new FileReader(stopwordsPath.get(lang));
        }

        // If the language is not supported by the database, stop the execution.
        if (is == null) {
            throw new NullPointerException("Language " + lang.getLanguage()
                    + " not available.");
        }

        List<String> doc
                = new BufferedReader(is).lines().collect(Collectors.toList());

        for (String line : doc) {
            // don't process comments
            if (!line.startsWith("##")) {
                String word = line.trim();
                if (!word.isEmpty()) {
                    stopwords.add(word);
                }
            }
        }

    }

}
