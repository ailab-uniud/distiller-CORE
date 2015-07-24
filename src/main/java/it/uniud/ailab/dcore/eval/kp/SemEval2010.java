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
package it.uniud.ailab.dcore.eval.kp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Evaluator for the SemEval 2010 keyphrase extraction task.
 *
 * @author Marco Basaldella
 */
public class SemEval2010 extends KeyphraseEvaluator {

    public SemEval2010(String goldStandardPath) {
        super(goldStandardPath);
    }

    @Override
    public String[] loadInputDocuments() {
        
        List<String> documents = new ArrayList<>();
        
        try {
            File dir = new File(this.getGoldStandardPath() + "/test");
            
            for (File f : dir.listFiles()) {
                String document = new String(Files.readAllBytes(f.toPath()));
                documents.add(document);
            }
        } catch (IOException ex) {
            Logger.getLogger(SemEval2010.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return documents.toArray(new String[documents.size()]);
    }

    @Override
    public String[][] loadGoldKeyphrases() {

        // default is a zero-sized output array
        String[][] keyphrases = new String[0][];

        try {
            
            File gold = new File(this.getGoldStandardPath() + "/test_answer/test.combined.stem.final");
            
            List<List<String>> buffer = new ArrayList<>();

            // navigate through the lines
            try (Stream<String> lines = Files.lines(gold.toPath(), StandardCharsets.UTF_8)) {
                
                for (String line : (Iterable<String>) lines::iterator) {
                    List<String> documentKPs = new ArrayList<>();
                    // remove the name of the document
                    line = line.substring(line.indexOf(':')+2);
                    for (String kp: line.split(",")) 
                        documentKPs.add(kp);
                    
                    buffer.add(documentKPs);
                }
            }
            
            // copy the gold standard to the output array
            
            keyphrases = new String[buffer.size()][];
            for (int i = 0; i < keyphrases.length; i++) {
                keyphrases[i] = buffer.get(i).toArray(new String[buffer.get(i).size()]);
            }

        } catch (IOException ex) {
            Logger.getLogger(SemEval2010.class.getName()).log(Level.SEVERE, null, ex);
        }

        return keyphrases;
    }
    
}
