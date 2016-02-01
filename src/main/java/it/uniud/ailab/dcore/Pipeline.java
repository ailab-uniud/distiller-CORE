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
package it.uniud.ailab.dcore;

import com.rits.cloning.Cloner;
import it.uniud.ailab.dcore.annotation.Annotator;
import java.util.LinkedList;
import java.util.List;

/**
 * A pipeline of stages.
 *
 * @author Marco Basaldella
 */
public class Pipeline implements Stage {
    
    private List<Stage> stages = new LinkedList<>();
    
    /**
     * Returns a deep copy of the stages in the pipeline.
     * 
     * @return the stages in the pipeline.
     */
    public List<Stage> getStages() {
        return (new Cloner()).deepClone(stages);
    }

    /**
     * Sets the stages of the pipeline.
     * 
     * @param stages the new pipeline.
     */
    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }
    
    /**
     * Adds a stage to the end of the pipeline.
     * 
     * @param s the stage to add.
     */
    public void addStage(Stage s) {
        this.stages.add(s);
    }

    /**
     * Runs the pipeline.
     * 
     * @param blackboard the blackboard to analyze
     */
    @Override
    public void run(Blackboard blackboard) {
        for (Stage s : stages)
            s.run(blackboard);
    }
    
}
