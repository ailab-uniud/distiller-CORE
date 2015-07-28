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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Map<String,String> loadInputDocuments() {
        
        Map<String,String> documents = new HashMap<>();
        
        try {
            File[] dir = new File(this.getGoldStandardPath() + "/test").listFiles();
            Arrays.sort(dir);
            
            for (File f : dir) {
                String document = String.join(
                        " ",
                        Files.readAllLines(
                                f.toPath(),StandardCharsets.UTF_8));
                
                String docName = f.getName().substring(0,f.getName().indexOf("."));
                
                documents.put(docName,document);
            }
        } catch (IOException ex) {
            Logger.getLogger(SemEval2010.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return documents;
    }

    @Override
    public Map<String,String[]> loadGoldKeyphrases() {

        // default is a zero-sized output array
        Map<String,String[]> keyphrases = null;

        try {
            
            File gold = new File(this.getGoldStandardPath() + "/test_answer/test.combined.stem.final");
            
            Map<String,String[]> buffer = new HashMap<>();

            // navigate through the lines
            try (Stream<String> lines = Files.lines(gold.toPath(), StandardCharsets.UTF_8)) {
                
                for (String line : (Iterable<String>) lines::iterator) {
                    List<String> documentKPs = new ArrayList<>();
                    // remove the name of the document
                    String kpLine = line.substring(line.indexOf(':')+2);
                    for (String kp: kpLine.split(",")) 
                        documentKPs.add(kp);
                    
                    buffer.put(line.substring(0,line.indexOf(':') - 1),
                            documentKPs.toArray(new String[documentKPs.size()]));
                }
            }
            
            // copy the gold standard to the output array
            keyphrases = buffer;

        } catch (IOException ex) {
            Logger.getLogger(SemEval2010.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Unable to load gold standard",ex);
        }

        return keyphrases;
    }
    
}
