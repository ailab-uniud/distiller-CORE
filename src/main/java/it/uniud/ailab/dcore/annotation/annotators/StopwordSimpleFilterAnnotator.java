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
package it.uniud.ailab.dcore.annotation.annotators;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
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
    
    public StopwordSimpleFilterAnnotator() {
        stopwordsPath = new HashMap<>();
        stopwords = new HashSet<>();
        
        stopwordsPath.put(Locale.ENGLISH,
                getClass().getClassLoader().
                getResource("ailab/stopwords/generic.txt").getFile());
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
        
        for (Gram g : blackboard.getGrams()) {
            if (stopwords.contains(g.getSurface()) ||
                    stopwords.contains(g.getTokens().get(0).getText()))
                blackboard.removeGram(g);
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
                if (!word.isEmpty())
                    stopwords.add(word);
            }
        }

    }

}
