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


import com.robypomper.java.JavaDate;

public class JOSPProtocol_ServiceToObject {

    // Common utils

    private static final String OBJ_REQ_NAME = "ServiceToObject";

    public static String getFullSrvId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 3, 1, OBJ_REQ_NAME);
    }

    public static String getSrvId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol_Service.fullSrvIdToSrvId(getFullSrvId(msg));
    }

    public static String getUsrId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol_Service.fullSrvIdToUsrId(getFullSrvId(msg));
    }

    public static String getInstId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol_Service.fullSrvIdToInstId(getFullSrvId(msg));
    }

    public static String getObjId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 3, 2, OBJ_REQ_NAME);
    }


    // Object Set Name

    public static final String OBJ_SETNAME_REQ_NAME = "ObjectSetName";
    private static final String OBJ_SETNAME_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_SETNAME_MSG";
    private static final String OBJ_SETNAME_REQ = OBJ_SETNAME_REQ_BASE + " %s\nfullSrvId:%s\nobjId:%s\nobjName:%s";

    public static String createObjectSetNameMsg(String fullSrvId, String objId, String newName) {
        return String.format(OBJ_SETNAME_REQ, JavaDate.getNow(), fullSrvId, objId, newName);
    }

    public static boolean isObjectSetNameMsg(String msg) {
        return msg.startsWith(OBJ_SETNAME_REQ_BASE);
    }

    public static String getObjectSetNameMsg_Name(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 4, 3, OBJ_SETNAME_REQ_NAME);
    }


    // Object Set Owner Id

    public static final String OBJ_SETOWNERID_REQ_NAME = "ObjectSetOwnerId";
    private static final String OBJ_SETOWNERID_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_SETOWNERID_MSG";
    private static final String OBJ_SETOWNERID_REQ = OBJ_SETOWNERID_REQ_BASE + " %s\nfullSrvId:%s\nobjId:%s\nownerId:%s";

    public static String createObjectSetOwnerIdMsg(String fullSrvId, String objId, String newOwnerId) {
        return String.format(OBJ_SETOWNERID_REQ, JavaDate.getNow(), fullSrvId, objId, newOwnerId);
    }

    public static boolean isObjectSetOwnerIdMsg(String msg) {
        return msg.startsWith(OBJ_SETOWNERID_REQ_BASE);
    }

    public static String getObjectSetOwnerIdMsg_OwnerId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 4, 3, OBJ_SETOWNERID_REQ_NAME);
    }


    // Object Add permission

    public static final String OBJ_ADDPERM_REQ_NAME = "ObjectAddPerm";
    private static final String OBJ_ADDPERM_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_ADDPERM_MSG";
    private static final String OBJ_ADDPERM_REQ = OBJ_ADDPERM_REQ_BASE + " %s\nfullSrvId:%s\nobjId:%s\nsrvId:%s\nusrId:%s\npermType:%s\nconnType:%s";

    public static String createObjectAddPermMsg(String fullSrvId, String objId, String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) {
        return String.format(OBJ_ADDPERM_REQ, JavaDate.getNow(), fullSrvId, objId, srvId, usrId, permType, connType);
    }

    public static boolean isObjectAddPermMsg(String msg) {
        return msg.startsWith(OBJ_ADDPERM_REQ_BASE);
    }

    public static String getObjectAddPermMsg_SrvId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 7, 3, OBJ_ADDPERM_REQ_NAME);
    }

    public static String getObjectAddPermMsg_UsrId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 7, 4, OBJ_ADDPERM_REQ_NAME);
    }

    public static JOSPPerm.Type getObjectAddPermMsg_PermType(String msg) throws JOSPProtocol.ParsingException {
        return JOSPPerm.Type.valueOf(JOSPProtocol.extractFieldFromResponse(msg, 7, 5, OBJ_ADDPERM_REQ_NAME));
    }

    public static JOSPPerm.Connection getObjectAddPermMsg_ConnType(String msg) throws JOSPProtocol.ParsingException {
        return JOSPPerm.Connection.valueOf(JOSPProtocol.extractFieldFromResponse(msg, 7, 6, OBJ_ADDPERM_REQ_NAME));
    }


    // Object Upd permission

    public static final String OBJ_UPDPERM_REQ_NAME = "ObjectUpdPerm";
    private static final String OBJ_UPDPERM_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_UPDPERM_MSG";
    private static final String OBJ_UPDPERM_REQ = OBJ_UPDPERM_REQ_BASE + " %s\nfullSrvId:%s\nobjId:%s\npermId:%s\nsrvId:%s\nusrId:%s\npermType:%s\nconnType:%s";

    public static String createObjectUpdPermMsg(String fullSrvId, String objId, String permId, String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) {
        return String.format(OBJ_UPDPERM_REQ, JavaDate.getNow(), fullSrvId, objId, permId, srvId, usrId, permType, connType);
    }

    public static boolean isObjectUpdPermMsg(String msg) {
        return msg.startsWith(OBJ_UPDPERM_REQ_BASE);
    }

    public static String getObjectUpdPermMsg_PermId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 8, 3, OBJ_UPDPERM_REQ_NAME);
    }

    public static String getObjectUpdPermMsg_SrvId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 8, 4, OBJ_UPDPERM_REQ_NAME);
    }

    public static String getObjectUpdPermMsg_UsrId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 8, 5, OBJ_UPDPERM_REQ_NAME);
    }

    public static JOSPPerm.Type getObjectUpdPermMsg_PermType(String msg) throws JOSPProtocol.ParsingException {
        return JOSPPerm.Type.valueOf(JOSPProtocol.extractFieldFromResponse(msg, 8, 6, OBJ_UPDPERM_REQ_NAME));
    }

    public static JOSPPerm.Connection getObjectUpdPermMsg_ConnType(String msg) throws JOSPProtocol.ParsingException {
        return JOSPPerm.Connection.valueOf(JOSPProtocol.extractFieldFromResponse(msg, 8, 7, OBJ_UPDPERM_REQ_NAME));
    }


    // Object Rem permission

    public static final String OBJ_REMPERM_REQ_NAME = "ObjectRemPerm";
    private static final String OBJ_REMPERM_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_REMPERM_MSG";
    private static final String OBJ_REMPERM_REQ = OBJ_REMPERM_REQ_BASE + " %s\nfullSrvId:%s\nobjId:%s\npermId:%s";

    public static String createObjectRemPermMsg(String fullSrvId, String objId, String permId) {
        return String.format(OBJ_REMPERM_REQ, JavaDate.getNow(), fullSrvId, objId, permId);
    }

    public static boolean isObjectRemPermMsg(String msg) {
        return msg.startsWith(OBJ_REMPERM_REQ_BASE);
    }

    public static String getObjectRemPermMsg_PermId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 4, 3, OBJ_REMPERM_REQ_NAME);
    }


    // Action Cmd Msg class

    public static final String CMD_MSG_NAME = "ActionReqest";
    private static final String CMD_MSG_BASE = JOSPProtocol.JOSP_PROTO + " CMD_MSG";
    private static final String CMD_MSG = CMD_MSG_BASE + " %s\nfullSrvId:%s/%s/%s\nobjId:%s\ncompPath:%s\ncmdType:%s\n%s";

    public static String createObjectActionCmdMsg(String fullSrvId, String objId, String compPath, JOSPActionCommandParams command) {
        String srvId = JOSPProtocol_Service.fullSrvIdToSrvId(fullSrvId);
        String usrId = JOSPProtocol_Service.fullSrvIdToUsrId(fullSrvId);
        String instId = JOSPProtocol_Service.fullSrvIdToInstId(fullSrvId);
        return ActionCmdMsg.fromCmdToMsg(new ActionCmdMsg(srvId, usrId, instId, objId, compPath, command));
    }

    public static boolean isObjectActionCmdMsg(String msg) {
        return msg.startsWith(CMD_MSG_BASE);
    }

    /**
     * Data class to return info contained in action command messages.
     */
    public static class ActionCmdMsg {

        // Internal vars

        private final String serviceId;
        private final String userId;
        private final String instanceId;
        private final String objectId;
        private final String componentPath;
        private final JOSPActionCommandParams command;


        // Constructor

        ActionCmdMsg(String serviceId, String userId, String instanceId, String objectId, String componentPath, JOSPActionCommandParams command) {
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


        // Casting

        static String fromCmdToMsg(ActionCmdMsg cmd) {
            return String.format(CMD_MSG, JavaDate.getNow(), cmd.getServiceId(), cmd.getUserId(), cmd.getInstanceId(), cmd.getObjectId(),
                    cmd.getComponentPath(), cmd.getCommand().getType(), cmd.getCommand().encode());
        }

    }


    // Events History Msg class (Request)

    public static final JOSPPerm.Type HISTORY_EVENTS_REQ_MIN_PERM = JOSPPerm.Type.None;         // ToDo Should be CoOwner???
    public static final String HISTORY_EVENTS_REQ_NAME = "HistoryEventsReq";
    private static final String HISTORY_EVENTS_REQ_BASE = JOSPProtocol.JOSP_PROTO + " H_EVENTS_MSG";
    private static final String HISTORY_EVENTS_REQ = HISTORY_EVENTS_REQ_BASE + " %s\nfullSrvId:%s\nobjId:%s\nreqId:%s\nlimits:%s\nevType:%s";

    public static String createHistoryEventsMsg(String fullSrvId, String objId, String reqId, HistoryLimits limits) {
        return createHistoryEventsMsg(fullSrvId, objId, reqId, limits, "");
    }

    public static String createHistoryEventsMsg(String fullSrvId, String objId, String reqId, HistoryLimits limits, String filterEventType) {
        return String.format(HISTORY_EVENTS_REQ, JavaDate.getNow(), fullSrvId, objId, reqId, HistoryLimits.toString(limits), filterEventType);
    }

    public static boolean isHistoryEventsMsg(String msg) {
        return msg.startsWith(HISTORY_EVENTS_REQ_BASE);
    }

    public static String getHistoryEventsMsg_FullSrvId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 6, 1, HISTORY_EVENTS_REQ_NAME);
    }

    public static String getHistoryEventsMsg_ObjId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 6, 2, HISTORY_EVENTS_REQ_NAME);
    }

    public static String getHistoryEventsMsg_ReqId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 6, 3, HISTORY_EVENTS_REQ_NAME);
    }

    public static HistoryLimits getHistoryEventsMsg_Limits(String msg) throws JOSPProtocol.ParsingException {
        String limitsStr = JOSPProtocol.extractFieldFromResponse(msg, 6, 4, HISTORY_EVENTS_REQ_NAME);
        return HistoryLimits.fromString(limitsStr);
    }

    public static String getHistoryEventsMsg_FilterEventType(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 6, 5, HISTORY_EVENTS_REQ_NAME);
    }


    // Status History Msg class (Request)

    public static final JOSPPerm.Type HISTORY_STATUS_REQ_MIN_PERM = JOSPPerm.Type.Status;
    public static final String HISTORY_STATUS_REQ_NAME = "HistoryStatusReq";
    private static final String HISTORY_STATUS_REQ_BASE = JOSPProtocol.JOSP_PROTO + " H_STATUS_MSG";
    private static final String HISTORY_STATUS_REQ = HISTORY_STATUS_REQ_BASE + " %s\nfullSrvId:%s\nobjId:%s\ncompPath:%s\nreqId:%s\nlimits:%s";

    public static String createHistoryCompStatusMsg(String fullSrvId, String objId, String compPath, String reqId, HistoryLimits limits) {
        return String.format(HISTORY_STATUS_REQ, JavaDate.getNow(), fullSrvId, objId, compPath, reqId, HistoryLimits.toString(limits));
    }

    public static boolean isHistoryCompStatusMsg(String msg) {
        return msg.startsWith(HISTORY_STATUS_REQ_BASE);
    }

    public static String getHistoryCompStatusMsg_FullSrvId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 6, 1, HISTORY_STATUS_REQ_NAME);
    }

    public static String getHistoryCompStatusMsg_ObjId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 6, 2, HISTORY_STATUS_REQ_NAME);
    }

    public static String getHistoryCompStatusMsg_CompPath(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 6, 3, HISTORY_STATUS_REQ_NAME);
    }

    public static String getHistoryCompStatusMsg_ReqId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 6, 4, HISTORY_STATUS_REQ_NAME);
    }

    public static HistoryLimits getHistoryCompStatusMsg_Limits(String msg) throws JOSPProtocol.ParsingException {
        String limitsStr = JOSPProtocol.extractFieldFromResponse(msg, 6, 5, HISTORY_STATUS_REQ_NAME);
        return HistoryLimits.fromString(limitsStr);
    }

}
