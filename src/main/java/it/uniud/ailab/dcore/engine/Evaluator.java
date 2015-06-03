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
package it.uniud.ailab.dcore.engine;

import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Required;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;

/**
 * This module reads the {@link it.uniud.ailab.dcore.persistence.Feature}s produced 
 * by the {@link org.uniud.dcore.engine.GramGenerator} and evaluates them to 
 * generate the output of the Distiller.
 * 
 * To correctly evaluate features, the module must know their syntax.
 * See the GramGenerator you're using to check how it outputs its result. 
 * 
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public abstract class Evaluator {
    
    public static final String SCORE = "Score";
    
    private Annotator[] annotators;
    private Locale language;
    
    // <editor-fold desc="getters and setters">
    @Required
    public void setAnnotators(Annotator[] annotators) {
        this.annotators = annotators;
    }
    
    public void setLanguage(Locale language) {
        this.language = language;
    }
    
    @Required
    public void setLanguageTag(String language) {
        this.language = Locale.forLanguageTag(language);
    }
    
    public Locale getLanguage() {
        return language;
    }
           
    // </editor-fold>
    
    public void generateAnnotations(DocumentComponent c) {
        for (Annotator a : annotators) {
            a.annotate(c);
        }
    }
    
    public abstract Map<Gram,Double> Score(DocumentComponent c);
    
}
