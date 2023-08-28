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
import com.robypomper.java.JavaDate;
import com.robypomper.josp.defs.core.objects.Params20;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Messaging types to use in Events messaging classes.
 */
public class JOSPStatusHistory {

    private static final String STATUS_HISTORY_REQ_FORMAT = "id:%s;compPath:%s;compType:%s;updatedAt:%s;payload:%s";

    private final long id;
    private final String compPath;
    private final String compType;
    private final Date updatedAt;
    private final String payload;

    @JsonCreator
    public JOSPStatusHistory(@JsonProperty("id") long id,
                             @JsonProperty("compPath") String compPath,
                             @JsonProperty("compType") String compType,
                             @JsonProperty("updatedAt") Date updatedAt,
                             @JsonProperty("payload") String payload) {
        this.id = id;
        this.compPath = compPath;
        this.compType = compType;
        this.updatedAt = updatedAt;
        this.payload = payload;
    }


    // Getters

    public long getId() {
        return id;
    }


    public String getCompPath() {
        return compPath;
    }

    public String getCompType() {
        return compType;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @JsonIgnore
    public String getUpdatedAtStr() {
        return JavaDate.DEF_DATE_FORMATTER.format(updatedAt);
    }

    public String getPayload() {
        return payload;
    }


    // Converters

    public static Params20.HistoryStatus toHistoryStatus(JOSPStatusHistory history) {
        Params20.HistoryStatus historyRes = new Params20.HistoryStatus();
        historyRes.id = history.getId();
        historyRes.compPath = history.getCompPath();
        historyRes.compType = history.getCompType();
        historyRes.updatedAt = history.getUpdatedAt();
        historyRes.payload = history.getPayload();
        return historyRes;
    }

    public static List<Params20.HistoryStatus> toHistoryStatuses(List<JOSPStatusHistory> histories) {
        List<Params20.HistoryStatus> historyRes = new ArrayList<>();
        for (JOSPStatusHistory h : histories)
            historyRes.add(toHistoryStatus(h));
        return historyRes;
    }

    public static String toString(JOSPStatusHistory statusHistory) {
        return String.format(STATUS_HISTORY_REQ_FORMAT, statusHistory.getId(), statusHistory.getCompPath(), statusHistory.getCompType(), JavaDate.DEF_DATE_FORMATTER.format(statusHistory.getUpdatedAt()), statusHistory.getPayload());
    }

    public static String toString(List<JOSPStatusHistory> statusesHistory) {
        StringBuilder str = new StringBuilder();
        for (JOSPStatusHistory s : statusesHistory) {
            str.append(toString(s));
            str.append("\n");
        }

        return str.toString();
    }

    public static JOSPStatusHistory fromString(String statusesHistoryStr) throws JOSPProtocol.ParsingException {
        String[] statusHistoryStrs = statusesHistoryStr.split(";");
        if (statusHistoryStrs.length != 5)
            throw new JOSPProtocol.ParsingException("Few fields in JOSPStatusHistory string");

        String id = statusHistoryStrs[0].substring(statusHistoryStrs[0].indexOf(":") + 1);
        String compPath = statusHistoryStrs[1].substring(statusHistoryStrs[1].indexOf(":") + 1);
        String compType = statusHistoryStrs[2].substring(statusHistoryStrs[2].indexOf(":") + 1);
        String updatedAt = statusHistoryStrs[3].substring(statusHistoryStrs[3].indexOf(":") + 1);
        String payload = statusHistoryStrs[4].substring(statusHistoryStrs[4].indexOf(":") + 1);

        try {
            return new JOSPStatusHistory(Long.parseLong(id), compPath, compType, JavaDate.DEF_DATE_FORMATTER.parse(updatedAt), payload);
        } catch (ParseException e) {
            throw new JOSPProtocol.ParsingException(String.format("Error parsing JOSPStatusHistory fileds: %s", e.getMessage()));
        }
    }

    public static List<JOSPStatusHistory> listFromString(String statusesHistoryStr) throws JOSPProtocol.ParsingException {
        List<JOSPStatusHistory> statuses = new ArrayList<>();

        for (String statusStr : statusesHistoryStr.split("\n"))
            statuses.add(fromString(statusStr));

        return statuses;
    }

    public static String logStatuses(List<JOSPStatusHistory> statusesHistory, boolean showCompInfo) {
        StringBuilder str = new StringBuilder();
        if (showCompInfo) {
            str.append("  +-------+--------------------+---------------+------------------------------------------+--------------------------------+\n");
            str.append("  | ID    | Updated At         | CompType      | CompPath                                 | Payload                        |\n");
            //str.append("  | 00000 | 20201029-172422355 | BooleanAction | 1234567890123456789012345678901234567890 | 123456789012345678901234567890 |\n");
            str.append("  +-------+--------------------+---------------+------------------------------------------+--------------------------------+\n");
        } else {
            str.append("  +-------+--------------------+--------------------------------+\n");
            str.append("  | ID    | Updated At         | Payload                        |\n");
            //str.append("  | 00000 | 20201029-172422355 | 123456789012345678901234567890 |\n");
            str.append("  +-------+--------------------+--------------------------------+\n");

        }

        for (JOSPStatusHistory s : statusesHistory) {
            if (showCompInfo) {
                int pathLength = s.getCompPath().length();
                String pathTruncated = pathLength<40 ? s.getCompPath() : "..." + s.getCompPath().substring(pathLength-37);
                str.append(String.format("  | %-5s | %-18s | %-13s | %-40s | %-30s |\n",
                        s.getId(), s.getUpdatedAtStr(), s.getCompType(), pathTruncated, s.getPayload()));
            } else
                str.append(String.format("  | %-5s | %-18s | %-30s |\n",
                        s.getId(), s.getUpdatedAtStr(), s.getPayload()));
        }

        if (showCompInfo)
            str.append("  +-------+--------------------+---------------+------------------------------------------+--------------------------------+\n");
        else
            str.append("  +-------+--------------------+--------------------------------+\n");

        return str.toString();
    }

}
