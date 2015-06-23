/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 * 
 * 	This file is part of the Distiller-CORE library.
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
package it.uniud.ailab.dcore;

import static it.uniud.ailab.dcore.annotation.annotators.GenericWikipediaAnnotator.WIKIFLAG;
import it.uniud.ailab.dcore.annotation.annotators.WikipediaInferenceAnnotator;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.DistilledOutput.*;
import it.uniud.ailab.dcore.annotation.annotations.InferenceAnnotation;
import it.uniud.ailab.dcore.annotation.Pipeline;
import it.uniud.ailab.dcore.annotation.annotations.UriAnnotation;
import it.uniud.ailab.dcore.annotation.Annotator;
import static it.uniud.ailab.dcore.utils.AnnotatorUtils.getAnnotatorSimpleName;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Required;


import java.util.HashMap;
import java.util.Map;

/**
 * The information extractor object. This is the class that runs the different
 * annotation pipelines.
 * 
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public class Distiller {

    // all these fields will be injected via setter method
    /**
     * The first step of the actual pipeline: this annotator should decide in
     * which language the document is written.
     */
    private Annotator languageDetector;

    /**
     * The annotation pipelines. There should be one for language.
     */
    private Map<Locale, Pipeline> pipelines = new HashMap<>();

    /**
     * The language of the document.
     */
    private Locale documentLocale = null;

    /**
     * The Object that will contain the text all its annotations.
     */
    private Blackboard blackboard;
    
    /**
     * The verbose mode flag. If the flag is set to true, Distiller will
     * print information on the work he's doing on stdout. Default is false.
     */
    private boolean verbose = false;

    /**
     * Sets the language detector.
     *
     * @param languageDetector the language detector.
     */
    @Required
    public void setLanguageDetector(Annotator languageDetector) {
        this.languageDetector = languageDetector;
    }

    /**
     * Set the annotation pipelines.
     *
     * @param pipelines the annotation pipelines.
     */
    @Required
    public void setPipelines(HashMap<Locale, Pipeline> pipelines) {
        this.pipelines = pipelines;
    }

    /**
     * Adds a pipeline to the pipelines map.
     *
     * @param locale the language that the pipeline will process
     * @param pipeline the pipeline to add
     */
    public void addPipeline(Locale locale, Pipeline pipeline) {
        pipelines.put(locale, pipeline);
    }

    /**
     * Sets the locale in which the text extraction will be performed. The value
     * should be null for auto-detection of the locale of the IETF formatted
     * language tag if manual locale setting is desired. For example, passing
     * "en-US" will set the locale to English. An empty locale equals to the
     * "auto" parameter.
     *
     * @param locale the locale to use while processing the text.
     */
    public void setLocale(Locale locale) throws IllegalArgumentException {
        this.documentLocale = locale;
    }

    /**
     * Gets the blackboard.
     * 
     * @return the blackboard.
     */
    public Blackboard getBlackboard() {
        return blackboard;
    }

    /**
     * Sets the verbose mode of the Distiller.
     * 
     * @param verbose true to display information of the distillation process;
     * false for silent distillation.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    
    
    /**
     * Perform the extraction of keyphrases of a specified string, and returns
     * the blackboard filled with document and annotations.
     *
     * @param text the text to distill.
     * @return the blackboard filled with the processed text
     */
    public Blackboard distillToBlackboard(String text) {

        blackboard = new Blackboard();
        blackboard.createDocument(text);

        if (documentLocale == null) // if no language has been set, automatically detect it.
        {
            if (languageDetector != null) {
                languageDetector.annotate(blackboard, blackboard.getStructure());
            } else // but if there's no language and no language detector, 
            // throw an exception.
            {
                throw new DistillerException(
                        "I can't decide the language of the document: no language is specified and no language detector is set.");
            }
        } else // set the pre-determined language
        {
            blackboard.getStructure().setLanguage(documentLocale);
        }

        Pipeline pipeline = pipelines.get(blackboard.getStructure().getLanguage());

        if (pipeline == null) {
            throw new DistillerException("No pipeline for the language "
                    + blackboard.getStructure().getLanguage().getLanguage());
        }

        for (Annotator annotator : pipeline.getAnnotators()) {
            
            if (verbose) {
                System.out.println(String.format("Running %s...",
                        getAnnotatorSimpleName(annotator)));
            }
            
            annotator.annotate(blackboard, blackboard.getStructure());
        }
        
        if (verbose) {
            System.out.println("Extraction complete!");
            System.out.println();
        }

        return blackboard;
    }

    /**
     * Perform the extraction of keyphrases of a specified string, and returns a
     * developer-friendly object that allows quick access to the extracted
     * information.
     *
     * @param text the text to extract
     * @return the distilled output
     */
    public DistilledOutput distill(String text) {

        DistilledOutput output = new DistilledOutput();

        output.setOriginalText(text);

        distillToBlackboard(text);

        output.setDetectedLanguage(blackboard.getStructure().
                getLanguage().getLanguage());

        // Copy the grams
        output.initializeGrams(blackboard.getGrams().size());

        for (int i = 0; i < output.getGrams().length; i++) {
            DetectedGram gram = output.getGrams()[i];
            Gram originalGram = blackboard.getGrams().get(i);
            gram.setSurface(originalGram.getSurface());
            gram.setKeyphraseness(originalGram.getFeature(
                    it.uniud.ailab.dcore.annotation.annotators.GenericEvaluatorAnnotator.SCORE));

            UriAnnotation wikiAnn = (UriAnnotation) originalGram.getAnnotation(WIKIFLAG);
            if (wikiAnn != null) {
                gram.setConceptName(wikiAnn.getSurface());
                gram.setConceptPath(wikiAnn.getUri().toASCIIString());
            }
        }

        output.initializeRelatedConcepts(blackboard.getAnnotations(
                WikipediaInferenceAnnotator.RELATED).size());

        for (int i = 0; i < output.getRelatedConcepts().length; i++) {
            InferredConcept related = output.getRelatedConcepts()[i];
            InferenceAnnotation originalRelatedConcept
                    = (InferenceAnnotation) blackboard.getAnnotations(
                            WikipediaInferenceAnnotator.RELATED).get(i);

            related.setConcept(originalRelatedConcept.getConcept());
            related.setConceptPath(originalRelatedConcept.getConcept());
            related.setScore(originalRelatedConcept.getScore());
        }

        output.initializeHypernyms(blackboard.getAnnotations(
                WikipediaInferenceAnnotator.HYPERNYMS).size());

        for (int i = 0; i < output.getHypernyms().length; i++) {
            InferredConcept hypernym = output.getHypernyms()[i];
            InferenceAnnotation originalHypernym = (InferenceAnnotation) blackboard.getAnnotations(
                    WikipediaInferenceAnnotator.HYPERNYMS).get(i);

            hypernym.setConcept(originalHypernym.getConcept());
            hypernym.setConceptPath(originalHypernym.getConcept());
            hypernym.setScore(originalHypernym.getScore());
        }

        output.setExtractionCompleted(true);

        return output;
    }
}
