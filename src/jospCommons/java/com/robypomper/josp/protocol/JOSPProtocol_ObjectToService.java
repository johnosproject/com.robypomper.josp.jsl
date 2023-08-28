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

import java.util.ArrayList;
import java.util.List;

public class JOSPProtocol_ObjectToService {

    // Common utils

    private static final String OBJ_REQ_NAME = "ObjectToService";

    public static String getObjId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 2, 1, OBJ_REQ_NAME);
    }


    // Object disconnected

    public static final String OBJ_DISCONNECT_REQ_NAME = "ObjectDisconnect";
    private static final String OBJ_DISCONNECT_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_DISCONNECT_MSG";
    private static final String OBJ_DISCONNECT_REQ = OBJ_DISCONNECT_REQ_BASE + " %s\nobjId:%s";

    public static String createObjectDisconnectMsg(String objId) {
        return String.format(OBJ_DISCONNECT_REQ, JavaDate.getNow(), objId);
    }

    public static boolean isObjectDisconnectMsg(String msg) {
        return msg.startsWith(OBJ_DISCONNECT_REQ_BASE);
    }


    // Object Info

    public static final String OBJ_INF_REQ_NAME = "ObjectInfo";
    private static final String OBJ_INF_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_INF_MSG";
    private static final String OBJ_INF_REQ = OBJ_INF_REQ_BASE + " %s\nobjId:%s\nobjName:%s\njodVersion:%s\nownerId:%s\nmodel:%s\nbrand:%s\ndescr:%s\nisCloudConnected:%s";

    public static String createObjectInfoMsg(String objId, String objName, String jodVersion, String ownerId, String model, String brand, String descr, boolean isCloudConnected) {
        return String.format(OBJ_INF_REQ, JavaDate.getNow(), objId, objName, jodVersion, ownerId, model, brand, descr, isCloudConnected);
    }

    public static boolean isObjectInfoMsg(String msg) {
        return msg.startsWith(OBJ_INF_REQ_BASE);
    }

    public static String getObjectInfoMsg_Name(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 9, 2, OBJ_INF_REQ_NAME);
    }

    public static String getObjectInfoMsg_JODVersion(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 9, 3, OBJ_INF_REQ_NAME);
    }

    public static String getObjectInfoMsg_OwnerId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 9, 4, OBJ_INF_REQ_NAME);
    }

    public static String getObjectInfoMsg_Model(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 9, 5, OBJ_INF_REQ_NAME);
    }

    public static String getObjectInfoMsg_Brand(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 9, 6, OBJ_INF_REQ_NAME);
    }

    public static String getObjectInfoMsg_LongDescr(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 9, 7, OBJ_INF_REQ_NAME);
    }

    public static boolean getObjectInfoMsg_IsCloudConnected(String msg) throws JOSPProtocol.ParsingException {
        return Boolean.parseBoolean(JOSPProtocol.extractFieldFromResponse(msg, 9, 8, OBJ_INF_REQ_NAME));
    }


    // Object Struct

    public static final String OBJ_STRUCT_REQ_NAME = "ObjectStruct";
    private static final String OBJ_STRUCT_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_STRUCT_MSG";
    private static final String OBJ_STRUCT_REQ = OBJ_STRUCT_REQ_BASE + " %s\nobjId:%s\n%s";

    public static String createObjectStructMsg(String objId, String struct) {
        return String.format(OBJ_STRUCT_REQ, JavaDate.getNow(), objId, struct);
    }

    public static boolean isObjectStructMsg(String msg) {
        return msg.startsWith(OBJ_STRUCT_REQ_BASE);
    }

    public static String getObjectStructMsg_Struct(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractPayloadFromResponse(msg, 3, 2, OBJ_STRUCT_REQ_NAME);
    }


    // Object Perms

    public static final String OBJ_PERMS_REQ_NAME = "ObjectPerms";
    private static final String OBJ_PERMS_REQ_BASE = JOSPProtocol.JOSP_PROTO + " OBJ_PERMS_MSG";
    private static final String OBJ_PERMS_REQ = OBJ_PERMS_REQ_BASE + " %s\nobjId:%s\n%s";

    public static String createObjectPermsMsg(String objId, String perms) {
        return String.format(OBJ_PERMS_REQ, JavaDate.getNow(), objId, perms);
    }

    public static boolean isObjectPermsMsg(String msg) {
        return msg.startsWith(OBJ_PERMS_REQ_BASE);
    }

    public static List<JOSPPerm> getObjectPermsMsg_Perms(String msg) throws JOSPProtocol.ParsingException {
        String permsStr = JOSPProtocol.extractPayloadFromResponse(msg, 3, 2, OBJ_PERMS_REQ_NAME);
        return JOSPPerm.listFromString(permsStr);
    }


    // Service Perms

    public static final String SRV_PERMS_REQ_NAME = "ServicePerms";
    private static final String SRV_PERMS_REQ_BASE = JOSPProtocol.JOSP_PROTO + " SRV_PERMS_MSG";
    private static final String SRV_PERMS_REQ = SRV_PERMS_REQ_BASE + " %s\nobjId:%s\npermType:%s\nconnType:%s";

    public static String createServicePermMsg(String objId, JOSPPerm.Type permType, JOSPPerm.Connection permConn) {
        return String.format(SRV_PERMS_REQ, JavaDate.getNow(), objId, permType, permConn);
    }

    public static boolean isServicePermsMsg(String msg) {
        return msg.startsWith(SRV_PERMS_REQ_BASE);
    }

    public static JOSPPerm.Type getServicePermsMsg_PermType(String msg) throws JOSPProtocol.ParsingException {
        return JOSPPerm.Type.valueOf(JOSPProtocol.extractFieldFromResponse(msg, 4, 2, SRV_PERMS_REQ_NAME));
    }

    public static JOSPPerm.Connection getServicePermsMsg_ConnType(String msg) throws JOSPProtocol.ParsingException {
        return JOSPPerm.Connection.valueOf(JOSPProtocol.extractFieldFromResponse(msg, 4, 3, SRV_PERMS_REQ_NAME));
    }


    // Status Upd Msg class

    public static final String UPD_MSG_NAME = "StatusUpdate";
    private static final String UPD_MSG_BASE = JOSPProtocol.JOSP_PROTO + " UPD_MSG";
    private static final String UPD_MSG = UPD_MSG_BASE + " %s\nobjId:%s\ncompPath:%s\ncmdType:%s\n%s";

    public static String createObjectStateUpdMsg(String objId, String compPath, JOSPStateUpdateParams udpdate) {
        return StateUpdMsg.fromUpdToMsg(new StateUpdMsg(objId, compPath, udpdate));
    }

    public static boolean isObjectStateUpdMsg(String msg) {
        return msg.startsWith(UPD_MSG_BASE);
    }

    /**
     * Data class to return info contained in state update messages.
     */
    public static class StateUpdMsg {

        // Internal vars

        private final String objectId;
        private final String componentPath;
        private final JOSPStateUpdateParams update;


        // Constructor

        StateUpdMsg(String objectId, String componentPath, JOSPStateUpdateParams update) {
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


        // Casting

        static String fromUpdToMsg(StateUpdMsg upd) {
            return String.format(UPD_MSG, JavaDate.getNow(), upd.getObjectId(),
                    upd.getComponentPath(), upd.getUpdate().getType(), upd.getUpdate().encode());
        }

    }


    // Events History Msg class (Response)

    public static final JOSPPerm.Type HISTORY_EVENTS_REQ_MIN_PERM = JOSPPerm.Type.None;         // ToDo Should be CoOwner???
    public static final String HISTORY_EVENTS_REQ_NAME = "HistoryEventsRes";
    private static final String HISTORY_EVENTS_REQ_BASE = JOSPProtocol.JOSP_PROTO + " H_EVENTS_MSG";
    private static final String HISTORY_EVENTS_REQ = HISTORY_EVENTS_REQ_BASE + " %s\nobjId:%s\nreqId:%s\n%s";

    public static String createHistoryEventsMsg(String objId, String reqId, List<JOSPEvent> eventsHistory) {
        return String.format(HISTORY_EVENTS_REQ, JavaDate.getNow(), objId, reqId, JOSPEvent.toString(eventsHistory));
    }

    public static boolean isHistoryEventsMsg(String msg) {
        return msg.startsWith(HISTORY_EVENTS_REQ_BASE);
    }

    public static String getHistoryEventsMsg_ObjId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 3, 1, HISTORY_EVENTS_REQ_NAME);
    }

    public static String getHistoryEventsMsg_ReqId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 3, 2, HISTORY_EVENTS_REQ_NAME);
    }

    public static List<JOSPEvent> getHistoryEventsMsg_HistoryStatus(String msg) throws JOSPProtocol.ParsingException {
        String eventsHistoryStr = JOSPProtocol.extractPayloadFromResponse(msg, 3, 3, HISTORY_EVENTS_REQ_NAME);
        return JOSPEvent.listFromString(eventsHistoryStr);
    }


    // Status History Msg class (Response)

    public static final JOSPPerm.Type HISTORY_STATUS_REQ_MIN_PERM = JOSPPerm.Type.Status;
    public static final String HISTORY_STATUS_REQ_NAME = "HistoryStatusRes";
    private static final String HISTORY_STATUS_REQ_BASE = JOSPProtocol.JOSP_PROTO + " H_STATUS_MSG";
    private static final String HISTORY_STATUS_REQ = HISTORY_STATUS_REQ_BASE + " %s\nobjId:%s\ncompPath:%s\nreqId:%s\n%s";

    public static String createHistoryCompStatusMsg(String objId, String compPath, String reqId, List<JOSPStatusHistory> statusesHistory) {
        return String.format(HISTORY_STATUS_REQ, JavaDate.getNow(), objId, compPath, reqId, JOSPStatusHistory.toString(statusesHistory));
    }

    public static boolean isHistoryCompStatusMsg(String msg) {
        return msg.startsWith(HISTORY_STATUS_REQ_BASE);
    }

    public static String getHistoryCompStatusMsg_ObjId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 4, 1, HISTORY_STATUS_REQ_NAME);
    }

    public static String getHistoryCompStatusMsg_CompPath(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 4, 2, HISTORY_STATUS_REQ_NAME);
    }

    public static String getHistoryCompStatusMsg_ReqId(String msg) throws JOSPProtocol.ParsingException {
        return JOSPProtocol.extractFieldFromResponse(msg, 4, 3, HISTORY_STATUS_REQ_NAME);
    }

    public static List<JOSPStatusHistory> getHistoryCompStatusMsg_HistoryStatus(String msg) {
        try {
            String historyStatusesStr = JOSPProtocol.extractPayloadFromResponse(msg, 4, 4, HISTORY_STATUS_REQ_NAME);
            return JOSPStatusHistory.listFromString(historyStatusesStr);
        } catch (JOSPProtocol.ParsingException ignore) {
        }

        return new ArrayList<>();
    }

}
