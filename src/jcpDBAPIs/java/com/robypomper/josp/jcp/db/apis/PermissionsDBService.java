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

import com.robypomper.josp.jcp.db.apis.entities.Permission;
import com.robypomper.josp.protocol.JOSPPerm;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class PermissionsDBService {

    // Internal vars

    private final PermissionRepository permissions;
    private final ObjectRepository objects;
    private final ServiceStatusRepository servicesStatus;


    // Constructor

    public PermissionsDBService(PermissionRepository permissions, ObjectRepository objects, ServiceStatusRepository servicesStatus) {
        this.permissions = permissions;
        this.objects = objects;
        this.servicesStatus = servicesStatus;
    }


    // Access methods

    public List<Permission> findByObj(String objId) throws DataIntegrityViolationException {
        return permissions.findByObjId(objId);
    }

    public List<Permission> findBySrv(String srvId) throws DataIntegrityViolationException {
        return permissions.findBySrvId(srvId);
    }

    public List<Permission> findBySrvExtended(String srvId) throws DataIntegrityViolationException {
        List<Permission> result = permissions.findBySrvId(srvId);
        result.addAll(permissions.findBySrvId(JOSPPerm.WildCards.USR_ALL.toString()));
        result.addAll(permissions.findBySrvId(JOSPPerm.WildCards.USR_OWNER.toString()));
        return result;
    }

    public List<Permission> addAll(List<Permission> objPerms) {
        return permissions.saveAll(objPerms);
    }

    public void removeAll(List<Permission> objPerms) {
        permissions.deleteAll(objPerms);
    }


    // Conversion Permission to/from JOSPPerm

    public static JOSPPerm dbPermToJOSPPerm(Permission permDB) {
        return new JOSPPerm(permDB.getId(), permDB.getObjId(), permDB.getSrvId(), permDB.getUsrId(), permDB.getType(), permDB.getConnection(), permDB.getUpdatedAt());
    }

    public static List<JOSPPerm> dbPermsToJOSPPerm(List<Permission> permDB) {
        List<JOSPPerm> permsDB = new ArrayList<>();
        for (Permission p : permDB)
            permsDB.add(dbPermToJOSPPerm(p));
        return permsDB;
    }

    public static Permission jospPermToDBPerm(JOSPPerm permJOSP) {
        Permission permDB = new Permission();
        permDB.setId(permJOSP.getId());
        permDB.setObjId(permJOSP.getObjId());
        permDB.setSrvId(permJOSP.getSrvId());
        permDB.setUsrId(permJOSP.getUsrId());
        permDB.setType(permJOSP.getPermType());
        permDB.setConnection(permJOSP.getConnType());
        permDB.setPermissionUpdatedAt(permJOSP.getUpdatedAt());
        return permDB;
    }

    public static List<Permission> jospPermsToDBPerms(List<JOSPPerm> permsJOSP) {
        List<Permission> permsDB = new ArrayList<>();
        for (JOSPPerm p : permsJOSP)
            permsDB.add(jospPermToDBPerm(p));
        return permsDB;
    }

}
