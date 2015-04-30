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
package org.uniud.dcore.engine;

import org.uniud.dcore.persistence.DocumentComponent;

/**
 * This module generates the n-grams of the document, using the 
 * {@link org.uniud.dcore.persistence.Annotation}s produced by the
 * {@link org.uniud.dcore.engine.PreProcessor} and assigning them
 * {@link org.uniud.dcore.persistence.Feature}s accordingly. 
 * 
 * To correctly evaluate annotations, the module must know their syntax.
 * See the single Annotators to check how they output their result. 
 * For example, different PoS taggers for the same language can use different
 * tag sets.
 * 
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public interface NGramGenerator {
    
    public abstract void generateNGrams(DocumentComponent component);
    
}
