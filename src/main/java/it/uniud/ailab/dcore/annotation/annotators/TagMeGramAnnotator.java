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
