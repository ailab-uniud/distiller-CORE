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
