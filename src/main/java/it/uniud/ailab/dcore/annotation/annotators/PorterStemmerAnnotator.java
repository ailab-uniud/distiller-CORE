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

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.AnnotationException;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Mention;
import it.uniud.ailab.dcore.persistence.Mention.Reference;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import it.uniud.ailab.dcore.utils.GramUtils;
import it.uniud.ailab.dcore.utils.SnowballStemmerSelector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.tartarus.snowball.SnowballStemmer;

/**
 *
 * @author Giorgia Chiaradia
 */
public class PorterStemmerAnnotator implements Annotator{

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        //annotate sentences
        List<Sentence> sentences = DocumentUtils.getSentences(component);
        
        // Get the appropriate stemmer
            SnowballStemmer stemmer = SnowballStemmerSelector.
                    getStemmerForLanguage(component.getLanguage());

            if (stemmer == null) {
                throw new AnnotationException(this,
                        "Stemmer not available for the language "
                        + component.getLanguage().getLanguage());
            }

        
        for(Sentence sentence : sentences){
            for(Gram gram : sentence.getGrams()){
                for(Token t : gram.getTokens()){
                    if(stemmer.stem()){
                        t.setStem(stemmer.getCurrent());
                    } else {
                        t.setStem(t.getText());
                    }
                }
            }
        }
        
        //annotate grams
        Map<String,Gram> mentions = blackboard.getGramsByType(GramUtils.MENTION);
        for(Gram g : mentions.values()){
           Mention m = (Mention)g;
           for(Token t : m.getAnaphorToken()){
               if(stemmer.stem()){
                        t.setStem(stemmer.getCurrent());
                    } else {
                        t.setStem(t.getText());
                    }
           }
           
           for(Reference r : m.getReferences()){
               for(Token t : r.getTokens()){
               if(stemmer.stem()){
                        t.setStem(stemmer.getCurrent());
                    } else {
                        t.setStem(t.getText());
                    }
               }
           }
        }
        
        //qui si dovrebbe riaggiornare la map di balckboard con i nuovi identificatori???
    }
    
    
    
}
