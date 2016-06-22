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
package it.uniud.ailab.dcore.wrappers.external;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.util.Locale;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.DocumentComposite;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.utils.ArabicDocProcessing;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A bootstrapper annotator for the Arabic language developed using the
 * Stanford Core NLP library. The annotator splits the document, performs PoS
 * tagging. This annotator supports only the Arabic language.
 *
 * @author Muhammad Helmy
 */
public class ArabicStanfordBootstrapper implements Annotator {

    /**
     * The Stanford NLP pipeline. The field is marked static to be optimized for
     * re-use, so that subsequent calls of annotate() don't have to reload
     * definitions every time, even for different instances of the annotator.
     */
    private static MaxentTagger tagger = null;
    /**
     * Annotate the document by splitting the document, performing PoS tagging
     * and Named Entity Recognition using the Stanford Core NLP tools.
     *
     * @param component the component to annotate.
     */
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        if (tagger == null) {

            tagger = new MaxentTagger(
                    getClass().getClassLoader().
                    getResource("stanford/arabic.tagger").getFile());

        }
        ArabicDocProcessing.init();
        // read some text in the text variable
        String docText = component.getText();        
        docText = ArabicDocProcessing.normalizeAlefAndYa(ArabicDocProcessing.preProcess(docText));
        try {
            docText = ArabicDocProcessing.processText(ArabicDocProcessing.SEGMENT, docText);
        } catch (Exception ex) {
            Logger.getLogger(ArabicStanfordBootstrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] sentsTxts = docText.split("(!|\\?|\\.|:)");
        for (int i=0; i<sentsTxts.length; i++) {
            String txt = sentsTxts[i].trim();
            if(txt==null || txt.length()==0)
                continue;             
            Sentence distilledSentence = new Sentence(txt, new Locale("ar"), "" + i);
            String sentTaggedTxt = ArabicDocProcessing.POSTageText(txt).trim();
            String[] sentTaggedWords = sentTaggedTxt.split(" ");
            String[] sentWords = txt.split(" ");
            int j=0;
            for (String taggedWord : sentTaggedWords) {
                taggedWord = taggedWord.trim();
                // this is the text of the token
                String word = sentWords[j++];//taggedWord.substring(0, taggedWord.indexOf("/"));
                if(word==null || word.length()==0)
                    continue;
                Token t = new Token(word);
                // this is the POS tag of the token                
                t.setPoS(taggedWord.substring(taggedWord.indexOf("/") + 1));
                distilledSentence.addToken(t);
            }            
            if(distilledSentence.getTokens().size()==0)
                continue;            
            ((DocumentComposite) component).addComponent(distilledSentence);
        }
    }
}
>>>>>>> 787cb30... Arabic Pipeline (Version 1.0) using RegEx NGramGenerator.
