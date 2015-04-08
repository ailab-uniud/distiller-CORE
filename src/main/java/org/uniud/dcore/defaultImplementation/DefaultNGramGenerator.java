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
package org.uniud.dcore.defaultImplementation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Required;
import org.uniud.dcore.engine.NGramGenerator;
import org.uniud.dcore.persistence.ConceptUnit;
import org.uniud.dcore.persistence.DocumentModel;
import org.uniud.dcore.persistence.EndOfTreeException;
import org.uniud.dcore.persistence.Sentence;
import org.uniud.dcore.persistence.Token;

/**
 *
 * @author Dado
 */
public class DefaultNGramGenerator extends NGramGenerator {

    private String posPatternsFilePath;
    private String lang;
    private final HashMap<String, Integer> validPOSPatterns;
    private int maxNgramSize;

    // Constructor
    public DefaultNGramGenerator() throws IOException, ParseException {
        HashMap<String, Integer> buffer = new HashMap<>();
        // let's read the POS Patterns file!
        BufferedReader reader = new BufferedReader(new FileReader(posPatternsFilePath));
        // it's a muddafakkin' JSON!
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(reader);
        JSONObject fileblock = (JSONObject) obj;
        JSONArray pagesBlock = (JSONArray) fileblock.get("languages");
        // let's find the right language
        Iterator<JSONObject> iterator = pagesBlock.iterator();
        while (iterator.hasNext()) {
            JSONObject languageblock = (iterator.next());
            String currLanguage = (String) languageblock.get("language");
            if (currLanguage == null ? lang == null : currLanguage.equals(lang)) {
                // allright, there we are! Now it's time to retrieve 
                // all the meaningful POS patterns from the file
                JSONArray patternBlock = (JSONArray) languageblock.get("patterns");
                Iterator<JSONObject> patternIterator = patternBlock.iterator();
                while (patternIterator.hasNext()) {
                    JSONObject pattern = (patternIterator.next());
                    String POSpattern = (String) pattern.get("pattern");
                    Long nounCount = (Long) pattern.get("nounCount");
                    // time to show the patterns who's the boss and slap 'em into the hashmap!
                    buffer.put(POSpattern, nounCount.intValue());
                }
            }
        }
        validPOSPatterns = buffer;
    }

    /**
     *
     * @param posPatternsFilePath
     */
    @Required
    public void setPosPatternsFilePath(String posPatternsFilePath) {
        this.posPatternsFilePath = posPatternsFilePath;
    }

    @Override
    public void generateNGrams() {
        ConceptUnit document = getDocument();
            // there be awesome
            spotNGrams(document);

    }

    private void spotNGrams(ConceptUnit block) {
        // TRVE RECVRSION OF STEEL
        try {
            for (ConceptUnit cu : block.getSubBlocks()) {
                spotNGrams(cu);
            }
        } catch (EndOfTreeException ex) {

            Sentence sent = (Sentence) block;
            // we have a sentence, let's find the goddamnn NGRAMS inside that bitch
            Token[] allWords = getTokens(sent);

            
            ArrayList<Token>[] buffer = new ArrayList[maxNgramSize];
            //initializing arrayLists
            for(int size = 0; size<maxNgramSize; size++){
                buffer[size] = new ArrayList<>();
            }
            for (int i = 0; i < allWords.length; i++) {
                Token word= allWords[i];
            for(int size = 0; size<maxNgramSize; size++){
                // if the buffer is not full...
                if(buffer[size].size()<size+1){
                    buffer[size].add(word);
                } else{
                    // removing the head of the list
                    buffer[size].remove(0);
                    buffer[size].add(word);
                }
                if (i>= size){
                   Integer nounValue = EvaluatePos(buffer[size]);
                   if (nounValue >0){
                       // NGRAm detected! let's generate the Ngram
                       // TO-DO
                   }
                }
            }
            }
        }
    }
    
        private Integer EvaluatePos(List<Token> candidate) {
            // building the tagged String by merging the positions of the array
            String taggedString ="";
            for(int i = 0; i<candidate.size(); i++){
                taggedString+=candidate.get(i).getAnnotation("POS");
                if(i<candidate.size()-1){
                    taggedString+="/";
                }
            }
            
        // now dead simple
            Integer nounValue;
        if (validPOSPatterns.containsKey(taggedString)) {
            nounValue = validPOSPatterns.get(taggedString);
        } else {
            nounValue = -1;

        }
        return nounValue;
    }

    private ConceptUnit getDocument() {
        return DocumentModel.Instance().getStructure();
    }


    private Token[] getTokens(Sentence sent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
