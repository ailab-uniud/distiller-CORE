/*
 * Copyright (C) 2016 Artificial Intelligence
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
package it.uniud.ailab.dcore.annotation.annotators;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.AnnotationException;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.annotation.DefaultAnnotations;
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.io.IOBlackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import it.uniud.ailab.dcore.utils.FileSystem;
import it.uniud.ailab.dcore.utils.SnowballStemmerSelector;
import it.uniud.ailab.dcore.wrappers.external.StanfordFastBootstrapperAnnotator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.tartarus.snowball.SnowballStemmer;

/**
 * A simple and raw tf-idf calculator for n-grams. It tokenizes each document
 * using OpenNLP, stems them with the Tartarus Stemmer, and marks each token
 * with a leading and trailing '§' mark.
 *
 * Then, when the tf-idf value of a token is search
 *
 * @author Marco Basaldella
 */
public class TokenTfIdfAnnotator implements Annotator {

    private String databasePath = "";

    private static Map<String, Map<String, Integer>> documents = new HashMap<>();
    private static Map<String, Integer> docLengths = new HashMap<>();

    private static final String docCachePath
            = FileSystem.getDistillerTmpPath()
            + FileSystem.getSeparator()
            + "tfidfdocuments.ser";

    private static final String docLengthCachePath
            = FileSystem.getDistillerTmpPath()
            + FileSystem.getSeparator()
            + "tdfidflengths.ser";

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    private boolean useStem = true;

    /**
     * Should the TF-IDF engine calculate frequency using stems or pure words?
     * Set this value to true for the first option, false otherwise.
     *
     * @param useStem use stemmed words to calculate TF-IDF if true.
     */
    public void setUseStem(boolean useStem) {
        this.useStem = useStem;
    }

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        initIndex(databasePath, component.getLanguage(), useStem);

        List<Sentence> sentences = DocumentUtils.getSentences(component);

        for (Sentence s : sentences) {
            for (Token t : s.getTokens()) {
                if (!t.hasAnnotation(DefaultAnnotations.TFIDF)) {

                    double tf, idf, tfIdf;
                    if (useStem) {
                        tf = tf(IOBlackboard.getCurrentDocument(),
                                t.getStem().toLowerCase());

                        idf = idf(t.getStem().toLowerCase());
                    } else {
                        tf = tfIdf(IOBlackboard.getCurrentDocument(),
                                t.getText().toLowerCase());

                        idf = idf(t.getText().toLowerCase());
                    }

                    tfIdf = tfIdf(tf, idf);

                    t.addAnnotation(new FeatureAnnotation(
                            DefaultAnnotations.TF,tf));
                    t.addAnnotation(new FeatureAnnotation(
                            DefaultAnnotations.IDF,idf));
                    t.addAnnotation(new FeatureAnnotation(
                            DefaultAnnotations.TFIDF, tfIdf));
                }
            }
        }
    }

    private double tf(String docId, String term) {
        double result = documents.get(docId).get(term);
        return result / docLengths.get(docId);
    }

    private double idf(String term) {
        double n = 0;
        for (Map<String, Integer> doc : documents.values()) {
            n += doc.containsKey(term) ? 1 : 0;
        }
        return Math.log10(documents.size() / n);
    }

    private double tfIdf(double tf, double idf) {
        return tf != 0 && idf != 0 ? tf * idf : 0;
    }

    private double tfIdf(String docId, String term) {
        double tf = tf(docId, term), idf = idf(term);
        return tf != 0 && idf != 0 ? tf * idf : 0;
    }

    private static void initIndex(String docPath, Locale locale, boolean doStem) {

        if (!documents.isEmpty()) {
            return;
        }

        if (docPath.isEmpty()) {
            System.out.println("You have not set a database path. "
                    + "Using input document folder instead...");

            docPath = IOBlackboard.getDocumentsFolder();
        }

        System.out.println("Looking for tf-idf cache...");

        try {

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(docCachePath));
            documents = (Map<String, Map<String, Integer>>) in.readObject();
            in.close();
            System.out.println("Loaded document occurrences from cache.");
        } catch (IOException | ClassNotFoundException iOException) {
            System.out.println("Unable to find or load document occurrences cache.");
        }

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(docLengthCachePath));
            docLengths = (Map<String, Integer>) in.readObject();
            in.close();
            System.out.println("Loaded documents length cache.");
        } catch (IOException | ClassNotFoundException iOException) {
            System.out.println("Unable to find or load documents length cache.");
        }

        if (!documents.isEmpty()) {
            return;
        }

        System.out.println("Building tf-idf index...");
        System.out.println("Loding document in " + docPath);
        for (File f : (new File(docPath)).listFiles()) {

            System.out.println("Loading " + f.getAbsolutePath() + "...");
            loadFile(f, locale, doStem);
        }

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(docCachePath));
            out.writeObject(documents);
            out.flush();
            out.close();
            System.out.println("Occurrences cache saved.");
        } catch (IOException iOException) {
            System.out.println("Unable to save occurrences cache: " + iOException.getLocalizedMessage());
        }

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(docLengthCachePath));
            out.writeObject(docLengths);
            out.flush();
            out.close();
            System.out.println("Documents length cache saved.");
        } catch (IOException iOException) {
            System.out.println("Unable to save cache: " + iOException.getLocalizedMessage());
        }
    }

    private static void loadFile(File f, Locale locale, boolean doStem) {
        BufferedReader br = null;
        String line;
        int tokenCount = 0;

        Map<String, Integer> document = new HashMap<>();

        try {

            br = new BufferedReader(new FileReader(f));
            while ((line = br.readLine()) != null) {

                String[] tokenizedDocument
                        = StanfordFastBootstrapperAnnotator.
                        tokenizeText(line);

                if (doStem) {

                    SnowballStemmer stemmer = SnowballStemmerSelector.getStemmerForLanguage(locale);
                    for (int i = 0; i < tokenizedDocument.length; i++) {

                        stemmer.setCurrent(tokenizedDocument[i]);
                        if (stemmer.stem()) {
                            tokenizedDocument[i] = stemmer.getCurrent();
                        }
                    }
                }

                for (String word : tokenizedDocument) {
                    document.put(word.toLowerCase(),
                            document.getOrDefault(word, 0) + 1);

                }

                tokenCount += tokenizedDocument.length;

            }

        } catch (FileNotFoundException e) {
            throw new AnnotationException(new RawTfIdfAnnotator(),
                    "Can't read the tf-idf database.", e);
        } catch (IOException e) {
            throw new AnnotationException(new RawTfIdfAnnotator(),
                    "Can't read the tf-idf  database.", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // don't care
                }
            }
        }

        documents.put(f.getAbsolutePath(), document);
        docLengths.put(f.getAbsolutePath(), tokenCount);

    }

}
