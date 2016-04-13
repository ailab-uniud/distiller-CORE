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
import it.uniud.ailab.dcore.utils.ListUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * A generic n-gram, a simple list of n words.
 * 
 * @author Marco Basaldella
 * @author Giorgia Chiaradia
 */
//@JsonIgnoreProperties({"surfaces,tokenLists,appaerances"})
public abstract class Gram extends Annotable {
    
    /**
     * The type of n-gram:it can be a concept, a keyphrase, a mention...
     */
    private final String type;
    
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
     * The identifier for a GRAM object.
     */
    public static final String GRAM = "GRAM";
    
    /**
     * Instantiates an n-gram. Usually, the surface should be simply the the 
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
     * @param type the type of gram that will be generated.
     */
    public Gram(String identifier, List<Token> sequence, String surface, 
            String type) {
        
        super(identifier);
        
        this.type = type;
        
        tokenLists = new ArrayList<>();
        tokenLists.add(sequence);
        
        surfaces = new ArrayList<>();
        surfaces.add(surface);
    }
    
    /**
     * Adds a surface to the n-gram. Duplicates are permitted.
     * 
     * @param surface the surface to add
     * @param tokens the tokens that form the surface
     */
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
     * Get the type of the Gram that depends on the type of Gram implementation.
     * 
     * @return the type of gram. 
     */
    public String getType(){
        return type;
    }

    /**
     * The tokens that form the most common surface of the gram.
     *
     * @return the tokens of the surface of the gram.
     */
    public List<Token> getTokens() {
        return tokenLists.get(surfaces.indexOf(ListUtils.mostCommon(surfaces)));
    }
    
    /**
     * Returns all the possible lists of tokens that form the gram.
     * 
     * @return all the possible lists of tokens that form the gram.
     */
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
    public String getSurface() {
        return ListUtils.mostCommon(surfaces);
    }
    
    /**
     * Returns all the surfaces of the gram. Note: may contain 
     * duplicates.
     * 
     * @return all the surfaces of the gram.
     */
    @JsonIgnore
    public List<String> getSurfaces() {
        return surfaces;
    }
    
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
    @JsonIgnore
    public List<DocumentComponent> getAppaerances() {
        return appareances;
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
    
}