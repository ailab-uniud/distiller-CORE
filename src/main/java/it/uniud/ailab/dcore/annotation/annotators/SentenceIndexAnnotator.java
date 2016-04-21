/*
 * Copyright (C) 2016 Artificial Intelligence
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
import it.uniud.ailab.dcore.annotation.DefaultAnnotations;
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;

/**
 * Annotates sentences and tokens with their starting and ending indexes in the document
 * string.
 *
 * @author Marco Basaldella
 */
public class SentenceIndexAnnotator implements Annotator {

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        
        String remainingText = blackboard.getText();
        int processedText = 0;

        for (Sentence s : DocumentUtils.getSentences(component)) {
            int startIndex = processedText + remainingText.indexOf(s.getText());
            int endIndex = startIndex + s.getText().length();

            s.addAnnotation(
                    new FeatureAnnotation(
                            DefaultAnnotations.START_INDEX, startIndex));

            s.addAnnotation(
                    new FeatureAnnotation(
                            DefaultAnnotations.END_INDEX, endIndex));

            String sentenceText = s.getText();
            int currentIndex = startIndex;
            for (Token t : s.getTokens()) {
                int tokenPosition = sentenceText.indexOf(t.getText()) + currentIndex;
                int step = t.getText().length();
                sentenceText = sentenceText.substring(step);
                while (!sentenceText.isEmpty() && 
                        Character.isWhitespace(sentenceText.charAt(0))) {
                    step++;
                    sentenceText = sentenceText.substring(1);
                }
                currentIndex += step;

                t.addAnnotation(
                        new FeatureAnnotation(
                                DefaultAnnotations.START_INDEX, tokenPosition));
                t.addAnnotation(
                        new FeatureAnnotation(
                                DefaultAnnotations.END_INDEX, tokenPosition + t.getText().length()));

            } // for (Token t : ... )
            
            processedText = endIndex;
            remainingText = blackboard.getText().substring(processedText);
            
        } // for (Sentence s : ... )

    }

}
