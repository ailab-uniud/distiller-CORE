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
package it.uniud.ailab.dcore.launchers;

import it.uniud.ailab.dcore.DistillerFactory;
import it.uniud.ailab.dcore.eval.kp.*;

/**
 * Run the evaluation routines for keyphrase extraction.
 *
 * @author Marco Basaldella
 */
public class KeyphraseEvaluation {

    public enum Dataset {

        SEMEVAL,
        DUC,
        Inspec
    };
    
    public static void main(String[] args) {
        
        // Let the main launcher handle this
        Launcher.main(args);
        
    }

    public static void evaluate(Dataset dataset, String folder) {
        KeyphraseEvaluator task;

        switch (dataset) {
            case SEMEVAL:
                task = new SemEval2010(folder);
                break;
            default:
                task = null;
        }

        if (task == null) {
            throw new UnsupportedOperationException(
                    "Unknown dataset:" + dataset);
        }

        task.evaluate(DistillerFactory.getDefaultEval());

    }
}
