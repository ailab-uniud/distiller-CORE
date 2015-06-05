/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 * 
 * 	This file is part of the Distiller-CORE library.
 * 
 * 	Distiller-CORE is free software; you can redistribute it and/or
 * 	modify it under the terms of the GNU Lesser General Public
 * 	License as published by the Free Software Foundation; either
 * 	version 2.1 of the License, or (at your option) any later version.
 *
 * 	Distiller-CORE is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * 	Lesser General Public License for more details.
 *
 * 	You should have received a copy of the GNU Lesser General Public
 * 	License along with this library; if not, write to the Free Software
 * 	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * 	MA 02110-1301  USA or see <http://www.gnu.org/licenses/>.
 */
package it.uniud.ailab.dcore.persistence;

import it.uniud.ailab.dcore.annotation.FeatureAnnotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.*;
import java.util.logging.Logger;

/**
 * The Gram is the data structure in which all the data concerning a NGram
 * is stored. 
 * 
 * @author Dario De Nart
 * @author Marco Basaldella
 */
public class Gram {
    
    private String identifier; // in a nutshell the stemmed NGRAM
    private String surface; // the NGRAM as it appears in the text and as it will be printed
    private ArrayList<Token> words; // the words forming the first occurrence of the NGRAM
    private List<DocumentComponent> appareances;  // the concept Units in which the NGRAM appears
    
    //private HashMap<String, Double> features; // all the n-gram's numeric features will be stored here
    private FeatureContainer features;
    
    
    
    /**
     * A n-gram, which is composed by a list of sequences, and has a list of 
     * features.
     * 
     * @param sequence 
     */
    public Gram(List<Token> sequence) {
        words= new ArrayList<>();
        words.addAll(sequence);
        identifier = "";
        //features = new HashMap<String, Double>();
        features = new FeatureContainer();
        appareances = new ArrayList<>();
    }
     
    public String getSignature() {
        // lazily generate the identifier
        if (identifier.isEmpty())
        {
            identifier = identifier + words.get(0).getText();
            for (int i = 1; i < words.size(); i++)
                identifier = identifier + " " + words.get(i).getStem();
        }
        
        return this.identifier.toLowerCase(); 
    }
    
    public List<Token> getTokens() {
        return words;
    }

    // <editor-fold desc="Feature Management">
    public void putFeature(String feature, double value) {
        features.put(feature, value);
    }
    
    public void putFeature(FeatureAnnotation f) {
        features.put(f);
    }
    
    public boolean hasFeature(String featureName) {
        return features.get(featureName) != null;
    }

    public double getFeature(String featureName) {
        // null check; if the feature is not specified, we assume it's 0.
        if (features.get(featureName) == null) {
            return 0;
        }
        return features.get(featureName).getValue();
    }

    public FeatureAnnotation[] getFeatures() {
        return features.getAll();
    }

    public void setFeatures(FeatureAnnotation[] features) {
        this.features.putAll(features);
    }
    // </editor-fold> 
    
    // <editor-fold desc="Location Management">      
    public void addAppaerance(DocumentComponent unit) {
        appareances.add(unit);
    }
    
    public List<DocumentComponent> getAppaerances() {
        return appareances;
    }
    // </editor-fold>
}

/**
 * A tryout to check if it's handier to pass Feature objects or a couple
 * <string,double> instead.
 * 
 * @author Marco Basaldella
 */
class FeatureContainer {
    
    private HashMap<String,Double> container;
    
    public FeatureContainer() {
        container = new HashMap<>();
    }
    
    public void put(FeatureAnnotation f) {
        this.put(f.getAnnotator(), f.getValue());
    }
    
    public void put(String name,double value) {
        container.put(name,value);
    }
    
    public FeatureAnnotation get(String key) {
        Double d = container.get(key);
        return d == null ? null : new FeatureAnnotation(key,d);
    }   
    
    public void putAll(FeatureAnnotation[] features) {
        for (FeatureAnnotation f : features) {
            this.put(f);
        }
    }
    
    public FeatureAnnotation[] getAll() {
        FeatureAnnotation[] features = new FeatureAnnotation[container.size()];
        int i = 0;
        
        for(Entry<String, Double> feature : container.entrySet())
        {
            features[i++] = new FeatureAnnotation(feature.getKey(),feature.getValue());
        }  
        
        return features;
    }
}