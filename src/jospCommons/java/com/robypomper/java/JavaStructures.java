/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.java;

/**
 * Utils class for Java data structures missing on early Java versions.
 */
public class JavaStructures {

    /**
     * Implementation of Pair class.
     *
     * @param <F> type of first element of the pair.
     * @param <S> type of second element of the pair.
     */
    public static class Pair<F, S> {

        // Internal vars

        private final F first;
        private final S second;


        // Constructors

        /**
         * Default constructor for Pair object.
         *
         * @param first  first element of the pair.
         * @param second second element of the pair.
         */
        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }


        // Getters

        /**
         * @return the first element of the current object.
         */
        public F getFirst() {
            return first;
        }

        /**
         * @return the second element of the current object.
         */
        public S getSecond() {
            return second;
        }


        // Object methods override

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pair)) {
                return false;
            }
            Pair<?, ?> p = (Pair<?, ?>) o;
            return p.first.equals(first) && p.second.equals(second);
        }

        @Override
        public int hashCode() {
            return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode());
        }

        @Override
        public String toString() {
            return String.format("Pair{%s, %s}", first.toString(), second.toString());
        }

    }

    /**
     * Implementation of Triplet class.
     *
     * @param <F> type of first element of the pair.
     * @param <S> type of second element of the pair.
     * @param <T> type of third element of the pair.
     */
    public static class Triplet<F, S, T> extends Pair<F, S> {

        // Internal vars

        private final T third;


        // Constructors

        /**
         * Default constructor for Triplet object.
         *
         * @param first  first element of the pair.
         * @param second second element of the pair.
         * @param third  third element of the pair.
         */
        public Triplet(F first, S second, T third) {
            super(first, second);
            this.third = third;
        }


        // Getters

        /**
         * @return the third element of the current object.
         */
        public T getThird() {
            return third;
        }


        // Object methods override

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Triplet)) {
                return false;
            }
            Triplet<?, ?, ?> p = (Triplet<?, ?, ?>) o;
            return p.getFirst().equals(getFirst())
                    && p.getSecond().equals(getSecond())
                    && p.third.equals(third);
        }

        @Override
        public int hashCode() {
            return super.hashCode() ^ (third == null ? 0 : third.hashCode());
        }

        @Override
        public String toString() {
            return String.format("Pair{%s, %s, %s}", getFirst().toString(), getSecond().toString(), third);
        }

    }

}
