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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


/**
 * Convenient class to convert common values from/to string.
 */
public class JavaFormatter {

    // Independent comparison for Boolean

    /**
     * List of possible <code>true</code> values for string to boolean
     * conversion.
     */
    public static final List<String> TRUE_ALIASES = Arrays.asList("TRUE", "1", "ON", "HIGH", "OPEN", "FULL");

    public static final String TRUE_STR = "TRUE";
    public static final String FALSE_STR = "FALSE";
    public static final String TRUE_STR_BIN = "1";
    public static final String FALSE_STR_BIN = "0";

    // Independent locale formatter for Double

    /**
     * Default locale that use point as decimal separator.
     */
    public static final Locale LOCALE_POINT_DEC = Locale.US;

    /**
     * Default locale that use comma as decimal separator.
     */
    public static final Locale LOCALE_COMMA_DEC = Locale.ITALY;

    /**
     * Default locale used by this class ({@link #LOCALE_POINT_DEC}).
     */
    public static final Locale LOCALE_STANDARD = LOCALE_POINT_DEC;


    // Boolean convert methods

    /**
     * Convert given boolean to string.
     *
     * @param b the boolean to convert.
     * @return {@value TRUE_STR} if given param is `true`, otherwsise the {@value FALSE_STR}.
     */
    public static String booleanToString(boolean b) {
        return b ? TRUE_STR : FALSE_STR;
    }

    /**
     * Convert given boolean to string.
     *
     * @param b the boolean to convert.
     * @return {@value TRUE_STR} if given param is `true`, otherwsise the {@value FALSE_STR}.
     */
    public static String booleanToStringBin(boolean b) {
        return b ? TRUE_STR_BIN : FALSE_STR_BIN;
    }

    /**
     * Convert given string to boolean.
     * <p>
     * This method check if given string is present in the {@link #TRUE_ALIASES}.
     * list. If it is, then return true.
     *
     * @param s the string to convert.
     * @return true if given string is contained in {@link #TRUE_ALIASES}.
     */
    public static boolean strToBoolean(String s) {
        return TRUE_ALIASES.contains(s);
    }


    // Double convert methods

    /**
     * Convert given double to string using {@link #LOCALE_STANDARD}.
     *
     * @param d the double to convert.
     * @return a String representing given double (with point decimal separator).
     */
    public static String doubleToStr(double d) {
        return String.format(LOCALE_STANDARD, "%f", d);
    }

    /**
     * Convert given double to string using {@link #LOCALE_POINT_DEC}.
     *
     * @param d the double to convert.
     * @return a String representing given double (with point decimal separator).
     */
    public static String doubleToStr_Point(double d) {
        return String.format(LOCALE_POINT_DEC, "%f", d);
    }

    /**
     * Convert given double to string using {@link #LOCALE_COMMA_DEC}.
     *
     * @param d the double to convert.
     * @return a String representing given double (with comma decimal separator).
     */
    public static String doubleToStr_Comma(double d) {
        return String.format(LOCALE_COMMA_DEC, "%f", d);
    }

    /**
     * Truncate and convert given double to string using {@link #LOCALE_STANDARD}.
     * <p>
     * Before conversion, given double is rounded ({@link Math#round(double)} to
     * integer.
     *
     * @param d the double to convert.
     * @return a String representing given double.
     */
    public static String doubleToStr_Truncated(double d) {
        return Integer.toString((int) Math.round(d));
    }

    /**
     * Convert given string to double.
     * <p>
     * This method try to manage string as double with point decimal separator,
     * if can't then it try to manage it with comma decimal separator.
     *
     * @param s the string to convert.
     * @return the parsed double or null if {@link ParseException} was throw.
     */
    public static Double strToDouble(String s) {
        try {
            try {
                try {
                    return (Double) (NumberFormat.getInstance(LOCALE_POINT_DEC)).parse(s);

                } catch (ClassCastException e) {
                    return (Double) (NumberFormat.getInstance(LOCALE_COMMA_DEC)).parse(s);
                }

            } catch (ClassCastException e) {
                return (NumberFormat.getInstance(LOCALE_POINT_DEC)).parse(s).doubleValue();
            }

        } catch (ParseException e) {
            return null;
        }
    }

}
