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

import java.util.ArrayList;
import java.util.List;

/**
 * A mention is a gram identifying a word, said anaphor, and a list of words, 
 * said references, which refer back to anaphor.
 * References or anaphoras could be pronouns, proper names or noun phrases, so 
 * phrases describing the anaphor.
 * 
 * @author Giorgia Chiaradia
 */
public class Mention extends Gram{

    /**
     * The list of n-grams representing the references to the anaphor.
     */
    private final List<Reference> referencesTokenList;
    
    public static final String MENTION = "Mention";
    
    
    public Mention(String identifier, List<Token> sequence, String surface) {
        super(identifier, sequence, surface,MENTION);
        referencesTokenList = new ArrayList<>();
    }
    
    public List<Token> getAnaphorToken(){
        return this.getTokens();
    }
    
    public String getAnaphor(){
        return this.getIdentifier();
    }
    
    public void addReference(String identifier, List<Token> tokens, String type){
        Reference ref = new Reference(identifier, tokens, type);
        referencesTokenList.add(ref);
    }
    
    public List<Reference> getReferences(){
        return referencesTokenList;
    }
    
    public class Reference{
        
        String identifier;
        List<Token> tokens;
        String type;
        
        public Reference(String identier,List<Token> tokens, String type){
            this.identifier = identier;
            this.tokens = tokens;
            this.type = type;
        }
        
        public String getType(){
            return type;
        }
        
        public List<Token> getTokens(){
            return tokens;
        }
        
        public String getIdentifier(){
            return identifier;
        }
    }
}
