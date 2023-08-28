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

import com.robypomper.josp.jcp.db.apis.entities.ObjectStatusHistory;
import com.robypomper.josp.protocol.HistoryLimits;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class StatusHistoryDBService {

    private static final String ENTITY_ID = "shId";
    private static final Sort.Direction ENTITY_PAGE_ORDER = Sort.Direction.DESC;
    private static final String ENTITY_PAGE_ORDER_FIELD = "updatedAt";

    // Internal vars

    private final StatusHistoryRepository statusesHistory;

    // Constructor

    public StatusHistoryDBService(StatusHistoryRepository statusesHistory) {
        this.statusesHistory = statusesHistory;
    }


    public ObjectStatusHistory add(ObjectStatusHistory stock) throws DataIntegrityViolationException {
        return statusesHistory.save(stock);
    }

    public List<ObjectStatusHistory> find(String objId, String compPath, HistoryLimits limits) {
        if (HistoryLimits.isLatestCount(limits))
            return statusesHistory.findByObjIdAndCompPath(objId, compPath, PageRequest.of(0, (int) (long) limits.getLatestCount(), Sort.by(Sort.Direction.DESC, ENTITY_ID)));

        if (HistoryLimits.isAncientCount(limits)) {
            List<ObjectStatusHistory> list = statusesHistory.findByObjIdAndCompPath(objId, compPath, PageRequest.of(0, (int) (long) limits.getAncientCount(), Sort.by(Sort.Direction.ASC, ENTITY_ID)));
            Collections.reverse(list);
            return list;
        }

        if (HistoryLimits.isIDRange(limits))
            return statusesHistory.findByObjIdAndCompPathAndShIdBetween(objId, compPath, limits.getFromIDOrDefault(), limits.getToIDOrDefault());

        if (HistoryLimits.isDateRange(limits))
            return statusesHistory.findByObjIdAndCompPathAndUpdatedAtBetween(objId, compPath, limits.getFromDateOrDefault(), limits.getToDateOrDefault());

        if (HistoryLimits.isPageRange(limits))
            return statusesHistory.findByObjIdAndCompPath(objId, compPath, PageRequest.of(limits.getPageNumOrDefault(), limits.getPageSizeOrDefault(), Sort.by(ENTITY_PAGE_ORDER, ENTITY_PAGE_ORDER_FIELD)));

        return statusesHistory.findByObjIdAndCompPath(objId, compPath, Sort.by(Sort.Direction.DESC, ENTITY_ID));
    }
}
