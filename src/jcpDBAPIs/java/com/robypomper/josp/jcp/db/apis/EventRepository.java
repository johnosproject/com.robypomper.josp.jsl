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
import com.robypomper.josp.types.josp.EventType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findBySrcIdOrderByEmittedAtDesc(String srcId);

    List<Event> findBySrcIdAndEvnType(String srcId, EventType evnType);

    List<Event> findBySrcId(@Param("srcId") String srcId);

    List<Event> findBySrcId(@Param("srcId") String srcId, Sort sort);

    List<Event> findBySrcId(@Param("srcId") String srcId, Pageable pageable);

    List<Event> findBySrcIdAndEmittedAtBetween(@Param("srcId") String srcId,
                                               @Param("startDate") Date startDate,
                                               @Param("endDate") Date endDate);

    List<Event> findBySrcIdAndEvnIdBetween(@Param("srcId") String srcId,
                                           @Param("startEvnId") long startEvnId,
                                           @Param("endEvnId") long endEvnId);

}
