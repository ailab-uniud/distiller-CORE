/*
 * Copyright (C) 2017 Artificial Intelligence
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
package it.uniud.ailab.dcore.utils;

import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Token;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Muhammad Helmy
 */
public class ArabicDocComponents extends Gram{
    public static final String ARABICDOCCOMPONENTS = "ARABICDOCCOMPONENTS";
    public String originalText, preProcessedText, parsedText, taggedText, segmentedText;
    //NPs: contains all the legal noun phrases found by the parser in the text
    public ArrayList<Phrase> NPs;
    public ArabicDocComponents(String identifier, List<Token> sequence, String surface, String type) {
        super(identifier, sequence, surface,ARABICDOCCOMPONENTS);
        NPs = new ArrayList<Phrase>();
    }    
}
