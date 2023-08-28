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

import com.robypomper.josp.jcp.db.apis.entities.GW;
import com.robypomper.josp.types.josp.gw.GWType;

import java.util.List;
import java.util.Optional;


@org.springframework.stereotype.Service
public class GWDBService {

    // Internal vars

    private final GWRepository gws;
    private final GWStatusRepository gwsStatus;


    // Constructor

    public GWDBService(GWRepository gws,
                       GWStatusRepository gwsStatus) {
        this.gws = gws;
        this.gwsStatus = gwsStatus;
    }


    // Access methods

    public Optional<GW> findById(String id) {
        return gws.findById(id);
    }

    public List<GW> getAll() {
        return gws.findAll();
    }

    public List<GW> getAll(GWType gwType) {
        return gws.findByType(gwType);
    }


    // Storage methods

    public GW save(GW gw) {
        return gws.save(gw);
    }

    public void delete(GW gw) {
        gws.delete(gw);
    }

    public void deleteAll() {
        gws.deleteAll();
    }

}
