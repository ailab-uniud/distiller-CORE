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
package it.uniud.ailab.dcore.wrappers.ontogene;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.annotation.DefaultAnnotations;
import static it.uniud.ailab.dcore.annotation.DefaultAnnotations.END_INDEX;
import static it.uniud.ailab.dcore.annotation.DefaultAnnotations.START_INDEX;
import it.uniud.ailab.dcore.annotation.annotations.ScoredAnnotation;
import it.uniud.ailab.dcore.annotation.annotations.TextAnnotation;
import it.uniud.ailab.dcore.annotation.annotators.GenericEvaluatorAnnotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import it.uniud.ailab.dcore.wrappers.external.CRFSuiteEvaluator;
import static it.uniud.ailab.dcore.wrappers.ontogene.OntogeneTsvAnalyzerAnnotator.DOCUMENT_ID;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Converts the CRF annotations into Keyphrases, to be used for the generation
 * of an OntoGene-like TSV output with the
 * {@link it.uniud.ailab.dcore.io.OntoGeneTsvWriter} class.
 *
 * @author Marco Basaldella
 */
public class OntoGenePostCRFAnnotator implements Annotator {

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        // the CRF stage may have added new entities to the ones already
        // detected by OntoGene. So, we empty the KP list and re-fill it
        // with the newly detected ones.
        // Before doing that, we need to save the document ID (which is needed
        // by the evaluation scripts).
        Collection<Keyphrase> kps = blackboard.getGramsByType(Keyphrase.KEYPHRASE);

        if (kps == null || kps.size() == 0) {
            return;
        }

        String docId
                = ((TextAnnotation) kps.iterator().next().getAnnotation(OntogeneTsvAnalyzerAnnotator.DOCUMENT_ID)).
                getAnnotation();

        // remove all the "old" kps
//        for (Keyphrase kp : kps) {
//            blackboard.removeGram(kp.getType(), kp);
//        }
        kps.clear();

        // used to generate IDs
        Random rng = new Random();

        for (Sentence s : DocumentUtils.getSentences(blackboard.getStructure())) {

            for (int i = 0; i < s.getTokens().size(); i++) {

                if (((TextAnnotation) s.getTokens().get(i)
                        .getAnnotation(CRFSuiteEvaluator.CRF_TAG))
                        .getAnnotation().equals("B_CRAFT")) {
                    List<Token> kpTokens = new ArrayList<>();

                    // add tokens while they are annotated as entity tokens
                    for (; i < s.getTokens().size()
                            && !((TextAnnotation) s.getTokens().get(i)
                            .getAnnotation(CRFSuiteEvaluator.CRF_TAG))
                            .getAnnotation().equals("NO_CRAFT"); i++) {
                        kpTokens.add(s.getTokens().get(i));
                    }

                    // get the values needed for the annotations
                    double startIndex = ((ScoredAnnotation) kpTokens.get(0)
                            .getAnnotation(DefaultAnnotations.START_INDEX))
                            .getScore();
                    double endIndex = ((ScoredAnnotation) kpTokens.get(kpTokens.size() - 1)
                            .getAnnotation(DefaultAnnotations.END_INDEX))
                            .getScore();

                    List<String> surfaces = new ArrayList<>();
                    for (Token t : kpTokens) {
                        surfaces.add(t.getText());
                    }

                    String surface = String.join(" ", surfaces);

                    Keyphrase kp = new Keyphrase(
                            surface + rng.nextInt(),
                            kpTokens,
                            surface);

                    kp.addAnnotation(new TextAnnotation(
                            DefaultAnnotations.SURFACE, surface));
                    kp.putFeature(START_INDEX, startIndex);
                    kp.putFeature(END_INDEX, endIndex);

                    kp.putFeature(GenericEvaluatorAnnotator.SCORE, 1);

                    kp.addAnnotation(
                            new TextAnnotation(
                                    DOCUMENT_ID,
                                    docId));

                    blackboard.addGram(kp);

                }
            }
        }
    }

}
