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

import java.util.List;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.annotations.TextAnnotation;
import it.uniud.ailab.dcore.annotation.annotations.UriAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.WikipediaUtils;

/**
 * Adds the Wikiflag as defined in
 * {@link it.uniud.ailab.dcore.annotation.annotators.GenericWikipediaAnnotator}
 * to grams which text coincides with the title of a Wikipedia page. 
 * Note: it requires a previous annotations of the tokens by
 * {@link it.uniud.ailab.dcore.annotation.annotators.TagMeTokenAnnotator}
 *
 * @author Marco Basaldella
 */
public class TagMeGramAnnotator implements Annotator, GenericWikipediaAnnotator {

    /**
     * Annotates the component.
     *
     * @param component
     */
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        for (Gram g : blackboard.getGrams()) {
            annotateGram(component,g);
        } // for (Gram g : ...
    }

    private void annotateGram(DocumentComponent component,Gram g) {
        // check if the gram coincides with a TagMe annotation
        
        for (List<Token> tokens : g.getTokenLists()) {
            
            // we should check if all the tokens of the gram have the
            // same annotation
            TextAnnotation a = (TextAnnotation) tokens.get(0).getAnnotation(TagMeTokenAnnotator.WIKIFLAG);
            
            if (a == null) {
                continue;
            }
            
            boolean isTagged = false;
            
            // the annotations have the same length, so we may have a legit
            // wikipedia surface as the gram
            if (a.getTokens().length == g.getTokens().size()) {
                
                isTagged = true;
                
                for (int i = 0; i < a.getTokens().length && isTagged; i++) {
                    isTagged = a.getTokens()[i].equals(
                            g.getTokens().get(i));
                }
                
                if (isTagged) {
                    g.putFeature(WIKIFLAG, 1);
                    
                    g.addAnnotation(new UriAnnotation(
                            WIKIURI,
                            a.getAnnotatedText(),
                            a.getAnnotation(),
                            WikipediaUtils.generateWikiUri(a.getAnnotation(),
                                    component.getLanguage())));
                }
            }
            
            if (isTagged) break;
            
        } // for (List<Token> tokens : ...   
    }

}
