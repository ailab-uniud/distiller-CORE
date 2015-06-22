/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
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

import it.uniud.ailab.dcore.annotation.Pipeline;
import it.uniud.ailab.dcore.annotation.annotators.*;
import it.uniud.ailab.dcore.wrappers.external.*;
import java.util.Locale;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A simple factory that generates the default Distiller configuration either
 * via XML configuration file or via Java code. We write this class also as
 * a tutorial for the users of this library, who can learn how to instantiate
 * the Distiller object studying this source code.
 *
 * @author Marco Basaldella
 */
public class DistillerFactory {
    
    /**
     * Instantiates a Distiller object using the default configuration and
     * returns it.
     *
     * @return a Distiller ready to work.
     */
    public static Distiller getDefault() {
        return getDefaultXML();
    }
    
    /**
     * Instantiates a Distiller object using the default configuration and
     * returns it.
     *
     * @return a Distiller ready to work.
     */
    private static Distiller getDefaultXML() {
        ApplicationContext context = new ClassPathXmlApplicationContext("default.xml");
        return (Distiller) context.getBean("distiller");
    }
    
    private static Distiller getDefaultCode() {
        Distiller d = new Distiller();
        
        // set the language detector tool
        d.setLanguageDetector(new CybozuLanguageDetectorAnnotator());
        
        // build the pipeline
        Pipeline p = new Pipeline();
        // split the text
        p.addAnnotator(new OpenNlpBootstrapperAnnotator());
        // add wikipedia tags to tokens
        p.addAnnotator(new TagMeTokenAnnotator());
        // generate ngrams
        p.addAnnotator(new SimpleNGramGeneratorAnnotator());
        // annotate ngrams
        p.addAnnotator(new StatisticalAnnotator());
        p.addAnnotator(new TagMeGramAnnotator());
        p.addAnnotator(new SyuzhetAnnotator());
        // evaluate ngram features
        
        LinearEvaluatorAnnotator evaluator = new LinearEvaluatorAnnotator();
        evaluator.addWeight(StatisticalAnnotator.DEPTH, 0.15);
        evaluator.addWeight(StatisticalAnnotator.HEIGHT, 0.25);
        evaluator.addWeight(StatisticalAnnotator.LIFESPAN, 0.1);
        evaluator.addWeight(StatisticalAnnotator.FREQUENCY, 0.1);  
        evaluator.addWeight(GenericNGramGeneratorAnnotator.NOUNVALUE,0.3);
        evaluator.addWeight(GenericWikipediaAnnotator.WIKIFLAG, 0.1);
       
        p.addAnnotator(evaluator);
        
        // infer concepts
        p.addAnnotator(new WikipediaInferenceAnnotator());
        // filter results
        p.addAnnotator(new SkylineGramFilterAnnotator());
                
        d.addPipeline(Locale.ENGLISH, p);
        d.addPipeline(Locale.ITALIAN, p);
        
        return d;
    }
    
}
