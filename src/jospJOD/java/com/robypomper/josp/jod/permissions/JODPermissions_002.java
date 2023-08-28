/*******************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jod.permissions;

import com.robypomper.java.JavaDate;
import com.robypomper.java.JavaFiles;
import com.robypomper.josp.callers.apis.core.permissions.Caller20;
import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.comm.JODLocalClientInfo;
import com.robypomper.josp.jod.events.Events;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * ToDo: doc JODPermissions_002
 */
public class JODPermissions_002 implements JODPermissions {


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JODPermissions.class);
    private final JODSettings_002 locSettings;
    private final JODObjectInfo objInfo;
    private final Caller20 apiPermissionsCaller;
    private final List<JOSPPerm> permissions = new ArrayList<>();
    private JODCommunication comm;


    // Constructors

    /**
     * Default constructor.
     *
     * @param settings  the JOD settings.
     * @param objInfo   the object's info.
     * @param jcpClient the jcp object client.
     */
    public JODPermissions_002(JODSettings_002 settings, JODObjectInfo objInfo, JCPAPIsClientObj jcpClient) throws PermissionInvalidObjIdException {
        this.objInfo = objInfo;
        this.locSettings = settings;
        this.apiPermissionsCaller = new Caller20(jcpClient);

        if (locSettings.getPermissionsPath().exists())
            loadFromFile();

        if (permissions.size() == 0) {
            generatePermissions();
            saveToFile();
        }

        String all = JOSPPerm.logPermissions(permissions);
        for (String s : all.split("\n"))
            log.info(s);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setCommunication(JODCommunication comm) throws JODStructure.CommunicationSetException {
        if (this.comm != null)
            throw new JODStructure.CommunicationSetException();
        this.comm = comm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syncObjPermissions() {
        if (comm == null) return;

        String permStr = JOSPPerm.toString(permissions);
        comm.sendToServices(JOSPProtocol_ObjectToService.createObjectPermsMsg(objInfo.getObjId(), permStr), JOSPPerm.Type.CoOwner);

        for (JODLocalClientInfo locConn : comm.getAllLocalClientsInfo()) {
            if (!locConn.isConnected())
                continue;

            JOSPPerm.Type permType = getServicePermission(locConn.getSrvId(), locConn.getUsrId(), JOSPPerm.Connection.OnlyLocal);
            try {
                comm.sendToSingleLocalService(locConn, JOSPProtocol_ObjectToService.createServicePermMsg(objInfo.getObjId(), permType, JOSPPerm.Connection.OnlyLocal), permType);

            } catch (JODCommunication.ServiceNotConnected e) {
                log.warn(String.format("Error on sending service's '%s' permission for object '%s' from JCP because %s", locConn.getFullSrvId(), objInfo.getObjId(), e.getMessage()));
            }
        }
    }

    // Access methods

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JOSPPerm> getPermissions() {
        return Collections.unmodifiableList(permissions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkPermission(String srvId, String usrId, JOSPPerm.Type minReqPerm, JOSPPerm.Connection connType) {
        if (objInfo.getOwnerId().equals(JODSettings_002.JODPERM_OWNER_DEF)) {
            log.debug(String.format("Permission %s for srvID %s and usrID %s GRANTED because no obj's owner set", minReqPerm, srvId, usrId));
            return true;
        }

        List<JOSPPerm> inherentPermissions = search(srvId, usrId);
        if (inherentPermissions.isEmpty()) {
            log.debug(String.format("Permission %s for srvID %s and usrID %s DENIED  because no permission found for specified srv/usr", minReqPerm, srvId, usrId));
            return false;
        }

        for (JOSPPerm p : inherentPermissions) {
            if (connType == JOSPPerm.Connection.LocalAndCloud
                    && p.getConnType() == JOSPPerm.Connection.OnlyLocal)
                continue;

            if (p.getPermType().compareTo(minReqPerm) >= 0) {
                log.debug(String.format("Permission %s for srvID %s and usrID %s GRANTED", minReqPerm, srvId, usrId));
                return true;
            }
        }

        log.debug(String.format("Permission %s for srvID %s and usrID %s DENIED", minReqPerm, srvId, usrId));
        return false;
    }

    // Comm::sendObjPresentation

    /**
     * {@inheritDoc}
     */
    @Override
    public JOSPPerm.Type getServicePermission(String srvId, String usrId, JOSPPerm.Connection connType) {
        if (objInfo.getOwnerId().equals(JODSettings_002.JODPERM_OWNER_DEF))
            return JOSPPerm.Type.CoOwner;

        List<JOSPPerm> inherentPermissions = search(srvId, usrId);
        if (inherentPermissions.isEmpty())
            return JOSPPerm.Type.None;

        JOSPPerm.Type highPerm = JOSPPerm.Type.None;
        for (JOSPPerm p : inherentPermissions) {
            if (connType == JOSPPerm.Connection.LocalAndCloud
                    && p.getConnType() == JOSPPerm.Connection.OnlyLocal)
                continue;

            if (p.getPermType().compareTo(highPerm) > 0)
                highPerm = p.getPermType();
        }

        return highPerm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermsForJSL() {
        return JOSPPerm.toString(getPermissions());
    }

    private JOSPPerm search(String permId) {
        for (JOSPPerm p : permissions)
            if (p.getId().equals(permId))
                return p;

        return null;
    }

    /**
     * Return the object's permission corresponding to given srvId and usrId.
     *
     * @param srvId the service's id.
     * @param usrId the user's id.
     * @return the object's permissions list.
     */
    @Deprecated
    private List<JOSPPerm> search(String srvId, String usrId) {
        List<JOSPPerm> inherentPermissions = new ArrayList<>();

        for (JOSPPerm p : permissions) {
            boolean exact_usr = p.getUsrId().equals(usrId);
            boolean all_usr = p.getUsrId().equals(JOSPPerm.WildCards.USR_ALL.toString());
            boolean owner = p.getUsrId().equals(JOSPPerm.WildCards.USR_OWNER.toString())
                    && objInfo.getOwnerId().equals(usrId);
            if (exact_usr || all_usr || owner) {
                boolean exact_srv = p.getSrvId().equals(srvId);
                boolean all_srv = p.getSrvId().equals(JOSPPerm.WildCards.SRV_ALL.toString());
                if (exact_srv || all_srv) {
                    inherentPermissions.add(p);
                }
            }
        }

        return inherentPermissions;
    }


    // Add/Upd/Rem methods


    /**
     * {@inheritDoc}
     */
    public boolean addPermissions(String srvId, String usrId, JOSPPerm.Type type, JOSPPerm.Connection connection) {
        log.info(String.format("Add permission to '%s' object with srvID %s, usrID %s connection '%s' and type '%s'", objInfo.getObjId(), srvId, usrId, connection, type));

        if (usrId == null || usrId.isEmpty()) {
            log.warn(String.format("Error on adding permission for '%s' object because usrId not set", objInfo.getObjId()));
            return false;
        }
        if (srvId == null || srvId.isEmpty()) {
            log.warn(String.format("Error on adding permission for '%s' object because srvId not set", objInfo.getObjId()));
            return false;
        }

        JOSPPerm newPerm = new JOSPPerm(objInfo.getObjId(), srvId, usrId, type, connection, new Date());
        permissions.add(newPerm);
        Events.registerPermAdded(newPerm);

        comm.syncObject();
        saveToFile();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean updPermissions(String permId, String srvId, String usrId, JOSPPerm.Type type, JOSPPerm.Connection connection) {
        log.info(String.format("Update permission to '%s' object with srvID %s, usrID %s connection '%s' and type '%s'", objInfo.getObjId(), srvId, usrId, connection, type));


        JOSPPerm existingPerm = search(permId);
        if (existingPerm == null)
            return false;

        // replace existing with (toDELETE) permission
        JOSPPerm newDelPerm = new JOSPPerm(existingPerm.getId(), existingPerm.getObjId(), srvId, usrId, type, connection, JavaDate.getNowDate());
        permissions.remove(existingPerm);
        permissions.add(newDelPerm);
        Events.registerPermUpdated(existingPerm, newDelPerm);

        comm.syncObject();
        saveToFile();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean remPermissions(String permId) {
        log.info(String.format("Remove permission to '%s' object with permId %s", objInfo.getObjId(), permId));

        JOSPPerm oldPerm = search(permId);
        if (oldPerm == null)
            return false;

        permissions.remove(oldPerm);
        Events.registerPermRemoved(oldPerm);

        comm.syncObject();
        saveToFile();
        return true;
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void startAutoRefresh() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAutoRefresh() {
    }


    // Storage methods

    /**
     * Load permissions from file specified from
     * {@link JODSettings_002#getPermissionsPath()}
     * to the local {@link #permissions} field.
     */
    private void loadFromFile() throws PermissionInvalidObjIdException {
        String permsStr;
        try {
            permsStr = JavaFiles.readString(locSettings.getPermissionsPath());

        } catch (IOException e) {
            log.error("Can't load permissions from file, generating new permissions.", e);
            Events.registerPermLoaded("Load permissions from file", e);
            return;
        }

        List<JOSPPerm> loadedPerms;
        try {
            loadedPerms = JOSPPerm.listFromString(permsStr);

        } catch (JOSPProtocol.ParsingException e) {
            log.error("Can't load permissions from file, generating new permissions.", e);
            Events.registerPermLoaded("Parsing permissions from file", e);
            return;
        }

        boolean mustUpdateObjID = false;
        for (JOSPPerm p : loadedPerms) {
            if (!objInfo.getObjId().equalsIgnoreCase(p.getObjId()))
                if (p.getObjId().endsWith("00000-00000"))
                    mustUpdateObjID = true;
                else
                    throw new PermissionInvalidObjIdException(objInfo.getObjId(), p);
        }

        if (mustUpdateObjID)
            updateObjIdAndSave();

        permissions.clear();
        permissions.addAll(loadedPerms);
        Events.registerPermLoaded("Load permissions from file", permissions);
    }


    /**
     * Save {@link #permissions} field to file specified from
     * {@link JODSettings_002#getPermissionsPath()}.
     */
    private void saveToFile() {
        try {
            JavaFiles.writeString(locSettings.getPermissionsPath(), JOSPPerm.toString(permissions));
            Events.registerPermLoaded("Save permissions to file", permissions);

        } catch (IOException e) {
            Events.registerPermLoaded("Save permissions to file", e);
            log.error("Can't save permissions on file, changes will be discharged on next reboot.", e);
        }
    }

    @Override
    public void updateObjIdAndSave() {
        synchronized (permissions) {
            List<JOSPPerm> updated = new ArrayList<>();
            for (JOSPPerm oldPerm : permissions) {
                JOSPPerm newPerm = new JOSPPerm(
                        oldPerm.getId(),
                        objInfo.getObjId(),
                        oldPerm.getSrvId(),
                        oldPerm.getUsrId(),
                        oldPerm.getPermType(),
                        oldPerm.getConnType(),
                        oldPerm.getUpdatedAt()
                );
                updated.add(newPerm);
            }
            permissions.clear();
            permissions.addAll(updated);
        }

        Events.registerPermLoaded("Updated obj's id on permissions", permissions);

        saveToFile();
    }


    // Permissions generate

    private void generatePermissions() {
        if (apiPermissionsCaller.isConnected()) {
            try {
                generatePermissionsFromJCP();

                Events.registerPermLoaded("Gen permissions on cloud", permissions);
                log.debug("Permissions generated on cloud");
                return;

            } catch (Throwable e) {
                log.warn(String.format("Error on generating object permission from JCP (%s)", e));
                Events.registerPermLoaded("Gen permissions on cloud", e);
            }
        }

        generatePermissionsLocally();
        log.debug("Permissions generated locally");

        Events.registerPermLoaded("Gen permissions locally", permissions);

        syncObjPermissions();
    }

    /**
     * Request to JCP a valid set of object's permissions and set them to
     * {@link #permissions} field.
     */
    private void generatePermissionsFromJCP() throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.RequestException, JCPClient2.ResponseException {
        synchronized (permissions) {
            permissions.clear();
            permissions.addAll(apiPermissionsCaller.getPermissionsGeneratedPUBLIC());
        }
    }

    /**
     * Generate a valid set of object's permissions and set them to
     * {@link #permissions} field.
     */
    private void generatePermissionsLocally() {
        synchronized (permissions) {
            permissions.clear();
            permissions.add(new JOSPPerm(objInfo.getObjId(), JOSPPerm.WildCards.SRV_ALL.toString(), JOSPPerm.WildCards.USR_OWNER.toString(), JOSPPerm.Type.CoOwner, JOSPPerm.Connection.LocalAndCloud, JavaDate.getNowDate()));
            permissions.add(new JOSPPerm(objInfo.getObjId(), JOSPPerm.WildCards.SRV_ALL.toString(), JOSPPerm.WildCards.USR_ALL.toString(), JOSPPerm.Type.CoOwner, JOSPPerm.Connection.OnlyLocal, JavaDate.getNowDate()));
        }
    }

}
