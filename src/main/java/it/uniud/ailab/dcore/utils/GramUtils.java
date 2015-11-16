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
package it.uniud.ailab.dcore.utils;

import it.uniud.ailab.dcore.persistence.Gram;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility methods on {@link it.uniud.ailab.dcore.persistence.Gram}s.
 *
 * @author Marco Basaldella
 */
public class GramUtils {

    public static List<String> getSurfaces(Collection<Gram> source) {

        ArrayList<String> results = new ArrayList<>();

        for (Gram element : source) {
            results.add(element.getSurface());
        }
        return results;
    }
    
    public static List<String> getIdentifiers(Collection<Gram> source) {

        ArrayList<String> results = new ArrayList<>();

        for (Gram element : source) {
            results.add(element.getIdentifier());
        }
        return results;
    }

}
