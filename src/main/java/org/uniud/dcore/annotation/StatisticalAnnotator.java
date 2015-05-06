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
package org.uniud.dcore.annotation;

import java.util.List;
import org.uniud.dcore.engine.Annotator;
import org.uniud.dcore.persistence.DocumentComponent;
import org.uniud.dcore.persistence.Gram;
import org.uniud.dcore.persistence.Sentence;
import org.uniud.dcore.utils.DocumentUtils;

/**
 * Annotates grams with statistical information such as their frequency, 
 * their width and their depth in the {@link org.uniud.dcore.persistence.DocumentComponent}
 * passed as input.
 * 
 * Document depth is defined as : ( index of sentence of first occurrence / total of sentences )
 * Document height is defined as : 1 - phrase depth.
 * Frequency is defined as : total # of occurrences / total # of sentences
 * 
 *
 * @author Marco Basaldella
 */
public class StatisticalAnnotator implements Annotator {
    
    // We use final fields to avoid spelling errors in feature naming.
    // Plus, is more handy to refer to a feature by ClassName.FeatureName, 
    // so that the code is much more readable.
    
    /**
     * Document depth, defined as ( index of sentence of first occurrence / total of sentences ).
     */
    public static final String DEPTH = "Depth";
    
    /**
     * Document height, defined as : 1 - document depth.
     */
    public static final String HEIGHT = "Height";
    
    /**
     * Document frequency, defined as the total count of occurrences of the
     * gram in text.
     */
    public static final String FREQUENCY = "Freq";
    
    /**
     * Life span, defined as frequency / total count of sentences.
     */
    public static final String LIFESPAN = "LifeSpan";
    
    /**
     * Annotates grams with statistical information such as their frequency,
     * their width and their depth in the
     * {@link org.uniud.dcore.persistence.DocumentComponent} passed as input.
     * 
     * @param component the component to analyze.
     */
    @Override
    public void annotate(DocumentComponent component) {

        List<Sentence> sentences = DocumentUtils.getSentences(component);

        int size = sentences.size();
        double count = 0;

        // Annotate grams with their statistical features.
        // The implementation is quite straightforward:
        // for the definitions of depth, height and frequency, just
        // see the variable declarations above.
        for (Sentence s : sentences) {
            count++;
            for (Gram g : s.getGrams()) {
                
                double depth = ( count / size );
                g.putFeature(DEPTH, depth);
                
                // check if it's the first appaerance
                // if not, set the height as the depth
                if (!g.hasFeature(HEIGHT))
                    g.putFeature(HEIGHT, depth);
                
                g.putFeature(LIFESPAN, g.getFeature(HEIGHT) - g.getFeature(DEPTH));                
                
                if (g.hasFeature(FREQUENCY))
                    g.putFeature(FREQUENCY,g.getFeature(FREQUENCY) + 1);
                else 
                    g.putFeature(FREQUENCY, 1);
                
                
            }            
        }        
    }    
}
