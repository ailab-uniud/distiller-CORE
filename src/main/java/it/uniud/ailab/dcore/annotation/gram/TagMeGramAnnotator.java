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
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Token;

/**
 * Adds the Wikiflag as defined in {@link it.uniud.ailab.dcore.annotation.generic.WikipediaAnnotator}
 * to grams which text coincides with a Wikipedia page. Note: it requires a previous 
 * annotations of the tokens by {@link it.uniud.ailab.dcore.annotation.tokens.TagMeTokenAnnotator}
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
    public void annotate(Blackboard blackboard,DocumentComponent component) {
        
        for (Gram g: blackboard.getGrams()) {
            // check if the gram coincides with a TagMe annotation
            List<Token> tokens = g.getTokens();
            
            int counter = 0;
            
            TextAnnotation a = tokens.get(counter).getAnnotation(TagMeTokenAnnotator.WIKIFLAG);
            
            boolean isTagged = a != null;
            
            // a gram is tagged with a wikiflag if ALL the tokens contain
            // a "tagme annotation", i.e. if it's the same as a page title
            
            // Special case: the gram may be a substring of a wikipedia page title,
            // but it not worth to check it since it's a very rare occurrrence and
            // it will just increase complexity. Plus, the possibility of this
            // rare occurrences depends mainly from the n-gram generator: a good
            // n-gram generator should never "cut" wikipedia titles in candidate
            // keyphrases. For example, if "software engineering" appears in 
            // the text, a good ngram generator should always choose 
            // the whole phrase as a candidate KP, and never the single words
            // "software" or "engineering"
            
            while (isTagged && ++counter < tokens.size()) {
                TextAnnotation b = tokens.get(counter).getAnnotation(TagMeTokenAnnotator.WIKIFLAG);
                isTagged = (b != null) ? b.equals(a) : false;
            }
            
            if (isTagged) {
                g.putFeature(WIKIFLAG, 1);
            }
        }
    }
    
}
