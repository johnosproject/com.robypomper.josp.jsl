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

package com.robypomper.josp.test.mocks.jod;

import com.robypomper.BuildInfo;
import com.robypomper.josp.consts.JOSPConstants;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODStructure;

public class MockJODObjectInfo implements JODObjectInfo {

    public static final String DEF_JOD_VERSION = BuildInfo.current.version;
    public static final String DEF_OBJ_ID = "xxxxx-xxxxx-xxxxx";
    public static final String DEF_OBJ_NAME = "TEST Object 001";
    public static final String DEF_OBJ_OWNER = JOSPConstants.ANONYMOUS_ID;
    public static final String DEF_OBJ_FULL_ID = DEF_OBJ_ID + "/" + DEF_OBJ_OWNER;
    public static final String DEF_OBJ_STRUCT_PATH = JODSettings_002.JODSTRUCT_PATH_DEF;
    public static final String DEF_OBJ_MODEL = "ABC-12345f";
    public static final String DEF_OBJ_BRAND = "TestManufacturer";
    public static final String DEF_OBJ_DESCR = "Mockup for JODObjectInfo class.";
    public static final String DEF_OBJ_STRUCT_STR = "{ \"model\": \"" + DEF_OBJ_MODEL + "\", \"brand\": \"" + DEF_OBJ_BRAND + "\", \"descr\": \"" + DEF_OBJ_DESCR + "\", \"descr_long\": \"..\", \"contains\": { \"BoolState\" : { \"type\": \"BooleanState\", \"listener\" : \"tstLAdv://sleep=1000;frequency=60;\" } } }";
    public static final String DEF_OBJ_STRUCT_STR_JSL = "...";

    private final String jodVersion;
    private final String objId;
    private String objName;
    private String objOwner;
    private final String objFullId;
    private final String objStructPath;
    private final String objStructStr;
    private final String objStructStrJSL;
    private final String objModel;
    private final String objBrand;
    private final String objDescr;

    public MockJODObjectInfo() {
        this(DEF_OBJ_ID);
    }

    public MockJODObjectInfo(String objId) {
        this(DEF_JOD_VERSION,
                objId,
                DEF_OBJ_NAME,
                DEF_OBJ_OWNER,
                DEF_OBJ_MODEL,
                DEF_OBJ_BRAND,
                DEF_OBJ_DESCR
                );
    }

    public MockJODObjectInfo(
            String jodVersion,
            String objId,
            String objName,
            String objOwner,
            String objModel,
            String objBrand,
            String objDescr
    ) {
        this(jodVersion, objId, objName, objOwner, DEF_OBJ_FULL_ID, DEF_OBJ_STRUCT_PATH, DEF_OBJ_STRUCT_STR, DEF_OBJ_STRUCT_STR_JSL, objModel, objBrand, objDescr);
    }

    public MockJODObjectInfo(
            String jodVersion,
            String objId,
            String objName,
            String objOwner,
            String objFullId,
            String objStructPath,
            String objStructStr,
            String objStructStrJSL,
            String objModel,
            String objBrand,
            String objDescr
    ) {
        this.jodVersion = jodVersion;
        this.objId = objId;
        this.objName = objName;
        this.objOwner = objOwner;
        this.objFullId = objFullId;
        this.objStructPath = objStructPath;
        this.objStructStr = objStructStr;
        this.objStructStrJSL = objStructStrJSL;
        this.objModel = objModel;
        this.objBrand = objBrand;
        this.objDescr = objDescr;
    }

    @Override
    public void setSystems(JODStructure structure, JODExecutorMngr executor, JODCommunication comm, JODPermissions permissions) {
    }

    @Override
    public String getJODVersion() {
        return jodVersion;
    }

    @Override
    public String getObjId() {
        return objId;
    }

    @Override
    public String getObjName() {
        return objName;
    }

    @Override
    public void setObjName(String newName) {
        this.objName = newName;
    }

    @Override
    public String getOwnerId() {
        return objOwner;
    }

    @Override
    public void setOwnerId(String ownerId) {
        this.objOwner = ownerId;
    }

    @Override
    public void resetOwnerId() {
        this.objOwner = JOSPConstants.ANONYMOUS_ID;
    }

    @Override
    public String getFullId() {
        return objFullId;
    }

    @Override
    public String getStructurePath() {
        return objStructPath;
    }

    @Override
    public String readStructureStr() {
        return objStructStr;
    }

    @Override
    public String getStructForJSL() {
        return objStructStrJSL;
    }

    @Override
    public String getBrand() {
        return objBrand;
    }

    @Override
    public String getModel() {
        return objModel;
    }

    @Override
    public String getLongDescr() {
        return objDescr;
    }

    @Override
    public void startAutoRefresh() {
    }

    @Override
    public void stopAutoRefresh() {
    }

    @Override
    public void syncObjInfo() {
    }

}
