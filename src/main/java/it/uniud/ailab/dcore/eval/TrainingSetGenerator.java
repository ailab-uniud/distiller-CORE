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
package it.uniud.ailab.dcore.eval;

import it.uniud.ailab.dcore.Distiller;
import it.uniud.ailab.dcore.io.GenericSheetPrinter;
import it.uniud.ailab.dcore.utils.Pair;
import java.util.List;

/**
 * Generates a training set running the a Distiller instance and evaluating its 
 * results, adding an annotation to each candidate item that identifies if 
 * the candidate is right or wrong.
 *
 * @author Marco Basaldella
 */
public abstract class TrainingSetGenerator {
    
    /**
     * The actual gold standard.
     */
    protected final GenericDataset goldStandard;
    
    /**
     * Creates an evaluator that will look for the gold standard in the specified
     * path.
     * 
     * @param goldStandard the dataset that contains the gold standard.
     */
    public TrainingSetGenerator(GenericDataset goldStandard) {
        this.goldStandard = goldStandard;
    } 

    /**
     * Get the path where the evaluator should search for the gold standard.
     * 
     * @return the folder that contains the gold standard.
     */
    public GenericDataset getGoldStandard() {
        return goldStandard;
    }
    
    /**
     * Evaluates a distiller instance using one or more metrics. Returns a list
     * of {@link it.uniud.ailab.dcore.utils.Pair} object, where the left
     * element of the pair is the identifier of a document, and the right
     * element of the pair is the result of the distillation of the document 
     * itself.
     * 
     * @param pipeline the distiller instance to evaluate
     * @return a list of pairs composed by strings of identifiers and tables
     * with the output for each file, with an extra annotation on the candidates
     * that identifies correct and wrong training set samples.
     */
    public abstract List<Pair<String,GenericSheetPrinter>>
         evaluate(Distiller pipeline);
}
