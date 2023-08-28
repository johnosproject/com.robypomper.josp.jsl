/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
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

package com.robypomper.josp.protocol;

// {PROTOCOL} {REQUEST/RESPONSE_TYPE} {SEND_TIME}
// {FULL_SRV_ID/OBJ_ID}
// [{key} {value}]
// [...]


import com.robypomper.java.JavaDate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

/**
 * Utils class to render and parse the JOSP protocol messages.
 * <p>
 * This class is used by both sides JOD and JSL.
 */
public class JOSPProtocol {

    // Class constants
    public static final String JOSP_PROTO_VERSION_2_0 = "2.0";

    public static final String DISCOVERY_TYPE = "_josp2._tcp";
    public static final boolean CLIENT_AUTH_REQUIRED = true;
    public static final boolean CERT_SHARING_ENABLE = true;
    public static final int CERT_SHARING_TIMEOUT = 30 * 1000;

    public static final String JOSP_PROTO_VERSION = JOSP_PROTO_VERSION_2_0;
    public static final String JOSP_PROTO_NAME = "JOSP";
    protected static final String JOSP_PROTO = JOSP_PROTO_NAME + "/" + JOSP_PROTO_VERSION;

    private static final String UPD_MSG_BASE = JOSPProtocol.JOSP_PROTO + " UPD_MSG";
    private static final String UPD_MSG = UPD_MSG_BASE + " %s\nobjId:%s\ncompPath:%s\nupdType:%s\n%s";
    private static final String CMD_MSG_BASE = JOSPProtocol.JOSP_PROTO + " CMD_MSG";
    private static final String CMD_MSG = CMD_MSG_BASE + " %s\nfullSrvId:%s/%s/%s\n%s\ncompPath:%s\ncmdType:%s\n%s";


    // Public vars

    private static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");


    // Status update message

    public static boolean isUpdMsg(String msg) {
        return msg.startsWith(UPD_MSG_BASE);
    }

    public static String generateUpdToMsg(String objId, String componentPath, JOSPStateUpdateParams update) {
        return fromUpdToMsg(new StatusUpd(objId, componentPath, update));
    }

    public static String fromUpdToMsg(StatusUpd upd) {
        return String.format(UPD_MSG, JavaDate.getNow(), upd.getObjectId(),
                upd.getComponentPath(), upd.getUpdate().getType(), upd.getUpdate().encode());
    }

    public static StatusUpd fromMsgToUpdStr(String msg) throws ParsingException {
        JOSPStateUpdateParams statusUpdate = new JOSPStateUpdateStr(extractUpdTypeFromMsg(msg), extractUpdDataFromMsg(msg));
        return new StatusUpd(extractUpdObjIdFromMsg(msg),
                extractUpdCompPathFromMsg(msg),
                statusUpdate);
    }

    public static StatusUpd fromMsgToUpd(String msg, Map<String, Class<? extends JOSPStateUpdateParams>> classes) throws ParsingException {
        Class<? extends JOSPStateUpdateParams> cl = classes.get(extractUpdTypeFromMsg(msg));
        if (cl == null)
            throw new ParsingException(String.format("Error on parsing 'UpdateMessage' because unknown state type '%s'", extractUpdTypeFromMsg(msg)));

        JOSPStateUpdateParams statusUpdate;
        try {
            Constructor<? extends JOSPStateUpdateParams> cnstr = cl.getConstructor(String.class);
            statusUpdate = cnstr.newInstance(extractUpdDataFromMsg(msg));

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ParsingException(String.format("Error on parsing 'UpdateMessage' because %s", e.getMessage()), e);
        }

        return new StatusUpd(extractUpdObjIdFromMsg(msg),
                extractUpdCompPathFromMsg(msg),
                statusUpdate);
    }

    private static String extractUpdObjIdFromMsg(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 5, 1, "UpdateMessage");
    }

    private static String extractUpdCompPathFromMsg(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 5, 2, "UpdateMessage");
    }

    private static String extractUpdTypeFromMsg(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 5, 3, "UpdateMessage");
    }

    private static String extractUpdDataFromMsg(String msg) throws JOSPProtocol.ParsingException {
        if (msg.split("\n").length < 5)
            throw new JOSPProtocol.ParsingException(String.format("Few lines in %s", "UpdateMessage"));

        StringBuilder data = new StringBuilder();
        String[] lines = msg.split("\n");
        for (int i = 4; i < lines.length; i++) {   // lines.length - 3 to discard the last 3 lines
            data.append(lines[i]).append("\n");
        }

        return data.toString();
    }

    public static StatusUpd extractStatusUpdFromMsg(String msg) throws ParsingException {
        JOSPStateUpdateParams statusUpdate = new JOSPProtocol.JOSPStateUpdateStr(extractUpdTypeFromMsg(msg), extractUpdDataFromMsg(msg));
        return new JOSPProtocol.StatusUpd(extractUpdObjIdFromMsg(msg),
                extractUpdCompPathFromMsg(msg),
                statusUpdate);
    }


    // Command action message

    public static boolean isCmdMsg(String msg) {
        return msg.startsWith(CMD_MSG_BASE);
    }

    public static String generateCmdToMsg(String srvId, String usrId, String instId, String objId, String componentPath, JOSPActionCommandParams command) {
        return fromCmdToMsg(new ActionCmd(srvId, usrId, instId, objId, componentPath, command));
    }

    public static String fromCmdToMsg(ActionCmd cmd) {
        return String.format(CMD_MSG, JavaDate.getNow(), cmd.getServiceId(), cmd.getUserId(), cmd.getInstanceId(), cmd.getObjectId(),
                cmd.getComponentPath(), cmd.getCommand().getType(), cmd.getCommand().encode());
    }

    public static ActionCmd fromMsgToCmdStr(String msg) throws ParsingException {
        JOSPActionCommandParams actionCommand = new JOSPActionCommandStr(extractCmdTypeFromMsg(msg), extractCmdDataFromMsg(msg));
        return new ActionCmd(extractCmdSrvIdFromMsg(msg), extractCmdUsrIdFromMsg(msg), extractCmdInstIdFromMsg(msg), extractCmdObjIdFromMsg(msg),
                extractCmdCompPathFromMsg(msg),
                actionCommand);
    }

    public static ActionCmd fromMsgToCmd(String msg, Map<String, Class<? extends JOSPActionCommandParams>> classes) throws ParsingException {
        Class<? extends JOSPActionCommandParams> cl = classes.get(extractCmdTypeFromMsg(msg));
        if (cl == null)
            throw new ParsingException(String.format("Error on parsing 'ActionMessage' because unknown action type '%s'", extractCmdTypeFromMsg(msg)));

        JOSPActionCommandParams actionCommand;
        try {
            Constructor<? extends JOSPActionCommandParams> cnstr = cl.getConstructor(String.class);
            actionCommand = cnstr.newInstance(extractCmdDataFromMsg(msg));

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ParsingException(String.format("Error on parsing 'ActionMessage' because %s", e.getMessage()), e);
        }

        return new ActionCmd(extractCmdSrvIdFromMsg(msg), extractCmdUsrIdFromMsg(msg), extractCmdInstIdFromMsg(msg), extractCmdObjIdFromMsg(msg),
                extractCmdCompPathFromMsg(msg),
                actionCommand);
    }

    private static String extractCmdSrvIdFromMsg(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 6, 1, "ActionMessage").split("/")[0];
    }

    private static String extractCmdUsrIdFromMsg(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 6, 1, "ActionMessage").split("/")[1];
    }

    private static String extractCmdInstIdFromMsg(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 6, 1, "ActionMessage").split("/")[2];
    }

    private static String extractCmdObjIdFromMsg(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 6, 2, "ActionMessage");
    }

    private static String extractCmdCompPathFromMsg(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 6, 3, "ActionMessage");
    }

    private static String extractCmdTypeFromMsg(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 6, 4, "ActionMessage");
    }

    private static String extractCmdDataFromMsg(String msg) throws JOSPProtocol.ParsingException {
        if (msg.split("\n").length < 6)
            throw new JOSPProtocol.ParsingException(String.format("Few lines in %s", "ActionMessage"));

        StringBuilder data = new StringBuilder();
        String[] lines = msg.split("\n");
        for (int i = 5; i < lines.length; i++) {   // lines.length - 3 to discard the last 3 lines
            data.append(lines[i]).append("\n");
        }

        return data.toString();
    }


    // Message classes

    /**
     * Data class to return info contained in status update messages.
     */
    public static class StatusUpd {

        // Internal vars

        private final String objectId;
        private final String componentPath;
        private final JOSPStateUpdateParams update;


        // Constructor

        StatusUpd(String objectId, String componentPath, JOSPStateUpdateParams update) {
            this.objectId = objectId;
            this.componentPath = componentPath;
            this.update = update;
        }


        // Getters

        public String getObjectId() {
            return objectId;
        }

        public String getComponentPath() {
            return componentPath;
        }

        public JOSPStateUpdateParams getUpdate() {
            return update;
        }

    }

    /**
     * Data class to return info contained in action command messages.
     */
    public static class ActionCmd {

        // Internal vars

        private final String serviceId;
        private final String userId;
        private final String instanceId;
        private final String objectId;
        private final String componentPath;
        private final JOSPActionCommandParams command;


        // Constructor

        protected ActionCmd(String serviceId, String userId, String instanceId, String objectId, String componentPath, JOSPActionCommandParams command) {
            this.serviceId = serviceId;
            this.userId = userId;
            this.instanceId = instanceId;
            this.objectId = objectId;
            this.componentPath = componentPath;
            this.command = command;
        }


        // Getters

        public String getServiceId() {
            return serviceId;
        }

        public String getUserId() {
            return userId;
        }

        public String getInstanceId() {
            return instanceId;
        }

        public String getObjectId() {
            return objectId;
        }

        public String getComponentPath() {
            return componentPath;
        }

        public JOSPActionCommandParams getCommand() {
            return command;
        }

    }


    // Message classes (internal purposes)

    public static class JOSPStateUpdateStr implements JOSPStateUpdateParams {

        private final String updType;
        private final String updData;

        public JOSPStateUpdateStr(String updType, String updData) {
            this.updType = updType;
            this.updData = updData;
        }

        @Override
        public String getType() {
            return updType;
        }

        @Override
        public String encode() {
            return updData;
        }

    }

    public static class JOSPActionCommandStr implements JOSPActionCommandParams {

        private final String cmdType;
        private final String cmdData;

        public JOSPActionCommandStr(String cmdType, String cmdData) {
            this.cmdType = cmdType;
            this.cmdData = cmdData;
        }

        @Override
        public String getType() {
            return cmdType;
        }

        @Override
        public String encode() {
            return cmdData;
        }

    }


    // Utils

    public static String extractFieldFromResponse(String msg, int msgMinLines, int fieldLine, String msgName) throws JOSPProtocol.ParsingException {
        String[] lines = msg.split("\n");
        if (lines.length < msgMinLines)
            throw new JOSPProtocol.ParsingException(String.format("Few lines in %s", msgName));

        return lines[fieldLine].substring(lines[fieldLine].indexOf(":") + 1);
    }

    public static String extractPayloadFromResponse(String msg, int msgMinLines, int fieldLine, String msgName) throws JOSPProtocol.ParsingException {
        String[] lines = msg.split("\n");
        if (lines.length < msgMinLines)
            throw new JOSPProtocol.ParsingException(String.format("Few lines in %s", msgName));

        StringBuilder str = new StringBuilder();
        for (int i = fieldLine; i < lines.length; i++) {
            str.append(lines[i]);
            str.append("\n");
        }

        return str.toString();
    }


    // Exceptions

    /**
     * Exceptions for local communication errors.
     */
    public static class ParsingException extends Throwable {
        public ParsingException(String msg) {
            super(msg);
        }

        public ParsingException(String msg, Throwable e) {
            super(msg, e);
        }
    }

}
