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
package it.uniud.ailab.dcore.annotation.annotators;

import java.util.List;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.utils.DocumentUtils;

/**
 * Annotates grams with statistical information such as their frequency, 
 * their width and their depth in the {@link it.uniud.ailab.dcore.persistence.DocumentComponent}
 * passed as input.
 * 
 * Document depth is defined as : ( index of sentence of last occurrence
 *                                  / total # of sentences )
 * Document height is defined as : ( index of sentence of first occurrence
 *                                  / total # of sentences )
 * Frequency is defined as : total # of occurrences / 
 *                           total # of sentences
 * Life span is defined as : ( index of sentence of last occurrence -
 *                              index of sentence of first occurrence) 
 *                              / total # of sentences.
 * 
 *
 * @author Marco Basaldella
 */
public class StatisticalAnnotator implements Annotator {
    
    // We use final fields to avoid spelling errors in feature naming.
    // Plus, is more handy to refer to a feature by ClassName.FeatureName, 
    // so that the code is much more readable.
    
    /**
     * Document depth, defined as ( index of sentence of last occurrence / total of sentences ).
     */
    public static final String DEPTH = "Depth";
    
    /**
     * Document depth, defined as ( index of sentence of first occurrence / total of sentences ).
     */
    public static final String HEIGHT = "Height";
    
    /**
     * Document frequency, defined as the total count of occurrences of the
     * gram in text.
     */
    public static final String FREQUENCY = "Freq";
    
    /**
     * Life span, defined as ( index of sentence of last occurrence -
     *                         index of sentence of first occurrence) 
     *                         / total # of sentences.
     * 
     * This can be expressed as (depth - (1 - height)) or equally as
     *                          depth + height - 1.
     */
    public static final String LIFESPAN = "LifeSpan";
    
    /**
     * Annotates grams with statistical information such as their frequency,
     * their width and their depth in the
     * {@link it.uniud.ailab.dcore.persistence.DocumentComponent} passed as input.
     * 
     * @param component the component to analyze.
     */
    @Override
    public void annotate(Blackboard blackboard,DocumentComponent component) {

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
                // if not, set the height 1 - depth
                if (!g.hasFeature(HEIGHT))
                    g.putFeature(HEIGHT, 1 - depth);
                
                g.putFeature(LIFESPAN, g.getFeature(DEPTH) + g.getFeature(HEIGHT) - 1);                
                
                if (g.hasFeature(FREQUENCY))
                    g.putFeature(FREQUENCY,g.getFeature(FREQUENCY) + 1);
                else 
                    g.putFeature(FREQUENCY, 1);
                
                
            }            
        }        
    }    
}
