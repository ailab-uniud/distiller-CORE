/*
 * Copyright (C) 2016 Artificial Intelligence
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
package it.uniud.ailab.dcore.annotation.annotators;

/**
 * This almost-empty interface is used to promote the interchangeability 
 * between different annotators: every annotator that is used to detect sections
 * inside the text should implement this interface and use the constants
 * defined in this interface to name sections.
 
 * @author Marco Basaldella
 */
public interface GenericStructureAnnotator {
    
    public static final String SECTION_TITLE = "Title";
    
    public static final String SECTION_ABSTRACT = "Abstract";
    
    public static final String SECTION_PREFIX = "Section_";
    
}
