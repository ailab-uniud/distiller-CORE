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

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.uniud.ailab.dcore.annotation.Annotable;
import it.uniud.ailab.dcore.annotation.Annotation;
import it.uniud.ailab.dcore.annotation.DefaultAnnotations;
import it.uniud.ailab.dcore.annotation.annotations.TextAnnotation;

/**
 * The smallest object of the Distiller, which represents a component of a
 * sentence (in most cases, a word). The class offers some wrappers to quickly
 * add part-of-speech tags, stem and lemma as annotation to the tokens. The
 * respective getters and setters are in fact actually a wrapper for adding
 * /getting the information inside/from a
 * {@link it.uniud.ailab.dcore.annotation.annotations.TextAnnotation}.
 */
public class Token extends Annotable {

    /**
     * Creates a token.
     *
     * @param text the text of the token.
     */
    public Token(String text) {
        super(text);
    }

    // <editor-fold desc="Id, stem and PoS">
    /**
     * Set the stem of the token. 
     *
     * @param stem the stemmed token
     */
    public void setStem(String stem) {
        this.addAnnotation(new TextAnnotation(
                DefaultAnnotations.STEM, stem));
    }

    /**
     * Set the POS tag of the token.
     *
     * @param PoS the POS tag.
     */
    public void setPoS(String PoS) {
        this.addAnnotation(new TextAnnotation(
                DefaultAnnotations.POS_TAG, PoS));
    }

    /**
     * Set the lemmatize form of the token.
     *
     * @param lemma the lemma for the token.
     */
    public void setLemma(String lemma) {
        this.addAnnotation(new TextAnnotation(
                DefaultAnnotations.LEMMA, lemma));
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
    @JsonIgnore
    public String getStem() {
        return this.hasAnnotation(DefaultAnnotations.STEM) ? 
                getAnnotation(DefaultAnnotations.STEM).getStringAt(0) :
                "";
    }

    /**
     * Returns the POS tag of the token.
     *
     * @return the POS tag of the token.
     */
    @JsonIgnore
    public String getPoS() {
        return this.hasAnnotation(DefaultAnnotations.POS_TAG) ? 
                getAnnotation(DefaultAnnotations.POS_TAG).getStringAt(0) :
                "";
    }

    /**
     * Returns the lemmatize form of the token.
     *
     * @return the lemma for token.
     */
    @JsonIgnore
    public String getLemma() {
        return this.hasAnnotation(DefaultAnnotations.LEMMA) ? 
                getAnnotation(DefaultAnnotations.LEMMA).getStringAt(0) :
                "";
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
     * @return the first annotation in the list generated by the given
     * annotator.
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
     * A full string representation of the token, which returns not only the
     * text, but also the stem and the annotations of the token.
     *
     * @return
     */
    @Override
    public String toString() {
        String ret = " { text : '" + getText() + "'";
        for (Annotation a : getAnnotations()) {
            if (a instanceof TextAnnotation) {
                ret = ret + ", (" + a.getAnnotator() + ":"
                        + ((TextAnnotation) a).getAnnotation() + ")";
            }
        }
        return ret + "}";
    }

    /**
     * Two tokens are equal if they have the same text, stem and POS tag. Tokens
     * with different annotation may just refer to same word in different
     * sentences; while the annotations are different, the word is the same.
     *
     * For example, "Engineering" per se and the word "Engineering" in "Software
     * Engineering" should be treated as equal, even if they may be annotated
     * with different Wikipedia entities.
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
        if (!this.getStem().equals(other.getStem())) {
            return false;
        }
        return getPoS().equals(other.getPoS());
    }

    @Override
    public String getIdentifier() {
        return getText();
    }

}
