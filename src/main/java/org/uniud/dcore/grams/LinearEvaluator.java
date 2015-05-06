/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 * 
 * 	This file is part of the Distiller-CORE library.
 * 
 * 	Distiller-CORE is free software; you can redistribute it and/or
 * 	modify it under the terms of the GNU Lesser General Public
 * 	License as published by the Free Software Foundation; either
 * 	version 2.1 of the License, or (at your option) any later version.
 *
 * 	Distiller-CORE is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * 	Lesser General Public License for more details.
 *
 * 	You should have received a copy of the GNU Lesser General Public
 * 	License along with this library; if not, write to the Free Software
 * 	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * 	MA 02110-1301  USA or see <http://www.gnu.org/licenses/>.
 */
package org.uniud.dcore.grams;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Required;
import org.uniud.dcore.engine.BlackBoard;
import org.uniud.dcore.engine.Evaluator;
import org.uniud.dcore.persistence.DocumentComponent;
import org.uniud.dcore.persistence.Feature;
import org.uniud.dcore.persistence.Gram;

/**
 *
 * @author Marco Basaldella
 */
public class LinearEvaluator extends Evaluator {
        
    private Map<String,Double> weights;
    
    @Required
    public void setWeights(HashMap<String,Double> weights) {
        this.weights = weights;
    }

    @Override
    public Map<Gram, Double> Score(DocumentComponent c) {
        generateAnnotations(c);
        
        HashMap<Gram,Double> scoredGrams = new HashMap<>();
        
        for(Gram g : BlackBoard.Instance().getGrams().values())
        {
            double score = 0;
            for (Feature f : g.getFeatures()) {
                if (weights.containsKey(f.getType())) {
                    score += f.getValue() * weights.get(f.getType());
                }
            }            
            g.putFeature(SCORE, score);
            scoredGrams.put(g, score);
        }
        
        return scoredGrams;       
    }    
}
