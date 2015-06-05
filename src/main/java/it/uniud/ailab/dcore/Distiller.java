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

import it.uniud.ailab.dcore.engine.Annotator;
import it.uniud.ailab.dcore.engine.Blackboard;
import it.uniud.ailab.dcore.engine.Evaluator;
import it.uniud.ailab.dcore.engine.NGramGenerator;
import it.uniud.ailab.dcore.engine.PreProcessor;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.annotation.FeatureAnnotation;
import it.uniud.ailab.dcore.annotation.component.WikipediaInferenceAnnotator;
import it.uniud.ailab.dcore.persistence.Gram;

/**
 * The keyphrase extractor object.
 *
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public class Distiller {

    // all these fields will be injected via setter method
    private Annotator languageDetector;
    private PreProcessor[] preProcessors;
    private NGramGenerator[] gramGenerators;
    private Evaluator evaluator;
    private String locale;
    
    // the blackboard that will contain the text and will be returned at last
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
    
    /**
     * Sets the locale in which the text extraction will be performed, using
     * "auto" for auto-detection of the locale of the IETF formatted language
     * tag if manual locale setting is desired. For example, wiring "en-US" will
     * set the locale to English.
     * 
     * @param locale 
     */
    @Required
    public void setLocale(String locale) throws IllegalArgumentException {
        
        if (!locale.equals(""))
        {
            // Check if the language tag is valid by generating a dummy locale
            try {
                Locale detectedLocale = Locale.forLanguageTag(locale);
            } catch (Exception e) {
                throw new IllegalArgumentException("Unsupported language tag: "+locale);
            }
        }
        
        this.locale = locale;
    }
    
    /**
     * Perform the extraction of keyphrases of a specified string.
     * 
     * @param text the text to distill.
     */
    public Blackboard extract(String text){
        
        blackboard = new Blackboard();
                
        blackboard.createDocument(text);
        boolean singleLanguage = true;
        
        // *** STEP 1 *** //
        // Language recognition. 
        
        languageDetector.annotate(blackboard,blackboard.getStructure());
        
        // *** STEP 2 *** //
        // Splitting and annotation.        
        
        // Now the language detector may have detected one or more languages in
        // the document, so it may or may have not splitted the document in one
        // or more subsections.
        
        singleLanguage = 
                blackboard.getStructure().getComponents().isEmpty();
        
        if (!singleLanguage) {
            // complex case: the text has been splitted
            // we have to iterate between the different components, 
            // iterate between the different preprocessors and apply the
            // one with the matching language
            
            for (DocumentComponent c :
                    blackboard.getStructure().getComponents()) {
                for (PreProcessor p : preProcessors) {
                    
                    if (p.getLanguage().equals(c.getLanguage())) {
                        p.generateAnnotations(blackboard,c);
                        break;
                    }
                }

            }
        } else { // simple case: text has not been splitted
            // apply the preprocessor over the root element.
            for (PreProcessor p : preProcessors) {
                if (p.getLanguage().equals(blackboard.getStructure().getLanguage())) {
                    p.generateAnnotations(blackboard,blackboard.getStructure());
                    break;
                }
            }
        }
        
//        System.out.println("Detected sentences: " + BlackBoard.Instance().getStructure().getComponents().size());
//
//        System.out.println(getAnnotatedComponent(BlackBoard.Instance().getStructure()));
        
        // *** STEP 3 *** //
        // N-gram generation.
        
        if (!singleLanguage) {
            // same as step 2
            
            // warning: it will be used the FIRST n-gram generator that
            // matches the desired language
            
            for (DocumentComponent c : 
                    blackboard.getStructure().getComponents()) {
                for (NGramGenerator g:  gramGenerators) {
                    if (g.getGramLanguages().contains(c.getLanguage())) {
                        g.generateNGrams(blackboard,c);
                        break;
                    }
                }

            }
        } else {
            for (NGramGenerator g : gramGenerators) {
                if (g.getGramLanguages().contains(
                        blackboard.getStructure().getLanguage())) {
                    g.generateNGrams(
                            blackboard,blackboard.getStructure());
                    break;
                }
            }
        }
        
        // *** STEP 4 *** //
        // Evaluation and scoring.
        
        evaluator.Score(blackboard,blackboard.getStructure());
        
        (new WikipediaInferenceAnnotator()).annotate(
                blackboard, blackboard.getStructure());

        return blackboard;
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

}
