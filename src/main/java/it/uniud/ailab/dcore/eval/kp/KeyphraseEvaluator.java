/*
 *  Copyright (C) 2015 Artificial Intelligence
 *  Laboratory @ University of Udine.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package it.uniud.ailab.dcore.eval.kp;

import opennlp.tools.stemmer.*;
import it.uniud.ailab.dcore.DistilledOutput;
import it.uniud.ailab.dcore.Distiller;
import it.uniud.ailab.dcore.eval.Evaluator;
import java.util.Map;

/**
 * Abstract keyphrase extraction evaluator object.
 *
 * @author Marco Basaldella
 */
public abstract class KeyphraseEvaluator extends Evaluator {

    /**
     * The input documents.
     */
    private Map<String, String> inputDocuments;
    /**
     * The keyphrases for the input documents.
     */
    private Map<String, String[]> goldKeyphrases;

    /**
     * An abstract evaluator for the Keyphrase Extraction task.
     *
     * @param goldStandardPath the directory of the gold standard.
     */
    public KeyphraseEvaluator(String goldStandardPath) {
        super(goldStandardPath);
    }

    /**
     * Loads the input documents and returns them.
     *
     * @return the input documents.
     */
    public abstract Map<String, String> loadInputDocuments();

    /**
     * Loads the gold standard keyphrases and returns them.
     *
     * @return the gold standard keyphrases.
     */
    public abstract Map<String, String[]> loadGoldKeyphrases();

    /**
     * Evaluate the keyphrases using the given dataset and settings.
     *
     * @param pipeline the distiller instance to evaluate.
     * @return the metrics and their score.
     */
    @Override
    public Map<String, Double> evaluate(Distiller pipeline) {

        inputDocuments = loadInputDocuments();
        goldKeyphrases = loadGoldKeyphrases();
        int docIndex = 0;
        double precision = 0;
        double recall = 0;
        double fmeasure = 0;

        for (Map.Entry<String, String> documentEntry : inputDocuments.entrySet()) {

            String document = documentEntry.getValue().replace("\\n", " ");

            System.out.println("Evaluating document " + ++docIndex
                    + " of " + inputDocuments.size() + "...");

            System.out.println("Document identifier: " + documentEntry.getKey());
            System.out.println("Document's first 40 chars: "
                    + document.substring(0, 40) + "...");

            DistilledOutput output = pipeline.distill(document);

            String[] kps = new String[15];
            for (int i = 0; i < kps.length; i++) {
                kps[i] = output.getGrams()[i].getSurface();
            }

            PorterStemmer stemmer = new PorterStemmer();
            for (int i = 0; i < kps.length; i++) {
                String[] tokens = kps[i].split(" ");
                for (int j = 0; j < tokens.length; j++) {
                    tokens[j] = stemmer.stem(tokens[j]);
                }
                kps[i] = String.join(" ", tokens);
            }

            double docPrecision = computePrecision(
                    kps, goldKeyphrases.get(documentEntry.getKey()));
            double docRecall = computeRecall(
                    kps, goldKeyphrases.get(documentEntry.getKey()));
            double docFMeasure = computeFMeasure(docPrecision, docRecall);

            System.out.println("Precision   : " + docPrecision);
            System.out.println("Recall      : " + docRecall);
            System.out.println("FMeasure    : " + docFMeasure);

            precision = precision + docPrecision;
            recall = recall + docRecall;
            fmeasure = fmeasure + docFMeasure;
        }

        precision = precision / inputDocuments.size();
        recall = recall / inputDocuments.size();
        fmeasure = fmeasure / inputDocuments.size();

        System.out.println();
        System.out.println("*** EVALUATION COMPLETE ***");
        System.out.println();
        System.out.println("Precision   : " + precision);
        System.out.println("Recall      : " + recall);
        System.out.println("F-Measure   : " + fmeasure);
        System.out.println();

        return null;
    }

    private double computePrecision(String[] kps, String[] goldKeyphrase) {
        double matches = 0;
        for (int i = 0; i < kps.length; i++) {
            for (int j = 0; j < goldKeyphrase.length; j++) {
                if (kps[i].toLowerCase().equals(goldKeyphrase[j])) {
                    matches++;
                }
            }
        }

        return matches / (kps.length * 1.0);
    }

    private double computeRecall(String[] kps, String[] goldKeyphrase) {
        double matches = 0;
        for (int i = 0; i < kps.length; i++) {
            for (int j = 0; j < goldKeyphrase.length; j++) {
                if (kps[i].toLowerCase().equals(goldKeyphrase[j])) {
                    matches++;
                }
            }
        }

        return matches / (goldKeyphrase.length * 1.0);
    }

    private double computeFMeasure(double precision, double recall) {

        return recall > 0 && precision > 0
                ? (2 * precision * recall) / (precision + recall)
                : 0;
    }

}
