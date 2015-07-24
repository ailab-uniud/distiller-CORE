/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 *
 * 	Licensed under the Apache License, Version 2.0 (the "License");
 * 	you may not use this file except in compliance with the License.
 * 	You may obtain a copy of the License at
 *
 * 	     http://www.apache.org/licenses/LICENSE-2.0
 *
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 */
package it.uniud.ailab.dcore.eval;

import it.uniud.ailab.dcore.Distiller;
import java.util.HashMap;

/**
 * Abstract evaluator object that runs a distiller pipeline and evaluates 
 * the results using one or more metrics.
 *
 * @author Marco Basaldella
 */
public abstract class Evaluator {
    
    /**
     * The path where the evaluator will find the input documents and the
     * gold standard results.
     */
    private final String goldStandardPath;
    
    /**
     * Creates an evaluator that will look for the gold standard in the specified
     * path.
     * 
     * @param goldStandardPath the folder that contains the gold standard.
     */
    public Evaluator(String goldStandardPath) {
        this.goldStandardPath = goldStandardPath;
    } 

    /**
     * Get the path where the evaluator should search for the gold standard.
     * 
     * @return the folder that contains the gold standard.
     */
    protected String getGoldStandardPath() {
        return goldStandardPath;
    }
    
    /**
     * Evaluates a distiller instance using one or more metrics.
     * 
     * @param pipeline the distiller instance to evaluate
     * @return an hashmap with the name of the metrics as keys and the result
     * of the evaluation of as values
     */
    public abstract HashMap<String,Double> evaluate(Distiller pipeline);
    
}
