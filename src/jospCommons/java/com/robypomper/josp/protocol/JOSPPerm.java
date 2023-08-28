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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.robypomper.java.JavaDate;
import com.robypomper.java.JavaRandomStrings;
import com.robypomper.josp.consts.JOSPConstants;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Messaging types to use in Permission messaging classes.
 */
public class JOSPPerm {

    /**
     * Define witch connection type is allow in the Permission.
     */
    public enum Connection {
        /**
         * Allow only when connection is local.
         */
        OnlyLocal(0),
        /**
         * Allow for both local and cloud connections.
         */
        LocalAndCloud(1);

        private final int level;

        Connection(int level) {
            this.level = level;
        }

        public boolean greaterThan(Connection other) {
            return this.level > other.level;
        }

        public boolean lowerThan(Connection other) {
            return this.level < other.level;
        }

    }

    /**
     * Define witch access type is allow in the Permission.
     */
    public enum Type {
        /**
         * Allow only access to object's basic info.
         */
        None(0),
        /**
         * Allow access to object's info and status updates.
         */
        Status(1),
        /**
         * Like {@link #Status}, plus allow action execution.
         */
        Actions(2),
        /**
         * Like {@link #Actions}, plus allow object's configuration.
         */
        CoOwner(3);

        private final int level;

        Type(int level) {
            this.level = level;
        }

        public boolean greaterThan(Type other) {
            return this.level > other.level;
        }

        public boolean lowerThan(Type other) {
            return this.level < other.level;
        }

    }

    /**
     * Define permission generation strategy.
     */
    public enum GenerateStrategy {
        /**
         * Permission generated following standard permissions rules.
         */
        STANDARD,
        /**
         * Permission generated following public permissions rules.
         */
        PUBLIC
    }

    /**
     * Define wildcards for service and user ids.
     */
    public enum WildCards {
        /**
         * Indicate that the permission in referred to the object owner.
         */
        USR_OWNER("#Owner"),
        /**
         * Indicate that the permission is applicable to all users.
         */
        USR_ALL("#All"),
        /**
         * Reference for anonymous user's id
         */
        USR_ANONYMOUS_ID(JOSPConstants.ANONYMOUS_ID),
        /**
         * Reference for anonymous user's name
         */
        USR_ANONYMOUS_NAME(JOSPConstants.ANONYMOUS_USERNAME),
        /**
         * Indicate that the permission is applicable to all services.
         */
        SRV_ALL("#All");

        private final String value;

        WildCards(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }

    }


    private static final String OBJ_PERMS_REQ_FORMAT = "id:%s;objId:%s;srvId:%s;usrId:%s;type:%s;conn:%s;upd:%s";

    private final String id;
    private final String objId;
    private final String srvId;
    private final String usrId;
    private final Type permType;
    private final Connection connType;
    private final Date updatedAt;

    public JOSPPerm(String objId, String srvId, String usrId, String permType, String connType, String updatedAt) {
        this(generateId(), objId, srvId, usrId, permType, connType, updatedAt);
    }

    public JOSPPerm(String id, String objId, String srvId, String usrId, String permType, String connType, String updatedAt) {
        this.id = id;
        this.objId = objId;
        this.srvId = srvId;
        this.usrId = usrId;
        this.permType = Type.valueOf(permType);
        this.connType = Connection.valueOf(connType);
        Date updTmp;
        try {
            updTmp = JavaDate.DEF_DATE_FORMATTER.parse(updatedAt);
        } catch (ParseException e) {
            updTmp = JavaDate.getNowDate();
        }
        this.updatedAt = updTmp;
    }

    public JOSPPerm(String objId, String srvId, String usrId, Type permType, Connection connType, Date updatedAt) {
        this(generateId(), objId, srvId, usrId, permType, connType, updatedAt);
    }

    public JOSPPerm(String id, String objId, String srvId, String usrId, Type permType, Connection connType, Date updatedAt) {
        this.id = id;
        this.objId = objId;
        this.srvId = srvId;
        this.usrId = usrId;
        this.permType = permType;
        this.connType = connType;
        this.updatedAt = updatedAt;
    }

    private static String generateId() {
        return JavaRandomStrings.randomAlfaString(20);
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getObjId() {
        return objId;
    }

    public String getSrvId() {
        return srvId;
    }

    public String getUsrId() {
        return usrId;
    }

    public Type getPermType() {
        return permType;
    }

    @JsonIgnore
    public String getPermTypeStr() {
        return permType.toString();
    }

    public Connection getConnType() {
        return connType;
    }

    @JsonIgnore
    public String getConnTypeStr() {
        return connType.toString();
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @JsonIgnore
    public String getUpdatedAtStr() {
        return JavaDate.DEF_DATE_FORMATTER.format(updatedAt);
    }


    // Converters

    public static JOSPPerm fromString(String permStr) throws JOSPProtocol.ParsingException {
        String[] permStrs = permStr.split(";");
        if (permStrs.length != 7)
            throw new JOSPProtocol.ParsingException("Few fields in JOSPPerm string");

        String id = permStrs[0].substring(permStrs[0].indexOf(":") + 1);
        String objId = permStrs[1].substring(permStrs[1].indexOf(":") + 1);
        String srvId = permStrs[2].substring(permStrs[2].indexOf(":") + 1);
        String usrId = permStrs[3].substring(permStrs[3].indexOf(":") + 1);
        String permType = permStrs[4].substring(permStrs[4].indexOf(":") + 1);
        String permConn = permStrs[5].substring(permStrs[5].indexOf(":") + 1);
        String updatedAt = permStrs[6].substring(permStrs[6].indexOf(":") + 1);

        return new JOSPPerm(id, objId, srvId, usrId, permType, permConn, updatedAt);
    }

    public static List<JOSPPerm> listFromString(String permsStr) throws JOSPProtocol.ParsingException {
        List<JOSPPerm> perms = new ArrayList<>();

        for (String permStr : permsStr.split("\n"))
            perms.add(JOSPPerm.fromString(permStr));

        return perms;
    }

    public static String toString(JOSPPerm perm) {
        return String.format(OBJ_PERMS_REQ_FORMAT, perm.getId(), perm.getObjId(), perm.getSrvId(), perm.getUsrId(), perm.getPermType(), perm.getConnType(), JavaDate.DEF_DATE_FORMATTER.format(perm.getUpdatedAt()));
    }

    public static String toString(List<JOSPPerm> perms) {
        StringBuilder str = new StringBuilder();
        for (JOSPPerm perm : perms) {
            str.append(toString(perm));
            str.append("\n");
        }

        return str.toString();
    }

    public static String logPermissions(List<JOSPPerm> permissions) {
        StringBuilder str = new StringBuilder();
        str.append("  +--------------------+----------------------+----------------------+---------------------------+-------------------------+-------------------------+\n");
        str.append("  | ObjId              | SrvId                | UsrId                | Perm. and Connection Type | Updated At              | PermId                  |\n");
        str.append("  +--------------------+----------------------+----------------------+---------------------------+-------------------------+-------------------------+\n");
        for (JOSPPerm p : permissions)
            str.append(String.format("  | %-18s | %-20s | %-20s | %-10s, %-13s | %-23s | %-23s |\n",
                    p.getObjId(), p.getSrvId(), p.getUsrId(), p.getPermType(), p.getConnType(), p.getUpdatedAtStr(), p.getId()));
        str.append("  +--------------------+----------------------+----------------------+---------------------------+-------------------------+-------------------------+\n");
        return str.toString();
    }

}
