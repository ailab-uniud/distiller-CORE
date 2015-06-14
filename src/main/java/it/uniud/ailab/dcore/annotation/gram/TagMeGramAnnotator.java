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
package it.uniud.ailab.dcore.annotation.gram;

import it.uniud.ailab.dcore.annotation.generic.WikipediaAnnotator;
import it.uniud.ailab.dcore.annotation.token.TagMeTokenAnnotator;
import java.util.List;
import it.uniud.ailab.dcore.engine.Annotator;
import it.uniud.ailab.dcore.engine.Blackboard;
import it.uniud.ailab.dcore.annotation.TextAnnotation;
import it.uniud.ailab.dcore.annotation.UriAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.WikipediaUtils;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Adds the Wikiflag as defined in
 * {@link it.uniud.ailab.dcore.annotation.generic.WikipediaAnnotator} to grams
 * which text coincides with a Wikipedia page. Note: it requires a previous
 * annotations of the tokens by
 * {@link it.uniud.ailab.dcore.annotation.tokens.TagMeTokenAnnotator}
 *
 * @author Marco Basaldella
 */
public class TagMeGramAnnotator implements Annotator, WikipediaAnnotator {

    /**
     * Annotates the component.
     *
     * @param component
     */
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        for (Gram g : blackboard.getGrams()) {
            // check if the gram coincides with a TagMe annotation
            List<Token> tokens = g.getTokens();

            
            // we should check if all the tokens of the gram have the 
            // same annotation
            for (TextAnnotation a : tokens.get(0).getAnnotations(TagMeTokenAnnotator.WIKIFLAG)) {

                // the annotations have the same length, so we may have a legit
                // wikipedia surface as the gram
                if (a.getTokens().length == g.getTokens().size()) {
                    
                    boolean isTagged = true;
                    
                    for (int i = 0; i < a.getTokens().length && isTagged; i++) {
                        isTagged = a.getTokens()[i].equals(
                                    g.getTokens().get(i));
                    }                    
                    
                    if (isTagged) {
                        g.putFeature(WIKIFLAG, 1);
                        
                        g.addAnnotation(new UriAnnotation(
                                WIKIFLAG,
                                a.getAnnotatedText(),
                                a.getAnnotation(),
                                WikipediaUtils.generateWikiUri(a.getAnnotation(),
                                        component.getLanguage())));
                    }
                }
            } // for (TextAnnotation a :  ...
        } // for (Gram g : ...
    }

}
