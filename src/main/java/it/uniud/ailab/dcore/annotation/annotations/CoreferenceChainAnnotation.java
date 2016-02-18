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

package it.uniud.ailab.dcore.annotation.annotations;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import it.uniud.ailab.dcore.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Annotation for the entire document of its parsing tree, build with stanford
 * coreNLP library. The annotation consists of a map that contains a chain of 
 * founded coreferences in the document.
 *
 * @author Giorgia Chiaradia
 */
public class CoreferenceChainAnnotation extends Annotation {
    /**
     * Collection of all the reference to the annotation string (anaphor).
     */
    private final Collection<Set<CorefChain.CorefMention>> mentions;
    
    /**
     * Variable for counting the number of references to the the annotation 
     * string which are pronouns.
     */
    private double numberOfReference;
    
    /**
     * A list of all the references which are not pronominal (proper names or 
     * nominal ones instead).
     */
    private List<String> notPronominalMentions;
    
    /**
     * The annotated document by stanford parser. 
     */
    private final edu.stanford.nlp.pipeline.Annotation document;
    
    public CoreferenceChainAnnotation(String annotator,
            Collection<Set<CorefChain.CorefMention>> mentions,
            edu.stanford.nlp.pipeline.Annotation stanfordDocument,
            String annotation) {
        
        super(annotator); //set up a new annotator for coreference
        this.mentions = mentions; //set up the list of mentions
        this.document = stanfordDocument; 
        super.addString(annotation); //set to annotator the anaphor as annotion
        
        setCoreferenceInfos(); //fill all the necessary info for coreference
                                // that are number of reference and list of 
                                // not pronominal mentions
    }
    
    /**
     * Get the anaphor with which the annotation is annotatated
     * 
     * @return the anaphor
     */
    public String getAnnotation() {
        return super.getStringAt(0);
    }
    
    /**
     * Get the number of references for tha annotation string counting only
     * the ones that are pronominal.
     * 
     * @return 
     */
    public double getNumberOfPronominalReferences(){
        return this.numberOfReference;
    }
    
    /**
     * Get the list of all the mentions typed as Proper (by proper names)
     * or as Nominal (by phrases describing the anaphor).
     * 
     * @return 
     */
    public List<String> getNotPronominalMentions(){
        
        return notPronominalMentions;
    }

    /**
     * A method that through iteration over parsing tree, count all the 
     * references which are pronominal and separate the others (proper and 
     * nominal) inserting them in global list.
     * To verify if metions are self referenced it is used the lemmatized form 
     * of anaphor and mentions too.
     */
    private void setCoreferenceInfos() {
        
        numberOfReference = 0;
        notPronominalMentions = new ArrayList<>();
        
        for (Set<CorefChain.CorefMention> chain : mentions){
            for(CorefChain.CorefMention ment : chain){
                String mention = setTokenLemma(ment, document);
                if(!mention.equalsIgnoreCase(getAnnotation())){
                    
                    if(ment.mentionType == Dictionaries.MentionType.PRONOMINAL)
                        numberOfReference++;
                    
                    else
                        notPronominalMentions.add(mention);
                }              
            }
        }
    }
    
    /**
     * A method to get the lemmatized form of the a mention in 
     * the coreference chain.
     * 
     * @param cm the mention in the coreference chain
     * @param document the parsed document annotated by Stanford coreNLP
     * @return the lemmatized form of the mention
     */
    private String setTokenLemma(CorefChain.CorefMention cm, 
            edu.stanford.nlp.pipeline.Annotation document){
        
            String lemmatizeToken = "";
            //get the stemmed form of the references, so the comparison with 
            //grams will be easier
            List<CoreLabel> tks = 
                    document.get(CoreAnnotations.SentencesAnnotation.class)
                            .get(cm.sentNum - 1)
                            .get(CoreAnnotations.TokensAnnotation.class);
                    //list of tokens which compose the anaphor
            
            StringBuilder stringBuiler = new StringBuilder();
            for(int i = cm.startIndex-1; i < cm.endIndex-1; i++){
                stringBuiler.append(
                        tks.get(i).get(CoreAnnotations.LemmaAnnotation.class));
                stringBuiler.append(" ");
            }
            
            lemmatizeToken = stringBuiler.toString().trim();
            return lemmatizeToken;
            
    }
    
}
