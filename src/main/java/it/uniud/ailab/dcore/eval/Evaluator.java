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
import java.util.Map;

/**
 * Abstract evaluator object that runs a distiller pipeline and evaluates 
 * the results using one or more metrics.
 *
 * @author Marco Basaldella
 */
public abstract class Evaluator {
    
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
    public Evaluator(GenericDataset goldStandard) {
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
     * Evaluates a distiller instance using one or more metrics.
     * 
     * @param pipeline the distiller instance to evaluate
     * @return an hashmap with the name of the metrics as keys and the result
     * of the evaluation of as values
     */
    public abstract Map<String,Double> evaluate(Distiller pipeline);
    
}
