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
package it.uniud.ailab.dcore.annotation.annotators;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.ArabicDocProcessing;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import java.util.List;

/**
 * This annotator annotates every token forming sentences with its lemmatized and stemmed forms (i.e lemma and stem).
 * Stemming and lemmatization processes are done by using <a href="http://www.nongnu.org/aramorph/">AraMorph</a> library. 
 * The functions of the library are encapsulated in {@link ArabicDocProcessing#lemmatizeDoc(String) lemmatizeDoc} and {@link ArabicDocProcessing#stemDoc(String) stemDoc} methods 
 * 
 * @author Muhammad Helmy
 */
public class AramorphStemmerAnnotator implements Annotator {

    /**     
     * @param blackboard
     * @param component 
     */
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        //annotate sentences
        List<Sentence> sentences = DocumentUtils.getSentences(component);
        //for every sentence
        for (Sentence sentence : sentences) 
            //for every token
            for (Token t : sentence.getTokens()){
                t.setStem(ArabicDocProcessing.stemDoc(t.getText()));
                t.setLemma(ArabicDocProcessing.lemmatizeDoc(t.getText()));
            }                
    }
}