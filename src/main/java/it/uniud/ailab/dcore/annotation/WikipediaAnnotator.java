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
 * This almost-empty interface is used to promote the interchangeability 
 * between different annotators: every Wikipedia annotator should use the 
 * Wikiflag here defined to annotate grams or tokens which are respectively 
 * title and part of the title of a Wikipedia page.
 * This way, subsequent steps of the Distiller do not have to be aware of 
 * what annotator precedes them, but they just have to now that someone put a 
 * "Wikiflag" over that n-gram (or token).
 *
 * @author Marco Basaldella
 */
public interface WikipediaAnnotator {
    
    /**
     * This field will be set to 1 if the specified gram coincides with a 
     * Wikipedia entry.
     */
    public static final String WIKIFLAG = "Wikiflag";
    
}
