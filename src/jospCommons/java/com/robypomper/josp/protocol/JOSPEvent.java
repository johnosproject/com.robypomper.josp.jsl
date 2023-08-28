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
import com.robypomper.josp.defs.core.events.Params20;
import com.robypomper.josp.types.josp.AgentType;
import com.robypomper.josp.types.josp.EventType;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Messaging types to use in Events messaging classes.
 */
public class JOSPEvent {

    // Internal constants

    private static final String EVENTS_REQ_FORMAT = "id:%s;type:%s;srcId:%s;srcType:%s;emittedAt:%s;phase:%s;payload:%s;errorPayload:%s";


    // Internal vars

    private final long id;
    private final EventType type;
    private final String srcId;
    private final AgentType srcType;
    private final Date emittedAt;
    private final String phase;
    private final String payload;
    private final String errorPayload;


    // Constructor

    @JsonCreator
    public JOSPEvent(@JsonProperty("id") long id,
                     @JsonProperty("type") EventType type,
                     @JsonProperty("srcId") String srcId,
                     @JsonProperty("srcType") AgentType srcType,
                     @JsonProperty("emittedAt") Date emittedAt,
                     @JsonProperty("phase") String phase,
                     @JsonProperty("payload") String payload,
                     @JsonProperty("errorPayload") String errorPayload) {
        this.id = id;
        this.type = type;
        this.srcId = srcId;
        this.srcType = srcType;
        this.emittedAt = emittedAt;
        this.phase = phase;
        this.payload = payload;
        this.errorPayload = errorPayload;
    }


    // Getters

    public long getId() {
        return id;
    }

    public EventType getType() {
        return type;
    }

    public String getSrcId() {
        return srcId;
    }

    public AgentType getSrcType() {
        return srcType;
    }

    public Date getEmittedAt() {
        return emittedAt;
    }

    @JsonIgnore
    public String getEmittedAtStr() {
        return JavaDate.DEF_DATE_FORMATTER.format(emittedAt);
    }

    public String getPhase() {
        return phase;
    }

    public String getPayload() {
        return payload;
    }

    public String getErrorPayload() {
        return errorPayload;
    }


    // Converters

    public static Params20.Event toEvent(JOSPEvent event) {
        Params20.Event eventRes = new Params20.Event();
        eventRes.id = event.getId();
        eventRes.type = event.getType();
        eventRes.srcId = event.getSrcId();
        eventRes.srcType = event.getSrcType();
        eventRes.emittedAt = event.getEmittedAt();
        eventRes.phase = event.phase;
        eventRes.payload = event.getPayload();
        eventRes.errorPayload = event.getErrorPayload();
        return eventRes;
    }

    public static List<Params20.Event> toEvents(List<JOSPEvent> events) {
        List<Params20.Event> eventsRes = new ArrayList<>();
        for (JOSPEvent e : events)
            eventsRes.add(toEvent(e));
        return eventsRes;
    }

    public static String toString(JOSPEvent event) {
        return String.format(EVENTS_REQ_FORMAT, event.getId(), event.getType(), event.getSrcId(), event.getSrcType(), JavaDate.DEF_DATE_FORMATTER.format(event.getEmittedAt()), event.phase, event.getPayload(), event.getErrorPayload());
    }

    public static String toString(List<JOSPEvent> statusesHistory) {
        StringBuilder str = new StringBuilder();
        for (JOSPEvent e : statusesHistory) {
            str.append(toString(e));
            str.append("\n");
        }

        return str.toString();
    }

    public static JOSPEvent fromString(String eventStr) throws JOSPProtocol.ParsingException {
        String[] eventStrs = eventStr.split(";");
        if (eventStrs.length != 8)
            throw new JOSPProtocol.ParsingException("Few fields in JOSPEvent string");

        String id = eventStrs[0].substring(eventStrs[0].indexOf(":") + 1);
        String type = eventStrs[1].substring(eventStrs[1].indexOf(":") + 1);
        String srcId = eventStrs[2].substring(eventStrs[2].indexOf(":") + 1);
        String srcType = eventStrs[3].substring(eventStrs[3].indexOf(":") + 1);
        String emittedAt = eventStrs[4].substring(eventStrs[4].indexOf(":") + 1);
        String phase = eventStrs[5].substring(eventStrs[5].indexOf(":") + 1);
        String payload = eventStrs[6].substring(eventStrs[6].indexOf(":") + 1);
        String errorPayload = eventStrs[7].substring(eventStrs[7].indexOf(":") + 1);

        try {
            return new JOSPEvent(Long.parseLong(id), EventType.valueOf(type), srcId, AgentType.valueOf(srcType), JavaDate.DEF_DATE_FORMATTER.parse(emittedAt), phase, payload, errorPayload);

        } catch (ParseException e) {
            throw new JOSPProtocol.ParsingException(String.format("Error parsing JOSPEvent fields: %s", e.getMessage()));
        }
    }

    public static List<JOSPEvent> listFromString(String eventsHistoryStr) throws JOSPProtocol.ParsingException {
        List<JOSPEvent> statuses = new ArrayList<>();

        for (String eventStr : eventsHistoryStr.split("\n"))
            try {
                statuses.add(fromString(eventStr));

            } catch (JOSPProtocol.ParsingException e) {
                System.err.println(String.format("Error on JOSPProtocol.ParsingException event, skip current event and continue (skipped event: %S)", eventStr));
            }

        return statuses;
    }

    public static String logEvents(List<JOSPEvent> events, boolean showSrcInfo) {
        StringBuilder str = new StringBuilder();
        if (showSrcInfo) {
            str.append("  +-------+--------------------+------------+------------+----------------------+----------------------+------------------------------------------+------------------------------------------+\n");
            str.append("  | ID    | Emitted At         | SrcType    | SrcId      | EventType            | Phase                | Payload                                  | Error                                    |\n");
            //str.append("  | 00000 | 20201029-172422355 | 1234567890 | 1234567890 | 12345678901234567890 | 12345678901234567890 | 1234567890123456789012345678901234567890 | 1234567890123456789012345678901234567890 |\n");
            str.append("  +-------+--------------------+------------+------------+----------------------+----------------------+------------------------------------------+------------------------------------------+\n");
        } else {
            str.append("  +-------+--------------------+----------------------+----------------------+------------------------------------------+------------------------------------------+\n");
            str.append("  | ID    | Emitted At         | EventType            | Phase                | Payload                                  | Error                                    |\n");
            //str.append("  | 00000 | 20201029-172422355 | 12345678901234567890 | 12345678901234567890 | 1234567890123456789012345678901234567890 | 1234567890123456789012345678901234567890 |\n");
            str.append("  +-------+--------------------+----------------------+----------------------+------------------------------------------+------------------------------------------+\n");
        }

        for (JOSPEvent e : events) {
            if (showSrcInfo) {
                int pLength = e.getPayload().length();
                String payloadTruncated = pLength < 30 ? e.getPayload() : "..." + e.getPayload().substring(pLength - 37);
                int peLength = e.getErrorPayload().length();
                String payloadErrorTruncated = pLength < 30 ? e.getErrorPayload() : "..." + e.getErrorPayload().substring(peLength - 37);
                str.append(String.format("  | %-5s | %-18s | %-10s | %-10s | %-10s | %-10s | %-30s | %-30s |\n",
                        e.getId(), e.getEmittedAtStr(), e.getType(), e.getSrcType(), e.getSrcId(), e.getPhase(), payloadTruncated, payloadErrorTruncated));
            } else
                str.append(String.format("  | %-5s | %-18s | %-20s | %-20s | %-30s | %-30s\n",
                        e.getId(), e.getEmittedAtStr(), e.getType(), e.getPhase(), e.getPayload(), e.getErrorPayload()));
        }

        if (showSrcInfo)
            str.append("  +-------+--------------------+---------------+---------------+-------------------------+----------------------+------------------------------------------+------------------------------------------+\n");
        else
            str.append("  +-------+--------------------+-------------------------+----------------------+------------------------------------------+------------------------------------------+\n");

        return str.toString();
    }

}
