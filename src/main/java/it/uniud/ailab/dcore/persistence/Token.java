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

import it.uniud.ailab.dcore.annotation.Annotable;
import it.uniud.ailab.dcore.annotation.Annotation;
import it.uniud.ailab.dcore.annotation.annotations.TextAnnotation;

/**
 * The smallest object of the Distiller, which represents a component of a sentence
 * (in most cases, a word).
 */
public class Token extends Annotable {

    /**
     * The stem of the word or, if a lemmatizer is used instead of a stemmer, 
     * the lemma.
     */
    private String stem;
    /**
     * The part-of-speech tag of the word.
     */
    private String PoS;

    /**
     * Creates a token.
     * 
     * @param text the text of the token.
     */
    public Token(String text){
        super(text);
    }
    
    // <editor-fold desc="Id, stem and PoS">
    /**
     * Set the stem of the token; if you don't have a stemmer for a certain
     * language, the lemmatized version of the word should work fine as well.
     * 
     * @param stem the stemmed token
     */
    public void setStem(String stem) {
        this.stem = stem;
    }
    
    /**
     * Set the POS tag of the token.
     * 
     * @param PoS the POS tag.
     */
    public void setPoS(String PoS) {
        this.PoS = PoS;
    }   
    
    /**
     * Returns the text of the token.
     * 
     * @return the text of the token.
     */
    public String getText() {
        return super.getIdentifier();
    }

    /**
     * Returns the stem of the token.
     * 
     * @return the stem of the token.
     */
    public String getStem() {
        return stem;
    }
    
    /**
     * Returns the POS tag of the token.
     * 
     * @return the POS tag of the token.
     */
    public String getPoS() {
        return PoS;
    }    
    // </editor-fold>
    
    
    // <editor-fold desc="Annotations">    
    /**
     * Gets all the annotations associated with the token that have been
     * generated by a specific annotator.
     * 
     * @param annotator the identifier of an annotator.
     * @return the annotations generated by the specified annotator.
     */
//    public List<TextAnnotation> getAnnotations(String annotator) {
//        List<TextAnnotation> ret = new ArrayList<>();
//        for (TextAnnotation ann : this.getAnnotations())
//        {
//            if (ann.getAnnotator().equals(annotator))  {
//                ret.add(ann);
//            }
//        }
//        return ret;
//    }
    
    /**
     * Check if the token has been annotated by a given annotator. Please note
     * that to retrieve all the annotations generated by an annotator you should
     * use getAnnotations() instead.
     * 
     * @param annotator the identifier of an annotator.
     * @return the first annotation in the list generated by the given annotator.
     */
//    public TextAnnotation hasAnnotation(String annotator) {
//        TextAnnotation a = null;
//        for (TextAnnotation b : this.getAnnotations())
//        {
//            if (b.getAnnotator().equals(annotator))  {
//                a = b;
//                break;
//            }
//        }
//        return a;
//    }
    // </editor-fold>
    
    /**
     * A full string representation of the token, which returns not only the text,
     * but also the stem and the annotations of the token.
     * 
     * @return 
     */
    @Override
    public String toString() {
        String ret = getText() + " {(POS:" + getPoS() + ")" ;
        for (Annotation a : getAnnotations()) {
            if (a instanceof TextAnnotation)
                ret = ret + ", (" + a.getAnnotator() + ":" + 
                        ((TextAnnotation) a).getAnnotation() + ")";
        }
        return ret + "}";
    }            

    /**
     * Two tokens are equal if they have the same text, stem and POS tag. 
     * Tokens with different annotation may just refer to same word in different
     * sentences; while the annotations are different, the word is the same.
     * 
     * For example, "Engineering" per se and the word "Engineering" in
     * "Software Engineering" should be treated as equal, even if they may
     * be annotated with different Wikipedia entities.
     * 
     * @param obj the token to compare with
     * @return true if the tokens are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Token other = (Token) obj;
        if (!getText().equals(other.getText())) {
            return false;
        }
        if (!stem.equals(other.stem)) {
            return false;
        }
        return PoS.equals(other.PoS);
    }

    @Override
    public String getIdentifier() {
        return getText();
    }
}
