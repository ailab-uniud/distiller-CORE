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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility to work on lists.
 *
 * @author Marco Basaldella
 */
public class ListUtils {

    /**
     * Return the most common element in a list. If there are two or more 
     * most common elements in a list, return the first one encountered.
     * 
     * @param <T> the type of the elements of the list
     * @param list the list to analyze
     * @return the most common element
     */
    public static <T> T mostCommon(List<T> list) {
        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Entry<T, Integer> max = null;

        for (Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue()) {
                max = e;
            }
        }

        return max == null? null : max.getKey();
    }
    
}
