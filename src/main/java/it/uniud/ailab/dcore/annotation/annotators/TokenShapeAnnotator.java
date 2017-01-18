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
import static it.uniud.ailab.dcore.annotation.DefaultAnnotations.*;
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.utils.DocumentUtils;

/**
 * Annotations over the shape of candidate token.
 *
 * @author Marco Basaldella
 */
public class TokenShapeAnnotator implements Annotator {
    
    public enum Mode {
        BINARY,
        COUNT
    }
    
    private Mode mode = Mode.COUNT;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
    
    

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        for (Sentence s : DocumentUtils.getSentences(component)) {
            for (Token t : s.getTokens()) {

                if (!t.hasAnnotation(UPPERCASE_COUNT)) {

                    int count = 0;
                    for (char c : t.getText().toCharArray()) {
                        if (Character.isUpperCase(c)) {
                            count++;
                        }
                    }
                    
                    // switch to binary if necessary
                    count = mode == Mode.COUNT ? count : 
                            count > 0 ? 1 : 0;

                    FeatureAnnotation f = new FeatureAnnotation(
                            UPPERCASE_COUNT, count);
                    t.addAnnotation(f);

                    int allUpper = count == t.getText().length() ? 1 : 0;
                    FeatureAnnotation f1 = new FeatureAnnotation(
                            ALL_UPPERCASE, allUpper);
                    t.addAnnotation(f1);

                    int insideCapt = 
                            t.getText().matches(
                            ".*[a-z0-9][A-Z].*") ? 1 : 0;
                            
                    // switch to binary if necessary
                    insideCapt = mode == Mode.COUNT ? insideCapt : 
                            insideCapt > 0 ? 1 : 0;

                    FeatureAnnotation f2 = new FeatureAnnotation(
                            INSIDE_CAPITALIZATION, insideCapt);
                    t.addAnnotation(f2);
                }

                if (!t.hasAnnotation(LOWERCASE_COUNT)) {

                    int count = 0;
                    for (char c : t.getText().toCharArray()) {
                        if (Character.isLowerCase(c)) {
                            count++;
                        }
                    }
                    
                    // switch to binary if necessary
                    count = mode == Mode.COUNT ? count : 
                            count > 0 ? 1 : 0;

                    FeatureAnnotation f = new FeatureAnnotation(LOWERCASE_COUNT, count);
                    t.addAnnotation(f);

                    int allLower = count == t.getText().length() ? 1 : 0;
                    FeatureAnnotation f1 = new FeatureAnnotation(ALL_LOWERCASE, allLower);
                    t.addAnnotation(f1);
                }

                if (!t.hasAnnotation(DIGITS_COUNT)) {

                    int count = 0;

                    for (char c : t.getText().toCharArray()) {
                        if (Character.isDigit(c)) {
                            count++;
                        }
                    }
                    
                    // switch to binary if necessary
                    count = mode == Mode.COUNT ? count : 
                            count > 0 ? 1 : 0;

                    FeatureAnnotation f = new FeatureAnnotation(DIGITS_COUNT, count);
                    t.addAnnotation(f);
                }

                if (!t.hasAnnotation(CHAR_COUNT)) {

                    FeatureAnnotation f = new FeatureAnnotation(CHAR_COUNT, t.getText().length());
                    t.addAnnotation(f);
                }

                if (!t.hasAnnotation(SYMBOLS_COUNT)) {

                    int count = 0;
                    for (char c : t.getText().toCharArray()) {
                        if (!Character.isSpaceChar(c)
                                && !Character.isLetterOrDigit(c)) {
                            count++;
                        }
                    }
                    
                    // switch to binary if necessary
                    count = mode == Mode.COUNT ? count : 
                            count > 0 ? 1 : 0;

                    FeatureAnnotation f = new FeatureAnnotation(SYMBOLS_COUNT, count);
                    t.addAnnotation(f);
                }

                if (!t.hasAnnotation(DASH_COUNT)) {
                    int count = 0;
                    for (char c : t.getText().toCharArray()) {
                        if (c == '-') {
                            count++;
                        }
                    }
                    
                    // switch to binary if necessary
                    count = mode == Mode.COUNT ? count : 
                            count > 0 ? 1 : 0;

                    FeatureAnnotation f = new FeatureAnnotation(DASH_COUNT, count);
                    t.addAnnotation(f);
                }

                if (!t.hasAnnotation(END_NUMBER)) {

                    int endNumber
                            = Character.isDigit(
                                    t.getText().charAt(
                                            t.getText().length() - 1))
                                    ? 1
                                    : 0;

                    FeatureAnnotation f = new FeatureAnnotation(END_NUMBER, endNumber);
                    t.addAnnotation(f);
                }

            }
        }
    }
}
