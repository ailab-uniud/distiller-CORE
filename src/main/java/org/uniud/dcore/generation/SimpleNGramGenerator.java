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
package org.uniud.dcore.generation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.uniud.dcore.engine.NGramGenerator;
import org.uniud.dcore.persistence.DocumentComponent;
import org.uniud.dcore.engine.BlackBoard;
import org.uniud.dcore.persistence.Feature;
import org.uniud.dcore.persistence.Gram;
import org.uniud.dcore.persistence.Sentence;
import org.uniud.dcore.persistence.Token;

/**
 * The default n-gram generator algorithm. This class generates relevant n-grams
 * checking, for every group of words, if their POS pattern can be found in a 
 * database of pre-made sequences. For example, "Software Engineering" should be
 * tagged with "Noun Noun", so if the POS pattern "Noun Noun" is in the database
 * the n-gram "Software Engineering" will be selected.
 * 
 * The database is a simple JSON file which is embedded with the project but,
 * if the user wants, is possible to let the algorithm use a custom database
 * by specifying it in the constructor. This may be useful to test other POS
 * patterns or to add support for new languages.
 * 
 * Contextual to the generation, the algorithm assigns the first feature to the
 * n-gram, which is the "Noun Value", i.e. the number of nouns in the gram. 
 * 
 * @author Dario De Nart
 * @author Marco Basaldella
 */
public class SimpleNGramGenerator implements NGramGenerator {
    
    /**
     * The identifier for the annotation produced by the n-gram generator, which
     * counts the number of nouns in a n-gram.
     */
    public static final String NOUNVALUE = "NounValue";

    // <editor-fold desc="private fields">
    /**
     * Path to the local POS Pattern JSON file.
     */
    private String posDatabasePath;

    /**
     * The languages that the n-gram generator will process.
     */
    private List<Locale> languages;
    
    /**
     * The POS patterns found.
     */
    private final HashMap<String, Integer> validPOSPatterns;
    
    /**
     * The maximum size of n-grams to detect.
     */
    private int maxNgramSize;
    // </editor-fold>

    // <editor-fold desc="constructor">
    /**
     * Initializes the nGram generator. Please note that the database is not
     * loaded until the actual extraction is performed.
     * 
     * @param posDatabasePath the path of the JSON file used as database
     */
    public SimpleNGramGenerator(String posDatabasePath) {

        validPOSPatterns = new HashMap<>();
        languages = new ArrayList<>();
        maxNgramSize = 3;
        this.posDatabasePath = posDatabasePath;
    }
    
    /**
     * Initializes the nGram generator with the default POS patterns. 
     */
    public SimpleNGramGenerator() {
        // a neat trick to load the database: instead of doing this.getClass(), 
        // since you can't use 'this' in a constructor call (like this(this.. )), 
        // we call getClass on the BlackBoard instance.
        this((BlackBoard.Instance().getClass().getClassLoader().
                getResource("ailab/posPatterns.json").getFile()));
    }
    // </editor-fold>
    
    // <editor-fold desc="getters and setters">    
    /**
     * Sets the languages that will be used in the n-gram generation pipeline.
     * 
     * @param langs the languages that will be used in the n-gram generation pipeline.
     * @see org.uniud.dcore.engine.NGramGenerator
     */
    @Override
    public void setGramLanguages(List<Locale> langs) {
        this.languages = langs;            
    }

    /**
     * Gets the languages that will be used in the n-gram generation pipeline.
     * 
     * @return the languages that will be used in the n-gram generation pipeline.
     * @see org.uniud.dcore.engine.NGramGenerator
     */
    @Override
    public List<Locale> getGramLanguages() {
        return this.languages;
    }
    // </editor-fold>
    
    // <editor-fold desc="worker methods">
    
    /**
     * Generates the n-grams of a {@link org.uniud.dcore.persistence.DocumentComponent}
     * by using the POS patterns and other annotations produced by the Annotators.
     * A NGram generator should offer support for a set of languages, so the 
     * Engine can decide what generator should use if there are more than one.
     * 
     * @param component the component to analyze.
     */
    @Override
    public void generateNGrams(DocumentComponent component) {

        // load the database, then
        // TODO: handle exceptions better
        try {
            loadDatabase(component.getLanguage());
        } catch (IOException | ParseException ex) {
            Logger.getLogger(SimpleNGramGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        // do the actual nGram generation.
        spotNGrams(component);

    }
    
    /**
     * Performs the actual work, by checking in a document if there are valid
     * nGram sequences that can be used as keyphrase.
     * 
     * @param component the DocumentComponent to analyze.
     */
    private void spotNGrams(DocumentComponent component) {

        // are we a sentence? if yes, spot the nGrams
        if (component.hasComponents()) {
            List<DocumentComponent> children = component.getComponents();

            // if not and we're a section, traverse the document tree recursively
            for (DocumentComponent child : children) {
                spotNGrams(child);
            }

        } else {
            
            Sentence sent = (Sentence) component;
            List<Token> allWords = sent.getTokens();

            // we keep n buffers of the last n words scanned 
            // (where n = maxngramsize ). The first buffer is of size 1, 
            // the second of size 2, ... and so on.
            // then, we compare these buffers with the valid known PoS patterns
            // and save the ngram if it matches.
            
            ArrayList<Token>[] lastReadBuffers = new ArrayList[maxNgramSize];
            for (int size = 0; size < maxNgramSize; size++) {
                lastReadBuffers[size] = new ArrayList<>();
            }
            for (int i = 0; i < allWords.size(); i++) {
                Token word = allWords.get(i);
                for (int size = 0; size < maxNgramSize; size++) {
                    // if the buffer is not full, fill it
                    if (lastReadBuffers[size].size() < size + 1) {
                        lastReadBuffers[size].add(word);
                    } else {
                        // else, cut the head and put the new word at the tail
                        lastReadBuffers[size].remove(0);
                        lastReadBuffers[size].add(word);
                    }
                    if (i >= size) {
                        // if the pattern is in the database AND contains a noun, then
                        // it's a nGram that could be a keyphrase.  
                        int nounValue = checkGramNounValue(lastReadBuffers[size]);
                        if (nounValue > 0) {
                            Gram g = new Gram(lastReadBuffers[size]);
                            g.putFeature(
                                new Feature(NOUNVALUE,nounValue ));
                            BlackBoard.Instance().addGram(component, g);
                        }
                    }
                } // for
            } // for
        } // if 
    }
    
    // </editor-fold>
    
    // <editor-fold desc="support methods">
    /**
     * Checks if a list of POS-tagged tokens contains a n-gram that could be
     * a valid keyphrase and returns their noun value.
     * 
     * @param candidate the list of tokens candidate to be a keyphrase
     * @return the noun value of the n-gram if it's in the database, -1 else.
     */
    private int checkGramNounValue(List<Token> candidate) {
        // building the tagged String by merging the positions of the array
        String taggedString = "";
        for (int i = 0; i < candidate.size(); i++) {
            taggedString += candidate.get(i).getPoS();
            if (i < candidate.size() - 1) {
                taggedString += "/";
            }
        }

        // check if the pattern is contained in the database
        Integer nounValue;
        if (validPOSPatterns.containsKey(taggedString)) {
            nounValue = validPOSPatterns.get(taggedString);
        } else {
            nounValue = -1;
        }
              
        return nounValue;
    }

    /**
     * Loads the nGram database according to the path and language specified in the
     * constructor.
     * 
     * @param lang the language to search in the database
     * @throws IOException if the database file is nonexistent or non accessible
     * @throws ParseException if the database file is malformed
     * @throws NullPointerException if the language requested is not in the database
     */
    private void loadDatabase(Locale lang) throws IOException, ParseException {
        // Get the POS pattern file and parse it.
        BufferedReader reader = new BufferedReader(new FileReader(posDatabasePath));

        Object obj = (new JSONParser()).parse(reader);
        JSONObject fileblock = (JSONObject) obj;
        JSONArray pagesBlock = (JSONArray) fileblock.get("languages");

        // Find the required language in the specified file
        Iterator<JSONObject> iterator = pagesBlock.iterator();
        JSONObject languageBlock = null;
        while (iterator.hasNext()) {
            languageBlock = (iterator.next());
            String currLanguage = (String) languageBlock.get("language");
            if (currLanguage.equals(lang.getLanguage())) {
                break;
            }
        }

        // If the language is not supported by the database, stop the execution.
        if (languageBlock == null) {
            throw new NullPointerException("Language " + lang.getLanguage() + " not found in file " + posDatabasePath);
        }

        JSONArray patternBlock = (JSONArray) languageBlock.get("patterns");
        Iterator<JSONObject> patternIterator = patternBlock.iterator();
        
        // put the patterns in the hashmap
        while (patternIterator.hasNext()) {
            JSONObject pattern = (patternIterator.next());
            String POSpattern = (String) pattern.get("pattern");
            Long nounCount = (Long) pattern.get("nounCount");
            validPOSPatterns.put(POSpattern, nounCount.intValue());
        }
    }
    // </editor-fold>
}
