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
import it.uniud.ailab.dcore.engine.BlackBoard;
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
import it.uniud.ailab.dcore.persistence.Feature;
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
    public void extract(String text){
                
        BlackBoard.Instance().createDocument(text);
        boolean singleLanguage = true;
        
        // *** STEP 1 *** //
        // Language recognition. 
        
        languageDetector.annotate(BlackBoard.Instance().getStructure());
        
        // *** STEP 2 *** //
        // Splitting and annotation.        
        
        // Now the language detector may have detected one or more languages in
        // the document, so it may or may have not splitted the document in one
        // or more subsections.
        
        singleLanguage = 
                BlackBoard.Instance().getStructure().getComponents().isEmpty();
        
        if (!singleLanguage) {
            // complex case: the text has been splitted
            // we have to iterate between the different components, 
            // iterate between the different preprocessors and apply the
            // one with the matching language
            
            for (DocumentComponent c : BlackBoard.Instance().
                    getStructure().getComponents()) {
                for (PreProcessor p : preProcessors) {
                    
                    if (p.getLanguage().equals(c.getLanguage())) {
                        p.generateAnnotations(c);
                        break;
                    }
                }

            }
        } else { // simple case: text has not been splitted
            // apply the preprocessor over the root element.
            for (PreProcessor p : preProcessors) {
                if (p.getLanguage().equals(
                        BlackBoard.Instance().getStructure().getLanguage())) {
                    p.generateAnnotations(BlackBoard.Instance().getStructure());
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
            
            for (DocumentComponent c : BlackBoard.Instance().
                    getStructure().getComponents()) {
                for (NGramGenerator g:  gramGenerators) {
                    if (g.getGramLanguages().contains(c.getLanguage())) {
                        g.generateNGrams(c);
                        break;
                    }
                }

            }
        } else {
            for (NGramGenerator g : gramGenerators) {
                if (g.getGramLanguages().contains(BlackBoard.Instance().getStructure().getLanguage())) {
                    g.generateNGrams(BlackBoard.Instance().getStructure());
                    break;
                }
            }
        }
        
        // *** STEP 4 *** //
        // Evaluation and scoring.
        
        Map<Gram,Double> scores = evaluator.Score(BlackBoard.Instance().getStructure());

        System.out.println("** SCORES **");
        
        Stream<Map.Entry<Gram,Double>> ordered = 
                scores.entrySet().stream().sorted(
                        Collections.reverseOrder(Map.Entry.comparingByValue())).limit(20);        
        
        for (Map.Entry<Gram,Double> e : ordered.collect(Collectors.toList())) {
            System.out.print(String.format("%-24s",e.getKey().getSignature()));
            System.out.print("\t\t");
            for (Feature f : e.getKey().getFeatures()) {
                System.out.print(String.format("%-12s:%8.3f ; ", f.getType(),f.getValue()));
            }
            
//            List<Annotation> ann = new ArrayList<Annotation>();
//            for (Token t : e.getKey().getTokens()) {
//                ann.addAll(t.getAnnotations());
//            }
//            
//            System.out.println();
//            System.out.print(String.format("%-24s"," "));
//            
//            for (Annotation a : ann) {
//                System.out.print(String.format("%-12s:\"%-12s\":%-12s ; ", 
//                        a.getAnnotator(), a.getAnnotatedText(), a.getAnnotation()));
//            }
            
            System.out.println();
        }


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
