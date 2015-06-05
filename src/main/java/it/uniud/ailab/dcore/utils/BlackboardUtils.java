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
package it.uniud.ailab.dcore.utils;

import it.uniud.ailab.dcore.annotation.Annotation;
import it.uniud.ailab.dcore.annotation.FeatureAnnotation;
import it.uniud.ailab.dcore.annotation.InferenceAnnotation;
import it.uniud.ailab.dcore.annotation.TextAnnotation;
import it.uniud.ailab.dcore.annotation.component.WikipediaInferenceAnnotator;
import it.uniud.ailab.dcore.engine.Blackboard;
import it.uniud.ailab.dcore.engine.Evaluator;
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

        System.out.println("** SCORES **");

        Collection<Gram> grams = b.getGrams();
        Map<Gram, Double> scoredGrams = new HashMap<>();

        for (Gram g : grams) {
            scoredGrams.put(g, g.getFeature(Evaluator.SCORE));
        }

        Stream<Map.Entry<Gram, Double>> ordered
                = scoredGrams.entrySet().stream().sorted(
                        Collections.reverseOrder(Map.Entry.comparingByValue())).limit(20);

        for (Map.Entry<Gram, Double> scoredGram : ordered.collect(Collectors.toList())) {
            System.out.print(String.format("%-24s", scoredGram.getKey().getSignature()));
            System.out.print("\t\t");
            for (FeatureAnnotation f : scoredGram.getKey().getFeatures()) {
                System.out.print(String.format("%-12s:%8.3f ; ", f.getAnnotator(), f.getValue()));
            }

            if (printAnnotations) {

                List<TextAnnotation> ann = new ArrayList<TextAnnotation>();
                for (Token t : scoredGram.getKey().getTokens()) {
                    ann.addAll(t.getAnnotations());
                }

                System.out.println();
                System.out.print(String.format("%-24s", " "));

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
