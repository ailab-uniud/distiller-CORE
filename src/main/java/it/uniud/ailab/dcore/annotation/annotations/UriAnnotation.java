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
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An annotation that annotates a chunk of text with an URI.
 *
 * @author Marco Basaldella
 */
public class UriAnnotation extends Annotation {

    public UriAnnotation(String annotator, String surface, String uriTitle, URI uri) {
        super(annotator);
        super.addString(surface);
        super.addString(uriTitle);
        super.addString(uri.toString());
    }

    public String getSurface() {
        return super.getStringAt(0);
    }

    public String getUriTitle() {
        return super.getStringAt(1);
    }

    public URI getUri() {

        URI uri = null;

        try {
            uri = new URI(super.getStringAt(2));
        } catch (URISyntaxException ex) {

            // The catch is mandatory, but it's very unlikely that
            // this may happen
            Logger.getLogger(InferenceAnnotation.class.getName())
                    .log(Level.SEVERE,
                            "Absurdity: URI converted to String cannot be converted back to URI",
                            ex);
        }

        return uri;
    }

    @Override
    public String toString() {
        return annotator + ": " + getUri().toASCIIString();
    }

}
