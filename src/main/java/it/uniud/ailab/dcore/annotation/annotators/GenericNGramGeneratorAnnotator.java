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
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;

/**
 * This module generates the n-grams of the document, using the 
 * {@link it.uniud.ailab.dcore.persistence.Annotation}s produced by the
 * previous Annotators and assigning them
 * {@link it.uniud.ailab.dcore.annotation.Feature}s accordingly. 
 * 
 * To correctly evaluate annotations, the module must know their syntax.
 * See the single Annotators to check how they output their result. 
 * 
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public interface GenericNGramGeneratorAnnotator extends Annotator {
    
    /**
     * An annotation which counts the number of nouns in a n-gram.
     */
    public static final String NOUNVALUE = "NounValue";
        
    /**
     * Generates the n-grams of a {@link it.uniud.ailab.dcore.persistence.DocumentComponent}
     * by using the POS patterns and other annotations produced by the Annotators.
     * 
     * @param blackboard the blackboard to analyze
     * @param component the component of the blackboard to analyze
     */
    @Override
    public abstract void annotate(
            Blackboard blackboard,DocumentComponent component);
      
}
