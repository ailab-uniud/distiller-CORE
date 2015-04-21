/*
 *     This file is part of Distiller-CORE.
 * 
 *     Distiller-CORE is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Distiller-CORE is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Distiller-CORE.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.uniud.dcore.persistence;

import java.util.HashMap;
import java.util.Locale;

/**
 * The Sentence class contains a document's sentence with all its annotations 
 * and is the most basic concept unit of a document. In the Composite pattern, 
 * this is the leaf of the structure.
 *
 * @author Dario De Nart
 * @author Marco Basaldella
 */
public class Sentence extends DocumentComponent {
    
    
    
    public Sentence(String rawString) {
        this.rawString = rawString;
    }
    
    public Sentence(String rawString,Locale language) {
        this.rawString = rawString;
        this.language = language;
    }

    /**
     * The raw string that represents the sentence.
     */
    private String rawString;

    /**
     * The language of the sentence.
     */
    private Locale language;

    /**
     * The {@link org.uniud.dcore.persistence.Token}s that form the sentence.
     */
    private Token[] tokenizedSentence;

    // <editor-fold desc="Getters and setters">
    public void setSentence(String sentence) throws IllegalStateException {
        if (this.rawString.isEmpty() || this.rawString == null) {
            this.rawString = sentence;
        } else {
            throw new IllegalStateException("Trying to set the content of the sentence twice.");
        }
    }

    @Override
    public String getRawText() {
        return rawString;
    }

    public Token[] getTokens() {

        return tokenizedSentence;
    }      
    
    public String[] getPosTaggedSentence() {
        String[] output = new String[tokenizedSentence.length];
        for (int i = 0; i < tokenizedSentence.length; i++) {
            output[i] = tokenizedSentence[i].getPoS();
        }
        return output;
    }

    public void setTokens(Token[] tokenzedSentence) {
        this.tokenizedSentence = tokenzedSentence;
    }

    /**
     * Returns the language of the sentence, formatted as an IETF language tag.
     *
     * @return the language of the sentence.
     * @see <a href="http://tools.ietf.org/html/rfc5646">RFC5646</a>
     * specification.
     */
    public Locale getLanguage() {
        return this.language;
    }

    /**
     * Sets the language of the sentence
     *
     * @param language the language of the sentence.
     * @throws IllegalStateException if the language is set more than once.
     */
    public void setLanguage(Locale language) throws IllegalStateException {
        if (this.language != null) {
            this.language = language;
        } else {
            throw new IllegalStateException(String.format(
                    "Trying to set language %s on sentence which is already set as %s",
                    language.toLanguageTag(), this.language));
        }
    }

    // </editor-fold>
    // writing the sentence with all annotations
    @Override
    public String toString() {
        String out = "";
//        for(int i=0; i< tokenizedSentence.length; i++){
//            out+=tokenizedSentence[i];
//            // writing all non-void annotations
//            for(String label:annotations.keySet()){
//                String[] annotation= annotations.get(label);
//                if(null!=annotation[i] && !"".equals(annotation[i])){
//                    out+="/" + annotation[i];
//                }
//            }
//            
//            if(i!=tokenizedSentence.length-1){
//                out+= " ";
//            }
//        }
        return out;
    }

    /**
     * A sentence has no sub-components in the document model, so a null value
     * is returned.
     * 
     * @return null, because the sentence has no children.
     */
    @Override
    public DocumentComposite[] getComponents() {
        return null;
    }

}
