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

import java.util.HashMap;
import java.util.Map;

/**
 * A generic for an object that can be annotated. 
 * 
 *
 * @author Marco Basaldella
 */
public abstract class Annotable {
    
    private Map<String,Annotation> annotations = new HashMap<>();
    
    public void addAnnotation(Annotation ann) {
        annotations.put(ann.getAnnotator(),ann);
    }
    
    public Annotation getAnnotation(String annotator) {
        return annotations.get(annotator);
    }
    
    public Annotation[] getAnnotations() {
        return annotations.values().toArray(new Annotation[annotations.size()]);
    }
    
    public boolean hasAnnotation(String annotator) {
        return annotations.containsKey(annotator);
    }
}
