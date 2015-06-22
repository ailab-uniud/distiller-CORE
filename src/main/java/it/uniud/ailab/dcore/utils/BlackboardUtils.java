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
package it.uniud.ailab.dcore.utils;

import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.annotation.annotations.InferenceAnnotation;
import it.uniud.ailab.dcore.annotation.annotations.TextAnnotation;
import it.uniud.ailab.dcore.annotation.annotators.WikipediaInferenceAnnotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.annotators.GenericEvaluatorAnnotator;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Token;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Marco
 */
public class BlackboardUtils {

    public static void printScores(Blackboard b, boolean printAnnotations) {

        Collection<Gram> grams = b.getGrams();
        
        System.out.println("");
        System.out.println(String.format(
                "Extraction completed with a grand total of %d grams distilled.",                
                grams.size()));
        System.out.println("");
        
        System.out.println("** SCORES **");

        Map<Gram, Double> scoredGrams = new HashMap<>();

        for (Gram g : grams) {
            scoredGrams.put(g, g.getFeature(GenericEvaluatorAnnotator.SCORE));
        }

        Stream<Map.Entry<Gram, Double>> ordered
                = scoredGrams.entrySet().stream().sorted(
                        Collections.reverseOrder(Map.Entry.comparingByValue())).limit(20);

        for (Map.Entry<Gram, Double> scoredGram : ordered.collect(Collectors.toList())) {
            System.out.print(String.format("%-24s", scoredGram.getKey().getSignature()));           
            for (FeatureAnnotation f : scoredGram.getKey().getFeatures()) {
                System.out.print(String.format("%-12s:%8.3f ; ", f.getAnnotator(), f.getValue()));
            }

            List<TextAnnotation> ann = new ArrayList<TextAnnotation>();

            for (Token t : scoredGram.getKey().getTokens()) {
                ann.addAll(t.getAnnotations());
            }

            if (printAnnotations && !ann.isEmpty()) {

                System.out.println();
                System.out.print(String.format("%-24s", "--Annotations:"));

                for (TextAnnotation a : ann) {
                    System.out.print(String.format("%-12s:\"%-12s\":%-12s ; ",
                            a.getAnnotator(), a.getAnnotatedText(), a.getAnnotation()));
                }
            }

            System.out.println();
        }
    }
    
    public static void printScores(Blackboard b) {
        printScores(b,false);
    }

    public static void printHypernyms(Blackboard b) {
        
        System.out.println("** HYPERNYMS **");
   
        List<InferenceAnnotation> inferences = new ArrayList<>();
        b.getAnnotations(WikipediaInferenceAnnotator.HYPERNYMS)
                .stream().forEach(a -> {
                    inferences.add((InferenceAnnotation) a);
                });

        Map<String, Double> inferenceMap = new HashMap<>();
        for (InferenceAnnotation a : inferences) {
            inferenceMap.put(a.getConcept(), a.getScore());
        }
        
        // sort the entries
        List<Map.Entry<String, Double>> ordered
                = inferenceMap.entrySet().stream()
                        .sorted(
                                Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .limit(20)
                        .collect(Collectors.toList());
        
        for (Map.Entry<String,Double> e : ordered) {
            System.out.println(String.format("%-32s:\t%8.3f ; ", 
                    e.getKey(),e.getValue()));
        }                
    }
    
    public static void printRelated(Blackboard b) {
        
        System.out.println("** RELATED CONCEPTS **");
        
        List<InferenceAnnotation> inferences = new ArrayList<>();
                b.getAnnotations(WikipediaInferenceAnnotator.RELATED)
                .stream().forEach(a -> {
                    inferences.add((InferenceAnnotation)a);
                });
        
        Map<String,Double> inferenceMap = new HashMap<>();
        for (InferenceAnnotation a : inferences) {
            inferenceMap.put(a.getConcept(),a.getScore());
        }
        
        // sort the entries
        List<Map.Entry<String, Double>> ordered
                = inferenceMap.entrySet().stream()
                        .sorted(
                                Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .limit(20)
                        .collect(Collectors.toList());
        
        for (Map.Entry<String,Double> e : ordered) {
            System.out.println(String.format("%-32s:\t%8.3f ; ", 
                    e.getKey(),e.getValue()));
        }                
    }
    
    public static void printInference(Blackboard b) {
        printHypernyms(b);
        printRelated(b);
    }

}
