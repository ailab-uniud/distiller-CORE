/*
 * Copyright (C) 2017 Artificial Intelligence
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
import it.uniud.ailab.dcore.annotation.annotations.TextAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;

/**
 * Adds the last n letters of each token as a feature. For example, when called
 * over the word "example" with length 2, adds the feature SUFFIX-2 with value
 * "le".
 *
 * @author Marco Basaldella
 */
public class TokenOwnAffixesAnnotator implements Annotator {

    // The length of the affix to attach
    private int affixLength;

    public void setAffixLength(int affixLength) {
        this.affixLength = affixLength;
    }

    public static final String SUFFIX_N = "Suffix-";
    public static final String PREFIX_N = "Prefix-";

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        String suffixAnnotation = SUFFIX_N + affixLength;
        String prefixAnnotation = PREFIX_N + affixLength;

        for (Sentence s : DocumentUtils.getSentences(component)) {
            for (Token t : s.getTokens()) {
                String token = t.getText();
                String suffix = token, prefix = token;

                if (suffix.length() > affixLength) {
                    suffix = suffix.substring(suffix.length() - affixLength);
                    prefix = prefix.substring(0,affixLength);
                }

                t.addAnnotation(
                        new TextAnnotation(suffixAnnotation, suffix));
                t.addAnnotation(
                        new TextAnnotation(prefixAnnotation, prefix));
            }
        }
    }
}
