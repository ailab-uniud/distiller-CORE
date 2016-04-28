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

import java.util.List;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.DefaultAnnotations;
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import it.uniud.ailab.dcore.utils.KeyphraseUtils;
import java.util.ArrayList;

/**
 * Annotates grams with statistical information such as their frequency, their
 * width and their depth in the
 * {@link it.uniud.ailab.dcore.persistence.DocumentComponent} passed as input.
 *
 * Document depth is defined as : ( index of sentence of last occurrence / total
 * # of sentences ) Document height is defined as : ( index of sentence of first
 * occurrence / total # of sentences ) Frequency is defined as : total # of
 * occurrences / total # of sentences Life span is defined as : ( index of
 * sentence of last occurrence - index of sentence of first occurrence) / total
 * # of sentences.
 *
 *
 * @author Marco Basaldella
 */
public class StatisticalAnnotator implements Annotator {

    // We use final fields to avoid spelling errors in feature naming.
    // Plus, is more handy to refer to a feature by ClassName.FeatureName, 
    // so that the code is much more readable.
    /**
     * Document depth of a gram, defined as ( index of sentence of last
     * occurrence / total # of sentences ).
     */
    public static final String DEPTH = "Depth";

    /**
     * Document height of a gram, defined as 1 - ( index of sentence of first
     * occurrence / total # of sentences ).
     */
    public static final String HEIGHT = "Height";

    /**
     * Document frequency, defined as the total count of occurrences of the gram
     * in text normalized by the number of sentences. Note: if a gram appears
     * twice in a sentence, is counted once.
     */
    public static final String FREQUENCY_SENTENCE = "Freq_Sentence";

    /**
     * Document frequency, defined as the total count of occurrences of the gram
     * in text.
     */
    public static final String FREQUENCY = "Freq_Absolute";

    /**
     * Life span of a gram, defined as ( index of sentence of last occurrence -
     * index of sentence of first occurrence) / total # of sentences.
     *
     * This can be expressed as (depth - (1 - height)) or equally as depth +
     * height - 1.
     */
    public static final String LIFESPAN = "LifeSpan";

    /**
     * Annotates grams and sentences with statistical information.
     * <p>
     * Grams are annotated with information such as their frequency, their width
     * and their depth in the
     * {@link it.uniud.ailab.dcore.persistence.DocumentComponent} passed as
     * input.
     * <p>
     * Sentences are annotated with their length, expressed both in number of
     * words and number of characters (including whitespaces).
     *
     *
     * @param component the component to analyze.
     */
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        List<Sentence> sentences = DocumentUtils.getSentences(component);

        int size = sentences.size();
        double count = 0;

        // Annotate grams with their statistical features.
        // The implementation is quite straightforward:
        // for the definitions of depth, height and frequency, just
        // see the variable declarations above.
        for (Sentence s : sentences) {
            count++;

            s.addAnnotation(
                    new FeatureAnnotation(
                            DefaultAnnotations.WORD_COUNT,
                            s.getTokens().size()));

            s.addAnnotation(
                    new FeatureAnnotation(
                            DefaultAnnotations.CHAR_COUNT,
                            s.getText().length()));

            //buffer to avoid writing some annotations more than once
            // every sentence
            List<String> surfaces = new ArrayList<>();

            for (Gram g : s.getGrams()) {
                Keyphrase k = (Keyphrase) g;
                if (!k.hasFeature(FREQUENCY)) {
                    k.putFeature(FREQUENCY,
                            KeyphraseUtils.
                                    getTextAppearancesCount(blackboard, k,true));
                }

                if (!surfaces.contains(k.getSurface())) {

                    surfaces.add(k.getSurface());

                    double depth = (count / size);
                    k.putFeature(DEPTH, depth);

                    // check if it's the first appaerance
                    // if not, set the height 1 - depth
                    if (!k.hasFeature(HEIGHT)) {
                        k.putFeature(HEIGHT, 1 - depth);
                    }

                    k.putFeature(LIFESPAN, k.getFeature(DEPTH) + k.getFeature(HEIGHT) - 1);

                    double increment = 1.0 / sentences.size();

                    if (k.hasFeature(FREQUENCY_SENTENCE)) {
                        k.putFeature(FREQUENCY_SENTENCE, k.getFeature(FREQUENCY_SENTENCE) + increment);
                    } else {
                        k.putFeature(FREQUENCY_SENTENCE, increment);
                    }

                }

            }
        }
    }
}
