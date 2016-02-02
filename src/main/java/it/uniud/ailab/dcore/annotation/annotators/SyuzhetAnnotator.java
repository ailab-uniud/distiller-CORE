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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.AnnotationException;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;

/**
 * Annotates the emotional intensity of grams; currently only the English
 * language is supported.
 *
 * @author Marco Basaldella
 * @see
 * <a href="http://www2.imm.dtu.dk/pubdb/views/publication_details.php?id=6010">The
 * AFINN Dataset page</a>
 * @see <a href="https://www.cs.uic.edu/~liub/FBS/sentiment-analysis.html">Liu
 * Bing's Sentiment Analysis works</a>
 */
public class SyuzhetAnnotator implements Annotator {

    public static final String INTENSITY_AVG = "Intensity_SentengeAverage";
    public static final String INTENSITY_SUM = "Intensity_SentenceSum";
    private static final String INTENSITY_COUNTER = "Intensity_Counter";

    public static final String POLARITY = "Polarity";
    private static final String POLARITY_COUNTER = "Polarity_Counter";

    private enum Dataset {

        BING,
        AFINN;
    }

    private Dataset dataset = Dataset.AFINN;

    private Map<String, Integer> weights = new HashMap<>();

    /**
     * Loads the word valence database created by Finn Årup Nielsen.
     */
    private void loadDefinitions() {

        if (dataset == Dataset.AFINN) {
            loadAfinn();
        } else {
            loadBing();
        }
    }

    /**
     * Loads Finn Årup Nielsen polarity database.
     */
    private void loadAfinn() {

        String csvFile = getClass().getClassLoader().getResource("afinn/afinn.txt").getFile();
        BufferedReader br = null;
        String line;
        String cvsSplitBy = "\t";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] parsedLine = line.split(cvsSplitBy);
                weights.put(parsedLine[0], Integer.parseInt(parsedLine[1]));

            }

        } catch (FileNotFoundException e) {
            throw new AnnotationException(this, "Can't read the AFINN database.", e);
        } catch (IOException e) {
            throw new AnnotationException(this, "Can't read the AFINN database.", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // don't care
                }
            }
        }
    }

    /**
     * Loads Liu Bing's polarity database.
     */
    private void loadBing() {
        String positiveFile = getClass().getClassLoader().getResource("bingliu/positive-words.txt").getFile();
        BufferedReader br = null;
        String line;

        try {

            br = new BufferedReader(new FileReader(positiveFile));
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(";")) {
                    weights.put(line, 1);
                }

            }

        } catch (FileNotFoundException e) {
            throw new AnnotationException(this, "Can't read Bing's positive words database.", e);
        } catch (IOException e) {
            throw new AnnotationException(this, "Can't read Bing's positive words database.", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // don't care
                }
            }
        }

        String negativeFile = getClass().getClassLoader()
                .getResource("bingliu/negative-words.txt").getFile();

        try {

            br = new BufferedReader(new FileReader(negativeFile));
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(";")) {
                    weights.put(line, -1);
                }

            }

        } catch (FileNotFoundException e) {
            throw new AnnotationException(this,
                    "Can't read Bing's negative words database.", e);
        } catch (IOException e) {
            throw new AnnotationException(this,
                    "Can't read Bing's negative words database.", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // don't care
                }
            }
        }
    }

    /**
     * Annotates the n-grams with their document intensity. The gram document
     * intensity is calculated as the mean intensity of the phrases where the
     * gram appears, where the intensity of a sentence is the sum of the
     * intensity of all its words.
     *
     * @param component
     */
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        // skip non-english components
        if (!component.getLanguage().getLanguage().equals("en")) {
            return;
        }

        loadDefinitions();

        List<Sentence> sentences = DocumentUtils.getSentences(component);

        for (Sentence s : sentences) {

            if (s.getGrams().isEmpty()) {
                continue;
            }

            double intensity = 0;
            int markedWords = 0;
            for (Token t : s.getTokens()) {
                if (weights.containsKey(t.getText())) {
                    intensity += weights.get(t.getText());
                    markedWords++;
                }
            }

            // set the polarity of the sentence to +1 if the overall
            // sentiment is positive, to -1
            // otherwise
            int polarity = intensity > 0 ? 1
                    : intensity < 0 ? -1
                            : 0;

            if (dataset == Dataset.BING) {

                for (Gram gram : s.getGrams()) {
                    Keyphrase k = (Keyphrase) gram;
                    if (!k.hasFeature(POLARITY_COUNTER)) {
                        k.putFeature(POLARITY, polarity);
                        k.putFeature(POLARITY_COUNTER, 1);
                    } else {
                        double counter = k.getFeature(POLARITY_COUNTER);
                        double avg_prev = k.getFeature(POLARITY);

                        double avg = avg_prev
                                + ((polarity - avg_prev)
                                / ++counter);

                        k.putFeature(POLARITY, avg);
                        k.putFeature(POLARITY_COUNTER, counter);
                    }
                }
            } else { // (dataset == Dataset.AFINN)

                for (Gram gram : s.getGrams()) {
                    Keyphrase k = (Keyphrase) gram;
                    if (!k.hasFeature(POLARITY_COUNTER)) {
                        k.putFeature(POLARITY, polarity);
                        k.putFeature(POLARITY_COUNTER, 1);
                    } else {
                        double counter = k.getFeature(POLARITY_COUNTER);
                        double avg_prev = k.getFeature(POLARITY);

                        double avg = avg_prev
                                + ((polarity - avg_prev)
                                / ++counter);

                        k.putFeature(POLARITY, avg);
                        k.putFeature(POLARITY_COUNTER, counter);
                    }

                    double sentenceAverageIntensity
                            = markedWords > 0
                                    ? intensity / (markedWords * 5)
                                    : 0;

                    if (!k.hasFeature(INTENSITY_COUNTER)) {
                        k.putFeature(INTENSITY_AVG, sentenceAverageIntensity);
                        k.putFeature(INTENSITY_SUM, sentenceAverageIntensity);
                        k.putFeature(INTENSITY_COUNTER, 1);
                    } else {
                        double counter = k.getFeature(INTENSITY_COUNTER);
                        double avg_prev_ia = k.getFeature(INTENSITY_AVG);

                        ++counter;
                        
                        double avg_ia = avg_prev_ia
                                + ((sentenceAverageIntensity - avg_prev_ia)
                                / counter);

                        double avg_prev_is = k.getFeature(INTENSITY_SUM);

                        double avg_is = avg_prev_is
                                + ((intensity - avg_prev_is)
                                / counter);

                        k.putFeature(INTENSITY_AVG, avg_ia);
                        k.putFeature(INTENSITY_SUM, avg_is);
                        k.putFeature(INTENSITY_COUNTER, counter);
                    }
                }
            }
        }
    }

}
