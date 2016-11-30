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

import java.util.Objects;

/**
 * A simple class representing a comparable pair. Useful when multiple return 
 * values are needed. This class that differs from 
 * {@link it.uniud.ailab.dcore.util.Pair} because it requires that the 
 * two memeber of the ComparablePair are Comparable themselves.<br/>
 * <br/>
 * The comparison is performed in left-to-right order. That is, <br/>
 * P1(l1,r1) < P2(l2,r2) iff l1 < r1 or ((l1 = l2) and (r1 < r2), <br/>
 * P1(l1,r1) = P2(l2,r2) iff l1 = r1 and r1 = r2 <br/>
 * P1(l1,r1) > P2(l2,r2) else <br/>
 * <br/>
 * For example, we have that<br/>
 * CP(1,2) < CP(2,2)<br/>
 * CP(2,2) = CP(2,2)<br/>
 * CP(3,2) > CP(2,2).
 *
 * @author Marco Basaldella
 * @param <L> the type of the left object.
 * @param <R> the type of the right object.
 */
public class ComparablePair<L extends Comparable,R extends Comparable> implements Comparable {
    
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
    public ComparablePair(L l, R r) {
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.left);
        hash = 71 * hash + Objects.hashCode(this.right);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ComparablePair<?, ?> other = (ComparablePair<?, ?>) obj;
        if (!Objects.equals(this.left, other.left)) {
            return false;
        }
        if (!Objects.equals(this.right, other.right)) {
            return false;
        }
        return true;
    }
    
    
    
    @Override
    public String toString() {
        return "L: {" + getLeft().toString() + "}; "
                + "R: {" + getRight().toString() + "}";
    }

    /**
     * Compares this object wit the specified object for order, comparing
     * the left item first and then, if the left items are equal, the right
     * item last.
     * 
     * @param obj the object to compare
     * @return a negative integer, zero, or a positive integer as this object is 
     * less than, equal to, or greater than the specified object. 
     */
    @Override
    public int compareTo(Object obj) {
        final ComparablePair<?, ?> other = (ComparablePair<?, ?>) obj;
        int leftComp = this.getLeft().compareTo(other.getLeft());        
        return leftComp != 0 ? leftComp :
                this.getRight().compareTo(other.getRight());
    }
}
