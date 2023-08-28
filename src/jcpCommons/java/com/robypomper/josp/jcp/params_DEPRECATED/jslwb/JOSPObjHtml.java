//package com.robypomper.josp.jcp.params_DEPRECATED.jslwb;
//
//import com.fasterxml.jackson.annotation.JsonAutoDetect;
//import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.Paths20;
//import com.robypomper.josp.jsl.objs.JSLRemoteObject;
//import com.robypomper.josp.protocol.JOSPPerm;
//
////import com.robypomper.josp.jcp.fe.jsl.JSLSpringService;
//
//@JsonAutoDetect
//public class JOSPObjHtml {
//
//    public final String id;
//    public final String name;
//    public final String model;
//    public final String owner;
//    public final boolean isConnected;
//    public final boolean isCloudConnected;
//    public final boolean isLocalConnected;
//    public final String jodVersion;
//    public final String pathSingle;
//    public final String pathStruct;
//    public final String pathPerms;
//    public final String pathPermsAdd;
//    public final String pathSetOwner;
//    public final String pathSetName;
//    public final String permission;
//
//    public JOSPObjHtml(JSLRemoteObject obj) {
//        this.id = obj.getId();
//        this.name = obj.getName();
//        this.model = obj.getInfo().getModel();
//        this.owner = obj.getInfo().getOwnerId();
//        this.isConnected = obj.getComm().isConnected();
//        this.isCloudConnected = obj.getComm().isCloudConnected();
//        this.isLocalConnected = obj.getComm().isLocalConnected();
//        this.jodVersion = obj.getInfo().getJODVersion();
//        this.pathSingle = Paths20.FULL_PATH_DETAILS.replace("{obj_id}", id);
//        this.pathStruct = com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.structure.Paths20.FULL_PATH_STRUCT.replace("{obj_id}", id);
//        this.pathPerms = Paths20.FULL_PATH_LIST.replace("{obj_id}", id);
//        this.pathPermsAdd = Paths20.FULL_PATH_ADD.replace("{obj_id}", id);
//        this.pathSetOwner = Paths20.FULL_PATH_OWNER.replace("{obj_id}", id);
//        this.pathSetName = Paths20.FULL_PATH_NAME.replace("{obj_id}", id);
//        this.permission = obj.getPerms().getServicePerm(JOSPPerm.Connection.LocalAndCloud).toString();
//    }
//
//}
