package it.uniud.ailab.dcore.wrappers.ontogene;

import com.opencsv.CSVReader;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.AnnotationException;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.annotation.DefaultAnnotations;
import it.uniud.ailab.dcore.annotation.annotations.ScoredAnnotation;
import it.uniud.ailab.dcore.io.IOBlackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import it.uniud.ailab.dcore.utils.FileSystem;
import it.uniud.ailab.dcore.utils.Pair;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
/**
 * This annotator loads candidates using the TSV files produced by
 * python-ontogene and annotates them with basic information.
 *
 *
 * @author Marco Basaldella
 */
public class OntogeneTsvAnalyzerAnnotator implements Annotator {

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        List<Pair<String, Integer>> terms = loadFromTsv();

        // suppose that the terms indice the TSV file are ordered, so iterate
        // through sentences using only one iterator for all terms to reduce
        // overhead
        Iterator<Sentence> sentenceIterator
                = DocumentUtils.getSentences(component).iterator();

        Sentence currentSentence = null;
        double sentenceStart = -1;
        double sentenceEnd = -1;

        for (Pair<String, Integer> term : terms) {

            // locate the correct sentence
            while (term.getRight() > sentenceEnd && sentenceIterator.hasNext()) {

                currentSentence = sentenceIterator.next();

                sentenceStart
                        = ((ScoredAnnotation) currentSentence.getAnnotation(DefaultAnnotations.START_INDEX))
                        .getScore();

                sentenceEnd
                        = ((ScoredAnnotation) currentSentence.getAnnotation(DefaultAnnotations.END_INDEX))
                        .getScore();

            }

            // if i reached the end of the document while looking for the
            // sentence, throw exception
            if ((term.getRight() > sentenceEnd && sentenceIterator.hasNext())
                    || currentSentence == null) {
                throw new AnnotationException(
                        this,
                        "Reached EOF while looking for term");
            }

            // look for the tokens
            String sentenceText = currentSentence.getText();
            double currentIndex = sentenceStart;

            Iterator<Token> tokenIterator = currentSentence.getTokens().iterator();

            List<Token> candidateTokens = new ArrayList<>();

            Token prevT = null;

            while (tokenIterator.hasNext() && candidateTokens.isEmpty()) {

                Token t = tokenIterator.next();
                double tokenPosition
                        = ((ScoredAnnotation) t.getAnnotation(DefaultAnnotations.START_INDEX))
                        .getScore();
                int step = t.getText().length();
                sentenceText = sentenceText.substring(step);

                if (tokenPosition >= term.getRight()) {

                    // it may happen that  there are some tokenization
                    // mismatches, e.g. words with an hyphen:
                    // take "foo-bar" as an example
                    // ontogene may tokenize "foo" and "bar" as two separate tokens,
                    // while Distiller's tokenizer may choose "foo-bar" 
                    // as a single token.
                    if (tokenPosition > term.getRight()) {

                        if (prevT == null) {
                            Logger.getLogger(OntogeneTsvAnalyzerAnnotator.class.getName()).
                                    log(Level.SEVERE,
                                            "Tokenization mismatch: " + t.getText()
                                            + " tokenized by OntoGene as " + term.getLeft());
                            throw new AnnotationException(this,
                                    "Ontogene Tokenization mismatch");
                        }

                        if (lcs(prevT.getText(), term.getLeft()) > 0) {
                            Logger.getLogger(OntogeneTsvAnalyzerAnnotator.class.getName()).
                                    log(Level.INFO,
                                            "Tokenization mismatch: " + prevT.getText()
                                            + " tokenized by OntoGene as " + term.getLeft());
                        } else {
                            Logger.getLogger(OntogeneTsvAnalyzerAnnotator.class.getName()).
                                    log(Level.SEVERE,
                                            "Tokenization mismatch: " + prevT.getText()
                                            + " tokenized by OntoGene as " + term.getLeft());
                            throw new AnnotationException(this,
                                    "Ontogene Tokenization mismatch");
                        }
                    }

                    double tokenEnd;

                    do {
                        tokenEnd
                                = ((ScoredAnnotation) t.getAnnotation(DefaultAnnotations.END_INDEX))
                                .getScore();
                        candidateTokens.add(t);

                        if (tokenIterator.hasNext()) {
                            t = tokenIterator.next();
                        } else {
                            // force exit the loop
                            tokenEnd = Double.MAX_VALUE;
                        }
                    } while (tokenEnd < term.getLeft().length() + term.getRight());
                } // if
                prevT = t;
            } // while

            if (candidateTokens.isEmpty()) {
                throw new AnnotationException(this,
                        "Can't find tokens for the term " + term.getLeft());
            }

            Keyphrase kp = new Keyphrase(term.getLeft(), candidateTokens, term.getLeft());
            blackboard.addGram(currentSentence, kp);
        }

    }

    private List<Pair<String, Integer>> loadFromTsv() {
        String tsvFileName = OntogeneUtils.getCurrentDocumentID();

        tsvFileName += ".tsv";

        File f = new File(IOBlackboard.getDocumentsFolder());
        tsvFileName = f.getParent() + FileSystem.getSeparator()
                + "tsv" + FileSystem.getSeparator() + tsvFileName;

        Logger.getLogger(OntogeneTsvAnalyzerAnnotator.class.getName()).
                log(Level.INFO, "Looking for TSV file in path: {0}", tsvFileName);

        CSVReader reader;

        try {
            reader = new CSVReader(new FileReader(tsvFileName), '\t');
        } catch (FileNotFoundException ex) {
            throw new AnnotationException(this, "Unable to open " + tsvFileName, ex);
        }

        String[] nextLine;

        List<Pair<String, Integer>> terms = new ArrayList<>();

        try {

            int prevStart = -1;
            int prevEnd = -1;
            String currentWord = null;

            while ((nextLine = reader.readNext()) != null) {

                int curStart = Integer.parseInt(nextLine[2]);
                int curEnd = Integer.parseInt(nextLine[3]);

                // (start) we're matching a new word: just update the indices
                if (currentWord == null) {
                    currentWord = nextLine[4];
                    prevStart = curStart;
                    prevEnd = curEnd;
                } else {
                    // if we've already matched a word,
                    // check the indices: if we're expanding, substitute 
                    // the word; if the indices stay the same, don't do
                    // anything; if the FIRST index moves, save the word
                    // and start a new match.

                    if (curStart > prevStart) {
                        if (curEnd > prevEnd) {
                            terms.add(new Pair<>(currentWord, prevStart));
                            currentWord = nextLine[4];
                            prevStart = curStart;
                            prevEnd = curEnd;
                        }

                    } else {
                        if (curEnd > prevEnd) {
                            currentWord = nextLine[4];
                            prevEnd = curEnd;
                        }
                    } // else
                } // if currentWord == null
            } // while
        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(OntogeneTsvAnalyzerAnnotator.class.getName()).log(
                    Level.SEVERE, "Error while processing TSV file");

            throw new AnnotationException(this, "Error while processing " + tsvFileName, ex);
        }
        return terms;
    }

    /**
     * Finds the length of the longest common substring between the input
     * strings.
     *
     * @param first the first input string
     * @param second the second input string
     * @return the length of the L.C.S.
     * @see
     * <a href="https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Longest_common_substring#Java"/>
     */
    private static int lcs(String first, String second) {
        if (first == null || second == null || first.length() == 0 || second.length() == 0) {
            return 0;
        }

        int maxLen = 0;
        int fl = first.length();
        int sl = second.length();
        int[][] table = new int[fl + 1][sl + 1];

        for (int s = 0; s <= sl; s++) {
            table[0][s] = 0;
        }
        for (int f = 0; f <= fl; f++) {
            table[f][0] = 0;
        }

        for (int i = 1; i <= fl; i++) {
            for (int j = 1; j <= sl; j++) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    if (i == 1 || j == 1) {
                        table[i][j] = 1;
                    } else {
                        table[i][j] = table[i - 1][j - 1] + 1;
                    }
                    if (table[i][j] > maxLen) {
                        maxLen = table[i][j];
                    }
                }
            }
        }
        return maxLen;
    }
}
