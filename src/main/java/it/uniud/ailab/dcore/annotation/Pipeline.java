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
package it.uniud.ailab.dcore.annotation;

import com.rits.cloning.Cloner;
import it.uniud.ailab.dcore.engine.Annotator;
import it.uniud.ailab.dcore.engine.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * A pipeline of annotators.
 *
 * @author Marco Basaldella
 */
public class Pipeline implements Annotator {
    
    private List<Annotator> annotators = new LinkedList<>();
    
    private Locale language;

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
     * Gets the language supported by the pipeline.
     * 
     * @return the language supported by the pipeline
     */
    public Locale getLanguage() {
        return language;
    }

    /**
     * Sets the language supported by the pipeline.
     * 
     * @param language the language supported by the pipeline
     */
    public void setLanguage(Locale language) {
        this.language = language;
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
