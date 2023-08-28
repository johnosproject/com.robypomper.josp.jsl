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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Utils lass to generate {@link Date} instances.
 */
public class JavaDate {

    // Class constants

    /**
     * Date formatter to convert date in ordering strings.
     * <p>
     * Date's strings can be ordered alphabetically and then from ancient to
     * newer.
     */
    public static final SimpleDateFormat ORDERED_DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");

    /**
     * Default date formatter used by this class.
     */
    public static final SimpleDateFormat DEF_DATE_FORMATTER = ORDERED_DATE_FORMATTER;


    // Get Now

    /**
     * Return current date time.
     *
     * @return current date time as {@link Date} instance.
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * Return current date time.
     * <p>
     * This method convert the Date instance to String using the
     * {@link #DEF_DATE_FORMATTER}.
     *
     * @return current date time as {@link String} instance.
     */
    public static String getNow() {
        return DEF_DATE_FORMATTER.format(getNowDate());
    }

    /**
     * Return current date time.
     *
     * @param dateFormatter formatter to use for date conversion to string.
     * @return current date time as {@link String} instance.
     */
    public static String getNow(DateFormat dateFormatter) {
        return dateFormatter.format(getNowDate());
    }


    // Get Epoch

    /**
     * Return epoch (midnight of 1st january 1970 UTC) date time.
     *
     * @return epoch date time as {@link Date} instance.
     */
    public static Date getEpochDate() {
        return new Date(0L);
    }

    /**
     * Return epoch (midnight of 1st january 1970 UTC) date time.
     * <p>
     * This method convert the Date instance to String using the
     * {@link #DEF_DATE_FORMATTER}.
     *
     * @return epoch date time as {@link String} instance.
     */
    public static String getEpoch() {
        return DEF_DATE_FORMATTER.format(getEpochDate());
    }

    /**
     * Return epoch (midnight of 1st january 1970 UTC) date time.
     *
     * @param dateFormatter formatter to use for date conversion to string.
     * @return epoch date time as {@link String} instance.
     */
    public static String getEpoch(DateFormat dateFormatter) {
        return dateFormatter.format(getEpochDate());
    }


    // Date manipulation

    /**
     * Returns current date, resetting the values ​​from the given partition up
     * to milliseconds.
     *
     * @param timePartition the time partition to remove from returned date.
     *                      See {@link Calendar}.
     * @return the exact Date instance.
     * @see #getDateExact(Date, int)
     */
    public static Date getDateExact(int timePartition) {
        return getDateExact(getNowDate(), timePartition);
    }

    /**
     * Returns given date, resetting the values ​​from the given partition up
     * to milliseconds.
     * <p>
     * For example, if given partition is {@link Calendar#DAY_OF_MONTH} then
     * this method return the midnight of current date.
     * <p>
     * More examples:
     * <code>
     * getNow()                                 // Fri Feb 05 18:44:07 CET 2021 with ms
     * getDateExact(Calendar.YEAR)              // Fri Jan 01 00:00:00 CET 2021
     * getDateExact(Calendar.MONTH)             // Mon Feb 01 00:00:00 CET 2021
     * getDateExact(Calendar.DAY_OF_MONTH)      // Fri Feb 05 00:00:00 CET 2021
     * getDateExact(Calendar.HOUR_OF_DAY)       // Fri Feb 05 18:00:00 CET 2021
     * getDateExact(Calendar.MINUTE)            // Fri Feb 05 18:44:00 CET 2021
     * getDateExact(Calendar.SECOND)            // Fri Feb 05 18:44:07 CET 2021 without ms
     * </code>
     *
     * <b>NB:</b> only partitionTime values listed in the example are valid.
     *
     * @param date          the date to make exact.
     * @param timePartition the time partition to remove from returned date.
     *                      See {@link Calendar}.
     * @return the exact Date instance.
     */
    public static Date getDateExact(Date date, int timePartition) {
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setTime(date);
        if (timePartition == Calendar.YEAR)
            cal.set(Calendar.MONTH, 0);
        if (timePartition == Calendar.YEAR || timePartition == Calendar.MONTH)
            cal.set(Calendar.DAY_OF_MONTH, 1);
        if (timePartition == Calendar.YEAR || timePartition == Calendar.MONTH || timePartition == Calendar.DAY_OF_MONTH)
            cal.set(Calendar.HOUR_OF_DAY, 0);
        if (timePartition == Calendar.YEAR || timePartition == Calendar.MONTH || timePartition == Calendar.DAY_OF_MONTH || timePartition == Calendar.HOUR_OF_DAY)
            cal.set(Calendar.MINUTE, 0);
        if (timePartition == Calendar.YEAR || timePartition == Calendar.MONTH || timePartition == Calendar.DAY_OF_MONTH || timePartition == Calendar.HOUR_OF_DAY || timePartition == Calendar.MINUTE)
            cal.set(Calendar.SECOND, 0);
        if (timePartition == Calendar.YEAR || timePartition == Calendar.MONTH || timePartition == Calendar.DAY_OF_MONTH || timePartition == Calendar.HOUR_OF_DAY || timePartition == Calendar.MINUTE || timePartition == Calendar.SECOND)
            cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Alter given date, adding <code>count</code> units to given partition
     * time.
     *
     * @param timePartition the time partition to alter. See {@link Calendar}.
     * @param count         the unit to add to timePartition. If it's negative
     *                      number then it will subtracted.
     * @return the altered Date instance.
     * @see #getDateAltered(Date, int, int)
     */
    public static Date getDateAltered(int timePartition, int count) {
        return getDateAltered(getNowDate(), timePartition, count);
    }

    /**
     * Alter given date, adding <code>count</code> units to given partition
     * time.
     * <p>
     * For example, if given partition is {@link Calendar#DAY_OF_MONTH} and
     * <code>count</code> id 1 then it return a Date instance of current time
     * but of tomorrow date. At the same time if <code>count</code> is '-1'
     * it return a yesterday date.
     * <p>
     * More examples:
     * <code>
     * getNow()                                 // Fri Feb 05 18:44:07 CET 2021
     * getDateAltered(Calendar.YEAR,1)          // Fri Jan 01 00:00:00 CET 2022
     * getDateAltered(Calendar.MONTH,1)         // Sat Mar 01 00:00:00 CET 2021
     * getDateAltered(Calendar.DAY_OF_MONTH,1)  // Fri Feb 06 00:00:00 CET 2021
     * getDateAltered(Calendar.HOUR_OF_DAY,1)   // Fri Feb 05 19:00:00 CET 2021
     * getDateAltered(Calendar.MINUTE,1)        // Fri Feb 05 18:45:00 CET 2021
     * getDateAltered(Calendar.SECOND,1)        // Fri Feb 05 18:44:08 CET 2021
     * </code>
     *
     * <b>NB:</b> only partitionTime values listed in the example are valid.
     *
     * @param date          the date to alter.
     * @param timePartition the time partition to alter. See {@link Calendar}.
     * @param count         the unit to add to timePartition. If it's negative
     *                      number then it will subtracted.
     * @return the altered Date instance.
     */
    public static Date getDateAltered(Date date, int timePartition, int count) {
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setTime(date);
        if (timePartition == Calendar.YEAR)
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + count);
        if (timePartition == Calendar.MONTH)
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + count);
        if (timePartition == Calendar.DAY_OF_MONTH)
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + count);
        if (timePartition == Calendar.HOUR_OF_DAY)
            cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + count);
        if (timePartition == Calendar.MINUTE)
            cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + count);
        if (timePartition == Calendar.SECOND)
            cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) + count);
        return cal.getTime();
    }

    /**
     * Alter and return exact date from current date.
     * <p>
     * This method combine {@link #getDateExact} and {@link #getDateAltered} methods.
     *
     * @param exactTimePartition   the time partition to remove from returned
     *                             date. See {@link Calendar}.
     * @param alteredTimePartition the time partition to alter.
     *                             See {@link Calendar}.
     * @param alteredCount         the unit to add to timePartition. If it's negative
     *                             number then it will subtracted.
     * @return the exact and altered Date instance.
     */
    public static Date getDateExactAltered(int exactTimePartition, int alteredTimePartition, int alteredCount) {
        return getDateAltered(getDateExact(getNowDate(), exactTimePartition), alteredTimePartition, alteredCount);
    }

    /**
     * Alter and return exact date from given date.
     * <p>
     * This method combine {@link #getDateExact} and {@link #getDateAltered} methods.
     *
     * @param exactTimePartition   the time partition to remove from returned
     *                             date. See {@link Calendar}.
     * @param alteredTimePartition the time partition to alter.
     *                             See {@link Calendar}.
     * @param alteredCount         the unit to add to timePartition. If it's negative
     *                             number then it will subtracted.
     * @return the exact and altered Date instance.
     */
    public static Date getDateExactAltered(Date date, int exactTimePartition, int alteredTimePartition, int alteredCount) {
        return getDateAltered(getDateExact(date, exactTimePartition), alteredTimePartition, alteredCount);
    }

}
