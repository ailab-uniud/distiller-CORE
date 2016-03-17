/*
 * Copyright (C) 2015 Artificial Intelligence
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
import it.uniud.ailab.dcore.persistence.Keyphrase;
import it.uniud.ailab.dcore.persistence.Token;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Annotates grams with the Document Phrase Maximality (DPM) feature. DPM gives
 * a hint of how much an n-gram is a concept of its own right. Ngrams with low
 * maximality tend to appear in the text just as subsets of longer ngrams,
 * therefore are less interesting. DPM is explained in detail in the following
 * paper http://ceur-ws.org/Vol-1384/paper2.pdf
 *
 * WARNING : this annotator requires the
 * {@link it.uniud.ailab.dcore.annotation.annotators.StatisticalAnnotator} to be
 * run previously in the pipeline.
 *
 * @author Dario De Nart
 * @author Marco Basaldella
 */
public class DocumentPhraseMaximalityAnnotator implements Annotator {

    /**
     * The phrase maximality in the document
     */
    public static final String MAXIMALITY = "Maximality";

    /**
     * Annotates grams with the Maximality feature. Maximality gives a hint of
     * how much an n-gram is a concept of its own right. ngrams with low
     * maximality tend to appear in the text just as subsets of longer phrases,
     * therefore are less interesting. For more information, please read the
     * <a href="http://ceur-ws.org/Vol-1384/paper2.pdf">original paper</a>.
     *
     * Please note that this annotator requires some other annotator to compute
     * the frequency of n-grams in the blackboard.
     *
     * @param blackboard the blackboard to annotate
     * @param component this parameter will be ignored, since maximality is a
     * document-wise feature.
     */
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        Map<String, Keyphrase> surfaces = new HashMap<>();

        Collection<Keyphrase> allKPs
                = blackboard.getGramsByType(Keyphrase.KEYPHRASE);

        for (Keyphrase k : allKPs) {
            String stemmedSurface = "";
            for (Token t : k.getTokens()) {
                stemmedSurface += t.getStem() + "§";
            }
            surfaces.put("§" + stemmedSurface, k);

        }

        for (Keyphrase k : allKPs) {
            String stemmedSurface = "";
            for (Token t : k.getTokens()) {
                stemmedSurface += t.getStem() + "§";
            }
            stemmedSurface = "§" + stemmedSurface;
            Double maximality = 0.0;
            for (Map.Entry<String, Keyphrase> surface : surfaces.entrySet()) {

                if (!surface.getKey().equals(stemmedSurface)
                        && (surface.getKey().contains(stemmedSurface))) {

                    double step
                            = surface.getValue().getFeature(StatisticalAnnotator.FREQUENCY)
                            / k.getFeature(StatisticalAnnotator.FREQUENCY);

                    maximality = Math.max(step, maximality);
                }
            }
            k.putFeature(MAXIMALITY, 1.0 - maximality);
        }
    }

}
