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
