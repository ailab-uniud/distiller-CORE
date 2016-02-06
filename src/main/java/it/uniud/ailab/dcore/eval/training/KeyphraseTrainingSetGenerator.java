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
package it.uniud.ailab.dcore.eval.training;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.Distiller;
import it.uniud.ailab.dcore.eval.GenericDataset;
import it.uniud.ailab.dcore.eval.TrainingSetGenerator;
import it.uniud.ailab.dcore.io.CsvPrinter;
import it.uniud.ailab.dcore.io.GenericSheetPrinter;
import it.uniud.ailab.dcore.io.IOBlackboard;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import it.uniud.ailab.dcore.utils.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Generates a training set for the Keyphrase Extraction task.
 *
 * @author Marco Basaldella
 */
public class KeyphraseTrainingSetGenerator extends TrainingSetGenerator {

    /**
     * A training set generator for the Keyphrase Extraction task.
     *
     * @param goldStandard the gold standard to evaluate against.
     */
    public KeyphraseTrainingSetGenerator(GenericDataset goldStandard) {
        super(goldStandard);
    }

    @Override
    public List<Pair<String, GenericSheetPrinter>> generateTrainingSet(Distiller pipeline) {
        if (!goldStandard.isLoaded()) {
            goldStandard.load();
        }

        List<Pair<String, GenericSheetPrinter>> outputFiles
                = doWork(pipeline,
                        goldStandard.getTrainingSet(),
                        goldStandard.getTrainingAnswers());

        return outputFiles;
    }

    @Override
    public List<Pair<String, GenericSheetPrinter>> generateTestSet(Distiller pipeline) {
        if (!goldStandard.isLoaded()) {
            goldStandard.load();
        }

        List<Pair<String, GenericSheetPrinter>> outputFiles
                = doWork(pipeline,
                        goldStandard.getTestSet(),
                        goldStandard.getTestAnswers());

        return outputFiles;
    }

    private List<Pair<String, GenericSheetPrinter>> doWork(Distiller pipeline,
            Map<String, String> workingSet,
            Map<String, String[]> workingAnswers) {

        int docIndex = 0;

        List<Pair<String, GenericSheetPrinter>> outputFiles = new ArrayList<>();
        for (Map.Entry<String, String> documentEntry
                : workingSet.entrySet()) {

            String document = documentEntry.getValue().replace("\\n", " ");

            System.out.println("Evaluating document " + ++docIndex
                    + " of " + workingSet.size() + "...");

            System.out.println("Document identifier: " + documentEntry.getKey());
            System.out.println("Document's first 40 chars: "
                    + document.substring(0, 40) + "...");

            String[] answers
                    = workingAnswers.
                    get(documentEntry.getKey());
            
            IOBlackboard.setCurrentDocument(
                    IOBlackboard.getDocumentsFolder() +
                    "/" + 
                    documentEntry.getKey());

            Blackboard b = pipeline.distillToBlackboard(document);

            Collection<Gram> candidates
                    = b.getKeyphrases();
            
            
            for (Gram gram : candidates) {
                Keyphrase candidate = (Keyphrase) gram;
                candidate.putFeature(goldStandard.getIdentifier(), 0);

                boolean found = false;
                for (int i = 0; !found && i < answers.length; i++) {
                    String answer = answers[i];
                    if (goldStandard.compare(gram.getSurface(), answer) == 0) {
                        candidate.putFeature(goldStandard.getIdentifier(), 1);
                        found = true;
                    }
                }
            }

            CsvPrinter printer = new CsvPrinter();
            printer.loadGrams(b);
            printer.addToAll("DocID",documentEntry.getKey());
            outputFiles.add(new Pair<>(documentEntry.getKey(), printer));
        }
        return outputFiles;
    }

}
