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

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Dario De Nart
 * @author Marco Basaldella
 */
// the Gram is the data structure in which we will store all the data concerning a n-gram
public class Gram {

    private String type;  // n-grams could came in different shapes: the default type is "text", which means that
    // the n-gram is part of the text read; another type is "meta", that stands for metadata
    // and means that the words forming the n-gram are read from metadata.
    private String ngram; // the real n-gram
    private String ngramStem; // stemmed n-gram, useful for statistical purposes
    private String posTag;    // pretty self-explicatory
    private String hook; // reference entity in external knowledge
    private ArrayList<String> relatedWords;
    private ArrayList<String> hypernyms;
    private HashMap<String, Double> features; // all the n-gram's numeric features will be stored here

    // CONSTRUCTOR and type management
    public Gram() {
        ngram = "VOID";
        type = "text";
        features = new HashMap<String, Double>();
    }

    public Gram(String type) {
        ngram = "VOID";
        this.type = type;
        features = new HashMap<String, Double>();
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    // boolean methods for quick type checking
    public boolean isText() {
        return (type == null ? "text" == null : type.equals("text"));
    }

    public boolean isMeta() {
        return (type == null ? "meta" == null : type.equals("meta"));
    }

    // FEATURES management
    public void putFeature(String feature, double value) {
        features.put(feature, value);
    }

    public double getFeature(String feature) {
        // null check; if the feature is not specified, we assume it's 0.
        if (features.get(feature) == null) {
            return 0;
        }
        return features.get(feature);
    }

    public HashMap getFeatures() {
        return features;
    }

    public void setFeatures(HashMap features) {
        this.features = features;
    }

    // get and set methods to read and write private variables
    public String getPosTag() {
        return posTag;
    }

    public void setPosTag(String posTag) {
        this.posTag = posTag;
    }

//    public double getOccurrencies() {
//        return Occurrencies;
//    }
//
//    public void setOccurrencies(double Occurrencies) {
//        this.Occurrencies = Occurrencies;
//    }
    public String getNgram() {
        return ngram;
    }

    public void setNgram(String ngram) {
        this.ngram = ngram;
        hook = ngram;
    }

    public void setNgramStem(String stem) {
        this.ngramStem = stem;
    }

    public String getNgramStem() {
        return ngramStem;
    }

    @Override
    public String toString() {
        return ngram;
    }

    public ArrayList<String> getHypernyms() {
        return hypernyms;
    }

    public ArrayList<String> getRelatedWords() {
        return relatedWords;
    }

    public void setHypernyms(ArrayList<String> hypernyms) {
        this.hypernyms = hypernyms;
    }

    public void setRelatedWords(ArrayList<String> relatedWords) {
        this.relatedWords = relatedWords;
    }

    public void setHook(String hook) {
        this.hook = hook;
    }

    public String getHook() {
        return hook;
    }
}
