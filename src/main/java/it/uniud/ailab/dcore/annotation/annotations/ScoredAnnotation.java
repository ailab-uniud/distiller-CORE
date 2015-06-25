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
package it.uniud.ailab.dcore.annotation.annotations;

/**
 * A common superclass for all the features that have a score. This interface
 * enables an annotation to be easily filtered.
 * 
 * @author Marco Basaldella
 */
public interface ScoredAnnotation {
    
    /**
     * Returns the score produced by the annotator.
     * 
     * @return the score produced by the annotator.
     */
    public abstract double getScore();
    
}
