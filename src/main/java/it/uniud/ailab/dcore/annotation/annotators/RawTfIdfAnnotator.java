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
import it.uniud.ailab.dcore.io.IOBlackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import it.uniud.ailab.dcore.utils.SnowballStemmerSelector;
import it.uniud.ailab.dcore.wrappers.external.OpenNlpBootstrapperAnnotator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
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
public class RawTfIdfAnnotator implements Annotator {

    public static final String TFIDF = "tf-idf";

    private static final Map<String, String> documents = new HashMap<>();
    private static final Map<String, Integer> docLengths = new HashMap<>();

    private static String previousFolder = "";

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        initIndex(component.getLanguage());

        List<Sentence> sentences = DocumentUtils.getSentences(component);

        for (Sentence s : sentences) {
            for (Gram g : s.getGrams()) {
                if (!g.hasAnnotation(TFIDF)) {

                    String stemmedSurface = g.getSurface();

                    String[] tokenizedSurface
                            = OpenNlpBootstrapperAnnotator.
                            tokenizeText(stemmedSurface,
                                    component.getLanguage().getLanguage());

                    SnowballStemmer stemmer = SnowballStemmerSelector.
                            getStemmerForLanguage(component.getLanguage());

                    for (int i = 0; i < tokenizedSurface.length; i++) {

                        stemmer.setCurrent(tokenizedSurface[i]);
                        if (stemmer.stem()) {
                            tokenizedSurface[i] = stemmer.getCurrent();
                        }
                    }

                    stemmedSurface = String.join(" ",
                            markTokens(tokenizedSurface)).trim();

                    ((Keyphrase) g).putFeature(TFIDF,
                            tfIdf(
                                    IOBlackboard.getCurrentDocument(),
                                    stemmedSurface));
                }
            }
        }
    }

    private double tf(String docId, String term) {
        double result = StringUtils.countMatches(documents.get(docId), term);
        return result / docLengths.get(docId);
    }

    private double idf(String term) {
        double n = 0;
        for (String doc : documents.values()) {
            n += doc.contains(term) ? 1 : 0;
        }
        return Math.log(documents.size() / n);
    }

    private double tfIdf(String docId, String term) {
        double tf = tf(docId, term), idf = idf(term);
        return tf != 0 && idf != 0 ? tf * idf : 0;
    }

    private static void initIndex(Locale locale) {

        String docPath = IOBlackboard.getDocumentsFolder();

        if (!documents.isEmpty()
                && previousFolder.equals(docPath)) {
            return;
        }

        System.out.println("Doc path from folder  : " + docPath);
        String parentPath = (new File(IOBlackboard.getCurrentDocument())).getParent();
        System.out.println("Doc path from document: " + parentPath);

        docPath = docPath == null
                ? parentPath
                : docPath;

        previousFolder = docPath;

        System.out.println("Building tf-idf index...");

        for (File f : (new File(docPath)).listFiles()) {

            System.out.println("Loading " + f.getAbsolutePath() + "...");
            loadFile(f, locale);
        }
    }

    private static void loadFile(File f, Locale locale) {
        BufferedReader br = null;
        StringBuilder document = new StringBuilder();
        String line;

        int tokenCount = 0;

        try {

            br = new BufferedReader(new FileReader(f));
            while ((line = br.readLine()) != null) {

                String[] tokenizedDocument
                        = OpenNlpBootstrapperAnnotator.
                        tokenizeText(line, locale.getLanguage());

                SnowballStemmer stemmer = SnowballStemmerSelector.getStemmerForLanguage(locale);

                for (int i = 0; i < tokenizedDocument.length; i++) {

                    stemmer.setCurrent(tokenizedDocument[i]);
                    if (stemmer.stem()) {
                        tokenizedDocument[i] = stemmer.getCurrent();
                    }
                }

                tokenCount += tokenizedDocument.length;

                document.append(String.join(" ", markTokens(tokenizedDocument))
                        .trim());
                document.append(" ");

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

        String documentText = document.toString().trim();
        documents.put(f.getAbsolutePath(), documentText);
        docLengths.put(f.getAbsolutePath(), tokenCount);

    }

    private static String[] markTokens(String[] tokens) {

        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = "§" + tokens[i] + "§";
        }

        return tokens;
    }

}
