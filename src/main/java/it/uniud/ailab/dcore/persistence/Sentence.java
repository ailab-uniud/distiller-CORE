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
package it.uniud.ailab.dcore.persistence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * The Sentence class contains a document's sentence with all its annotations 
 * and is the most basic concept unit of a document. In the Composite pattern, 
 * this is the leaf of the structure.
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 * 
 * Add a global variable which contains the number of phrases (single sentence)
 * a sentence is made up and create its setter and getter methods.
 * 
 * @modify by Giorgia Chiaradia
 */
public class Sentence extends DocumentComponent {
    
    // <editor-fold desc="Private fields">

    /**
     * The {@link it.uniud.ailab.dcore.persistence.Token}s that form the sentence.
     */
    private List<Token> tokenizedSentence;

    /**
     * The {@link it.uniud.ailab.dcore.persistence.Gram}s that have been detected 
     * in the sentence.
     */
    private List<Gram> grams;
    
    /**
     * The number of simple phrases contained in a sentence
     */
    private int phraseNumber;
    
    // </editor-fold>
    
    /**
     * Creates a sentence with the specified text and language.
     * 
     * @param text the text of the sentence
     * @param language the language of the sentence
     * @param identifier the output friendly identifier of the sentence
     */    
    public Sentence(String text,Locale language,String identifier) {
        super(text,language,identifier);
        tokenizedSentence = new ArrayList<>();
        grams = new ArrayList<>(); 
        phraseNumber = 1;
    }
    
    /**
     * Creates a sentence with the specified text. This requires
     * a call to setLanguage before many of the annotators can actually work.
     * 
     * @param text the text of the sentence
     * @param identifier the output friendly identifier of the sentence
     */
    public Sentence(String text,String identifier) {
        this(text,null,identifier);
    }
    
    /**
     * Sets the tokens of the sentence.
     * 
     * @param tokenzedSentence the tokens of the sentence.
     */
    public void setTokens(List<Token> tokenzedSentence) {
        this.tokenizedSentence = tokenzedSentence;
    }
    
    /**
     * Appends a tokens at the end of the token list of the sentence.
     * 
     * @param t the token to add
     */
    public void addToken(Token t) {
        this.tokenizedSentence.add(t);
    }
    
    /**
     * Returns the tokens of the sentence.
     * 
     * @return the tokens of the sentence.
     */
    public List<Token> getTokens() {
        return tokenizedSentence;
    }      
    
    /**
     * Returns an array containing the POS tags of the tokens of the sentence.
     * 
     * @return an array containing the POS tags of the sentence.
     */
    public String[] getPosTaggedSentence() {
        String[] output = new String[tokenizedSentence.size()];
        for (int i = 0; i < tokenizedSentence.size(); i++) {
            output[i] = tokenizedSentence.get(i).getPoS();
        }
        return output;
    }    
    // </editor-fold>
    
    /**
     * Get the text of the sentence.
     * 
     * @return the text of the sentence.
     */
    @Override
    public String toString() {
        return getText();
    }

    /**
     * A sentence has no sub-components in the document model, so a null value
     * is returned.
     * 
     * @return null, because the sentence has no children.
     */
    @Override
    public List<DocumentComponent> getComponents() {
        return null;
    }

    /**
     * Add a n-gram to the sentence.
     * 
     * @param g the gram to add. 
     */
    @Override
    public void addGram(Gram g) {
        grams.add(g);
    }
    
    /**
     * Get all the grams of a sentence.
     * 
     * @return the grams that have been found in the sentence.
     */
    @Override
    public List<Gram> getGrams() {
        return grams;
    }

    /**
     * Removes a gram from the sentence.
     * 
     * @param gramToRemove the gram to remove
     */
    @Override
    public void removeGram(Gram gramToRemove) {

        for (Iterator<Gram> gramIterator = grams.iterator(); 
                gramIterator.hasNext();)
        {
            Gram gram = gramIterator.next();
            
            if (gram.getIdentifier().equals(gramToRemove.getIdentifier())) {
                gramIterator.remove();
            }
                
        }
    }

    /**
     * Set the number of phrase in the sentence.
     * 
     * @param numberOfPhrases 
     */
    public void setPhraseNumber(int numberOfPhrases){
        assert(numberOfPhrases > 0):"a sentence is always made up of almost 1 phrase";
        
        this.phraseNumber = numberOfPhrases;
    }
    
    /**
     * Get the total number of phrases in the sentence:is one when the sentence is simple.
     * 
     * @return the number of phrases which compose the sentence 
     */
    public int getPhraseNumber(){
        return this.phraseNumber;
    }
    
}
