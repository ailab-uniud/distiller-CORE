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
package it.uniud.ailab.dcore.utils;

import it.uniud.ailab.dcore.annotation.Annotator;

/**
 * Some utilities that help with Annotators.
 *
 * @author Marco Basaldella
 */
public class AnnotatorUtils {
    
    /**
     * Get the class name of the Annotator.
     * 
     * @param a the annotator.
     * @return the name of the annotator.
     */
    public static String getAnnotatorSimpleName(Annotator a) {
        
        return a.getClass().getSimpleName();
    }
    
}