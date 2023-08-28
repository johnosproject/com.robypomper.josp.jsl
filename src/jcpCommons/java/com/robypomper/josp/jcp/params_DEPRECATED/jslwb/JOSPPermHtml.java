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

package com.robypomper.josp.jcp.params_DEPRECATED.jslwb;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.permissions.Paths20;
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.Date;

@JsonAutoDetect
public class JOSPPermHtml {

    public final String id;
    public final String objId;
    public final String srvId;
    public final String usrId;
    public final JOSPPerm.Type type;
    public final JOSPPerm.Connection connection;
    public final Date lastUpdate;
    public final String pathUpd;
    public final String pathDel;
    public final String pathDup;

    public JOSPPermHtml(JOSPPerm perm) {
        this.id = perm.getId();
        this.objId = perm.getObjId();
        this.srvId = perm.getSrvId();
        this.usrId = perm.getUsrId();
        this.type = perm.getPermType();
        this.connection = perm.getConnType();
        this.lastUpdate = perm.getUpdatedAt();
        this.pathUpd = Paths20.FULL_PATH_UPD.replace("{obj_id}", objId).replace("{perm_id}", id);
        this.pathDel = Paths20.FULL_PATH_DEL.replace("{obj_id}", objId).replace("{perm_id}", id);
        this.pathDup = Paths20.FULL_PATH_DUP.replace("{obj_id}", objId).replace("{perm_id}", id);
    }

}
