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
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Filters out from the blackboard grams of a specific type (like Keyphrases)
 * that match the provided regular expression.
 *
 * @author Marco Basaldella
 */
public class RegexFilterAnnotator implements Annotator {

    private String filter;

    private String target;

    /**
     * The regular expression to match.
     *
     * @param filter the regular expression to match
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * The type of grams to filter.
     *
     * @param target the type of grams to filter.
     */
    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        List<Gram> gramsToRemove = new ArrayList<>();

        Collection<Object> gramColletion = blackboard.getGramsByType(target);
        if (gramColletion == null) {
            Logger.getLogger(RegexFilterAnnotator.class.getName()).
                    log(Level.WARNING, "I can't run the filter because I "
                            + "found no grams of type {0}.", target);
        } else {

            for (Object gramObj : gramColletion) {
                Gram g = (Gram) gramObj;
                if (g.getSurface().matches(filter)) {
                    gramsToRemove.add(g);
                }
            }

            for (Gram g : gramsToRemove) {
                blackboard.removeGram(target, g);
            }
        }
    }
}
