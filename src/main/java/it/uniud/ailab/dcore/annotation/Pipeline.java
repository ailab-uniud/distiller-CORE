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
package it.uniud.ailab.dcore.annotation;

import com.rits.cloning.Cloner;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import java.util.LinkedList;
import java.util.List;

/**
 * A pipeline of annotators.
 *
 * @author Marco Basaldella
 */
public class Pipeline implements Annotator {
    
    private List<Annotator> annotators = new LinkedList<>();
    
    /**
     * Returns a deep copy of the annotators in the pipeline.
     * 
     * @return the annotators in the pipeline.
     */
    public List<Annotator> getAnnotators() {
        return (new Cloner()).deepClone(annotators);
    }

    /**
     * Sets the annotators of the pipeline.
     * 
     * @param annotators the new pipeline.
     */
    public void setAnnotators(List<Annotator> annotators) {
        this.annotators = annotators;
    }
    
    /**
     * Adds an annotator to the end of the pipeline.
     * 
     * @param a the annotator to add.
     */
    public void addAnnotator(Annotator a) {
        this.annotators.add(a);
    }

    /**
     * Runs the pipeline.
     * 
     * @param blackboard the blackboard to annotate
     * @param component the document component in the blackboard to annotate
     */
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        for (Annotator a : annotators)
            a.annotate(blackboard, component);
    }
    
}
