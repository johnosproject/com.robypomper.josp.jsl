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

package com.robypomper.josp.jod.objinfo;

import com.robypomper.comm.exception.PeerConnectionException;
import com.robypomper.comm.exception.PeerDisconnectionException;
import com.robypomper.java.JavaFiles;
import com.robypomper.java.JavaThreads;
import com.robypomper.josp.callers.apis.core.objects.Caller20;
import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.events.Events;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * This is the JOD object info implementation.
 * <p>
 * This implementation collect all object's info from local
 * {@link com.robypomper.josp.jod.JOD.Settings} or via JCP Client request at
 * API Objs via the support class {@link Caller20}.
 */
public class JODObjectInfo_002 implements JODObjectInfo {

    // Class constants

    String FULL_ID_FORMATTER = "%s/%s";

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JODObjectInfo.class);
    private final JODSettings_002 locSettings;
    private final Caller20 apiObjsCaller;
    private JODStructure structure;
    private JODExecutorMngr executorMngr;
    private JODCommunication comm;
    private JODPermissions permissions;
    private final String jodVersion;


    // Constructor

    /**
     * Create new object info.
     * <p>
     * This constructor create an instance of {@link Caller20} and request
     * common/mandatory info for caching them.
     *
     * @param settings   the JOD settings.
     * @param jcpClient  the JCP client.
     * @param jodVersion the current JOD implementation version.
     */
    public JODObjectInfo_002(JODSettings_002 settings, JCPAPIsClientObj jcpClient, String jodVersion) {
        this.locSettings = settings;
        this.apiObjsCaller = new Caller20(jcpClient);
        this.jodVersion = jodVersion;

        if (getObjIdHw().isEmpty())
            generateObjIdHw();

        apiObjsCaller.getClient().setObjectId(getObjId());
        if (getObjId().isEmpty()) {
            log.info("No Object ID, generate new one.");
            generateObjId();
        } else if (getObjId().endsWith("00000-00000")) {
            if (apiObjsCaller.isConnected()) {
                log.info(String.format("Local Object ID (%s), generate new one.", getObjId()));
                generateObjId();
            } else {
                log.info(String.format("Local Object ID (%s), generate new one on connection (observer).", getObjId()));
                apiObjsCaller.getClient().addConnectionListener(generateObjIdConnectionListener);
            }
        } else
            log.info(String.format("Loaded Object ID (%s)", getObjId()));

        if (getObjName().isEmpty())
            generateObjName();
    }


    // Object's systems

    /**
     * {@inheritDoc}
     */
    public void setSystems(JODStructure structure, JODExecutorMngr executorMngr, JODCommunication comm, JODPermissions permissions) {
        this.structure = structure;
        this.executorMngr = executorMngr;
        this.comm = comm;
        this.permissions = permissions;
    }


    // Obj's info

    /**
     * {@inheritDoc}
     */
    public String getFullId() {
        return String.format(FULL_ID_FORMATTER, getObjId(), getOwnerId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJODVersion() {
        return jodVersion;
    }

    /**
     * The Hardware ID is the id that allow to identify a physical object.
     * <p>
     * It help to identify same physical object also when the Object ID was reset.
     *
     * @return the object's Hardware ID.
     */
    private String getObjIdHw() {
        return locSettings.getObjIdHw();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getObjId() {
        return locSettings.getObjIdCloud();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getObjName() {
        return locSettings.getObjName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObjName(String newName) {
        String oldName = getObjName();
        locSettings.setObjName(newName);

        log.info(String.format("Updated name for object '%s' (old: '%s', new: '%s')", getObjId(), oldName, newName));
        Events.registerInfoUpd("objName", oldName, newName);

        syncObjInfo();
    }


    // User's info

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOwnerId() {
        return locSettings.getOwnerId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOwnerId(String newOwnerId) {
        String oldOwner = getOwnerId();
        locSettings.setOwnerId(newOwnerId);

        log.info(String.format("Updated owner id for object '%s' (old: '%s', new: '%s')", getObjId(), oldOwner, newOwnerId));
        Events.registerInfoUpd("objOwner", oldOwner, newOwnerId);

        JavaThreads.initAndStart(new Runnable() {
            @Override
            public void run() {
                log.info(String.format("Update Object ID (%s), because new owner", getObjId()));
                generateObjId();    // > saveObjId() > JODPermissions.updateObjIdAndSave()
            }
        }, "OBJ_ID_GENERATION");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetOwnerId() {
        setOwnerId(JODPermissions.ANONYMOUS_ID);
    }


    // Structure's info
    // ToDo move to JODStructure

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStructurePath() {
        return locSettings.getStructurePath().getPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readStructureStr() {
        try {
            return JavaFiles.readString(locSettings.getStructurePath());

        } catch (IOException e) {
            log.warn(String.format("Error on structure string loading from '%s' file because %s check JOD configs", locSettings.getStructurePath(), e.getMessage()), e);
            throw new RuntimeException(String.format("Error on structure string loading from '%s' file check JOD configs", locSettings.getStructurePath()), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStructForJSL() throws JODStructure.ParsingException {
        return structure.getStructForJSL();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBrand() {
        return structure.getRoot().getBrand();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModel() {
        return structure.getRoot().getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLongDescr() {
        return structure.getRoot().getDescr();
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void startAutoRefresh() {
        assert structure != null
                && executorMngr != null
                && comm != null
                && permissions != null;

        log.info(String.format("Start JODObjectInfo auto-refresh for '%s' object", getObjId()));

        syncObjInfo();  // ToDo can be removed?
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAutoRefresh() {
        log.info(String.format("Stop JODObjectInfo auto-refresh for '%s' object", getObjId()));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void syncObjInfo() {
        if (structure == null)
            return;

        String objInfoMsg = JOSPProtocol_ObjectToService.createObjectInfoMsg(
                getObjId(),
                getObjName(),
                getJODVersion(),
                getOwnerId(),
                structure.getRoot().getModel(),
                structure.getRoot().getBrand(),
                structure.getRoot().getDescr(),
                comm.getCloudConnection().getState().isConnected());
        comm.sendToServices(objInfoMsg, JOSPPerm.Type.Status);
    }


    // Obj's id

    private void generateObjId() {
        String oldObjId = getObjId();
        String newObjId;
        try {
            if (oldObjId.isEmpty())
                newObjId = apiObjsCaller.getIdGenerated(getObjIdHw(), getOwnerId());
            else
                newObjId = apiObjsCaller.getIdRegenerated(getObjIdHw(), getOwnerId());

            log.info(String.format("Object ID generated on cloud '%s'", newObjId));
            Events.registerInfoUpd("objId", oldObjId, newObjId);

            apiObjsCaller.getClient().removeConnectionListener(generateObjIdConnectionListener);

        } catch (Throwable e) {
            log.info(String.format("Error generating Object ID (old: %s), generate new one on connection (observer)", getObjId()));
            apiObjsCaller.getClient().addConnectionListener(generateObjIdConnectionListener);

            newObjId = String.format("%s-00000-00000", getObjIdHw());

            log.info(String.format("Object ID generated locally '%s'", newObjId));
            Events.registerInfoUpd("objId", newObjId);
        }

        saveObjId(newObjId);
    }

    private void saveObjId(String generatedObjId) {
        boolean wasLocalRunning = false;
        if (comm != null && comm.isLocalRunning()) {
            wasLocalRunning = true;
            try {
                comm.stopLocal();

            } catch (JODCommunication.LocalCommunicationException e) {
                log.warn("Error on stopping local communication on save object's id");
            }
        }

        boolean wasCloudRunning = false;
        if (comm != null && (comm.getCloudConnection().getState().isConnected() || comm.getCloudConnection().getState().isConnecting())) {
            wasCloudRunning = true;
            try {
                comm.getCloudConnection().disconnect();
            } catch (PeerDisconnectionException e) {
                log.warn("Error on stopping cloud communication on save object's id");
            }
        }

        locSettings.setObjIdCloud(generatedObjId);
        apiObjsCaller.getClient().setObjectId(generatedObjId);
        if (permissions != null)
            permissions.updateObjIdAndSave();

        syncObjInfo();

        if (wasCloudRunning) {
            try {
                comm.getCloudConnection().connect();

            } catch (PeerConnectionException e) {
                log.warn("Error on starting cloud communication on save object's id");
            }
        }

        if (wasLocalRunning) {
            try {
                comm.startLocal();

            } catch (JODCommunication.LocalCommunicationException e) {
                log.warn("Error on starting local communication on save object's id");
            }
        }
    }


    // Generators

    private void generateObjIdHw() {
        String oldObjIdHW = getObjIdHw();
        String newObjIdHW = LocalObjectInfo.generateObjIdHw();
        locSettings.setObjIdHw(newObjIdHW);

        log.info(String.format("Object ID HW generated '%s'", getObjIdHw()));
        Events.registerInfoUpd("objIdHW", oldObjIdHW, newObjIdHW);
    }

    private void generateObjName() {
        String oldObjName = getObjName();
        String newObjName = LocalObjectInfo.generateObjName();
        locSettings.setObjName(newObjName);

        log.info(String.format("Object name generated '%s'", getObjName()));
        Events.registerInfoUpd("objName", oldObjName, newObjName);
    }


    // Generating listener

    private final JCPClient2.ConnectionListener generateObjIdConnectionListener = new JCPClient2.ConnectionListener() {

        @Override
        public void onConnected(JCPClient2 jcpClient) {
            log.info(String.format("Local Object ID (%s), generate new one on connection (listener)", getObjId()));
            generateObjId();
        }

        @Override
        public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
        }

        @Override
        public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
        }

        @Override
        public void onDisconnected(JCPClient2 jcpClient) {
        }
    };

}
