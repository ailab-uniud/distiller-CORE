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

import com.opencsv.CSVReader;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.AnnotationException;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Annotates grams with a value if they contain a word containing a an affix
 * from the specified databases. The value can be both a 0/1 flag or a
 * continuous value between 0 and 1, where 1 is the score of the most common
 * affix and 0 is the score of the least common one.
 *
 * The input file should be a tab-separated file with the affix (prefix/suffix)
 * in the first column and (optional) its score in the second one. The affixes
 * should be sorted by popularity.
 *
 * @author Marco Basaldella
 */
public class AffixesAnnotator implements Annotator {

    public static final String PREFIX = "Prefix";
    public static final String SUFFIX = "Suffix";

    private String prefixAnnotationID;
    private String suffixAnnotationID;

    public enum AffixMode {

        TOP50,
        TOP100,
        CONTINUOUS
    }

    // the path where to look for the prefix file
    private String prefixPath;
    // the path where to look for the suffix file
    private String suffixPath;
    // the name of the database from which the afflixes come. Used to
    // have a meaningful annotation ID
    private String databaseName;
    // the length of the afflixes to look for
    private int affixLength;

    // default: let the ML algorithm decide the threshold
    private AffixMode mode = AffixMode.CONTINUOUS;

    private Map<String, Double> prefixes = null;
    private Map<String, Double> suffixes = null;

    private Map<String, Map<String, Double>> prefixCache = new HashMap<>();
    private Map<String, Map<String, Double>> suffixCache = new HashMap<>();

    public String getPrefixPath() {
        return prefixPath;
    }

    public void setPrefixPath(String prefixPath) {
        this.prefixPath = prefixPath;
    }

    public String getSuffixPath() {
        return suffixPath;
    }

    public void setSuffixPath(String suffixPath) {
        this.suffixPath = suffixPath;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        init();

        for (Sentence s : DocumentUtils.getSentences(component)) {
            for (Gram g : s.getGrams()) {
                
                // use a simpler tokenization of the surface
                String[] tokens
//                        = // get the tokens from the gram
//                        Arrays.stream(g.getTokens().toArray(new Token[0])).
//                        // get the text from the tokens
//                        map(Token::getText).
//                        // put the text of the tokens inside the array
//                        toArray(String[]::new);
                        = g.getSurface().toLowerCase().split(" -");

                // the concept is: look if one of the words contains an affix.
                // if yes, update the score of the annotation if the
                // new affix has higher score.
                // this includes a bit of overload for the top50 and top100
                // modes but allows a much cleaner code.
                // note that it's not mandatory that both prefixes and suffixes
                // are loaded; if one of the maps is empty, it's just skipped.
                if (!(prefixes == null)) {
                    List<String> gramPrefixes = Arrays.stream(tokens)
                            .filter(token -> token.length() >= affixLength)
                            .map(token -> token.substring(0, affixLength))
                            .collect(Collectors.toList());
                    for (String prefix : gramPrefixes) {
                        if (prefixes.containsKey(prefix)) {
                            double prefixScore = prefixes.get(prefix);

                            if (g.hasAnnotation(prefixAnnotationID)) {
                                prefixScore = Math.max(prefixScore,
                                        ((FeatureAnnotation) g.getAnnotation(prefixAnnotationID))
                                        .getScore());
                            }

                            g.addAnnotation(
                                    new FeatureAnnotation(prefixAnnotationID,
                                            prefixScore));
                        }
                    }
                }

                if (!(suffixes == null)) {
                    
                    List<String> gramSuffixes = Arrays.stream(tokens)
                            .filter(token -> token.length() >= affixLength)
                            .map(token -> token.substring(token.length() - affixLength))
                            .collect(Collectors.toList());
                    for (String suffix : gramSuffixes) {
                        if (suffixes.containsKey(suffix)) {
                            double suffixScore = suffixes.get(suffix);

                            if (g.hasAnnotation(suffixAnnotationID)) {
                                suffixScore = Math.max(suffixScore,
                                        ((FeatureAnnotation) g.getAnnotation(suffixAnnotationID))
                                        .getScore());
                            }

                            g.addAnnotation(
                                    new FeatureAnnotation(suffixAnnotationID,
                                            suffixScore));
                        }
                    }

                }

            }
        }

    }

    private void init() {

        prefixAnnotationID = PREFIX + "_" + databaseName;
        suffixAnnotationID = SUFFIX + "_" + databaseName;

        prefixes = prefixCache.get(databaseName);
        suffixes = suffixCache.get(databaseName);

        if (prefixes != null || suffixes != null) {
            // found something in cache, so don't need to load from file.

            // set the length of the affixes using the first element of the sets
            affixLength = prefixes != null
                    ? prefixes.entrySet().iterator().next().getKey().length()
                    : suffixes.entrySet().iterator().next().getKey().length();
            return;
        }

        if (prefixPath != null && !prefixPath.isEmpty()) {
            prefixes = new HashMap<>();

            // load prefixes
            CSVReader reader;
            try {

                reader = new CSVReader(new FileReader(prefixPath), '\t');

                String[] line = reader.readNext();
                double maxScore = Double.parseDouble(line[1]);
                int counter = 0;
                int maxSteps = 0;

                if (mode == AffixMode.TOP50) {
                    maxSteps = 50;
                } else if (mode == AffixMode.TOP100) {
                    maxSteps = 100;
                } else {
                    maxSteps = Integer.MAX_VALUE;
                }

                while (line != null && counter < maxSteps) {

                    double score = 1;

                    if (mode == AffixMode.CONTINUOUS) {
                        score = Integer.parseInt(line[1]) / maxScore;
                    }

                    prefixes.put(line[0], score);

                    counter++;
                    line = reader.readNext();
                }

            } catch (IOException ex) {
                throw new AnnotationException(this, "Cannot open file " + prefixPath, ex);
            }
        }

        if (suffixPath != null && !suffixPath.isEmpty()) {
            suffixes = new HashMap<>();
            // load suffixes
            CSVReader reader;
            try {

                reader = new CSVReader(new FileReader(suffixPath), '\t');

                String[] line = reader.readNext();
                double maxScore = Double.parseDouble(line[1]);
                int counter = 0;
                int maxSteps = 0;

                if (mode == AffixMode.TOP50) {
                    maxSteps = 50;
                } else if (mode == AffixMode.TOP100) {
                    maxSteps = 100;
                } else {
                    maxSteps = Integer.MAX_VALUE;
                }

                while (line != null && counter < maxSteps) {

                    double score = 1;

                    if (mode == AffixMode.CONTINUOUS) {
                        score = Integer.parseInt(line[1]) / maxScore;
                    }

                    suffixes.put(line[0], score);

                    counter++;
                    line = reader.readNext();
                }

            } catch (IOException ex) {
                throw new AnnotationException(this, "Cannot open file " + prefixPath, ex);
            }
        }

        // set the length of the affixes using the first element of the sets
        affixLength = prefixes != null
                ? prefixes.entrySet().iterator().next().getKey().length()
                : suffixes.entrySet().iterator().next().getKey().length();

        prefixCache.put(databaseName, prefixes);
        suffixCache.put(databaseName, suffixes);

    }

}
