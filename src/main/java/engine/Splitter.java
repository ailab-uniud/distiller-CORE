/*
 *     This file is part of Distiller-CORE.
 * 
 *     Distiller-CORE is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Distiller-CORE is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Distiller-CORE.  If not, see <http://www.gnu.org/licenses/>.
 */
package engine;

import org.uniud.dcore.persistence.*;

/**
 *
 * @author Marco Basaldella <basaldella.marco.1 at spes.uniud.it>
 * @author Dario De Nart <denart. dario at uniud.it>
 */
abstract class Splitter {
    
    protected abstract ConceptUnit[] Split(String rawText);
    
    public void Run(String rawText) throws Exception {
        
        ConceptUnit[] splitted = Split(rawText);
        
        // check di coerenza...
        String check = "";
        for (ConceptUnit cu:splitted)
        {
            check = check.concat(cu.getRawText());
        }
        
        if (check.equals(rawText))
            throw new Exception();
        else
            DocumentModel.Instance().CreateDocument(rawText, splitted);
        
                
    }
    
}