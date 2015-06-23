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
package it.uniud.ailab.dcore.annotation.annotations;

import it.uniud.ailab.dcore.annotation.Annotation;
import java.net.URI;

/**
 * An annotation that annotates a chunk of text with an URI.
 * 
 * @author Marco Basaldella
 */
public class UriAnnotation extends Annotation {
    
    private final String surface;
    private final String uriTitle;
    private final URI uri;

    public UriAnnotation(String annotator, String surface, String uriTitle, URI uri) {
        super(annotator);
        this.surface = surface;
        this.uriTitle = uriTitle;
        this.uri = uri;
    }

    public String getSurface() {
        return surface;
    }

    public String getUriTitle() {
        return uriTitle;
    }

    public URI getUri() {
        return uri;
    }
    
}
