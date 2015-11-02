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
