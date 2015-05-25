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
package org.uniud.dcore.wrappers.external;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.util.List;
import org.tartarus.snowball.ext.italianStemmer;
import org.uniud.dcore.engine.Annotator;
import org.uniud.dcore.persistence.DocumentComponent;
import org.uniud.dcore.persistence.DocumentComposite;
import org.uniud.dcore.persistence.Sentence;
import org.uniud.dcore.persistence.Token;
import org.uniud.dcore.utils.CueUtils;

/**
 *
 * @author Marco Basaldella
 */
public class ItalianBootstapper implements Annotator {

    @Override
    public void annotate(DocumentComponent component) {
        
        MaxentTagger tagger = new MaxentTagger(
                "/home/red/Downloads/it-pos-maxent.bin");
        
        // tokenize the component within the cue  library
        List<String> rawSentences = CueUtils.splitSentence(
                component.getText(), component.getLanguage());
        
        italianStemmer stemmer = new italianStemmer();
        
        // create the sub-components, then tokenize and annotate them
        for (String rawSentence : rawSentences) {
            Sentence sentence = new Sentence(rawSentence,component.getLanguage());
            
            // tokenize the sentence and then re-wrap it to feed Maxent tagger
            // correctly (see Javadoc of tagTokenizedString for more information)
            String taggedString = tagger.tagTokenizedString(String.join(" ", CueUtils.tokenizeSentence(rawSentence, component.getLanguage())));
            for (String taggedToken : taggedString.split(" ")) {
                String[] splittedToken = taggedToken.split("/");
                Token t = new Token(splittedToken[0]);
                t.setPoS(splittedToken[1]);
                
                stemmer.setCurrent(splittedToken[0]);
                if (stemmer.stem()) {
                    t.setStem(stemmer.getCurrent());
                } else {
                    System.err.println("Error while stemming word "+splittedToken[0]);
                    t.setStem(splittedToken[0]);
                }
                
                sentence.addToken(t);
            }
            
            ((DocumentComposite)component).addComponent(sentence);
            
            
        }
        
        
    }
    
}
