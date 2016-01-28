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
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import it.uniud.ailab.dcore.utils.Pair;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * An italian lemmatizer based on the Morph-it! lexicon by the SSLMIT (Scuola
 * Superiore di Lingue Moderne per Interpreti e Traduttori) of the University of
 * Bologna, and on the italian models for Apache OpenNLP by Andrea Ciapetti.
 *
 * @author Marco Basaldella
 */
public class ItalianLemmatizerAnnotator implements Annotator {

    /**
     * The dictionary of lemmas. Each word is mapped to one or more lemmas, and
     * the PoS tag relative to that lemma association. The left element of the
     * pair is the lemma, the right one is the PoS tag.
     */
    Map<String, List<Pair<String, String>>> lemmas = new HashMap<>();
    /**
     * Mapping between Ciapetti's and MorphIt's tagsets
     */
    Map<String, String> mapping = new HashMap<>();

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        try {
            loadLexicon();
        } catch (IOException ex) {
            Logger.getLogger(ItalianLemmatizerAnnotator.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Sentence s : DocumentUtils.getSentences(component)) {
            for (Token t : s.getTokens()) {

                String lemma = t.getText();

                List<Pair<String, String>> lemmasList = lemmas.get(t.getText());

                if (lemmasList != null) {
                    // if there's only one candidate lemma, don't care
                    // about the mapping

                    if (lemmasList.size() == 1) {
                        lemma = lemmasList.get(0).getLeft();
                    } else {
                        // elsewhise, there are more lemmas for that word:
                        // get the lemma that matches the longest pos-tag
                        // prefix.
                        String mappedTag = "";
                        for (Pair<String, String> candidate : lemmasList) {

                            String candidateMappedTag
                                    = mapping.get(candidate.getRight());
                            // keep only the tag before the ":" 
                            if (candidateMappedTag != null) {
                                candidateMappedTag
                                        = candidateMappedTag.split(":")[0];

                                if (t.getPoS().startsWith(candidateMappedTag)
                                        && mappedTag.length()
                                        < candidateMappedTag.length()) {
                                    mappedTag = candidateMappedTag;
                                    lemma = candidate.getLeft();
                                }
                            }
                        }
                    }
                }
                t.setLemma(lemma);
            }
        }
    }

    private void loadLexicon() throws IOException {
        String lexiconPath = getClass().getClassLoader().
                getResource("morph-it/morph-it_048.gz").getFile();

        InputStream is;

        // running from command-line and loading inside the JAR
        if (lexiconPath.contains("!")) {
            is = getClass().getResourceAsStream(
                    lexiconPath.substring(
                            lexiconPath.lastIndexOf("!") + 1));
        } else {
            // normal operation
            is = new FileInputStream(lexiconPath);
        }

        InputStream gzipStream = new GZIPInputStream(is);
        Reader decoder = new InputStreamReader(gzipStream, StandardCharsets.UTF_8);
        BufferedReader buffered = new BufferedReader(decoder);

        String line;

        // put the lemmas in the hashmap
        while ((line = buffered.readLine()) != null) {

            String[] splittedLine = line.split("\\t");
            if (splittedLine.length != 3) {
                continue;
            }

            if (lemmas.containsKey(splittedLine[0])) {
                lemmas.get(splittedLine[0]).
                        add(new Pair<>(splittedLine[1], splittedLine[2]));
            } else {

                List l = new ArrayList<>();
                l.add(new Pair<>(splittedLine[1], splittedLine[2]));

                lemmas.put(splittedLine[0], l);
            }
        }

        // now, load the mapping between Ciapetti's tags and the Morph-it ones
        InputStreamReader isr;

        String mappingPath = getClass().getClassLoader().
                getResource("morph-it/morph-it_mapping").getFile();

        // running from command-line and loading inside the JAR
        if (mappingPath.contains("!")) {
            isr = new InputStreamReader(
                    getClass().getResourceAsStream(
                            mappingPath.substring(
                                    mappingPath.lastIndexOf("!") + 1)));
        } else {
            // normal operation
            isr = new FileReader(mappingPath);
        }

        BufferedReader mappingReader = new BufferedReader(isr);
        while ((line = mappingReader.readLine()) != null) {

            // skip comments
            if (line.startsWith("#") || line.isEmpty()) {
                continue;
            }

            String[] splittedLine = line.split("\\t");
            mapping.put(splittedLine[0], splittedLine[1]);

        }
    }

}
