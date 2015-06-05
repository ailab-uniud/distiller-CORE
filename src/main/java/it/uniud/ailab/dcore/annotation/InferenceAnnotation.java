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
package it.uniud.ailab.dcore.annotation;

/**
 *
 * @author Marco Basaldella
 */
public class InferenceAnnotation extends Annotation {
    
    /**
     * The concept inferred.
     */
    private final String concept;

    /**
     * The score associated to the concept.
     */
    private final double score;
    
    /**
     * Instantiates the annotation.
     * 
     * @param annotator
     * @param concept
     * @param score 
     */
    public InferenceAnnotation(String annotator,String concept,double score) {
        super(annotator);
        this.concept = concept;
        this.score = score;
    }
    
    /**
     * Gets the concept inferred by the annotator.
     * 
     * @return the concept.
     */
    public String getConcept() {
        return concept;
    }

    /**
     * Gets the scored associated to the concept inferred.
     * 
     * @return the score.
     */
    public double getScore() {
        return score;
    }
}
