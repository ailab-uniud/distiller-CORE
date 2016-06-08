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
 * Annotates the gram with a flag if contains a greek letter (in extended form,
 * eg 'alpha', as a subword.
 *
 * @author Marco Basaldella
 */
public class GreekLetterAnnotator implements Annotator {

    public static final String CONTAINS_GREEK = "ContainsGreek";

    private final String[] greek = new String[]{
        "alpha",
        "beta",
        "gamma",
        "delta",
        "epsilon",
        "zeta",
        "eta",
        "theta",
        "iota",
        "kappa",
        "lambda",
        "mu",
        "nu",
        "xi",
        "omicron",
        "pi",
        "rho",
        "sigma",
        "tau",
        "upsilon",
        "phi",
        "chi",
        "psi",
        "omega"
    };

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        for (Sentence s : DocumentUtils.getSentences(component)) {
            for (Gram g : s.getGrams()) {
                if (!g.hasAnnotation(CONTAINS_GREEK)) {

                    boolean found = false;
                    for (int i = 0; i < greek.length & !found; i++) {
                        found = g.getSurface().
                                matches(".*\\b" + greek[i] + "\\b.*");
                    }

                    FeatureAnnotation f
                            = new FeatureAnnotation(CONTAINS_GREEK,
                                    found ? 1 : 0);
                    g.addAnnotation(f);

                }

            }
        }
    }
}
