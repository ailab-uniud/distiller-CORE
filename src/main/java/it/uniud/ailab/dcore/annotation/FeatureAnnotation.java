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
 * A feature is a value generated by an annotator that expresses some
 * property of the annotated object. For example, a sentiment annotator
 * may assign a high value over a gram for positive sentiment, and negative 
 * otherwise.
 * 
 * @author Marco Basaldella
 */
public class FeatureAnnotation extends Annotation {
    
    private final double value;
    
    /**
     * A feature.
     * 
     * @param annotator the annotator that generated the feature.
     * @param value the value of the feature.
     */
    public FeatureAnnotation(String annotator,double value) {
        super(annotator);
        this.value = value;
    }
    
    /**
     * Get the value of the feature.
     * 
     * @return the value of the feature
     */
    public double getValue() {
        return value;
    }  
}
