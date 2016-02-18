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

/**
 * This almost-empty interface is used to promote the interchangeability 
 * between different annotators: every Wikipedia annotator should use the 
 * Wikiflag here defined to annotate grams or tokens which are respectively 
 * title and part of the title of a Wikipedia page.
 * 
 * This way, subsequent steps of the distiller do not have to be aware of 
 * what annotator precedes them, but they just have to now that someone put a 
 * "Wikiflag" over that n-gram (or generic Annotable).
 *
 * @author Marco Basaldella
 */
public interface GenericWikipediaAnnotator {
    
    /**
     * This should be used to add FeatureAnnotations with value 1 when the 
     * specified Annotable is associated to a Wikipedia entry.
     */
    public static final String WIKIFLAG = "Wikiflag";
    
    /**
     * This should be used to add UriAnnotations with a the Wikipedia URI to
     * associate at the Annotable object.
     */
    public static final String WIKIURI = "Wikiuri";
    
    
}
