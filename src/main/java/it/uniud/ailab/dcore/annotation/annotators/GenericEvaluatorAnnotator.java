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

import it.uniud.ailab.dcore.annotation.Annotator;

/**
 * Evaluators read the {@link it.uniud.ailab.dcore.annotation.Feature}s produced 
 * by the {@link org.uniud.dcore.engine.GramGenerator} and other annotators and 
 * evaluates them to generate the output of the Distiller.
 * 
 * To correctly evaluate features, evaluators must know their meaning.
 * See the GramGenerator and the gram annotators you're using to check 
 * what their annotation mean.
 * 
 * This class is provided as a superclass for Evaluators, to use them
 * interchangeably in the annotation pipeline.
 * 
 * Note that one may decide to use different evaluators in the pipeline by
 * not implementing this interface; while this offers the possibility to 
 * combine different evaluation techniques in a single pipeline, it causes the 
 * inability to use subsequent annotators in the pipeline that depend on this
 * interface.
 * 
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public interface GenericEvaluatorAnnotator extends Annotator {
    
    /**
     * The importance of a gram in the document, also known as keyphraseness. 
     */
    public static final String SCORE = "Score";
    
}
