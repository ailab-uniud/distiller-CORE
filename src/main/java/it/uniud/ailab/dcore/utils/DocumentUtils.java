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
package it.uniud.ailab.dcore.utils;

import java.util.ArrayList;
import java.util.List;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Sentence;

/**
 * Utilies for the {@link it.uniud.ailab.dcore.persistence.DocumentComponent}
 * interface and its composite pattern classes
 * {@link it.uniud.ailab.dcore.persistence.DocumentComposite} and
 * {@link it.uniud.ailab.dcore.persistence.Sentence}, used to perform common
 * operations on a document (or parts of it).
 *
 * @author Marco Basaldella
 */
public class DocumentUtils {

    /**
     * Gets all the sentences of a document component.
     *
     * @param component the component of which you want the sentences
     * @return the sentences of the component
     */
    public static List<Sentence> getSentences(DocumentComponent component) {
        List<Sentence> ret = new ArrayList<>();

        for (DocumentComponent c : component.getComponents()) {
            if (c.getComponents() == null) {
                ret.add((Sentence) c);
            } else {
                ret.addAll(getSentences(c));
            }
        }

        return ret;
    }

    public static int getNumberOfPhrasesInDocument(DocumentComponent component) {
        int numberOfPhrases = 0;
        List<Sentence> sentences = getSentences(component);
        for (Sentence sentence : sentences) {

            numberOfPhrases =+ sentence.getPhraseNumber();

        }
        
        System.out.println(numberOfPhrases);
        return numberOfPhrases;
    }
}
