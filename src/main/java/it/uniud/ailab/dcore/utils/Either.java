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
 * A class that can store a value that can be of type either A or either B.
 *
 * While not "pure" functional code, this class is mean to be a lightweight
 * implementation of the classic functional {@code Either} type.
 *
 * @author Marco Basaldella
 * @param <A> the left type
 * @param <B> the right type
 */
public abstract class Either<A, B> {

    /**
     * The stored value.
     */
    protected Object value;

    /**
     * Private constructor, so that only Left and Right can be Either.
     */
    private Either() {
    }

    /**
     * Check if the instance is a Left
     *
     * @return true if is a Left
     */
    public abstract boolean isLeft();

    /**
     * Check if the instance is a Right
     *
     * @return true if is a Right
     */
    public abstract boolean isRight();

    /**
     * Get the L-value.
     *
     * @return the left value.
     */
    public abstract A getLeft();

    /**
     * Get the R-value.
     *
     * @return the right value.
     */
    public abstract B getRight();

    /**
     * Concrete implementation of the Left element of the Either.
     *
     * No JavaDoc inside the class since it's self-explanatory. See the abstract
     * class for explanations.
     *
     * @param <L> the left type
     * @param <R> the right type
     */
    public final static class Left<L, R> extends Either<L, R> {

        public Left(L l) {
            this.value = l;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public L getLeft() {
            return (L) value;
        }

        @Override
        public R getRight() {
            throw new UnsupportedOperationException(
                    "Trying to extract r-value from a Left");
        }
    }

    /**
     * Concrete implementation of the Right element of the Either.
     *
     * No javadoc inside the class since it's self-explanatory. See the abstract
     * class for explanations.
     *
     * @param <L> the left type
     * @param <R> the right type
     */
    public final static class Right<L, R> extends Either<L, R> {

        public Right(R r) {
            this.value = r;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public L getLeft() {
            throw new UnsupportedOperationException(
                    "Trying to extract l-value from a Right");
        }

        @Override
        public R getRight() {
            return (R) value;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Either<?, ?> other = (Either<?, ?>) obj;

        if (!(this.isLeft() == other.isLeft())) {
            return false;
        }

        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Either{" + 
                (isLeft() ? "left " : "right ") +
                "value=" + value + '}';
    }
}
