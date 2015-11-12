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
package it.uniud.ailab.dcore.launchers;

import it.uniud.ailab.dcore.eval.datasets.SemEval2010;
import it.uniud.ailab.dcore.DistillerFactory;
import it.uniud.ailab.dcore.eval.GenericDataset;
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
        GenericDataset kpDataset;

        switch (dataset) {
            case SEMEVAL:
                kpDataset = new SemEval2010(folder);
                break;
            default:
                kpDataset = null;
        }

        if (kpDataset == null) {
            throw new UnsupportedOperationException(
                    "Unknown dataset:" + dataset);
        }

        (new KeyphraseEvaluator(kpDataset)).
                evaluate(DistillerFactory.getDefaultEval());

    }
}
