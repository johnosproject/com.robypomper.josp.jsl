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

import com.robypomper.josp.jcp.db.apis.entities.ObjectOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ObjectOwnerRepository extends JpaRepository<ObjectOwner, String> {

    /**
     * <pre>
     * 	SELECT count(*) FROM (
     * 	  SELECT owner_id, count(*) FROM object_owner
     * 	    GROUP BY owner_id
     * 	) as t
     * </pre>
     */
    @Query(value = "SELECT count(*) FROM (\n" +
            "  SELECT owner_id, count(*) FROM object_owner\n" +
            "    GROUP BY owner_id\n" +
            ") as t\n",
            nativeQuery = true)
    long countOwners();

}
