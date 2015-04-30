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
package org.uniud.dcore.engine;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Required;
import org.uniud.dcore.persistence.DocumentComponent;
import static org.uniud.dcore.utils.DocumentUtils.getAnnotatedComponent;

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
    private NGramGenerator gramGenerator;
    private Evaluator evaluator;
    private String locale;

    @Required
    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }
    
    @Required
    public void setGramGenerator(NGramGenerator gramGenerator) {
        this.gramGenerator = gramGenerator;
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
    
    public void extract(String text){
                
        BlackBoard.Instance().createDocument(text);
        
        // *** STEP 1 *** //
        // Language recognition. 
        
        languageDetector.annotate(BlackBoard.Instance().getStructure());
        
        // *** STEP 2 *** //
        // Splitting and annotation.        
        
        // Now the language detector may have detected one or more languages in
        // the document, so it may or may have not splitted the document in one
        // or more subsections.
        
        if (BlackBoard.Instance().getStructure().getComponents().size() > 0) {
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

        System.out.println("Detected sentences: " + BlackBoard.Instance().getStructure().getComponents().size());

        System.out.println(getAnnotatedComponent(BlackBoard.Instance().getStructure()));
        
        // *** STEP 3 *** //
        // N-gram generation.
        
        
        // *** STEP 4 *** //
        // Evaluation and scoring.
        

    }

}
