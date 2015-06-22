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
package it.uniud.ailab.dcore;

import it.uniud.ailab.dcore.DistilledOutput.*;
import it.uniud.ailab.dcore.annotation.InferenceAnnotation;
import it.uniud.ailab.dcore.annotation.Pipeline;
import it.uniud.ailab.dcore.annotation.UriAnnotation;
import it.uniud.ailab.dcore.engine.Annotator;
import it.uniud.ailab.dcore.engine.Blackboard;
import it.uniud.ailab.dcore.engine.Evaluator;
import it.uniud.ailab.dcore.engine.NGramGenerator;
import it.uniud.ailab.dcore.engine.PreProcessor;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.annotation.component.WikipediaInferenceAnnotator;
import static it.uniud.ailab.dcore.annotation.generic.WikipediaAnnotator.WIKIFLAG;
import it.uniud.ailab.dcore.persistence.Gram;
import java.util.HashMap;
import java.util.Map;

/**
 * The keyphrase extractor object.
 *
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public class Distiller {

    // all these fields will be injected via setter method
    
    /**
     * The first step of the actual pipeline: this annotator should
     * decide in which language the document is written.
     */
    private Annotator languageDetector;
    
    /**
     * The annotation pipelines. There should be one for language.
     */
    private Map<Locale,Pipeline> pipelines = new HashMap<>();
    
    
    
    private PreProcessor[] preProcessors;
    private NGramGenerator[] gramGenerators;
    private Evaluator evaluator;
    private Locale documentLocale = null;

    // the blackboard that will contain the text all its annotations.
    private Blackboard blackboard;

    
    
    @Required
    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Required
    public void setGramGenerators(NGramGenerator gramGenerators[]) {
        this.gramGenerators = gramGenerators;
    }

    @Required
    public void setPreProcessors(PreProcessor[] preProcessors) {
        this.preProcessors = preProcessors;
    }

    @Required
    public void setLanguageDetector(Annotator languageDetector) {
        this.languageDetector = languageDetector;
    }

    @Required
    public void setPipelines(HashMap<Locale,Pipeline> pipelines) {
        this.pipelines = pipelines;
    }
    
    public void addPipeline(Locale locale,Pipeline pipeline) {
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
     * Perform the extraction of keyphrases of a specified string, and returns
     * the blackboard filled with document and annotations.
     *
     * @param text the text to distill.
     * @return the blackboard filled with the processed text
     */
    public Blackboard distillToBlackboard(String text) {

        blackboard = new Blackboard();
        blackboard.createDocument(text);
        
        if (documentLocale == null)
            // if no language has been set, automatically detect it.
            if (languageDetector != null)
                languageDetector.annotate(blackboard, blackboard.getStructure());
            else
                // but if there's no language and no language detector, 
                // throw an exception.
                throw new RuntimeException(
                        "I can't decide the language of the document: no language is specified and no language detector is set.");
        else 
            // set the pre-determined language
            blackboard.getStructure().setLanguage(documentLocale);

        Pipeline pipeline = pipelines.get(blackboard.getStructure().getLanguage());
        
        if (pipeline == null) {
            throw new RuntimeException("No pipeline for the language " + 
                    blackboard.getStructure().getLanguage().getLanguage());
        }
        
        for (Annotator annotator : pipeline.getAnnotators()) {
            annotator.annotate(blackboard,blackboard.getStructure());
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
                    it.uniud.ailab.dcore.engine.Evaluator.SCORE));
            
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
            InferenceAnnotation originalRelatedConcept = 
                    (InferenceAnnotation) blackboard.getAnnotations(
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

    // <editor-fold desc="Support methods">
    /**
     * Instantiates a Distiller object using the default configuration and
     * returns it.
     *
     * @return a Distiller ready to work.
     */
    public static Distiller getDefault() {
        ApplicationContext context = new ClassPathXmlApplicationContext("default.xml");
        return (Distiller) context.getBean("distiller");
    }
    // </editor-fold>

    public Blackboard getBlackboard() {
        return blackboard;
    }

}
