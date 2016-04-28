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
package it.uniud.ailab.dcore.utils;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils that rely on, or use the class
 * {@link it.uniud.ailab.dcore.persistence.Keyphrase}.
 *
 * @author Marco Basaldella
 */
public class KeyphraseUtils {

    /**
     * Looks for the number of time the surface(s) of a keyphrase appear in the
     * text of the document. Note that the lookup looks for the keyphrase as a
     * whole word, not as part of another word. E.g.: looking for "foo" in the
     * text "foo bar foobar" would result in 1 occurrence according to this
     * method.
     *
     * @param b the blackboard
     * @param k the keyphrase
     * @return the number of occurrences of the keyphrase in the text.
     */
    public static int getTextAppearancesCount(Blackboard b, Keyphrase k) {
        return getTextAppearancesCount(b, k, false);
    }

    /**
     * Looks for the number of time the surface(s) of a keyphrase appear in the
     * text of the document. Note that the lookup looks for the keyphrase as a
     * whole word, not as part of another word. E.g.: looking for "foo" in the
     * text "foo bar foobar" would result in 1 occurrence according to this
     * method.
     *
     * @param b the blackboard
     * @param k the keyphrase
     * @param caseInsensitive true to make the lookup case-insensitive; false
     * otherwise.
     * @return the number of occurrences of the keyphrase in the text.
     */
    public static int getTextAppearancesCount(Blackboard b, Keyphrase k,
            boolean caseInsensitive) {

        List<String> processedSurfaces = new ArrayList<>();

        int appearances = 0;

        for (String s : k.getSurfaces()) {

            if (caseInsensitive) {
                s = s.toLowerCase();
            }

            if (!processedSurfaces.contains(s)) {
                processedSurfaces.add(s);
                s = Pattern.quote(s);

                Pattern pattern = Pattern.compile("\\W" + s + "\\W");
                Matcher matcher = null;
                if (caseInsensitive) 
                    matcher = pattern.matcher(b.getText().toLowerCase());
                else
                    matcher = pattern.matcher(b.getText());

                while (matcher.find()) {
                    appearances++;
                }
            }
        }

        if (appearances == 0) {
            // here we have a problem: probably the string surface does not
            // contain only alphanumeric characters but also something that
            // was difficult to tokenize, like an hyphen, a whitespace, and so
            // on, that makes the word-boundary operator \\b fail the match.
            // E.g.: consider "foo-bar" tokenized as "foo-" and "bar", 
            // \bfoo-\b wouldn't match in the string because after the dash we
            // don't have a word boundary.

            // So, simply look for the surface without any hassle; it's very 
            // unlikely that it will be in the middle of another word now
            processedSurfaces = new ArrayList<>();

            for (String s : k.getSurfaces()) {

                if (!processedSurfaces.contains(s)) {
                    processedSurfaces.add(s);
                    s = Pattern.quote(s);
                    Pattern pattern = Pattern.compile(s);
                    Matcher matcher = pattern.matcher(b.getText());

                    while (matcher.find()) {
                        appearances++;
                    }
                }
            }
        }

        if (appearances == 0) // now it's serious: the string is NOT in the original text.
        {
            throw new IllegalStateException(
                    "Looking for term \"" + k.getSurface()
                    + "\" while it does not appear in the text. ");
        }

        return appearances;
    }

}
