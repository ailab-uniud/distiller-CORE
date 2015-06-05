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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.commons.io.FileUtils;
import org.tartarus.snowball.SnowballStemmer;
import it.uniud.ailab.dcore.annotation.AnnotationException;
import it.uniud.ailab.dcore.engine.Annotator;
import it.uniud.ailab.dcore.engine.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.DocumentComposite;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.SnowballStemmerSelector;

/**
 * A bootstrapper annotator for the English language developed using the Apache
 * OpenNLP library. The annotator splits the document, tokenizes it and performs
 * POS tagging; the stemming is performed using the Snowball stemmer instead.
 *
 * This annotator can support every language the OpenNLP library supports.
 *
 * @author Marco Basaldella
 */
public class OpenNlpBootstrapper implements Annotator {

    private static final Logger LOG = Logger.getLogger(Annotator.class.getName());

    /**
     * Variable that contains the database paths of the models for the various
     * OpenNLP component. The name of the entries should match the naming
     * convention of the OpenNLP toolkit, i.e. ($lang)-($tool), e.g. as
     * "en-token".
     */
    private final Map<String, String> databasePaths
            = new HashMap<>();

    // Caches for the models used, so subsequent calls of the annotator
    // don't have to reload the models
    private static Map<String, SentenceModel> sentenceModelsCache
            = new HashMap<>();

    private static Map<String, TokenizerModel> tokenizerModelsCache
            = new HashMap<>();

    private static Map<String, POSModel> posModelsCache
            = new HashMap<>();

    /**
     * Annotates the document using the Apache OpenNLP tools.
     *
     * For more info and a CoreNLP tutorial, please refer to
     * <a href="http://opennlp.apache.org/documentation/manual/opennlp.html">
     * the official Apache documentation</a>.
     *
     * For a list of models that are ready to use with CoreNLP, please see
     * <a href="http://opennlp.sourceforge.net/models-1.5/">
     * this model list on the Apache OpenNLP website</a>.
     *
     * For the Italian language, we use Andrea Ciapetti's models, which are
     * available on
     * <a href="http://github.com/aciapetti/opennlp-italian-models">
     * his GitHub page</a>.
     *
     * @param component the component to annotate.
     */
    @Override
    public void annotate(Blackboard blackboard,DocumentComponent component) {

        if (databasePaths.entrySet().isEmpty()) {
            SetDefaultModels();
        }

        // Download the models (if necessary)
        PrepareModels();

        // Language tag used to retrieve the datasets
        String langTag = component.getLanguage().getLanguage();

        // Split the text into sentences
        SentenceModel sentModel = getSentenceModel(langTag + "-sent");

        SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentModel);
        String sentences[] = sentenceDetector.sentDetect(component.getText());

        // Get the right models
        TokenizerModel tokenModel = getTokenizerModel(langTag + "-token");
        POSModel POSModel = getPOSTaggerModel(langTag + "-pos-maxent");

        // Iterate through sentences and produce the distilled objects, 
        // i.e. a sentence object with pos-tagged and stemmed tokens.
        for (String sentenceString : sentences) {

            // the distilled sentence object
            Sentence sentence = new Sentence(sentenceString);
            sentence.setLanguage(component.getLanguage());

            // Tokenize the sentence
            Tokenizer tokenizer = new TokenizerME(tokenModel);
            String tokens[] = tokenizer.tokenize(sentenceString);

            // POS tag the tokens
            POSTaggerME tagger = new POSTaggerME(POSModel);
            String tags[] = tagger.tag(tokens);

            // Get the appropriate stemmer
            SnowballStemmer stemmer = SnowballStemmerSelector.
                    getStemmerForLanguage(component.getLanguage());

            if (stemmer == null) {
                throw new AnnotationException(this,
                        "Stemmer not available for the language "
                        + component.getLanguage().getLanguage());
            }

            // put the features detected by OpenNLP in the distiller's
            // sentence
            for (int i = 0; i < tokens.length; i++) {
                Token t = new Token(tokens[i]);
                t.setPoS(tags[i]);

                stemmer.setCurrent(tokens[i]);
                if (stemmer.stem()) {
                    t.setStem(stemmer.getCurrent());
                } else {
                    t.setStem(tokens[i]);
                }
                sentence.addToken(t);

            } // for 

            ((DocumentComposite) component).addComponent(sentence);

        } // for (String sentenceString : sentences)
    } // annotate

    // <editor-fold desc="model loading">
    /**
     * Public property to override the default models; the keys should be in the
     * format ($lang)-($tool), as for example "en-token".
     *
     * @param paths the paths of the OpenNLP models to be used.
     */
    public void setModelPaths(Map<String, String> paths) {
        databasePaths.clear();
        databasePaths.putAll(paths);
    }

    /**
     * Sets the default URLs for some known OpenNLP models.
     */
    private void SetDefaultModels() {
        databasePaths.put("en-sent", "http://opennlp.sourceforge.net/models-1.5/en-sent.bin");
        databasePaths.put("en-token", "http://opennlp.sourceforge.net/models-1.5/en-token.bin");
        databasePaths.put("en-pos-maxent", "http://opennlp.sourceforge.net/models-1.5/en-pos-maxent.bin");
        databasePaths.put("pt-sent", "http://opennlp.sourceforge.net/models-1.5/pt-sent.bin");
        databasePaths.put("pt-token", "http://opennlp.sourceforge.net/models-1.5/pt-token.bin");
        databasePaths.put("pt-pos-maxent", "http://opennlp.sourceforge.net/models-1.5/pt-pos-maxent.bin");
        databasePaths.put("it-sent", "https://github.com/aciapetti/opennlp-italian-models/blob/master/models/it/it-sent.bin?raw=true");
        databasePaths.put("it-token", "https://github.com/aciapetti/opennlp-italian-models/blob/master/models/it/it-token.bin?raw=true");
        databasePaths.put("it-pos-maxent", "https://github.com/aciapetti/opennlp-italian-models/blob/master/models/it/it-pos-maxent.bin?raw=true");
    }

    /**
     * Checks if the database entry for the POStagger are local or web resources
     * and downloads the online ones.
     *
     */
    private void PrepareModels() {

        Map<String, String> correctPaths = new HashMap<>();

        for (Map.Entry e : databasePaths.entrySet()) {

            String entryKey = (String) e.getKey();
            String entryValue = (String) e.getValue();

            try {

                URL url = new URL((String) e.getValue());
                // if we're dealing with a local file, then
                // we don't care and continue.
                if (isLocalFile(url)) {
                    //LOG.log(Level.INFO, "Using {0} as local path...", e.getValue());
                    continue;
                }

                // Download the new file and put it in a local folder
                String newFileName = "OpenNLPmodels/"
                        + url.getPath().substring(url.getPath().lastIndexOf("/") + 1,
                                url.getPath().length());
                ;

                // Check if the file already exists (i.e. we have probably
                // downloaded it before). If exists, then we're happy and 
                // don't download anything
                File f = new File(newFileName);
                if (f.exists()) {
                    //LOG.log(Level.INFO, "Using {0} as local cache...", newFileName);
                    correctPaths.put(entryKey, f.getCanonicalPath());
                    continue;
                }

                //LOG.log(Level.INFO, "Starting to download {0}", url.toString());
                FileUtils.copyURLToFile(url, f);
                //LOG.log(Level.INFO, "OpenNLP database saved in {0}", f.getCanonicalPath());

                correctPaths.put(entryKey, f.getAbsolutePath());

            } catch (MalformedURLException ex) {
                //LOG.log(Level.INFO, "Using {0} as local path...", e.getValue());
            } catch (IOException ex) {
                //LOG.log(Level.SEVERE, "Savefile error", ex);
                throw new AnnotationException(this,"Failed to download " + e.getValue(),ex);
            } finally {

                // if something went wrong, put the default value.
                if (!correctPaths.containsKey(entryKey)) {
                    correctPaths.put(entryKey, entryValue);
                }
            }
        }

        // update the old map with the new values
        databasePaths.clear();
        databasePaths.putAll(correctPaths);

    }

    // </editor-fold>
    // <editor-fold desc="model caching">
    /**
     * Loads a sentence model or retrieves it from cache if has been already
     * loaded before.
     *
     * @param modelId the model to retrieve
     * @return the loaded model
     */
    public SentenceModel getSentenceModel(String modelId) {

        // if the model has not already been loaded, cache it
        if (!sentenceModelsCache.containsKey(modelId)) {

            // Split the text into sentences
            InputStream sentModelIn = null;
            SentenceModel sentModel = null;
            String sentPath = "";

            try {
                sentPath = databasePaths.get(modelId);
                sentModelIn = new FileInputStream(sentPath);
                sentModel = new SentenceModel(sentModelIn);
            } catch (IOException e) {
                throw new AnnotationException(this,
                        "Error while loading the model file \""
                        + sentPath + "\".",
                        e);
            } catch (NullPointerException e) {
                throw new AnnotationException(this,
                        "Error while looking for the model \""
                        + modelId + "\".",
                        e);
            } finally {
                if (sentModelIn != null) {
                    try {
                        sentModelIn.close();
                    } catch (IOException e) {
                        throw new AnnotationException(this,
                                "Error while loading the model file '\""
                                + modelId + "\".",
                                e);
                    }
                }
            }
            sentenceModelsCache.put(modelId, sentModel);
            return sentModel;
        }
        return sentenceModelsCache.get(modelId);
    }
    
    /**
     * Loads a tokenizer model or retrieves it from cache if has been already
     * loaded before.
     *
     * @param modelId the model to retrieve
     * @return the loaded model
     */
    public TokenizerModel getTokenizerModel(String modelId) {

        // if the model has not already been loaded, cache it
        if (!tokenizerModelsCache.containsKey(modelId)) {

            // Split the text into sentences
            InputStream tokenModelIn = null;
            TokenizerModel tokenizerModel = null;
            String sentPath = "";

            try {
                sentPath = databasePaths.get(modelId);
                tokenModelIn = new FileInputStream(sentPath);
                tokenizerModel = new TokenizerModel(tokenModelIn);
            } catch (IOException e) {
                throw new AnnotationException(this,
                        "Error while loading the model file \""
                        + sentPath + "\".",
                        e);
            } catch (NullPointerException e) {
                throw new AnnotationException(this,
                        "Error while looking for the model \""
                        + modelId + "\".",
                        e);
            } finally {
                if (tokenModelIn != null) {
                    try {
                        tokenModelIn.close();
                    } catch (IOException e) {
                        throw new AnnotationException(this,
                                "Error while loading the model file '\""
                                + modelId + "\".",
                                e);
                    }
                }
            }
            tokenizerModelsCache.put(modelId, tokenizerModel);
            return tokenizerModel;
        }
        return tokenizerModelsCache.get(modelId);
    }
    
    /**
     * Loads a POStagger model or retrieves it from cache if has been already
     * loaded before.
     *
     * @param modelId the model to retrieve
     * @return the loaded model
     */
    public POSModel getPOSTaggerModel(String modelId) {

        // if the model has not already been loaded, cache it
        if (!posModelsCache.containsKey(modelId)) {

            // Split the text into sentences
            InputStream POSModelIn = null;
            POSModel POSModel = null;
            String sentPath = "";

            try {
                sentPath = databasePaths.get(modelId);
                POSModelIn = new FileInputStream(sentPath);
                POSModel = new POSModel(POSModelIn);
            } catch (IOException e) {
                throw new AnnotationException(this,
                        "Error while loading the model file \""
                        + sentPath + "\".",
                        e);
            } catch (NullPointerException e) {
                throw new AnnotationException(this,
                        "Error while looking for the model \""
                        + modelId + "\".",
                        e);
            } finally {
                if (POSModelIn != null) {
                    try {
                        POSModelIn.close();
                    } catch (IOException e) {
                        throw new AnnotationException(this,
                                "Error while loading the model file '\""
                                + modelId + "\".",
                                e);
                    }
                }
            }
            posModelsCache.put(modelId, POSModel);
            return POSModel;
        }
        return posModelsCache.get(modelId);
    }

    //</editor-fold>
    // <editor-fold desc="utilities">
    private static boolean isLocalFile(URL url) {
        String scheme = url.getProtocol();
        String host = url.getHost();
        return "file".equalsIgnoreCase(scheme)
                && (host == null || "".equals(host));
    }
    // </editor-fold>
}
