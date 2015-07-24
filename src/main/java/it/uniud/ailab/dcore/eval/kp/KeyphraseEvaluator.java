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
package it.uniud.ailab.dcore.eval.kp;

import it.uniud.ailab.dcore.Distiller;
import it.uniud.ailab.dcore.eval.Evaluator;
import java.util.HashMap;

/**
 * Abstract keyphrase extraction evaluator object.
 *
 * @author Marco Basaldella
 */
public abstract class KeyphraseEvaluator extends Evaluator {
    
    /**
     * The input documents.
     */
    private String[] inputDocuments;
    /**
     * The keyphrases for the input documents.
     */
    private String[][] goldKeyphrases;

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
    public abstract String[] loadInputDocuments();
    
    /**
     * Loads the gold standard keyphrases and returns them. Please note that
     * they must be in the same order of the respecting input documents, i.e.
     * the first row of the array must contain the keyphrases of the first
     * element returned by loadInputDocuments().
     * 
     * @return the gold standard keyphrases.
     */
    public abstract String[][] loadGoldKeyphrases();
    
    /**
     * Evaluate the keyphrases using the given dataset and settings.
     * 
     * @param pipeline the distiller instance to evaluate.
     * @return the metrics and their score.
     */
    @Override
    public HashMap<String,Double> evaluate(Distiller pipeline) {
        
        inputDocuments = loadInputDocuments();
        goldKeyphrases = loadGoldKeyphrases();
        
        return null;
    }
    
}
