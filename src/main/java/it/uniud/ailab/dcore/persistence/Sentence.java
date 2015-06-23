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
package it.uniud.ailab.dcore.persistence;

import it.uniud.ailab.dcore.annotation.annotations.TextAnnotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The Sentence class contains a document's sentence with all its annotations 
 * and is the most basic concept unit of a document. In the Composite pattern, 
 * this is the leaf of the structure.
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public class Sentence extends DocumentComponent {
    
    // <editor-fold desc="Private fields">

    /**
     * The language of the sentence.
     */
    private Locale language;

    /**
     * The {@link it.uniud.ailab.dcore.persistence.Token}s that form the sentence.
     */
    private List<Token> tokenizedSentence;

    /**
     * The {@link it.uniud.ailab.dcore.persistence.Gram}s that have been detected 
     * in the sentence.
     */
    private List<Gram> grams;  
    
    // </editor-fold>
    
    /**
     * Creates a sentence with the specified text and language.
     * 
     * @param text the text of the sentence
     * @param language the language of the sentence
     */    
    public Sentence(String text,Locale language) {
        super(text,language);
        tokenizedSentence = new ArrayList<>();
        grams = new ArrayList<>();        
    }
    
    /**
     * Creates a sentence with the specified text. This requires
     * a call to setLanguage before many of the annotators can actually work.
     * 
     * @param text the text of the sentence.
     */
    public Sentence(String text) {
        this(text,null);
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
     * Get all the annotations associated with the single tokens that compose
     * the sentence.
     * 
     * @return all the annotations of all the tokens.
     */
    @Override
    public List<TextAnnotation> getAnnotations() {
        List<TextAnnotation> ret = new ArrayList<TextAnnotation>();
        for (Token t : getTokens())
        {
            ret.addAll(t.getAnnotations());
        }
        
        return ret;
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

}
