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

import com.robypomper.josp.types.josp.gw.GWType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;


/**
 * Entity class for <code>gw</code> table of the <code>jcp_apis</code> database.
 * <p>
 * This table contains the ids, the type and versions of all registered GWs.
 * <p>
 * It's populated during object startup when it tries to register him self to the
 * JCP. A the same time, a new record to the {@link ObjectInfo} table with more
 * details on registered object will be created.
 */
@Entity
@Data
public class GW {

    // Mngm

    @Id
    @Column(nullable = false, unique = true)
    private String gwId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GWType type;

    private String gwAddr;

    private int gwPort;

    private String gwAPIsAddr;

    private int gwAPIsPort;

    private int clientsMax = 0;

    @Column(nullable = false)
    private String version;


    // Details

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private GWStatus status;


    // Extra profile

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

}

