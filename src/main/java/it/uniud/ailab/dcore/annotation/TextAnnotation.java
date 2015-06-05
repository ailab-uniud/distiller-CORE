/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 * 
 * 	This file is part of the Distiller-CORE library.
 * 
 * 	Distiller-CORE is free software; you can redistribute it and/or
 * 	modify it under the terms of the GNU Lesser General Public
 * 	License as published by the Free Software Foundation; either
 * 	version 2.1 of the License, or (at your option) any later version.
 *
 * 	Distiller-CORE is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * 	Lesser General Public License for more details.
 *
 * 	You should have received a copy of the GNU Lesser General Public
 * 	License along with this library; if not, write to the Free Software
 * 	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * 	MA 02110-1301  USA or see <http://www.gnu.org/licenses/>.
 */
package it.uniud.ailab.dcore.annotation;

/**
 *
 * @author Marco Basaldella
 */
public class TextAnnotation extends Annotation {
    
    /**
     * The annotated string. For example, "software engineering".
     */
    private final String annotatedText;
    /**
     * The annotation. For example, address of the Wikipedia page 
     * "Software_Engineering".
     */
    private final String annotation ;
    
    public TextAnnotation(String annotator, String annotatedText, String annotation) {
        super(annotator);
        this.annotatedText = annotatedText;
        this.annotation = annotation;
    }
    
    public String getAnnotatedText() {
        return annotatedText;
    }
    
    public String getAnnotation() {
        return annotation;
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
        
        return (this.annotatedText.equals(other.getAnnotatedText())) &&
                (this.annotator.equals(other.getAnnotator())) &&
                (this.annotation.equals(other.getAnnotation()));
    }

}
