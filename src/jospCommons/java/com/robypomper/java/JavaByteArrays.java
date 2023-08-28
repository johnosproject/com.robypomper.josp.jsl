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

import java.util.Arrays;


/**
 * Utils class to help managing byte arrays.
 */
public class JavaByteArrays {

    // Byte array utils

    /**
     * Returns the index within give byte array of the first occurrence of
     * the specified <code>target</code>
     *
     * @param array  the byte array.
     * @param target the byte array to search for.
     * @return the index of the first occurrence of <code>target</code> array,
     * -1 otherwise.
     */
    public static int indexOf(byte[] array, byte[] target) {
        if (array.length < target.length)
            return -1;

        for (int i = 0; i < array.length - target.length + 1; i++)
            if (array[i] == target[0]) {
                boolean contains = true;
                for (int k = 0; k < target.length; k++)
                    contains &= array[i + k] == target[k];
                if (contains)
                    return i;
            }
        return -1;
    }

    /**
     * Returns true if and only if given byte array contains the
     * <code>target</code> array.
     *
     * @param array  the byte array.
     * @param target the byte array to search for.
     * @return true if <code>array</code> contains <code>target</code>, false
     * otherwise
     */
    public static boolean contains(byte[] array, byte[] target) {
        return indexOf(array, target) >= 0;
    }

    /**
     * Concatenate given byte arrays.
     *
     * @param first  first byte array.
     * @param second second byte array.
     * @return an array compose by <code>first</code> and <code>second</code>
     * arrays.
     */
    public static byte[] append(byte[] first, byte[] second) {
        byte[] dataTmp = new byte[first.length + second.length];
        System.arraycopy(first, 0, dataTmp, 0, first.length);
        System.arraycopy(second, 0, dataTmp, first.length, second.length);
        return dataTmp;
    }

    /**
     * Remove all initial and final spaces (<code>' '</code> char) from given
     * byte array.
     *
     * @param array the array to trim.
     * @return the array without initial and final spaces.
     */
    public static byte[] trim(byte[] array) {
        int from = 0;
        int to = array.length;
        while (from < array.length && array[from] == ' ')
            from++;
        while (to > 0 && array[to - 1] == ' ')
            to--;

        if (from >= to)
            return new byte[0];

        return Arrays.copyOfRange(array, from, to);
    }

    /**
     * Return a given array removing all bytes after <code>delimiter</code> included.
     *
     * @param array     the array to reduce.
     * @param delimiter the byte array that delimit the returned array.
     * @return given <code>array</code> until <code>delimiter</code> occurrence.
     */
    public static byte[] before(byte[] array, byte[] delimiter) {
        int index = indexOf(array, delimiter);
        return Arrays.copyOf(array, index);
    }

    /**
     * Return a given array removing all bytes before <code>delimiter</code> included.
     *
     * @param array     the array to reduce.
     * @param delimiter the byte array that delimit the returned array.
     * @return given <code>array</code> from <code>delimiter</code> occurrence.
     */
    public static byte[] after(byte[] array, byte[] delimiter) {
        int index = indexOf(array, delimiter);
        if (index == -1)
            return new byte[0];
        return Arrays.copyOfRange(array, index + delimiter.length, array.length);
    }

}
