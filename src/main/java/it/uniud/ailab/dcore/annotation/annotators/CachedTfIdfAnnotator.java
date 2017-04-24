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
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import it.uniud.ailab.dcore.utils.FileSystem;
import it.uniud.ailab.dcore.utils.SnowballStemmerSelector;
import it.uniud.ailab.dcore.wrappers.external.OpenNlpBootstrapperAnnotator;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.tartarus.snowball.SnowballStemmer;

/**
 *
 * @author Marco Basaldella
 */
public class CachedTfIdfAnnotator implements Annotator {

    private String databasePath;

    private static Map<String, String[]> documents = new HashMap<>();
    private static Map<String, Double> idfCache = new HashMap<>();

    private static final String docCachePath
            = FileSystem.getDistillerTmpPath()
            + FileSystem.getSeparator()
            + "tfidfdocs.ser";

    private static final String idfCachePath
            = FileSystem.getDistillerTmpPath()
            + FileSystem.getSeparator()
            + "idf.ser";

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        initIndex(
                getDatabasePath(),
                component.getLanguage());

        int idfSize = idfCache.size();

        List<Sentence> sentences = DocumentUtils.getSentences(component);

        for (Sentence s : sentences) {
            for (Gram g : s.getGrams()) {
                if (!g.hasAnnotation(DefaultAnnotations.TFIDF)) {

                    double tf = ((FeatureAnnotation) g.getAnnotation(
                            DefaultAnnotations.TF)).getScore();

                    double idf = idf(
                            g.getTokens().toArray(new Token[g.getTokens().size()]));

                    g.addAnnotation(
                            new FeatureAnnotation(
                                    DefaultAnnotations.TFIDF,
                                    tfIdf(tf, idf)));
                }
            }
        }

        if (idfSize != idfCache.size()) {
            writeIdfCache();
        }
    }

    private double idf(Token[] tokens) {
        double n = 0;

        String key = "";
        for (Token t : tokens) {
            key += t.getStem() + " ";
        }
        key = key.trim();

        if (idfCache.containsKey(key)) {
            return idfCache.get(key);
        }

        for (String[] document : documents.values()) {
            boolean found = false;

            for (int i = 0; !found && i < document.length - tokens.length; i++) {

                boolean matching = true;

                for (int j = 0; matching && j < tokens.length; j++) {
                    matching = (document[i + j].
                            equalsIgnoreCase(tokens[j].getStem()));
                }

                found = matching;
            }

            if (found) {
                n++;
            }
        }

        double idf = -1 * log2(
                n != 0
                        ? n / documents.size()
                        : 1 / (documents.size() + 1)
        );

        idfCache.put(key, idf);
        return idf;
    }

    private static double log2(double n) {
        return Math.log(n) / Math.log(2);
    }

    private double tfIdf(double tf, double idf) {

        return tf != 0 && idf != 0 ? tf * idf : 0;
    }

    private static void initIndex(String docPath, Locale locale) {

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
            documents = (Map<String, String[]>) in.readObject();
            in.close();
            System.out.println("Loaded documents from cache.");
        } catch (IOException | ClassNotFoundException iOException) {
            System.out.println("Unable to find or load document cache.");
        }

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(idfCachePath));
            idfCache = (Map<String, Double>) in.readObject();
            in.close();
            System.out.println("Loaded idf cache.");
        } catch (IOException | ClassNotFoundException iOException) {
            System.out.println("Unable to find or load idf cache.");
        }

        if (!documents.isEmpty()) {
            return;
        }

        System.out.println("Building tf-idf index...");
        System.out.println("Loding document in " + docPath);
        for (File f : (new File(docPath)).listFiles()) {

            System.out.println("Loading " + f.getAbsolutePath() + "...");
            loadFile(f, locale);
        }

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(docCachePath));
            out.writeObject(documents);
            out.flush();
            out.close();
            System.out.println("Cache saved.");
        } catch (IOException iOException) {
            System.out.println("Unable to save cache: " + iOException.getLocalizedMessage());
        }
    }

    private static void writeIdfCache() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(idfCachePath));
            out.writeObject(idfCache);
            out.flush();
            out.close();
            System.out.println("IDF cache saved.");
        } catch (IOException iOException) {
            System.out.println("Unable to save cache: " + iOException.getLocalizedMessage());
        }
    }

    private static void loadFile(File f, Locale locale) {
        BufferedReader br = null;
        String line;

        List<String> tokenizedDocument = new ArrayList<String>();

        try {

            String[] tokenizedLine = null;

            br = new BufferedReader(new FileReader(f));
            while ((line = br.readLine()) != null) {

                if (locale.getLanguage().equals("en")) {
                    tokenizedLine
                            = StanfordFastBootstrapperAnnotator.
                            tokenizeText(line);

                } else {
                    tokenizedLine
                            = OpenNlpBootstrapperAnnotator.
                            tokenizeText(line, locale.getLanguage());

                }

                SnowballStemmer stemmer = SnowballStemmerSelector.getStemmerForLanguage(locale);

                for (int i = 0; i < tokenizedLine.length; i++) {

                    stemmer.setCurrent(tokenizedLine[i]);
                    if (stemmer.stem()) {
                        tokenizedLine[i] = stemmer.getCurrent();
                    }
                }

                for (String token : tokenizedLine) {
                    tokenizedDocument.add(token);
                }

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

        // if for some uknwown reason still didn't work
        if (tokenizedDocument.isEmpty()) {
            throw new AnnotationException(new RawTfIdfAnnotator(),
                    "Can't read the tf-idf  database.");
        }

        documents.put(f.getAbsolutePath(), tokenizedDocument.toArray(
                new String[tokenizedDocument.size()]));

    }
}
