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

package com.robypomper.josp.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.java.JavaDate;

import java.util.Calendar;
import java.util.Date;


@SuppressWarnings("unused")
public class HistoryLimits {

    // Default HistoryLimits
    public static Date LAST_HOUR_FROM() { return JavaDate.getDateAltered(JavaDate.getNowDate(), Calendar.HOUR_OF_DAY, -1); }
    public static Date PAST_HOUR_FROM() { return JavaDate.getDateExactAltered(Calendar.HOUR_OF_DAY, Calendar.HOUR_OF_DAY, -1); }
    public static Date PAST_HOUR_TO() { return JavaDate.getDateExact(Calendar.HOUR_OF_DAY); }

    public static HistoryLimits NO_LIMITS() { return new HistoryLimits(null, null, null, null, null, null, null, null); }
    public static HistoryLimits LATEST(int count) { return new HistoryLimits(count, null, null, null, null, null, null, null); }
    public static HistoryLimits ANCIENT(int count) { return new HistoryLimits(null, count, null, null, null, null, null, null); }
    public static HistoryLimits FROM_ID(long id) { return new HistoryLimits(null, null, id, null, null, null, null, null); }
    public static HistoryLimits TO_ID(long id) { return new HistoryLimits(null, null, null, id, null, null, null, null); }
    public static HistoryLimits BETWEEN_ID(long start, long end) { return new HistoryLimits(null, null, start, end, null, null, null, null); }
    public static HistoryLimits LAST_HOUR() { return new HistoryLimits(null, null, null, null, LAST_HOUR_FROM(), null, null, null); }
    public static HistoryLimits PAST_HOUR() { return new HistoryLimits(null, null, null, null, PAST_HOUR_FROM(), PAST_HOUR_TO(), null, null); }
    public static HistoryLimits PAGE(int page, int size) { return new HistoryLimits(null, null, null, null, null, null, page, size); }
    public static HistoryLimits ERROR() { return new HistoryLimits(10, 10, null, null, null, null, null, null); }

    // Internal consts
    private static final String LATEST_JSON_FORMATTER = "{ \"latestCount\":\"%d\" }";
    private static final String ANCIENT_JSON_FORMATTER = "{ \"ancientCount\":\"%d\" }";
    private static final String ID_RANGE_JSON_FORMATTER_A = "\"fromID\":\"%d\"";
    private static final String ID_RANGE_JSON_FORMATTER_B = ", \"toID\":\"%d\"";
    private static final String DATE_RANGE_JSON_FORMATTER_A = "\"fromDate\":\"%d\"";
    private static final String DATE_RANGE_JSON_FORMATTER_B = ", \"toDate\":\"%d\"";
    private static final String PAGE_RANGE_JSON_FORMATTER_A = "\"pageNum\":\"%d\"";
    private static final String PAGE_RANGE_JSON_FORMATTER_B = ", \"pageSize\":\"%d\"";


    // Internal vars

    @JsonProperty("latestCount")
    private int latestCount = -1;
    @JsonProperty("ancientCount")
    private int ancientCount = -1;
    @JsonProperty("fromID")
    private long fromID = -1;
    @JsonProperty("toID")
    private long toID = -1;
    @JsonProperty("fromDate")
    private Date fromDate = null;
    @JsonProperty("toDate")
    private Date toDate = null;
    @JsonProperty("pageNum")
    private int pageNum = -1;
    @JsonProperty("pageSize")
    private int pageSize = -1;


    // Constructor

    @JsonCreator
    public HistoryLimits(@JsonProperty("latestCount") Integer latestCount,
                         @JsonProperty("ancientCount") Integer ancientCount,
                         @JsonProperty("fromID") Long fromID,
                         @JsonProperty("toID") Long toID,
                         @JsonProperty("fromDate") Date fromDate,
                         @JsonProperty("toDate") Date toDate,
                         @JsonProperty("pageNum") Integer pageNum,
                         @JsonProperty("pageSize") Integer pageSize) {
        this.latestCount = latestCount!=null ? latestCount : this.latestCount;
        this.ancientCount = ancientCount!=null ? ancientCount : this.ancientCount;
        this.fromID = fromID!=null ? fromID : this.fromID;
        this.toID = toID!=null ? toID : this.toID;
        this.fromDate = fromDate!=null ? fromDate : this.fromDate;
        this.toDate = toDate!=null ? toDate : this.toDate;
        this.pageNum = pageNum!=null ? pageNum : this.pageNum;
        this.pageSize = pageSize!=null ? pageSize : this.pageSize;

        checkHistoryLimitsExclusivity();
    }

    private void checkHistoryLimitsExclusivity() {
        int count = 0;
        count += isLatestCount(this) ? 1 : 0;
        count += isAncientCount(this) ? 1 : 0;
        count += isIDRange(this) ? 1 : 0;
        count += isDateRange(this) ? 1 : 0;
        count += isPageRange(this) ? 1 : 0;
        if (count<=1)
            return;

        if (isLatestCount(this)
                && (isAncientCount(this) || isIDRange(this) || isDateRange(this) || isPageRange(this))) {
            if (isAncientCount(this))
                throw new IllegalArgumentException("isLatestCount and isAncientCount");
            if (isIDRange(this))
                throw new IllegalArgumentException("isLatestCount and isIDRange");
            if (isDateRange(this))
                throw new IllegalArgumentException("isLatestCount and isDateRange");
            throw new IllegalArgumentException("isLatestCount and isPageRange");
        }

        if (isAncientCount(this)
                && (isIDRange(this) || isDateRange(this)  || isPageRange(this))) {
            if (isIDRange(this))
                throw new IllegalArgumentException("isAncientCount and isIDRange");
            if (isDateRange(this))
                throw new IllegalArgumentException("isAncientCount and isDateRange");
            throw new IllegalArgumentException("isAncientCount and isPageRange");
        }

        if (isIDRange(this)
                && (isDateRange(this)  || isPageRange(this))) {
            if (isDateRange(this))
                throw new IllegalArgumentException("isIDRange and isDateRange");
            throw new IllegalArgumentException("isIDRange and isPageRange");
        }

        if (isDateRange(this)
                && (isPageRange(this))) {
            throw new IllegalArgumentException("isDateRange and isPageRange");
        }
    }


    // HistoryLimits type checks

    public static HistoryLimits fromString(String limitsStr) throws JOSPProtocol.ParsingException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(limitsStr, HistoryLimits.class);

        } catch (JsonProcessingException e) {
            throw new JOSPProtocol.ParsingException(String.format("Can't deserialize '%s' string to HistoryLimits.", limitsStr), e);
        }
    }

    public static String toString(HistoryLimits limits) {

        // Jackson method, not working! It put all fields in the serialized output
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            return mapper.writeValueAsString(limits);
//
//        } catch (JsonProcessingException e) {
//            throw new JOSPProtocol.ParsingException(String.format("Can't serialize '%s' HistoryLimits to string.", limits),e);
//        }


        if (isLatestCount(limits))
            return String.format(LATEST_JSON_FORMATTER, limits.latestCount);

        if (isAncientCount(limits))
            return String.format(ANCIENT_JSON_FORMATTER, limits.ancientCount);

        if (isIDRange(limits)) {
            String s = String.format(ID_RANGE_JSON_FORMATTER_A, limits.fromID);
            if (limits.toID != -1)
                s += String.format(ID_RANGE_JSON_FORMATTER_B, limits.toID);
            return "{" + s + "}";
        }

        if (isDateRange(limits)) {
            String s = String.format(DATE_RANGE_JSON_FORMATTER_A, limits.fromDate.getTime());
            if (limits.toDate != null)
                s += String.format(DATE_RANGE_JSON_FORMATTER_B, limits.toDate.getTime());
            return "{" + s + "}";
        }

        if (isPageRange(limits)) {
            String s = String.format(PAGE_RANGE_JSON_FORMATTER_A, limits.pageNum);
            if (limits.pageSize != -1)
                s += String.format(PAGE_RANGE_JSON_FORMATTER_B, limits.pageSize);
            return "{" + s + "}";
        }

        return "{}";



        // toString()
        //if (isLatestCount(this))
        //    return "latestCount: " + latestCount + "";
        //
        //if (isAncientCount(this))
        //    return "ancientCount: " + ancientCount + "";
        //
        //if (isPageRange(this)) {
        //    String s = "pageNum: " + pageNum + "";
        //    if (pageSize != -1)
        //        s += "; pageSize: " + pageSize + "";
        //    return s;
        //}
        //
        //if (isDateRange(this)) {
        //    String s = "fromDate: " + JavaDate.DEF_DATE_FORMATTER.format(fromDate) + "";
        //    if (toDate != null)
        //        s += "; toDate: " + JavaDate.DEF_DATE_FORMATTER.format(toDate) + "";
        //    return s;
        //}
        //
        //return "NoLimits";
    }


    // Getters

    public static boolean isLatestCount(HistoryLimits hl) {
        return hl.latestCount != -1;
    }

    public static boolean isAncientCount(HistoryLimits hl) {
        return hl.ancientCount != -1;
    }

    public static boolean isIDRange(HistoryLimits hl) {
        return hl.fromID != -1 || hl.toID != -1;
    }

    public static boolean isDateRange(HistoryLimits hl) {
        return hl.fromDate != null || hl.toDate != null;
    }

    public static boolean isPageRange(HistoryLimits hl) {
        return hl.pageNum != -1 || hl.pageSize != -1;
    }


    // Getters (required by swagger)

    @JsonIgnore
    public int getLatestCount() {
        return latestCount;
    }

    @JsonIgnore
    public int getAncientCount() {
        return ancientCount;
    }

    @JsonIgnore
    public long getFromID() {
        return fromID;
    }

    @JsonIgnore
    public long getFromIDOrDefault() {
        return fromID!=-1 ? fromID : 0;
    }

    @JsonIgnore
    public long getToID() {
        return toID;
    }

    @JsonIgnore
    public long getToIDOrDefault() {
        return toID!=-1 ? toID : Long.MAX_VALUE;
    }

    @JsonIgnore
    public Date getFromDate() {
        return fromDate;
    }

    @JsonIgnore
    public Date getFromDateOrDefault() {
        return fromDate!=null ? fromDate : new Date(0);
    }

    @JsonIgnore
    public Date getToDate() {
        return toDate;
    }

    @JsonIgnore
    public Date getToDateOrDefault() {
        //return toDate!=null ? toDate : new Date(Long.MAX_VALUE);    //                          64bit max date
        //return toDate!=null ? toDate : new Date(253402297199L);     // 23:59:59 31/12/9999      max date < year 4 digit
        return toDate!=null ? toDate : new Date(2147483647000L);    // 03:14:07 19/01/2038      32bit max date
    }

    @JsonIgnore
    public int getPageNum() {
        return pageNum;
    }

    @JsonIgnore
    public int getPageNumOrDefault() {
        return pageNum!=-1 ? pageNum : 0;
    }

    @JsonIgnore
    public int getPageSize() {
        return pageSize;
    }

    @JsonIgnore
    public int getPageSizeOrDefault() {
        return pageSize!=-1 ? pageSize : 10;
    }

    // Converters

    @Override
    public String toString() {
        return toString(this);
    }

}
