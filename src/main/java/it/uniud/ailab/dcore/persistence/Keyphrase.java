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
import it.uniud.ailab.dcore.annotation.Annotation;
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.utils.ListUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * The Gram is the data structure in which all the data concerning a NGram is
 * stored.
 *
 * @author Dario De Nart
 * @author Marco Basaldella
 */
//@JsonIgnoreProperties({"surfaces,tokenLists,appaerances,features"})
public class Keyphrase extends Gram {

    /**
     * The different list of words forming the surface of the gram.
     */
    private List<List<Token>> tokenLists;
    /**
     * The different string representation of the gram.
     */
    private final List<String> surfaces;
    /**
     * The concept Units in which the gram appears.
     */
    private List<DocumentComponent> appareances;
    
    /**
     * The identifier for a Keyphrase object.
     */
    public static final String KEYPHRASE = "Keyphrase"; 

    /**
     * Instantiated an n-gram. Usually, the surface should be simply the the 
     * concatenation of the text of the tokens. The signature can be used for 
     * comparison, so be sure to generate different signatures for n-grams
     * that are different in your domain. For example, you can use the sequence 
     * of the stems of the tokens, so that n-grams with the same stemmed form 
     * are considered equal.
     * 
     *
     * @param sequence the tokens that form the gram
     * @param identifier unique identifier of the gram.
     * @param surface the pretty-printed string representation of the gram
     */
    public Keyphrase(String identifier, List<Token> sequence, String surface) {
        
        super(identifier, sequence, surface,KEYPHRASE);
        
        tokenLists = new ArrayList<>();
        tokenLists.add(sequence);
        
        surfaces = new ArrayList<>();
        surfaces.add(surface);
        appareances = new ArrayList<>();
    }
    
    /**
     * Adds a surface to the n-gram. Duplicates are permitted.
     * 
     * @param surface the surface to add
     * @param tokens the tokens that form the surface
     */
    @Override
    public void addSurface(String surface,List<Token> tokens) {
        surfaces.add(surface);
        tokenLists.add(tokens);
    }
    
     /**
     * Adds a group of surfaces to the n-gram. Duplicates are permitted.
     * 
     * @param surfaces the surface to add
     * @param tokenLists the tokens that form the surface
     */
    @Override
    public void addSurfaces(List<String> surfaces,List<List<Token>> tokenLists) {
        
        if (surfaces.size() != tokenLists.size())
            throw new IllegalArgumentException(
                "Mismatching size of surfaces and token lists.");
        
        this.surfaces.addAll(surfaces);
        
        // note: do not use addAll. The references are lost if you don't copy
        for (List<Token> t : tokenLists) {
            this.tokenLists.add(new ArrayList<Token>(t));
        }
    }

    /**
     * The tokens that form the most common surface of the gram.
     *
     * @return the tokens of the surface of the gram.
     */
    @Override
    public List<Token> getTokens() {
        return tokenLists.get(surfaces.indexOf(ListUtils.mostCommon(surfaces)));
    }
    
    /**
     * Returns all the possible lists of tokens that form the gram.
     * 
     * @return all the possible lists of tokens that form the gram.
     */
    @Override
    @JsonIgnore
    public List<List<Token>> getTokenLists() {
        return tokenLists;
    }

    /**
     * The human-readable form of the gram. This is the most common surface
     * between all the surfaces associated with the gram; if there are more than
     * one, the first one that has been added to the gram is selected.
     *
     * @return the human-readable form of the gram.
     */
    @Override
    public String getSurface() {
        return ListUtils.mostCommon(surfaces);
    }
    
    /**
     * Returns all the surfaces of the gram. Note: may contain 
     * duplicates.
     * 
     * @return all the surfaces of the gram.
     */
    @Override
    @JsonIgnore
    public List<String> getSurfaces() {
        return surfaces;
    }

    /**
     * The identifier of the gram. Please note that it is possible that two
     * grams with different surface or tokens may have the same identifier, 
     * based on the policy of the class that generated the gram.
     * 
     * For example, "italian" and "Italy" may have the same identifier, because
     * the identifier has been generated using the same stem "ital". Otherwise,
     * the identifier may be the same link on an external ontology: in this 
     * case, both words may have been associated with the entity "Italy".
     * 
     *
     * @return the signature of the gram.
     */
    @Override
    public String getIdentifier() {
        return super.getIdentifier();
    }

    // <editor-fold desc="Feature and annotation Management">

    /**
     * Adds a feature to the gram.
     *
     * @param feature the identifier of the feature
     * @param value the value of the feature
     */
    public void putFeature(String feature, double value) {        
        addAnnotation(new FeatureAnnotation(feature,value));
        
    }

    /**
     * Adds a feature to the gram,
     *
     * @param f the feature to add.
     */
    public void putFeature(FeatureAnnotation f) {
        addAnnotation(f);
    }

    /**
     * Check if the gram has been annotated by the annotator specified via input
     * string.
     *
     * @param featureName the name of the feature to search
     * @return true if the gram has the feature; false otherwise
     */
    public boolean hasFeature(String featureName) {
        return this.hasAnnotation(featureName);
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
        if (!this.hasAnnotation(featureName)) {
            return 0;
        }
        return ((FeatureAnnotation) getAnnotation(featureName))
                .getScore();
    }

    /**
     * Returns all the features associated with the gram.
     *
     * @return all the features associated with the gram.
     */
    @JsonIgnore
    public FeatureAnnotation[] getFeatures() {
        
        List<FeatureAnnotation> features = new ArrayList<>();
        
        for (Annotation ann : getAnnotations()) {
            if (ann instanceof FeatureAnnotation)
                features.add((FeatureAnnotation)ann);
        }
        
        return features.toArray(new FeatureAnnotation[features.size()]);        
    }

    /**
     * Sets the features of the gram, deleting the previous ones (if any).
     *
     * @param features the new features of the gram.
     */
    public void setFeatures(FeatureAnnotation[] features) {
        for (FeatureAnnotation f : features)
            this.addAnnotation(f);
    }
    // </editor-fold> 

    // <editor-fold desc="Location Management">      
    /**
     * Adds an appearance of the gram; in other words, adds the component in
     * which the gram appears to the list of the appearances.
     *
     * @param component the component in which the gram appears
     */
    @Override
    public void addAppaerance(DocumentComponent component) {
        appareances.add(component);
    }

    /**
     * Gets all the components in which the gram appears.
     *
     * @return all the components in which the gram appears.
     */
    @Override
    public List<DocumentComponent> getAppaerances() {
        return appareances;
    }
    // </editor-fold>
}
