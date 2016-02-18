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
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import java.util.ArrayList;
import java.util.List;

/**
 * Removes from the blackboard n-grams which identifier is a substring of
 * another n-gram identifier. Note: this works if the identifier is the surface
 * of the n-gram or if the identifier is the stemmed surface of the n-gram.
 *
 * @author Marco Basaldella
 */
public class GramMergerAnnotator implements Annotator {

    /**
     *
     * Removes from the blackboard n-grams which identifier is a substring of
     * another n-gram identifier. Note: this works if the identifier is the
     * surface of the n-gram or if the identifier is the stemmed surface of the
     * n-gram.
     *
     * @param blackboard the blackboard to analyze
     * @param component the component to analyze (<b>not used</b>)
     */
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        List<String> ids = new ArrayList<>();

        for (Gram g : blackboard.getKeyphrases()) {
            Keyphrase k = (Keyphrase)g;
            ids.add(k.getIdentifier());
        }

        // REVERSE SORT: the longest string goes first!
        //
        // The comparator is implemented to get reverse ordering.
        // the comparator returns 0 if the strings are same length
        // a number < 0 if s1.length > s2.length
        // a number > 0 if s2.length < s1.length
        
        ids.sort((s1, s2) -> s2.length() - s1.length());

        // The basic idea is to create a string like that:
        // validIds = $$id_1$$id_2$$id_3$$...
        // Where we guarantee that no id_i is substring of another id_k.
        
        // To do that we reverse sort the gram ids by length ( longest first)
        // and build the validIds string with this logic:
        // if the current id_i is not a substring of validIds, then we append
        // it to the string; else, if id_i is substring of validIds, it means 
        // that a longer id_k has been added to the string, where id_i is 
        // substring of id_k.
        
        // As a separator we use a double carriage-return-newline sequence, 
        // which should never appear inside a gram.        
        
        List<String> idsToKeep = new ArrayList<>();
        final String separator = "\r\n\r\n";
        String validIds = separator;

        for (String id : ids) {
            if (!validIds.contains(id)) {
                idsToKeep.add(id);
                validIds = validIds + id + separator;
            }
        }
        
        for (Gram g : blackboard.getKeyphrases()) {
            Keyphrase k = (Keyphrase)g;
            if (!idsToKeep.contains(k.getIdentifier()))
                blackboard.removeKeyphrase(k);
        }

    }

}
