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

/**
 * A simple class representing a pair. Useful when multiple return values
 * are needed. Note that differs from {@link it.uniud.ailab.dcore.utils.Either}
 * in the fact that the Pair has two values of type L or R, while Either
 * holds only a value of type L either R.
 *
 * @author Marco Basaldella
 * @param <L> the type of the left object.
 * @param <R> the type of the right object.
 */
public class Pair<L,R> {
    
    /**
     * The left value.
     */
    private final L left;
    /**
     * The right value.
     */
    private final R right;
    
    /**
     * Constructs a pair.
     * 
     * @param l the left object.
     * @param r the right object.
     */
    public Pair(L l, R r) {
        this.left = l;
        this.right = r;
    }
    
    /**
     * Returns the left object.
     * 
     * @return the left object.
     */
    public L getLeft() {
        return left;
    }
    
    /**
     * Returns the right object.
     * 
     * @return the right object.
     */    
    public R getRight() {
        return right;
    }
}
