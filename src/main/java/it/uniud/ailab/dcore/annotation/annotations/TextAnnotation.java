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

package it.uniud.ailab.dcore.annotation.annotations;

import it.uniud.ailab.dcore.annotation.Annotation;
import it.uniud.ailab.dcore.persistence.Token;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple annotation of one (or more tokens) with a string. This way we can
 * annotate text portions of the document with other text.
 *
 * @author Marco Basaldella
 */
public class TextAnnotation extends Annotation {
    
    /**
     * The annotated string. For example, "software engineering" is formed by
     * the tokens "software" and "engineering".
     */
    private final Token[] annotatedTokens;
    
    public TextAnnotation(String annotator, Token[] annotatedTokens, String annotation) {
        super(annotator);
        this.annotatedTokens = annotatedTokens;
        super.addString(annotation);
        
        List<String> tokenText = new ArrayList<>();
        Arrays.asList(annotatedTokens).
                stream().forEach(t -> {
                  tokenText.add(t.getText());
                });
        
        super.addString(String.join(" ", tokenText));
        
    }
    
    public String getAnnotation() {
        return super.getStringAt(0);
    }
    
    public String getAnnotatedText() {
        return super.getStringAt(1);
    }    
    
    
    public Token[] getTokens() {
        return annotatedTokens;
    }
    
    /**
     * Two text annotations are the same if the annotator, the annotated
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
                (this.getAnnotation().equals(other.getAnnotation()));
    }

}
