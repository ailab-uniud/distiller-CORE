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

import java.util.ArrayList;
import java.util.List;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * The n-gram generator algorithm based on regular expression for pos tagging. 
 * This class generates relevant n-grams checking, for every group of words, 
 * if their POS pattern match some regular expressions. For example, "Software Engineering" should be
 * tagged with "Noun Noun", so if the POS pattern "Noun Noun" is in the database
 * the n-gram "Software Engineering" will be selected.
 *
 * The regular expression select specific pos tag sequences:
 * - a first level indentifies all the string composed by nouns, verbs and adjectives;
 * - a second level includes strings such as "quality of service", introducing 
 *  pattern like Noun In Noun
 * - a third level identifies strings containing past participle verbs used as 
 *  adjectives, as 
 *
 * Contextual to the generation, the algorithm assigns the first feature to the
 * n-gram, which is the "Noun Value", which is proportional to the number of
 * nouns in the gram.
 *
 * @author Giorgia Chiaradia
 */
public class RegexNGramGeneratorAnnotator implements GenericNGramGeneratorAnnotator {

    // <editor-fold desc="private fields">
    
    /**
     * The languages that the n-gram generator will process and their POS
     * pattern database paths.
     */
    private Map<Locale, String> posDatabasePaths;
    
    /**
     * The regular expressions used to check if the gram has a valid 
     * pos pattern.
     */
    private List<String> validPosPatterns;
    /**
     * Pattern for first level tagging selection: composition of nouns, adjectives
     * and verbs.
     */
    private String pattern1 = "((JJ|NN|NNP|NNS|NNPS)[/])*(NN|NNP|NNS|NNPS|VB|VBG)";
    
    /**
     * Pattern for secodn level tagging selection: composition of nouns and 
     * prepositions.
     */
    private String pattern2 = "((NN|NNS)[/])(IN[/])(NN|NNS)";
    
    /**
     * Pattern for third level tagging selection: composition of nouns, adjectives
     * and verbs in past participle form.
     */
    private String pattern3 = "((JJ|VBN)[/])*(NN|NNS)";
   
    /**
     * The maximum size of n-grams to detect.
     */
    private int maxGramSize;

    /**
     * The default maximum size of n-grams.
     */
    private static final int DEFAULT_MAX_NGRAM_SIZE = 3;

    // </editor-fold>
    // <editor-fold desc="constructor">
    /**
     * Initializes the nGram generator.
     */
    public RegexNGramGeneratorAnnotator() {

        validPosPatterns = new ArrayList<String>();
        posDatabasePaths = new HashMap<>();
        maxGramSize = DEFAULT_MAX_NGRAM_SIZE;
        posDatabasePaths.put(Locale.ENGLISH,
                getClass().getClassLoader().
                getResource("ailab/posPatterns/en-regex-patterns.json").getFile());
    }
    // </editor-fold>
  
    
    /**
     * Sets the database paths of the POS patterns.
     *
     * @param posDatabasePaths the database paths
     */
    public void setPosDatabasePaths(Map<Locale, String> posDatabasePaths) {
        this.posDatabasePaths = posDatabasePaths;
    }

    /**
     * Adds to the database paths of the POS patterns a file path for a
     * specified language..
     *
     * @param locale the language of the new path
     * @param path the path of the POS pattern file for the language
     */
    public void addPosDatabasePaths(Locale locale, String path) {
        posDatabasePaths.put(locale, path);
    }
    
    /**
     * Sets the maximum size of an n-gram. If the size is not specified, the
     * maximum n-gram size is initialized to DEFAULT_MAX_NGRAM_SIZE.
     *
     * @param maxNgramSize the maximum size of an n-gram
     */
    public void setMaxGramSize(int maxNgramSize) {
        this.maxGramSize = maxNgramSize;
    }

    // <editor-fold desc="worker methods">
    /**
     * Generates the n-grams of a
     * {@link it.uniud.ailab.dcore.persistence.DocumentComponent} by using the
     * POS patterns and other annotations produced by the Annotators. A NGram
     * generator should offer support for a set of languages, so the Engine can
     * decide what generator should use if there are more than one.
     *
     * @param component the component to analyze.
     */
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        try {
            loadDatabase(component.getLanguage());
        } catch (IOException | ParseException ex) {
            Logger.getLogger(SimpleNGramGeneratorAnnotator.class.getName()).log(Level.SEVERE, null, ex);
        }
        spotNGrams(blackboard, component);

    }

    /**
     * Performs the actual work, by checking in a document if there are valid
     * nGram sequences that can be used as keyphrase.
     *
     * @param component the DocumentComponent to analyze.
     */
    /**
     * Performs the actual work, by checking in a document if there are valid
     * nGram sequences that can be used as keyphrase.
     *
     * @param component the DocumentComponent to analyze.
     */
    private void spotNGrams(Blackboard blackboard, DocumentComponent component) {

        // are we a sentence? if yes, spot the nGrams
        if (component.hasComponents()) {
            List<DocumentComponent> children = component.getComponents();

            // if not and we're a section, traverse the document tree recursively
            for (DocumentComponent child : children) {
                spotNGrams(blackboard, child);
            }

        } else {

            String sentenceText = component.getText();
            Sentence sent = (Sentence) component;
            List<Token> allWords = sent.getTokens();

            // build the token start and end substring indexes in the input string
            int startIndexes[] = new int[allWords.size()];
            int endIndexes[] = new int[allWords.size()];

            // track how much text we've already scanned
            int searchWordFrom = 0;

            for (int i = 0; i < allWords.size(); i++) {
                startIndexes[i] = sentenceText.indexOf(
                        allWords.get(i).getText(), searchWordFrom);
                endIndexes[i] = startIndexes[i] + allWords.get(i).getText().length();

                searchWordFrom = endIndexes[i];
            }

            // we keep n buffers of the last n words scanned 
            // (where n = maxngramsize ). The first buffer is of size 1, 
            // the second of size 2, ... and so on.
            // then, we compare these buffers with the valid known PoS patterns
            // and save the ngram if it matches.
            ArrayList<Token>[] lastReadBuffers = new ArrayList[maxGramSize];
            for (int size = 0; size < maxGramSize; size++) {
                lastReadBuffers[size] = new ArrayList<>();
            }

            for (int i = 0; i < allWords.size(); i++) {
                Token word = allWords.get(i);
                for (int size = 0; size < maxGramSize; size++) {
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

                            // the identifier is the stem of the words
                            String identifier = lastReadBuffers[size].get(0).getText();
                            for (int k = 1; k < lastReadBuffers[size].size(); k++) {
                                identifier = identifier + " "
                                        + lastReadBuffers[size].get(k).getStem();
                            }

                            identifier = identifier.toLowerCase();
                            int startIndex = 
                                    startIndexes[i - (lastReadBuffers[size].size() - 1)];
                            int endIndex = endIndexes[i];
                                    
                            
                            Keyphrase g = new Keyphrase(
                                    identifier,
                                    lastReadBuffers[size],
                                    sentenceText.substring(
                                            startIndex,
                                            endIndex));

                            g.putFeature(new FeatureAnnotation(
                                    NOUNVALUE, ((float) nounValue) / (float)g.getTokens().size()));
                            blackboard.addGram(component, g);
                        }
                    }
                } // for
            } // for
        } // if 
    }

    // </editor-fold>
    // <editor-fold desc="support methods">
    /**
     * Checks if a list of POS-tagged tokens contains a n-gram that could be a
     * valid keyphrase and returns their noun value.
     *
     * @param candidate the list of tokens candidate to be a keyphrase
     * @return the noun value of the n-gram if it's a valid pattern, -1 else.
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

        // check if the sequence of pos tag is a valid pattern
        Integer nounValue = -1;
        for(String pattern : validPosPatterns){
            if(taggedString.matches(pattern)){
                //counting the number of noun present in the string
                int index = taggedString.indexOf("NN");
                String posTagString = taggedString;
                nounValue = 0;
                    while (index != -1) {
                        nounValue++;
                        posTagString = posTagString.substring(index + 1);
                        index = posTagString.indexOf("NN");
                    }            
            
                System.out.println(taggedString + " = "+ nounValue);
        
            } else {
                break;
            }
        }
        
        return nounValue;
    }
    
    private void loadDatabase(Locale lang) throws IOException, ParseException {
        // Get the POS pattern file and parse it.

        InputStreamReader is;

        // running from command-line and loading inside the JAR
        if (posDatabasePaths.get(lang).contains("!")) {
            is = new InputStreamReader(
                    getClass().getResourceAsStream(
                    posDatabasePaths.get(lang).substring(
                            posDatabasePaths.get(lang).lastIndexOf("!") + 1)));
        } else {
            // normal operation
            is = new FileReader(posDatabasePaths.get(lang));
        }

        
        BufferedReader reader = new BufferedReader(is);
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
            throw new NullPointerException("Language " + lang.getLanguage()
                    + " not found in file " + posDatabasePaths.get(lang));
        }

        JSONArray patternBlock = (JSONArray) languageBlock.get("patterns");

        Iterator<JSONObject> patternIterator = patternBlock.iterator();

        // put the patterns in the hashmap
        while (patternIterator.hasNext()) {
            JSONObject pattern = (patternIterator.next());
            String POSpattern = (String) pattern.get("pattern");
            validPosPatterns.add(POSpattern);
        }
    }
    // </editor-fold>
}
