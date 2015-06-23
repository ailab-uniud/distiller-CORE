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

import it.uniud.ailab.dcore.annotation.Annotation;
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.*;

/**
 * The Gram is the data structure in which all the data concerning a NGram is
 * stored.
 *
 * @author Dario De Nart
 * @author Marco Basaldella
 */
public class Gram {

    /**
     * The string representation of the gram.
     */
    private final String surface;
    /**
     * The stemmed (or lemmatized) surface.
     */
    private String identifier;
    /**
     * The words forming the surface of the gram.
     */
    private ArrayList<Token> words;
    /**
     * The concept Units in which the gram appears.
     */
    private List<DocumentComponent> appareances;
    /**
     * Annotations over the gram.
     */
    private List<Annotation> annotations;

    /**
     * The gram
     * {@link it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation}
     * container. This special structure is needed for faster getting of
     * features, since they're the main way for evaluators to calcolate the
     * importance of a gram in a document.
     *
     * Note that one may add FeatureAnnotations in the annotations list, but
     * this way is not guaranteed that every evaluator will read them.
     */
    private FeatureContainer features;

    /**
     * A n-gram, which is composed by a list of sequences, and has a list of
     * features.
     *
     * @param sequence
     */
    public Gram(List<Token> sequence, String surface) {
        words = new ArrayList<>();
        annotations = new ArrayList<>();
        words.addAll(sequence);
        this.surface = surface;
        identifier = "";
        //features = new HashMap<String, Double>();
        features = new FeatureContainer();
        appareances = new ArrayList<>();
    }

    /**
     * A string that identifies the gram.
     *
     * @return a string that identifies the gram.
     */
    public String getSurface() {
        return this.surface;
    }

    /**
     * The signature of the gram is the stemmed or lemmatized version of the
     * surface. This way. grams with the same surface may have the same
     * signature.
     *
     * @return the signature of the gram.
     */
    public String getSignature() {
        // lazily generate the identifier
        if (identifier.isEmpty()) {
            identifier = identifier + words.get(0).getText();
            for (int i = 1; i < words.size(); i++) {
                identifier = identifier + " " + words.get(i).getStem();
            }
        }

        return this.identifier.toLowerCase();
    }

    /**
     * The tokens of the identifier of the gram.
     *
     * @return the tokens of the identifier of the gram.
     */
    public List<Token> getTokens() {
        return words;
    }

    // <editor-fold desc="Feature and annotation Management">
    /**
     * Get the annotation produced by the annotator identified by the input
     * string.
     *
     * @param annotator the annotator identifier
     * @return an annotation
     */
    public Annotation getAnnotation(String annotator) {
        for (Annotation a : annotations) {
            if (a.getAnnotator().equals(annotator)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Adds an annotation to the gram.
     *
     * @param annotation the annotation to add
     */
    public void addAnnotation(Annotation annotation) {
        annotations.add(annotation);
    }

    /**
     * Adds a feature to the gram.
     *
     * @param feature the identifier of the feature
     * @param value the value of the feature
     */
    public void putFeature(String feature, double value) {
        features.put(feature, value);
    }

    /**
     * Adds a feature to the gram,
     *
     * @param f the feature to add.
     */
    public void putFeature(FeatureAnnotation f) {
        features.put(f);
    }

    /**
     * Check if the gram has been annotated by the annotator specified via input
     * string.
     *
     * @param featureName the name of the feature to search
     * @return true if the gram has the feature; false otherwise
     */
    public boolean hasFeature(String featureName) {
        return features.get(featureName) != null;
    }

    /**
     * Gets the feature generated by the annotator specified via input string.
     *
     * Please note that this method makes no difference between a feature that
     * has been assigned with value 0 and a feature that has not been assigned
     * to the gram, since in both cases the value 0 will be returned.
     *
     * @param featureName the name of the feature to search
     * @return the value of the feature. Returns 0 if the feature is not in the
     * gram.
     */
    public double getFeature(String featureName) {
        // null check; if the feature is not specified, we assume it's 0.
        if (features.get(featureName) == null) {
            return 0;
        }
        return features.get(featureName).getValue();
    }

    /**
     * Returns all the features associated with the gram.
     *
     * @return all the features associated with the gram.
     */
    public FeatureAnnotation[] getFeatures() {
        return features.getAll();
    }

    /**
     * Sets the features of the gram, deleting the previous ones (if any).
     *
     * @param features the new features of the gram.
     */
    public void setFeatures(FeatureAnnotation[] features) {
        this.features.putAll(features);
    }
    // </editor-fold> 

    // <editor-fold desc="Location Management">      
    /**
     * Adds an appearance of the gram; in other words, adds the component in
     * which the gram appears to the list of the appearances.
     *
     * @param component the component in which the gram appears
     */
    public void addAppaerance(DocumentComponent component) {
        appareances.add(component);
    }

    /**
     * Gets all the components in which the gram appears.
     *
     * @return all the components in which the gram appears.
     */
    public List<DocumentComponent> getAppaerances() {
        return appareances;
    }
    // </editor-fold>

    /**
     * For optimization sake, FeatureAnnotations are unboxed and put in a
     * HashMap, which is much more convenient in storing and expecially
     * retrieving key-value pairs.
     * 
     * The code of this class is not commented since it's self-explanatory.
     *
     * @author Marco Basaldella
     */
    private class FeatureContainer {

        private HashMap<String, Double> container;

        public FeatureContainer() {
            container = new HashMap<>();
        }

        public void put(FeatureAnnotation f) {
            this.put(f.getAnnotator(), f.getValue());
        }

        public void put(String name, double value) {
            container.put(name, value);
        }

        public FeatureAnnotation get(String key) {
            Double d = container.get(key);
            return d == null ? null : new FeatureAnnotation(key, d);
        }

        public void putAll(FeatureAnnotation[] features) {
            for (FeatureAnnotation f : features) {
                this.put(f);
            }
        }

        public FeatureAnnotation[] getAll() {
            FeatureAnnotation[] features = new FeatureAnnotation[container.size()];
            int i = 0;

            for (Entry<String, Double> feature : container.entrySet()) {
                features[i++] = new FeatureAnnotation(feature.getKey(), feature.getValue());
            }

            return features;
        }
    }
}
