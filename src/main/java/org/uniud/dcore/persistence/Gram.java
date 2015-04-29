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
package org.uniud.dcore.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.*;

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
    
    
    
    // CONSTRUCTOR and type management
    public Gram(List<Token> sequence) {
        words= new ArrayList<>();
        words.addAll(sequence);
        //features = new HashMap<String, Double>();
        features = new FeatureContainer();
        appareances = new ArrayList<>();
    }
     
    public String getSignature() {
        // TODO: generate a consistent signature
        return this.identifier; 
    }

    // <editor-fold desc="Feature Management">
    public void putFeature(String feature, double value) {
        features.put(feature, value);
    }
    
    public void putFeature(Feature f) {
        features.put(f);
    }
    
    public Feature getFeature(String featureName) {
        return features.get(featureName);
    }

    public double getFeatureValue(String featureName) {
        // null check; if the feature is not specified, we assume it's 0.
        if (features.get(featureName) == null) {
            return 0;
        }
        return features.get(featureName).getValue();
    }

    public Feature[] getFeatures() {
        return features.getAll();
    }

    public void setFeatures(Feature[] features) {
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
        container = new HashMap<String,Double>();
    }
    
    public void put(Feature f) {
        this.put(f.getType(), f.getValue());
    }
    
    public void put(String name,double value) {
        container.put(name,value);
    }
    
    public Feature get(String key) {
        Double d = container.get(key);
        return d == null ? null : new Feature(key,d);
    }   
    
    public void putAll(Feature[] features) {
        for (Feature f : features) {
            this.put(f);
        }
    }
    
    public Feature[] getAll() {
        Feature[] features = new Feature[container.size()];
        int i = 0;
        
        for(Entry<String, Double> feature : container.entrySet())
        {
            features[i++] = new Feature(feature.getKey(),feature.getValue());
        }  
        
        return features;
    }
}
