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
package org.uniud.dcore.persistence;

/**
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public abstract class ConceptUnit {
    
    // <editor-fold desc="abstract methods">
    public abstract String getRawText() ;
    // </editor-fold>
    
    /**
     * The language of the concept unit.
     */
    private String language;
    
    /**
     * Sets the language of the concept unit.
     * 
     * @param language the language of the unit, specified with the IETF language tag. 
     * @throws IllegalStateException if the language is set more than once.
     * @see <a href="http://tools.ietf.org/html/rfc5646">RFC5646</a> specification.
     */
    public void setLanguage(String language) throws IllegalStateException
    {
        if (this.language != null && !this.language.isEmpty())
            this.language = language;
        else 
            throw new IllegalStateException(String.format(
                    "Trying to set language %s on ConceptUnit which is already set as %s",
                    language,this.language));
    }
    

    
}
