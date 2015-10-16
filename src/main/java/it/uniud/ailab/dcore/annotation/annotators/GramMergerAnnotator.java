/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 *
 * 	Licensed under the Apache License, Version 2.0 (the "License");
 * 	you may not use this file except in compliance with the License.
 * 	You may obtain a copy of the License at
 *
 * 	     http://www.apache.org/licenses/LICENSE-2.0
 *
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 */
package it.uniud.ailab.dcore.annotation.annotators;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
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

        for (Gram g : blackboard.getGrams()) {
            ids.add(g.getIdentifier());
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
        
        for (Gram g : blackboard.getGrams()) {
            if (!idsToKeep.contains(g.getIdentifier()))
                blackboard.removeGram(g);
        }

    }

}
