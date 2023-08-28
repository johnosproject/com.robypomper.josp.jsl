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

import javax.persistence.*;
import java.util.Date;


/**
 * Entity class for <code>object_id</code> table of the <code>jcp_apis</code> database.
 * <p>
 * This table contains all ids assigned to objects and guarantee his uniqueness.
 * <p>
 * New record is added when an object requests the generation of an object id.
 * The generated object id is associated to object's hardware id and the his
 * owner id. So when same object/owner requests for the object id generation,
 * the same object id will always returned.
 */
@Entity
@Data
@Table(indexes = {@Index(columnList = "objIdHw,usrId")})
public class ObjectId {

    // Mngm

    @Id
    @Column(nullable = false, unique = true)
    private String objId;

    @Column(nullable = false)
    private String objIdHw;

    @Column(nullable = false)
    private String usrId;

    private String oldObjId;


    // Extra profile

    @CreationTimestamp
    private Date createdAt;

}

