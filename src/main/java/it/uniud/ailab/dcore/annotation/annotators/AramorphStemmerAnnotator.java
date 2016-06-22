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

import gpl.pierrick.brihaye.aramorph.AraMorph;
import gpl.pierrick.brihaye.aramorph.Solution;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.AnnotationException;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.ArabicDocProcessing;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import java.util.List;
import java.util.Vector;

/**
 * Implementation of Porter's Stemmer algorithm. 
 * This annotator annotates every token forming sentences with its stemming form.
 * Stemming process usually chops off the ends of words in the hope of obtaining
 * the base form of the word. It often includes the removal of derivational 
 * affixes. 
 * You can always annotate with stem also token of blackboard grams, just 
 * iterating on the list of grams selected by gram type.
 * 
 * @author Giorgia Chiaradia
 */
public class AramorphStemmerAnnotator implements Annotator {

    /**
     * Annotate tokens from every sentence with a proper stem based on the 
     * language of the document. 
     * It also annotate the mentions token, so to facilitate comparisons during
     * aanaphora resolution task. 
     * 
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
            for (Token t : sentence.getTokens()) 
                t.setStem(ArabicDocProcessing.lemmatizeDoc(t.getText()));
    }
}