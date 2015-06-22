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
package it.uniud.ailab.dcore.engine;

import java.util.List;
import java.util.Locale;
import it.uniud.ailab.dcore.persistence.DocumentComponent;

/**
 * This module generates the n-grams of the document, using the 
 * {@link it.uniud.ailab.dcore.persistence.Annotation}s produced by the
 * {@link it.uniud.ailab.dcore.engine.PreProcessor} and assigning them
 * {@link it.uniud.ailab.dcore.annotation.Feature}s accordingly. 
 * 
 * To correctly evaluate annotations, the module must know their syntax.
 * See the single Annotators to check how they output their result. 
 * For example, different PoS taggers for the same language can use different
 * tag sets.
 * 
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public interface NGramGenerator extends Annotator {
        
    /**
     * Generates the n-grams of a {@link it.uniud.ailab.dcore.persistence.DocumentComponent}
     * by using the POS patterns and other annotations produced by the Annotators.
     * A NGram generator should offer support for a set of languages, so the 
     * Engine can decide what generator should use if there are more than one.
     * 
     * @param blackboard the blackboard to analyze
     * @param component the component of the blackboard to analyze
     */
    public abstract void annotate(
            Blackboard blackboard,DocumentComponent component);
    
    /**
     * Sets the languages that the n-gram generator should process in a determined
     * execution pipeline. Note that these languages can be a subset of the 
     * languages effectively supported by the generator, but a specific pipeline
     * can purposely ignore them. As an example, we may have two generators that
     * both support English and Klingon; but only one of them should run in a
     * language pipeline, so one will be used with language "en" and the other
     * with language "tlh" (the ISO 639-2 code for the Klingon language).
     * 
     * @param langs the languages that the generator will process.
     */
    public abstract void setGramLanguages(List<Locale> langs);
    
    /**
     * Gets the languages that the n-gram generator should process in a determined
     * execution pipeline.
     * 
     * @return the languages that the generator will process.
     */
    public abstract List<Locale> getGramLanguages();
    
}
