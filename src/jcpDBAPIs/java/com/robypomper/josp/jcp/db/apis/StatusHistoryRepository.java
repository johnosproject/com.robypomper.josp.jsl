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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface StatusHistoryRepository extends JpaRepository<ObjectStatusHistory, Long> {

    List<ObjectStatusHistory> findByObjIdAndCompPath(@Param("objId") String objId,
                                                     @Param("compPath") String compPath);

    List<ObjectStatusHistory> findByObjIdAndCompPath(@Param("objId") String objId,
                                                     @Param("compPath") String compPath,
                                                     Sort sort);

    List<ObjectStatusHistory> findByObjIdAndCompPath(@Param("objId") String objId,
                                                     @Param("compPath") String compPath,
                                                     Pageable pageable);

    List<ObjectStatusHistory> findByObjIdAndCompPathAndUpdatedAtBetween(@Param("objId") String objId,
                                                                        @Param("compPath") String compPath,
                                                                        @Param("startDate") Date startDate,
                                                                        @Param("endDate") Date endDate);

    List<ObjectStatusHistory> findByObjIdAndCompPathAndShIdBetween(@Param("objId") String objId,
                                                                   @Param("compPath") String compPath,
                                                                   @Param("startShId") long startShId,
                                                                   @Param("endShId") long endShId);

}
