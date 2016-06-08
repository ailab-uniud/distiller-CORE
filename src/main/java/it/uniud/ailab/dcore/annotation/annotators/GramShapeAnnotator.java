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
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.utils.DocumentUtils;

/**
 * Annotations over the shape of candidate gram.
 *
 * @author Marco Basaldella
 */
public class GramShapeAnnotator implements Annotator {

    public static final String UPPERCASE_COUNT = "UpperCaseCount";

    public static final String LOWERCASE_COUNT = "LowerCaseCount";

    public static final String DIGITS_COUNT = "DigitsCount";

    public static final String CHAR_COUNT = "CharCount";

    public static final String SPACE_COUNT = "SpaceCount";

    public static final String SYMBOLS_COUNT = "SymbolsCount";

    public static final String DASH_COUNT = "DashCount";

    public static final String ALL_UPPERCASE = "AllUppercase";

    public static final String ALL_LOWERCASE = "AllLowercase";

    public static final String INSIDE_CAPITALIZATION = "InsideCapitalization";

    public static final String END_NUMBER = "EndsWithNumber";

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        for (Sentence s : DocumentUtils.getSentences(component)) {
            for (Gram g : s.getGrams()) {

                if (!g.hasAnnotation(UPPERCASE_COUNT)) {

                    int count = 0;
                    for (char c : g.getSurface().toCharArray()) {
                        if (Character.isUpperCase(c)) {
                            count++;
                        }
                    }

                    FeatureAnnotation f = new FeatureAnnotation(
                            UPPERCASE_COUNT, count);
                    g.addAnnotation(f);

                    int allUpper = count == g.getSurface().length() ? 1 : 0;
                    FeatureAnnotation f1 = new FeatureAnnotation(
                            ALL_UPPERCASE, allUpper);
                    g.addAnnotation(f1);

                    int insideCapt = 
                            g.getSurface().matches(
                            ".*[a-z0-9][A-Z].*") ? 1 : 0;
                            

                    FeatureAnnotation f2 = new FeatureAnnotation(
                            INSIDE_CAPITALIZATION, insideCapt);
                    g.addAnnotation(f2);
                }

                if (!g.hasAnnotation(LOWERCASE_COUNT)) {

                    int count = 0;
                    for (char c : g.getSurface().toCharArray()) {
                        if (Character.isLowerCase(c)) {
                            count++;
                        }
                    }

                    FeatureAnnotation f = new FeatureAnnotation(LOWERCASE_COUNT, count);
                    g.addAnnotation(f);

                    int allLower = count == g.getSurface().length() ? 1 : 0;
                    FeatureAnnotation f1 = new FeatureAnnotation(ALL_LOWERCASE, allLower);
                    g.addAnnotation(f1);
                }

                if (!g.hasAnnotation(DIGITS_COUNT)) {

                    int count = 0;

                    for (char c : g.getSurface().toCharArray()) {
                        if (Character.isDigit(c)) {
                            count++;
                        }
                    }

                    FeatureAnnotation f = new FeatureAnnotation(DIGITS_COUNT, count);
                    g.addAnnotation(f);
                }

                if (!g.hasAnnotation(CHAR_COUNT)) {

                    FeatureAnnotation f = new FeatureAnnotation(CHAR_COUNT, g.getSurface().length());
                    g.addAnnotation(f);
                }

                if (!g.hasAnnotation(SPACE_COUNT)) {

                    int count = 0;
                    for (char c : g.getSurface().toCharArray()) {
                        if (Character.isSpaceChar(c)) {
                            count++;
                        }
                    }

                    FeatureAnnotation f = new FeatureAnnotation(SPACE_COUNT, count);
                    g.addAnnotation(f);
                }

                if (!g.hasAnnotation(SYMBOLS_COUNT)) {

                    int count = 0;
                    for (char c : g.getSurface().toCharArray()) {
                        if (!Character.isSpaceChar(c)
                                && !Character.isLetterOrDigit(c)) {
                            count++;
                        }
                    }

                    FeatureAnnotation f = new FeatureAnnotation(SYMBOLS_COUNT, count);
                    g.addAnnotation(f);
                }

                if (!g.hasAnnotation(DASH_COUNT)) {
                    int count = 0;
                    for (char c : g.getSurface().toCharArray()) {
                        if (c == '-') {
                            count++;
                        }
                    }

                    FeatureAnnotation f = new FeatureAnnotation(DASH_COUNT, count);
                    g.addAnnotation(f);
                }

                if (!g.hasAnnotation(END_NUMBER)) {

                    int endNumber
                            = Character.isDigit(
                                    g.getSurface().charAt(
                                            g.getSurface().length() - 1))
                                    ? 1
                                    : 0;

                    FeatureAnnotation f = new FeatureAnnotation(END_NUMBER, endNumber);
                    g.addAnnotation(f);
                }

            }
        }
    }
}
