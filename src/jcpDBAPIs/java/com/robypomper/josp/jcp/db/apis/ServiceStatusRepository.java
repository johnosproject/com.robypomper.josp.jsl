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

import com.robypomper.josp.jcp.db.apis.entities.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServiceStatusRepository extends JpaRepository<ServiceStatus, String> {

    List<ServiceStatus> findAll();

    List<ServiceStatus> findBySrvId(String srvId);

    List<ServiceStatus> findByUsrId(String usrId);

    List<ServiceStatus> findBySrvIdAndUsrId(String srvId, String usrId);

    long countByOnline(boolean online);

    /**
     * <pre>
     *  SELECT count(*) FROM (
     *    SELECT srv_id, count(*) FROM service_status
     *      WHERE online='true'
     *      GROUP BY srv_id
     *  ) as t;
     * </pre>
     */
    @Query(value = "SELECT count(*) FROM (" +
            "  SELECT srv_id, count(*) FROM service_status" +
            "    WHERE online='true'" +
            "    GROUP BY srv_id" +
            ") as t",
            nativeQuery = true)
    long countServicesOnline();
}
