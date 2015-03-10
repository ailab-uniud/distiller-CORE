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
 * The Sentence class contains a document's sentence with all its annotations
 * @author Dado
 */
public class Sentence {
    // the sentence
    private String sentence;
    // its tokenized version
    private String[] tokenizedSentence;
    // annotations are string arrays and are packed in this HashMap
    // annotation arrays mus be coherent with tokenizedSentence
    /*
        tokenizedSentence   w1|w2|w3|w4|...
        annotation1         a1|a2|a3|a4|...
        annotation2           |b1|  |  |...
    
    */
    private HashMap<String, String[]> annotations;

    public HashMap<String, String[]> getAnnotations() {
        return annotations;
    }

    public String getSentence() {
        return sentence;
    }

    public String[] getTokenzedSentence() {
        return tokenizedSentence;
    }
    
    //maybe we should put a few constraints in this method to force consistency between
    // the tokenizedSentence and the annotation.
    public void addAnnotation(String label, String[] annotation){
        annotations.put(label, annotation);
    }

    public void setAnnotations(HashMap<String, String[]> annotations) {
        this.annotations = annotations;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public void setTokenzedSentence(String[] tokenzedSentence) {
        this.tokenizedSentence = tokenzedSentence;
    }
    
    // writing the sentence with all annotations
    @Override
    public String toString(){
        String out = "";
        for(int i=0; i< tokenizedSentence.length; i++){
            out+=tokenizedSentence[i];
            // writing all non-void annotations
            for(String label:annotations.keySet()){
                String[] annotation= annotations.get(label);
                if(null!=annotation[i] && !"".equals(annotation[i])){
                    out+="/" + annotation[i];
                }
            }
            //
            if(i!=tokenizedSentence.length-1){
                out+= " ";
            }
        }
        return out;
    }
    
    
}
