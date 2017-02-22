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
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.utils.FileSystem;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import opennlp.tools.stemmer.PorterStemmer;

/**
 * Loader for the Arabic Keyphrase Extraction Corpus.
 *
 * @author Marco Basaldella
 */
public class Akec extends GenericDataset {

    private String sort = "sort_frequency";
    private String type = "raw";

    /**
     * Set which one of the keyphrase sortings available in AKEC should be used.
     * The parameter should be the string identifying the folder containing the
     * sort (e.g. "sort_frequency").
     *
     * @param sort the string identifying the folder containing the sort (e.g.
     * "sort_frequency").
     */
    public void setSort(String sort) {
        this.sort = sort;
    }

    /**
     * Set which one of the document types available in AKEC should be used. The
     * parameter should be the string identifying the folder containing the
     * documents of the wanted type (e.g. "raw").
     *
     * @param type the string identifying the folder containing the documents of
     * the wanted type (e.g. "raw").
     */
    public void setType(String type) {
        this.type = type;
    }

    public Akec(String goldStandardPath) {
        super(goldStandardPath, "AKEC");
    }

    @Override
    public Map<String, String> loadTestSet() {

        Map<String, String> documents = new HashMap<>();

        try {

            for (String fileName : Files.readAllLines(
                    Paths.get(datasetPath).resolve("test.ids"),
                    StandardCharsets.UTF_8
            )) {

                String document = String.join(
                        "\n",
                        Files.readAllLines(
                                Paths.get(datasetPath).
                                resolve("documents").
                                resolve(type).
                                resolve(fileName),
                                StandardCharsets.UTF_8));

                String docName = fileName;

                documents.put(docName, document);
            }

        } catch (IOException ex) {
            Logger.getLogger(Akec.class.getName()).log(Level.SEVERE, null, ex);
        }

        return documents;
    }

    @Override
    public Map<String, String[]> loadTestAnswers() {

        Map<String, String[]> keyphrases = null;

        try {

            List<String> testIds = Files.readAllLines(
                    Paths.get(datasetPath).resolve("test.ids"),
                    StandardCharsets.UTF_8
            );

            File gold = Paths.get(datasetPath).resolve("keyphrases")
                    .resolve(sort).resolve("lemmatized.txt").toFile();

            Map<String, String[]> buffer = new HashMap<>();

            // navigate through the lines
            try (Stream<String> lines = Files.lines(gold.toPath(), StandardCharsets.UTF_8)) {

                for (String line : (Iterable<String>) lines::iterator) {
                    List<String> documentKPs = new ArrayList<>();
                    // remove the name of the document

                    String fileId = line.substring(0, line.indexOf(':') - 1).trim();

                    if (testIds.contains(fileId + ".txt")) {

                        String kpLine = line.substring(line.indexOf(':') + 2);
                        for (String kp : kpLine.split(",")) {
                            documentKPs.add(kp.trim());
                        }

                        buffer.put(fileId,
                                documentKPs.toArray(new String[documentKPs.size()]));

                    }
                }
            }

            // copy the gold standard to the output array
            keyphrases = buffer;

        } catch (IOException ex) {
            Logger.getLogger(Akec.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Unable to load gold standard", ex);
        }

        return keyphrases;
    }

    @Override
    protected Map<String, String> loadTrainingSet() {
        
        Map<String, String> documents = new HashMap<>();

        try {

            for (String fileName : Files.readAllLines(
                    Paths.get(datasetPath).resolve("train.ids"),
                    StandardCharsets.UTF_8
            )) {

                String document = String.join(
                        "\n",
                        Files.readAllLines(
                                Paths.get(datasetPath).
                                resolve("documents").
                                resolve(type).
                                resolve(fileName),
                                StandardCharsets.UTF_8));

                String docName = fileName;

                documents.put(docName, document);
            }

        } catch (IOException ex) {
            Logger.getLogger(Akec.class.getName()).log(Level.SEVERE, null, ex);
        }

        return documents;
    }

    @Override
    protected Map<String, String[]> loadTrainingAnswers() {

        Map<String, String[]> keyphrases = null;

        try {

            List<String> trainIds = Files.readAllLines(
                    Paths.get(datasetPath).resolve("train.ids"),
                    StandardCharsets.UTF_8
            );

            File gold = Paths.get(datasetPath).resolve("keyphrases")
                    .resolve(sort).resolve("lemmatized.txt").toFile();

            Map<String, String[]> buffer = new HashMap<>();

            // navigate through the lines
            try (Stream<String> lines = Files.lines(gold.toPath(), StandardCharsets.UTF_8)) {

                for (String line : (Iterable<String>) lines::iterator) {
                    List<String> documentKPs = new ArrayList<>();
                    // remove the name of the document

                    String fileId = line.substring(0, line.indexOf(':') - 1).
                            trim() + ".txt";

                    if (trainIds.contains(fileId )) {

                        String kpLine = line.substring(line.indexOf(':') + 2);
                        for (String kp : kpLine.split(",")) {
                            documentKPs.add(kp.trim());
                        }

                        buffer.put(fileId,
                                documentKPs.toArray(new String[documentKPs.size()]));

                    }
                }
            }

            // copy the gold standard to the output array
            keyphrases = buffer;

        } catch (IOException ex) {
            Logger.getLogger(Akec.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Unable to load gold standard", ex);
        }

        return keyphrases;
    }

    /**
     * Compares a <b>candidate</b> item with a <b>dataset provided</b> item.
     * Please note that the the object to test <b>must</b> be passed as first
     * parameter, while the object to test against <b>must</b> be passed as
     * second parameter.
     *
     * @param o1 the {@link it.uniud.ailab.dcore.persistence.Gram} to test, as
     * generated by the Distiller
     * @param o2 the reference {@link java.lang.String}, as provided by the 
     * training set.
     * @return 0 if one of the stemmed surfaces of o1 is equal to stemmed o2,
     * another number otherwise.
     */
    @Override
    public int compare(Object o1, Object o2) {
        
        Gram gram = (Gram)o1;
        String gold = (String)o2;
        
        return gram.getIdentifier().compareTo(gold);
        
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getTrainingFolder() {
        return datasetPath + FileSystem.getSeparator() + "train";
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getTestFolder() {
        return datasetPath + FileSystem.getSeparator() + "test";
    }

}
