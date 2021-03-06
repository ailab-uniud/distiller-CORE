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
import it.uniud.ailab.dcore.utils.FileSystem;
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
import opennlp.tools.stemmer.PorterStemmer;

/**
 * Evaluator for the SemEval 2010 keyphrase extraction task.
 *
 * @author Marco Basaldella
 */
public class SemEval2010 extends GenericDataset {

    public SemEval2010(String goldStandardPath) {
        super(goldStandardPath, "SEMEVAL2010");
    }

    @Override
    public Map<String, String> loadTestSet() {

        Map<String, String> documents = new HashMap<>();

        try {
            File[] dir = new File(datasetPath + 
                    FileSystem.getSeparator() +
                    "test").listFiles();
            Arrays.sort(dir);

            for (File f : dir) {
                String document = String.join(
                        " ",
                        Files.readAllLines(
                                f.toPath(), StandardCharsets.UTF_8));

                String docName = f.getName() ;//.substring(0, f.getName().indexOf("."));

                documents.put(docName, document);
            }
        } catch (IOException ex) {
            Logger.getLogger(SemEval2010.class.getName()).log(Level.SEVERE, null, ex);
        }

        return documents;
    }

    @Override
    public Map<String, String[]> loadTestAnswers() {

        // default is a zero-sized output array
        Map<String, String[]> keyphrases = null;

        try {

            File gold = new File(datasetPath + 
                    FileSystem.getSeparator() +
                    "test_answer" +
                    FileSystem.getSeparator() +
                    "test.combined.stem.final");

            Map<String, String[]> buffer = new HashMap<>();

            // navigate through the lines
            try (Stream<String> lines = Files.lines(gold.toPath(), StandardCharsets.UTF_8)) {

                for (String line : (Iterable<String>) lines::iterator) {
                    List<String> documentKPs = new ArrayList<>();
                    // remove the name of the document
                    String kpLine = line.substring(line.indexOf(':') + 2);
                    for (String kp : kpLine.split(",")) {
                        documentKPs.add(kp);
                    }

                    buffer.put(line.substring(0, line.indexOf(':') - 1)
                            +".txt.final"
                            ,
                            documentKPs.toArray(new String[documentKPs.size()]));
                }
            }

            // copy the gold standard to the output array
            keyphrases = buffer;

        } catch (IOException ex) {
            Logger.getLogger(SemEval2010.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Unable to load gold standard", ex);
        }

        return keyphrases;
    }

    @Override
    protected Map<String, String> loadTrainingSet() {
        Map<String, String> documents = new HashMap<>();

        try {
            File[] dir = new File(datasetPath + 
                    FileSystem.getSeparator() +
                    "train").listFiles();
            Arrays.sort(dir);

            for (File f : dir) {

                // skip files containing results
                if (!f.getName().startsWith("train")) {

                    String document = String.join(
                            " ",
                            Files.readAllLines(
                                    f.toPath(), StandardCharsets.UTF_8));

                    String docName = f.getName(); //.substring(0, f.getName().indexOf("."));

                    documents.put(docName, document);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SemEval2010.class.getName()).log(Level.SEVERE, null, ex);
        }

        return documents;
    }

    @Override
    protected Map<String, String[]> loadTrainingAnswers() {

        Map<String, String[]> keyphrases = null;

        try {

            File gold = new File(datasetPath + 
                    FileSystem.getSeparator() +
                    "train_answer" +
                    FileSystem.getSeparator() +
                    "train.combined.stem.final");

            Map<String, String[]> buffer = new HashMap<>();

            // navigate through the lines
            try (Stream<String> lines = Files.lines(gold.toPath(), StandardCharsets.UTF_8)) {

                for (String line : (Iterable<String>) lines::iterator) {
                    List<String> documentKPs = new ArrayList<>();
                    // remove the name of the document
                    String kpLine = line.substring(line.indexOf(':') + 2);
                    for (String kp : kpLine.split(",")) {
                        documentKPs.add(kp);
                    }

                    buffer.put(line.substring(0, line.indexOf(':') - 1)
                            +".txt.final",
                            documentKPs.toArray(new String[documentKPs.size()]));
                }
            }

            // copy the gold standard to the output array
            keyphrases = buffer;

        } catch (IOException ex) {
            Logger.getLogger(SemEval2010.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Unable to load gold standard", ex);
        }

        return keyphrases;
    }

    /**
     * Compares a <b>candidate</b> item with a <b>dataset provided</b> item. Please
     * note that the the object to test <b>must</b> be passed as first
     * parameter, while the object to test against <b>must</b> be passed as
     * second parameter.
     *
     * @param o1 the object to test, generated by the Distiller
     * @param o2 the reference object, provided by the training set.
     * @return 0 if o1 and o2 are equal, another number (indetermined)
     * otherwise.
     */
    @Override
    public int compare(String o1, String o2) {
        PorterStemmer stemmer = new PorterStemmer();
        String[] tokens = o1.split(" ");

        for (int i = 0; i < tokens.length; i++) {

            // this is necessary because SEMEVAL tokenizes in a different
            // way, using not only spaces but also hyphens to separate 
            // tokens 
            if (tokens[i].indexOf('-') < 0) {
                tokens[i] = stemmer.stem(tokens[i]);
            } else {
                String[] subtokens = tokens[i].split("-");
                for (int j = 0; j < subtokens.length; j++) {
                    subtokens[j] = stemmer.stem(subtokens[j]);
                }
                
                tokens[i] = String.join("-",subtokens);
            }
        }
        o1 = String.join(" ", tokens);

        boolean found = false;

        if (o2.indexOf('+') < 0) {
            found = o1.equals(o2);
        } else {
            String[] goldKPs = o2.split("\\+");
            for (int i = 0; i < goldKPs.length && !found; i++) {
                found = o1.equals(goldKPs[i]);
            }
        }

        return found ? 0 : 1;
    }

    /**
     * {@inheritDoc} 
     * @return {@inheritDoc} 
     */
    @Override
    public String getTrainingFolder() {
        return datasetPath + "/train";
    }

    /**
     * {@inheritDoc} 
     * @return {@inheritDoc} 
     */
    @Override
    public String getTestFolder() {
        return datasetPath + "/test";
    }

}
