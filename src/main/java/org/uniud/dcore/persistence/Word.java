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
package org.uniud.dcore.persistence;

/**
 *
 * @author Marco Basaldella
 */
public class Word {
    
    private String stem;
    private String PoS;
    
    private Annotation[] annotations;

    /**
     * @return the stem
     */
    public String getStem() {
        return stem;
    }

    /**
     * @param stem the stem to set
     */
    public void setStem(String stem) {
        this.stem = stem;
    }

    /**
     * @return the PoS
     */
    public String getPoS() {
        return PoS;
    }

    /**
     * @param PoS the PoS to set
     */
    public void setPoS(String PoS) {
        this.PoS = PoS;
    }

    /**
     * @return the annotations
     */
    public Annotation[] getAnnotations() {
        return annotations;
    }

    /**
     * @param annotations the annotations to set
     */
    public void setAnnotations(Annotation[] annotations) {
        this.annotations = annotations;
    }
    
}
