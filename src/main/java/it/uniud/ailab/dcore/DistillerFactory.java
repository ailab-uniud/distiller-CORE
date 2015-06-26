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
import java.io.IOException;
import java.util.Locale;
import org.springframework.beans.factory.BeanDefinitionStoreException;
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
     * Instantiates a Distiller object using the default XML configuration; 
     * if it's not available, uses the safer (but less precise) code configuration,
     * which excludes TagMe and inference from the distillation process.
     *
     * @return a Distiller ready to work.
     */
    public static Distiller getDefault() {
        
        // While the code under this comment may be ugly, it works.
        //
        // It tries to load the default XML config. If the load fails, throws
        // the cause of the failure and immediately catches it.
        //
        // If the config file is not accessible (due to permission, 
        // non-existance, or whatever), the exception is caught and the default
        // code configuration runs.
        //
        // Otherwise, the exception is re-thrown, so that the developer can
        // handle the errors in the config file, which are the other most likely
        // cause of failure of configuration loading falure.
        
        try {
            return getDefaultXML();            
        } catch (BeanDefinitionStoreException bsde) {
            try {
                throw bsde.getCause();
            } catch (IOException ioe) {
                // the configuration file does not exist or is not accessible:
                // load the fallback configuration
                System.out.println(
                        "Distiller config file not found: using fallback configuration");
                return getDefaultCode();
            } catch (Throwable te) {
                throw bsde;
            }            
        }        
    }
    
    /**
     * Instantiates a Distiller object using the default configuration and
     * returns it. Please note that you should create a config.xml file 
     * and copy the content of default.xml inside it to get the framework to work.
     *
     * @return a Distiller ready to work.
     */
    private static Distiller getDefaultXML() {
        ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");
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
        
        // Uncomment the lines below to use the TagMe service
        
        // TagMeTokenAnnotator tagme = new TagMeTokenAnnotator();        
        // tagme.setApiKey("INSERT KEY HERE");        
        // p.addAnnotator(tagme);
        
        // generate ngrams
        p.addAnnotator(new SimpleNGramGeneratorAnnotator());
        // annotate ngrams
        p.addAnnotator(new StatisticalAnnotator());
        
        // Uncomment to use TagMe
        // p.addAnnotator(new TagMeGramAnnotator());
        
        // Uncomment to use the emotional intensity annotator.
        // This way you'll see how different annotators lead to different
        // keyphrases detection
        // p.addAnnotator(new SyuzhetAnnotator());
        
        
        // evaluate ngram features        
        LinearEvaluatorAnnotator evaluator = new LinearEvaluatorAnnotator();
        evaluator.addWeight(StatisticalAnnotator.DEPTH, 0.15);
        evaluator.addWeight(StatisticalAnnotator.HEIGHT, 0.25);
        evaluator.addWeight(StatisticalAnnotator.LIFESPAN, 0.1);
        evaluator.addWeight(StatisticalAnnotator.FREQUENCY, 0.1);  
        evaluator.addWeight(GenericNGramGeneratorAnnotator.NOUNVALUE,0.3);
        evaluator.addWeight(GenericWikipediaAnnotator.WIKIFLAG, 0.1);
       
        p.addAnnotator(evaluator);
        
        // Uncomment the line below to infer concepts.
        // Watch out: the inference process sends lots of requests to Wikipedia, 
        // so it significantly slows down the process
        // p.addAnnotator(new WikipediaInferenceAnnotator());
        // filter results
        p.addAnnotator(new SkylineGramFilterAnnotator());
                
        d.addPipeline(Locale.ENGLISH, p);
        d.addPipeline(Locale.ITALIAN, p);
        
        return d;
    }
    
}
