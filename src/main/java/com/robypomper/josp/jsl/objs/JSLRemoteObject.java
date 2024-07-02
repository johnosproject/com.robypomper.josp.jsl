/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2024 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.robypomper.josp.jsl.objs;

import com.robypomper.josp.jsl.objs.remote.ObjComm;
import com.robypomper.josp.jsl.objs.remote.ObjInfo;
import com.robypomper.josp.jsl.objs.remote.ObjPerms;
import com.robypomper.josp.jsl.objs.remote.ObjStruct;
import com.robypomper.josp.jsl.objs.structure.JSLAction;
import com.robypomper.josp.jsl.objs.structure.JSLActionParams;
import com.robypomper.josp.protocol.JOSPPerm;


/**
 * JOD Object representation for JSL library.
 */
public interface JSLRemoteObject {

    // Remote Object's basic info

    /**
     * @return the object's id.
     */
    String getId();

    /**
     * @return the object's name.
     */
    String getName();

    // Remote Object's sections getters

    ObjInfo getInfo();

    ObjStruct getStruct();

    ObjPerms getPerms();

    ObjComm getComm();

    //... getStatusHistory();

    //... getEventHistory();


    // To / From Object Msg

    void sendObjectCmdMsg(JSLAction component, JSLActionParams command) throws ObjectNotConnected, MissingPermission;

    boolean processFromObjectMsg(String msg, JOSPPerm.Connection connType) throws Throwable;


    // Exceptions

    /**
     * Exceptions thrown when accessing to a not connected object.
     */
    class ObjectNotConnected extends Throwable {
        private static final String MSG = "Can't access to '%s' object because not connected.";

        public ObjectNotConnected(JSLRemoteObject obj) {
            super(String.format(MSG, obj.getInfo().getId()));
        }

        public ObjectNotConnected(JSLRemoteObject obj, Throwable t) {
            super(String.format(MSG, obj.getInfo().getId()), t);
        }
    }

    /**
     * Exceptions thrown when accessing to a not connected object.
     */
    class MissingPermission extends Throwable {
        private static final String MSG = "Can't access to '%s' object because missing permission (required: %s; actual: %s; msg: '%s').";

        public MissingPermission(JSLRemoteObject obj, JOSPPerm.Connection onlyLocal, JOSPPerm.Type permType, JOSPPerm.Type minReqPerm, String msg) {
            super(String.format(MSG, obj.getInfo().getId(), minReqPerm, permType, msg.substring(0, msg.indexOf('\n'))));
        }
    }

    /**
     * Exceptions thrown when accessing to a not connected object.
     */
    class ComponentInitException extends Throwable {
        private static final String MSG = "Error for object '%s' on %s.";

        public ComponentInitException(JSLRemoteObject obj, String msg) {
            super(String.format(MSG, obj.getInfo().getId(), msg));
        }

        public ComponentInitException(JSLRemoteObject obj, String msg, Throwable t) {
            super(String.format(MSG, obj.getInfo().getId(), msg), t);
        }
    }

    /**
     * Exceptions for structure parsing, file load... errors.
     */
    class ParsingException extends Throwable {
        private static final String MSG_WRAPPER = "Error for object '%s' on %s.";
        private static final String MSG = "(@line: %d; col: %d)";

        public ParsingException(JSLRemoteObject obj, String msg) {
            super(String.format(MSG_WRAPPER, obj.getInfo().getId(), msg));
        }

        public ParsingException(JSLRemoteObject obj, String msg, Throwable e) {
            super(String.format(MSG_WRAPPER, obj.getInfo().getId(), msg), e);
        }

        public ParsingException(JSLRemoteObject obj, String msg, Throwable e, int line, int col) {
            super(String.format(MSG_WRAPPER, obj.getInfo().getId(), msg + String.format(MSG, line, col)));
        }
    }

    /**
     * Exception thrown when the structure initialization try to generate an
     * unknown component type.
     */
    class ParsingUnknownTypeException extends ParsingException {
        private static final String MSG = "Unknown type '%s' for '%s' JOD Component.";

        public ParsingUnknownTypeException(JSLRemoteObject obj, String compType, String compName) {
            super(obj, String.format(MSG, compType, compName));
        }
    }

}
