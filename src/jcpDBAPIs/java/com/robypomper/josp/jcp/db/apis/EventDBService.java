/*******************************************************************************
 * The John Cloud Platform is the set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jcp.db.apis;

import com.robypomper.josp.jcp.db.apis.entities.Event;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.types.josp.EventType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
public class EventDBService {

    private static final String ENTITY_ID = "evnId";
    private static final Sort.Direction ENTITY_PAGE_ORDER = Sort.Direction.DESC;
    private static final String ENTITY_PAGE_ORDER_FIELD = "emittedAt";

    // Internal vars

    private final EventRepository events;


    // Constructor

    public EventDBService(EventRepository events) {
        this.events = events;
    }


    // Access methods


    // Access methods

    public List<Event> findBySrcId(String srcId) {
        return events.findBySrcIdOrderByEmittedAtDesc(srcId);
    }

    public List<Event> findBySrcIdAndEvnType(String srcId, EventType type) {
        return events.findBySrcIdAndEvnType(srcId, type);
    }

    public Event add(Event stock) throws DataIntegrityViolationException {
        return events.save(stock);
    }

    public List<Event> find(String srcId, HistoryLimits limits) {
        if (HistoryLimits.isLatestCount(limits))
            return events.findBySrcId(srcId, PageRequest.of(0, (int) (long) limits.getLatestCount(), Sort.by(Sort.Direction.DESC, ENTITY_ID)));

        if (HistoryLimits.isAncientCount(limits)) {
            List<Event> list = events.findBySrcId(srcId, PageRequest.of(0, (int) (long) limits.getAncientCount(), Sort.by(Sort.Direction.ASC, ENTITY_ID)));
            Collections.reverse(list);
            return list;
        }

        if (HistoryLimits.isIDRange(limits))
            return events.findBySrcIdAndEvnIdBetween(srcId, limits.getFromIDOrDefault(), limits.getToIDOrDefault());

        if (HistoryLimits.isDateRange(limits))
            return events.findBySrcIdAndEmittedAtBetween(srcId, limits.getFromDateOrDefault(), limits.getToDateOrDefault());

        if (HistoryLimits.isPageRange(limits))
            return events.findBySrcId(srcId, PageRequest.of(limits.getPageNumOrDefault(), limits.getPageSizeOrDefault(), Sort.by(ENTITY_PAGE_ORDER, ENTITY_PAGE_ORDER_FIELD)));

        return events.findBySrcId(srcId, Sort.by(Sort.Direction.DESC, ENTITY_ID));
    }

}
