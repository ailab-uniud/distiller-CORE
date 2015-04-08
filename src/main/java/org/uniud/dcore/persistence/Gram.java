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

/**
 * The Gram is the data structure in which all the data concerning a NGram
 * is stored. 
 * 
 * @author Dario De Nart
 * @author Marco Basaldella
 */
public class Gram {

    private String type;  // n-grams could came in different shapes: the default type is "text", which means that
    // the n-gram is part of the text read; another type is "meta", that stands for metadata
    // and means that the words forming the n-gram are read from metadata.
    private String identifier; // in a nutshell the stemmed NGRAM
    private String surface; // the NGRAM as it appears in the text and as it will be printed
    private ArrayList<Token> words; // the words forming the first occurrence of the NGRAM
    private List<ConceptUnit> appareances;  // the concept Units in which the NGRAM appears
    private HashMap<String, Double> features; // all the n-gram's numeric features will be stored here
    
    
    // CONSTRUCTOR and type management
    public Gram(List<Token> sequence) {
        this("text",sequence);
    }

    public Gram(String type, List<Token> sequence) {
        words= new ArrayList<>();
        words.addAll(sequence);
        this.type = type;
        features = new HashMap<String, Double>();
        appareances = new ArrayList<ConceptUnit>();
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

    // Location management   
      
    public void addAppaerance(ConceptUnit unit) {
        appareances.add(unit);
    }
    
    public List<ConceptUnit> getAppaerances() {
        return appareances;
    }

    public String getSignature() {
        // TODO: generate a consistent signature
        return this.toString(); 
    }
  
}
