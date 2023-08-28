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

package com.robypomper.josp.jcp.db.apis.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;


/**
 * Entity class for <code>object_info</code> table of the <code>jcp_apis</code> database.
 * <p>
 * This table contains the detailed info of all registered objects. This table
 * act as support/details table for {@link Object} table that contains only the
 * user id and his name for fastest access.
 * <p>
 * It's populated during object startup when it tries to register him self to the
 * JCP.
 */
@Entity
@Data
public class ObjectInfo {

    // Mngm

    @Id
    @Column(nullable = false, unique = true)
    private String objId;


    // Profile

    @Column(nullable = false)
    private String brand = "";

    @Column(nullable = false)
    private String model = "";

    @Column(nullable = false)
    private String longDescr = "";


    // Extra profile

    @CreationTimestamp
    private Date createdAt;

}

