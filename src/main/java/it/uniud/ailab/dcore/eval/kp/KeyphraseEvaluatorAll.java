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
package it.uniud.ailab.dcore.eval.kp;

import it.uniud.ailab.dcore.DistilledOutput;
import it.uniud.ailab.dcore.Distiller;
import it.uniud.ailab.dcore.eval.Evaluator;
import it.uniud.ailab.dcore.eval.GenericDataset;
import java.util.Map;

/**
 * Abstract keyphrase extraction evaluator object.
 *
 * @author Marco Basaldella
 */
public class KeyphraseEvaluatorAll extends Evaluator {

    private static boolean verbose = false;

    /**
     * An evaluator for the Keyphrase Extraction task.
     *
     * @param goldStandard the gold standard to evaluate.
     */
    public KeyphraseEvaluatorAll(GenericDataset goldStandard) {
        super(goldStandard);
    }

    /**
     * Evaluate the keyphrases using the given dataset and settings.
     *
     * @param pipeline the distiller instance to evaluate.
     * @return the metrics and their score.
     */
    @Override
    public Map<String, Double> evaluate(Distiller pipeline) {

        verbose = pipeline.getVerbose();

        if (!goldStandard.isLoaded()) {
            goldStandard.load();
        }

        int docIndex = 0;
        double precision = 0;
        double recall = 0;
        double fmeasure = 0;

        for (Map.Entry<String, String> documentEntry
                : goldStandard.getTestSet().entrySet()) {

            String document = documentEntry.getValue().replace("\\n", " ");

            System.out.println("Evaluating document " + ++docIndex
                    + " of " + goldStandard.getTestSet().size() + "...");

            System.out.println("Document identifier: " + documentEntry.getKey());
            System.out.println("Document's first 40 chars: "
                    + document.substring(0, 40) + "...");

            DistilledOutput output = pipeline.distillForEval(document);

            String[] kps = new String[pipeline.getBlackboard().getKeyphrases().size()];
            for (int i = 0; i < kps.length; i++) {
                kps[i] = pipeline.getBlackboard()
                        .getKeyphrases().get(i).getSurface();
            }

            double docPrecision = computePrecision(
                    kps,
                    goldStandard.getTestAnswers().get(documentEntry.getKey()));
            double docRecall = computeRecall(
                    kps,
                    goldStandard.getTestAnswers().get(documentEntry.getKey()));
            double docFMeasure = computeFMeasure(docPrecision, docRecall);

            System.out.println("Precision   : " + docPrecision);
            System.out.println("Recall      : " + docRecall);
            System.out.println("FMeasure    : " + docFMeasure);

            precision = precision + docPrecision;
            recall = recall + docRecall;
            fmeasure = fmeasure + docFMeasure;
        }

        precision = precision / goldStandard.getTestSet().size();
        recall = recall / goldStandard.getTestSet().size();
        fmeasure = fmeasure / goldStandard.getTestSet().size();

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
                if (goldStandard.compare(
                        kps[i].toLowerCase(), goldKeyphrase[j]) == 0) {
                    matches++;
                }
            }
        }

        return matches / (kps.length * 1.0);
    }
 
    private double computeRecall(String[] kps, String[] goldKeyphrase) {
        double matches = 0;
        for (int j = 0; j < goldKeyphrase.length; j++) {

            boolean matched = false;

            for (int i = 0; i < kps.length && !matched; i++) {
                if (goldStandard.compare(
                        kps[i].toLowerCase(), goldKeyphrase[j]) == 0) {
                    matches++;
                    matched = true;
                }
            }

            if (!matched && verbose) {
                System.out.println(
                        "Non matched keyphrase: " + goldKeyphrase[j]);
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
