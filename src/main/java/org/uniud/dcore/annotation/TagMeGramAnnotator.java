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
package org.uniud.dcore.annotation;

import java.util.List;
import org.uniud.dcore.engine.Annotator;
import org.uniud.dcore.engine.BlackBoard;
import org.uniud.dcore.persistence.Annotation;
import org.uniud.dcore.persistence.DocumentComponent;
import org.uniud.dcore.persistence.Gram;
import org.uniud.dcore.persistence.Token;

/**
 *
 * @author Marco Basaldella
 */
public class TagMeGramAnnotator implements Annotator {
    
    /**
     * This field will be set to 1 if the specified gram coincides with a 
     * Wikipedia entry.
     */
    public static final String WIKIFLAG = "Wikiflag";

    @Override
    public void annotate(DocumentComponent component) {
        
        for (Gram g: BlackBoard.Instance().getGrams().values()) {
            // check if the gram coincides with a TagMe annotation
            List<Token> tokens = g.getTokens();
            
            int counter = 0;
            
            Annotation a = tokens.get(counter).getAnnotation(TagMeTokenAnnotator.TAGMEANNOTATION);
            
            boolean isTagged = a != null;
            
            while (isTagged && ++counter < tokens.size()) {
                Annotation b = tokens.get(counter).getAnnotation(TagMeTokenAnnotator.TAGMEANNOTATION);
                isTagged = (b != null) ? b.equals(a) : false;
            }
            
            if (isTagged) {
                g.putFeature(WIKIFLAG, 1);
            }
        }
    }
    
}