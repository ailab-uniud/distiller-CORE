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
package org.uniud.dcore.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

/**
 * The keyphrase extractor object.
 *
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public class Distiller {

    // all these fields will be injected via setter method
    private Splitter splitter;
    private PreProcessor preProcessor;
    private NGramGenerator gramGenerator;
    private Evaluator evaluator;

    @Required
    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }
    
    @Required
    public void setGramGenerator(NGramGenerator gramGenerator) {
        this.gramGenerator = gramGenerator;
    }
    
    @Required
    public void setPreProcessor(PreProcessor preProcessor) {
        this.preProcessor = preProcessor;
    }
    
    @Required
    public void setSplitter(Splitter splitter) {
        this.splitter = splitter;
    }

}
