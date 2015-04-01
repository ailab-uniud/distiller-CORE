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

/**
 * The Sentence class contains a document's sentence with all its annotations.
 *
 * @author Dario De Nart
 * @author Marco Basaldella
 */
public class Sentence extends ConceptUnit {

    /**
     * The raw string that represents the sentence.
     */
    private String rawString;

    /**
     * The language of the sentence.
     */
    private String language;

    /**
     * The {@link org.uniud.dcore.persistence.Word}s that form the sentence.
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

    public Token[] getTokenzedSentence() {

        return tokenizedSentence;
    }      
    
    public String[] getPosTaggedSentence() {
        String[] output = new String[tokenizedSentence.length];
        for (int i = 0; i < tokenizedSentence.length; i++) {
            output[i] = tokenizedSentence[i].getAnnotation("POS");
        }
        return output;
    }

    public void setTokenzedSentence(Token[] tokenzedSentence) {
        this.tokenizedSentence = tokenzedSentence;
    }

    /**
     * Returns the language of the sentence, formatted as an IETF language tag.
     *
     * @return the language of the sentence.
     * @see <a href="http://tools.ietf.org/html/rfc5646">RFC5646</a>
     * specification.
     */
    public String GetLanguage() {
        return this.language;
    }

    /**
     * Sets the language of the sentence
     *
     * @param language the language of the sentence, specified with the IETF
     * language tag.
     * @throws IllegalStateException if the language is set more than once.
     * @see <a href="http://tools.ietf.org/html/rfc5646">RFC5646</a>
     * specification.
     */
    public void setLanguage(String language) throws IllegalStateException {
        if (this.language != null && !this.language.isEmpty()) {
            this.language = language;
        } else {
            throw new IllegalStateException(String.format(
                    "Trying to set language %s on sentence which is already set as %s",
                    language, this.language));
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

    @Override
    public ConceptBlock[] getSubBlocks() throws EndOfTreeException {
        throw new EndOfTreeException(); //To change body of generated methods, choose Tools | Templates.
    }

}
