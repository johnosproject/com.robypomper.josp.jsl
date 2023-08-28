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
 * Enum type for JVM's version management and comparison.
 */
public enum JavaVersion {

    /**
     * Dynamic JavaVersion value that correspond to current JVM version.
     */
    JAVA_CURRENT(Double.parseDouble(System.getProperty("java.specification.version"))),

    /**
     * JVM version 1.5.
     */
    JAVA_5(1.5),

    /**
     * JVM version 1.6.
     */
    JAVA_6(1.6),

    /**
     * JVM version 1.7.
     */
    JAVA_7(1.7),

    /**
     * JVM version 1.8 (Java 8).
     */
    JAVA_8(1.8),

    /**
     * JVM version 9.
     */
    JAVA_9(9),

    /**
     * JVM version 10.
     */
    JAVA_10(10),

    /**
     * JVM version 11.
     */
    JAVA_11(11),

    /**
     * JVM version 12.
     */
    JAVA_12(12),

    /**
     * JVM version 13.
     */
    JAVA_13(13),

    /**
     * JVM version 14.
     */
    JAVA_14(14),

    /**
     * JVM version 15.
     */
    JAVA_15(15),

    /**
     * JVM version 16.
     */
    JAVA_16(16),

    /**
     * JVM version 17.
     */
    JAVA_17(17);


    // Internal vars

    private double val;


    // Constructors

    JavaVersion(double val) {
        this.val = val;
    }


    // Comparison methods

    /**
     * Equal JavaVersion comparison.
     *
     * @param other the JavaVersion to compare with.
     * @return true only if current Java Version and <code>other</code> are
     * equals.
     */
    public boolean equal(JavaVersion other) {
        return val == other.val;
    }

    /**
     * Greater JavaVersion comparison.
     *
     * @param other the JavaVersion to compare with.
     * @return true only if current Java Version is greater than
     * <code>other</code>.
     */
    public boolean greater(JavaVersion other) {
        return val > other.val;
    }

    /**
     * Greater or equals JavaVersion comparison.
     *
     * @param other the JavaVersion to compare with.
     * @return true only if current Java Version is greater or equals than
     * <code>other</code>.
     */
    public boolean greaterEqual(JavaVersion other) {
        return val >= other.val;
    }

    /**
     * Lesser JavaVersion comparison.
     *
     * @param other the JavaVersion to compare with.
     * @return true only if current Java Version is lesser than
     * <code>other</code>.
     */
    public boolean lesser(JavaVersion other) {
        return val < other.val;
    }

    /**
     * Lesser or equals JavaVersion comparison.
     *
     * @param other the JavaVersion to compare with.
     * @return true only if current Java Version is lesse or equals than
     * <code>other</code>.
     */
    public boolean lesserEqual(JavaVersion other) {
        return val <= other.val;
    }

    /**
     * Convert current JavaVersion to string.
     *
     * @return the String representing the JavaVersion number.
     */
    public String toString() {
        return Double.toString(val);
    }

}
