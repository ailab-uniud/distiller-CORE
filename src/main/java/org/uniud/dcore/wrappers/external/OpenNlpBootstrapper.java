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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.tartarus.snowball.SnowballStemmer;
import org.uniud.dcore.engine.Annotator;
import org.uniud.dcore.persistence.DocumentComponent;
import org.uniud.dcore.persistence.DocumentComposite;
import org.uniud.dcore.persistence.Sentence;
import org.uniud.dcore.persistence.Token;
import org.uniud.dcore.utils.SnowballStemmerSelector;

/**
 * A bootstrapper annotator for the English language developed using the 
 * Apache OpenNLP library. The annotator splits the document, tokenizes it and
 * performs POS tagging; the stemming is performed using the Snowball stemmer 
 * instead.
 * 
 * This annotator can support every language the OpenNLP library supports.
 * 
 * @author Marco Basaldella
 */
public class OpenNlpBootstrapper implements Annotator {

    private final String italianPosPath
            = "/home/red/tools/opennlp-italian-models/models/it/";

    /**
     * Annotates the document using the Apache OpenNLP tools.
     * 
     * For more info and a CoreNLP tutorial, please refer to 
     * <a href="http://opennlp.apache.org/documentation/manual/opennlp.html">
     * the official Apache documentation</a>.
     * 
     * For a list of models that are ready to use with CoreNLP,
     * please see <a href="http://opennlp.sourceforge.net/models-1.5/">
     * this model list on the Apache OpenNLP website</a>.
     * 
     * For the Italian language, we use Andrea Ciapetti's models, which are 
     * available on <a href="http://github.com/aciapetti/opennlp-italian-models">
     * his GitHub page</a>.
     * 
     * @param component the component to annotate.
     */
    @Override
    public void annotate(DocumentComponent component) {
        
        // Split the text into sentences
        
        InputStream sentModelIn = null;
        SentenceModel sentModel = null;
        try {

            sentModelIn = new FileInputStream(italianPosPath + "it-sent.bin");
            sentModel = new SentenceModel(sentModelIn);
        } catch (IOException e) {
            throw new RuntimeException("Error while loading the sentence splitting models.");
        } finally {
            if (sentModelIn != null) {
                try {
                    sentModelIn.close();
                } catch (IOException e) {
                }
            }
        }

        SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentModel);
        String sentences[] = sentenceDetector.sentDetect(component.getText());

        for (String sentenceString : sentences) {

            // the distilled sentence object
            
            Sentence sentence = new Sentence(sentenceString);
            sentence.setLanguage(component.getLanguage());
            
            // Tokenize the sentence
            
            InputStream tokenModelIn = null;
            TokenizerModel tokenModel = null;
            
            try {
                tokenModelIn = new FileInputStream(italianPosPath + "it-token.bin");
                tokenModel = new TokenizerModel(tokenModelIn);
            } catch (IOException e) {
                throw new RuntimeException("Error while loading the tokenizer models.");
            } finally {
                if (tokenModelIn != null) {
                    try {
                        tokenModelIn.close();
                    } catch (IOException e) {
                    }
                }
            }
            
            Tokenizer tokenizer = new TokenizerME(tokenModel);
            String tokens[] = tokenizer.tokenize(sentenceString);

            // POS tag the tokens
            InputStream POSModelIn = null;
            POSModel POSModel = null;

            try {
                POSModelIn = new FileInputStream(italianPosPath  + "it-pos-maxent.bin");
                POSModel = new POSModel(POSModelIn);
            } catch (IOException e) {
                // Model loading failed, handle the error
                e.printStackTrace();
            } finally {
                if (POSModelIn != null) {
                    try {
                        POSModelIn.close();
                    } catch (IOException e) {
                    }
                }
            }
            POSTaggerME tagger = new POSTaggerME(POSModel);
            String tags[] = tagger.tag(tokens);
            
            // Get the appropriate stemmer
            SnowballStemmer stemmer = SnowballStemmerSelector.
                    getStemmerForLanguage(component.getLanguage());
            
            if (stemmer == null)
                throw new RuntimeException(
                        "Stemmer not available for the language " + 
                                component.getLanguage().getLanguage());
            
            // put the features detected by OpenNLP in the distiller's
            // sentence
            
            for (int i = 0; i < tokens.length; i++) {
                Token t = new Token(tokens[i]);                
                t.setPoS(tags[i]);
                
                stemmer.setCurrent(tokens[i]);
                if (stemmer.stem())
                    t.setStem(stemmer.getCurrent());
                else
                    t.setStem(tokens[i]);
                sentence.addToken(t);
                
            } // for 
            
            ((DocumentComposite)component).addComponent(sentence);
            
        } // for (String sentenceString : sentences)
    } // annotate
}
