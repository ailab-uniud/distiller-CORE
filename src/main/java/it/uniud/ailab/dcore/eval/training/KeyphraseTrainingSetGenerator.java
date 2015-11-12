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
import it.uniud.ailab.dcore.DistilledOutput;
import it.uniud.ailab.dcore.Distiller;
import it.uniud.ailab.dcore.eval.GenericDataset;
import it.uniud.ailab.dcore.eval.TrainingSetGenerator;
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
    public Distiller evaluate(Distiller pipeline) {
        if (goldStandard.isLoaded()) {
            goldStandard.load();
        }
        
        int docIndex = 0;


        for (Map.Entry<String, String> documentEntry
                : goldStandard.getTestSet().entrySet()) {

            String document = documentEntry.getValue().replace("\\n", " ");

            System.out.println("Evaluating document " + ++docIndex
                    + " of " + goldStandard.getTestSet().size() + "...");

            System.out.println("Document identifier: " + documentEntry.getKey());
            System.out.println("Document's first 40 chars: "
                    + document.substring(0, 40) + "...");

            Blackboard output = pipeline.distillToBlackboard(document);

        }

        return pipeline;
    }
    
}
