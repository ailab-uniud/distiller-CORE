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

package it.uniud.ailab.dcore.annotation.annotations;

import it.uniud.ailab.dcore.annotation.Annotation;
import it.uniud.ailab.dcore.persistence.Token;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Marco Basaldella
 */
public class TextAnnotation extends Annotation {
    
    /**
     * The annotated string. For example, "software engineering" is formed by
     * the tokens "software" and "engineering".
     */
    private final Token[] annotatedTokens;
    
    /**
     * The annotation. For example, address of the Wikipedia page 
     * "Software_Engineering".
     */
    private final String annotation ;
    
    
    public TextAnnotation(String annotator, Token[] annotatedTokens, String annotation) {
        super(annotator);
        this.annotatedTokens = annotatedTokens;
        this.annotation = annotation;
    }
    
    public String getAnnotatedText() {
        
        List<String> tokenText = new ArrayList<>();
        
        Arrays.asList(annotatedTokens).
                stream().forEach(t -> {
                  tokenText.add(t.getText());
                });
        
        return String.join(" ", tokenText);
    }
    
    public String getAnnotation() {
        return annotation;
    }
    
    public Token[] getTokens() {
        return annotatedTokens;
    }
    
    /**
     * Two annotations are the same if the annotator, the annotated
     * text and the annotation are the same. 
     * 
     * For example, the words "Software" and "Engineering" may be annotated by
     * the same annotation from a "Wikipedia" annotator, the annotates the 
     * text "Software Engineering" with the name of the "Software_Engineering"
     * page. Then, they may have two separated "Software" and "Engineering" 
     * annotations that point to the respective pages in Wikipedia.
     * 
     * @param obj
     * @return true if the annotations are the same, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        TextAnnotation other = (TextAnnotation) obj;
        
        return (this.getAnnotatedText().equals(other.getAnnotatedText())) &&
                (this.annotator.equals(other.getAnnotator())) &&
                (this.annotation.equals(other.getAnnotation()));
    }

}
