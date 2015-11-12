/*
 * Copyright (C) 2015 Artificial Intelligence
 * Laboratory @ University of Udine.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package it.uniud.ailab.dcore.eval.datasets;

import it.uniud.ailab.dcore.eval.GenericDataset;
import it.uniud.ailab.dcore.eval.kp.KeyphraseEvaluator;
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
public class SemEval2010 extends GenericDataset {

    public SemEval2010(String goldStandardPath) {
        super(goldStandardPath);
    }

    @Override
    public Map<String,String> loadTestSet() {
        
        Map<String,String> documents = new HashMap<>();
        
        try {
            File[] dir = new File(datasetPath + "/test").listFiles();
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
    public Map<String,String[]> loadTestAnswers() {

        // default is a zero-sized output array
        Map<String,String[]> keyphrases = null;

        try {
            
            File gold = new File(datasetPath + "/test_answer/test.combined.stem.final");
            
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

    @Override
    protected Map<String, String> loadTrainingSet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Map<String, String[]> loadTrainingAnswers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}