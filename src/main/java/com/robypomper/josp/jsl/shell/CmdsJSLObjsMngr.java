/*******************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
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

package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import com.robypomper.josp.jsl.comm.JSLGwS2OClient;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.*;
import com.robypomper.josp.jsl.objs.structure.*;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPEvent;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPHistory;

import java.util.List;


public class CmdsJSLObjsMngr {

    public static final String PRE = "\n\n";
    public static final String POST = "\n\n";
    private final JSLObjsMngr objs;

    public CmdsJSLObjsMngr(JSLObjsMngr objs) {
        this.objs = objs;
    }


    // All objects

    @Command(description = "Print all known objects.")
    public String objsPrintAll() {
        StringBuilder s = new StringBuilder("KNOWN OBJECTS LIST\n");
        s.append("  (ID) Obj's Name                          Local Conn.     (Perm)  Cloud Conn.     (Perm)  \n");
        for (JSLRemoteObject obj : objs.getAllObjects()) {
            String localObjState = getLocalObjState(obj);
            JSLGwS2OClient cloudClient = ((DefaultObjComm) obj.getComm()).getCloudConnection();
            String cloudObjState = getCloudObjState(cloudClient, obj);
            JOSPPerm.Type localPerm = obj.getPerms().getServicePerm(JOSPPerm.Connection.OnlyLocal);
            JOSPPerm.Type cloudPerm = obj.getPerms().getServicePerm(JOSPPerm.Connection.LocalAndCloud);
            s.append(String.format("- %-40s %-15s %-7s %-15s %-7s\n", "(" + obj.getId() + ") " + obj.getName(), localObjState, localPerm, cloudObjState, cloudPerm));
        }

        return s.toString();
    }

    @Command(description = "Print all connected objects.")
    public String objsPrintAllConnected() {
        StringBuilder s = new StringBuilder("CONNECTED OBJECTS LIST\n");
        for (JSLRemoteObject obj : objs.getAllConnectedObjects())
            s.append(String.format("- %-30s (%s)\n", obj.getName(), obj.getId()));

        return s.toString();
    }


    // Object's info

    @Command(description = "Print object's info.")
    public String objPrintObjectInfo(String objId) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        String s = "";
        s += "Obj. Id:          " + obj.getInfo().getId() + "\n";
        s += "Obj. Name:        " + obj.getInfo().getName() + "\n";
        s += "Owner Id:         " + obj.getInfo().getOwnerId() + "\n";
        s += "Obj. JOD version: " + obj.getInfo().getJODVersion() + "\n";
        s += "JCP Comm:         " + obj.getComm().isCloudConnected() + "\n";
        s += "    perm:         " + obj.getPerms().getServicePerm(JOSPPerm.Connection.LocalAndCloud) + "\n";
        s += "Direct Comm:      " + obj.getComm().isLocalConnected() + "\n";
        s += "       perm:      " + obj.getPerms().getServicePerm(JOSPPerm.Connection.OnlyLocal) + "\n";

        return s;
    }

    @Command(description = "Print object's structure.")
    public String objPrintObjectStruct(String objId) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        if (!obj.getStruct().isInit())
            return String.format("Object '%s' not presented to current service", objId);

        return printRecursive(obj.getStruct().getStructure(), 0);
    }

    private String printRecursive(JSLComponent comp, int indent) {
        String indentStr = new String(new char[indent]).replace('\0', ' ');
        String compStr = String.format("%s- %s", indentStr, comp.getName());

        String compVal = "";
        if (comp instanceof JSLBooleanState)
            compVal = Boolean.toString(((JSLBooleanState) comp).getState());
        else if (comp instanceof JSLRangeState)
            compVal = Double.toString(((JSLRangeState) comp).getState());

        System.out.printf("%-30s %-15s %s%n", compStr, comp.getType(), compVal);

        if (comp instanceof JSLContainer)
            for (JSLComponent subComp : ((JSLContainer) comp).getComponents())
                printRecursive(subComp, indent + 2);
        return null;
    }

    @Command(description = "Print all connection of given objId.")
    public String objPrintObjectConnections(String objId) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        StringBuilder s = new StringBuilder("CONNECTION LIST\n");

        JSLGwS2OClient cloudClient = ((DefaultObjComm) obj.getComm()).getCloudConnection();
        s.append(String.format("- Cloud %-12s %s\n", getCloudObjState(cloudClient, obj), cloudClient));
        for (JSLLocalClient client : ((DefaultObjComm) obj.getComm()).getLocalClients()) {
            s.append(String.format("- Local %-12s %s\n", client.getState(), client));
        }

        return s.toString();
    }

    @Command(description = "Print all permissions of given objId.")
    public String objPrintObjectPermissions(String objId) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        String s = String.format("Current service has '%s/%s' (Local/Cloud) permission on object '%s'\n", obj.getPerms().getServicePerm(JOSPPerm.Connection.OnlyLocal), obj.getPerms().getServicePerm(JOSPPerm.Connection.LocalAndCloud), objId);
        s += "OBJECT'S PERMISSIONS LIST\n";
        s += JOSPPerm.logPermissions(obj.getPerms().getPerms());
        return s;
    }

    @Command(description = "Print all events of given objId.")
    public String objPrintObjectEvents(String objId) {
        return doObjPrintObjectEvents(objId, HistoryLimits.NO_LIMITS());
    }

    @Command(description = "Print latest 10 events of given objId.")
    public String objPrintObjectEventsLatest(String objId) {
        return doObjPrintObjectEvents(objId, HistoryLimits.LATEST(10));
    }

    @Command(description = "Print ancient 10 events of given objId.")
    public String objPrintObjectEventsAncient(String objId) {
        return doObjPrintObjectEvents(objId, HistoryLimits.ANCIENT(10));
    }

    @Command(description = "Print all events from specified event's id of given objId.")
    public String objPrintObjectEventsFromID(String objId, long id) {
        return doObjPrintObjectEvents(objId, HistoryLimits.FROM_ID(id));
    }

    @Command(description = "Print all events until specified event's id of given objId.")
    public String objPrintObjectEventsToID(String objId, long id) {
        return doObjPrintObjectEvents(objId, HistoryLimits.TO_ID(id));
    }

    @Command(description = "Print all events contained in specified event's id range (inclusive) of given objId.")
    public String objPrintObjectEventsBetweenID(String objId, long start, long end) {
        return doObjPrintObjectEvents(objId, HistoryLimits.BETWEEN_ID(start,end));
    }

    @Command(description = "Print last hour events of given objId.")
    public String objPrintObjectEventsLastHour(String objId) {
        return doObjPrintObjectEvents(objId, HistoryLimits.LAST_HOUR());
    }

    @Command(description = "Print past hour all events of given objId.")
    public String objPrintObjectEventsPastHour(String objId) {
        return doObjPrintObjectEvents(objId, HistoryLimits.PAST_HOUR());
    }

    @Command(description = "Print specified page of events of given objId.")
    public String objPrintObjectEventsPage(String objId, int page, int size) {
        return doObjPrintObjectEvents(objId, HistoryLimits.PAGE(page,size));
    }

    @Command(description = "Send a bad formatted request.")
    public String objPrintObjectEventsError(String objId) {
        return doObjPrintObjectEvents(objId, HistoryLimits.ERROR());
    }

    private String doObjPrintObjectEvents(String objId, HistoryLimits limits) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // Get statuses history
        List<JOSPEvent> eventsHistory = null;
        try {
            eventsHistory = obj.getInfo().getEventsHistory(limits, 10);
        } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
            return String.format("Object '%s' not connected, can't get Events", obj.getId());
        } catch (JSLRemoteObject.MissingPermission e) {
            return String.format("Missing permission to object '%s', can't get Events\n%s", obj.getId(), e.getMessage());
        }

        if (eventsHistory==null || eventsHistory.isEmpty())
            return String.format("No events for '%s' Object", objId);

        return String.format("Events for '%s' Object\n", objId) +
                JOSPEvent.logEvents(eventsHistory, false);
    }

    @Command(description = "Set object's name.")
    public String objSetObjectName(String objId, String objName) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        String oldName = obj.getName();
        try {
            obj.getInfo().setName(objName);
        } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
            return String.format("Object '%s' not connected, can't update name", obj.getId());
        } catch (JSLRemoteObject.MissingPermission e) {
            return String.format("Missing permission to object '%s', can't update name\n%s", obj.getId(), e.getMessage());
        }

        return String.format("Object '%s' name updated from '%s' to '%s'", obj.getId(), oldName, obj.getName());
    }


    // Object's status

    @Command(description = "Print object's component status.")
    public String objStatus(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStruct().getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLBooleanState) {
            compVal = Boolean.toString(((JSLBooleanState) comp).getState());
        } else if (comp instanceof JSLRangeState) {
            compVal = Double.toString(((JSLRangeState) comp).getState());
        }

        if (!compVal.isEmpty())
            return String.format("%s::%s = %s", objId, compPath, compVal);

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

    @Command(description = "Print object's component status history.")
    public String objStatusHistory(String objId, String compPath) {
        return doObjStatusHistory(objId, compPath, HistoryLimits.NO_LIMITS());
    }

    @Command(description = "Print latest 10 object's component status history.")
    public String objStatusHistoryLatest(String objId, String compPath) {
        return doObjStatusHistory(objId, compPath, HistoryLimits.LATEST(10));
    }

    @Command(description = "Print ancient 10 object's component status history.")
    public String objStatusHistoryAncient(String objId, String compPath) {
        return doObjStatusHistory(objId, compPath, HistoryLimits.ANCIENT(10));
    }

    @Command(description = "Print all object's component status history from specified id.")
    public String objStatusHistoryFromID(String objId, String compPath, long id) {
        return doObjStatusHistory(objId, compPath, HistoryLimits.FROM_ID(id));
    }

    @Command(description = "Print all object's component status history until specified id.")
    public String objStatusHistoryToID(String objId, String compPath, long id) {
        return doObjStatusHistory(objId, compPath, HistoryLimits.TO_ID(id));
    }

    @Command(description = "Print all object's component status history contained in the specified range (inclusive).")
    public String objStatusHistoryBetweenID(String objId, String compPath, long start, long end) {
        return doObjStatusHistory(objId, compPath, HistoryLimits.BETWEEN_ID(start,end));
    }

    @Command(description = "Print latest hour object's component status history.")
    public String objStatusHistoryLastHour(String objId, String compPath) {
        return doObjStatusHistory(objId, compPath, HistoryLimits.LAST_HOUR());
    }

    @Command(description = "Print latest hour object's component status history.")
    public String objStatusHistoryPastHour(String objId, String compPath) {
        return doObjStatusHistory(objId, compPath, HistoryLimits.PAST_HOUR());
    }

    @Command(description = "Print specified page of object's component status history.")
    public String objStatusHistoryPage(String objId, String compPath, int page, int size) {
        return doObjStatusHistory(objId, compPath, HistoryLimits.PAGE(page,size));
    }

    @Command(description = "Send a bad formatted request.")
    public String objStatusHistoryError(String objId, String compPath) {
        return doObjStatusHistory(objId, compPath, HistoryLimits.ERROR());
    }

    private String doObjStatusHistory(String objId, String compPath, HistoryLimits limits) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStruct().getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        // Get statuses history
        List<JOSPHistory> statusHistory = null;
        try {
            statusHistory = obj.getStruct().getComponentHistory(comp, limits, 30);
        } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
            return String.format("Object '%s' not connected, can't get component's Status History", obj.getId());
        } catch (JSLRemoteObject.MissingPermission e) {
            return String.format("Missing permission to object '%s', can't get Status History\n%s", obj.getId(), e.getMessage());
        }

        if (statusHistory.isEmpty())
            return String.format("No history for Component '%s' of '%s' Object", compPath, objId);

        return String.format("Status History for Component '%s' of '%s' Object\n", compPath, objId) +
                JOSPHistory.logStatuses(statusHistory, false);
    }


    // Object's actions

    @Command(description = "Exec object's boolean action.")
    public String objActionBooleanSwitch(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStruct().getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLBooleanAction) {
            try {
                ((JSLBooleanAction) comp).execSwitch();
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send boolean action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send boolean action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Boolean action executed on component with path '%s' in '%s' object with switched value", compPath, objId);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

    @Command(description = "Exec object's boolean true action.")
    public String objActionBooleanTrue(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStruct().getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLBooleanAction) {
            try {
                ((JSLBooleanAction) comp).execSetTrue();
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send boolean action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send boolean action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Boolean action executed on component with path '%s' in '%s' object with 'true' value", compPath, objId);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

    @Command(description = "Exec object's boolean false action.")
    public String objActionBooleanFalse(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStruct().getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLBooleanAction) {
            try {
                ((JSLBooleanAction) comp).execSetFalse();
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send boolean action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send boolean action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Boolean action executed on component with path '%s' in '%s' object with 'false' value", compPath, objId);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

    @Command(description = "Exec object's range action.")
    public String objActionRange(String objId, String compPath, String actionDoubleParam) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStruct().getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLRangeAction) {
            try {
                ((JSLRangeAction) comp).execSetValue(Double.parseDouble(actionDoubleParam));
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send range action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send range action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Range action executed on component with path '%s' in '%s' object with '%s' value", compPath, objId, actionDoubleParam);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

    @Command(description = "Exec object's range increase action.")
    public String objActionRangeIncrease(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStruct().getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLRangeAction) {
            try {
                ((JSLRangeAction) comp).execIncrease();
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send range action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send range action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Range action executed on component with path '%s' in '%s' object with increase", compPath, objId);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

    @Command(description = "Exec object's range decrease action.")
    public String objActionRangeDecrease(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStruct().getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLRangeAction) {
            try {
                ((JSLRangeAction) comp).execDecrease();
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send range action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send range action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Range action executed on component with path '%s' in '%s' object with decrease", compPath, objId);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

    @Command(description = "Exec object's range max action.")
    public String objActionRangeMax(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStruct().getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLRangeAction) {
            try {
                ((JSLRangeAction) comp).execSetMax();
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send range action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send range action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Range action executed on component with path '%s' in '%s' object with max", compPath, objId);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

    @Command(description = "Exec object's range min action.")
    public String objActionRangeMin(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStruct().getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLRangeAction) {
            try {
                ((JSLRangeAction) comp).execSetMin();
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send range action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send range action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Range action executed on component with path '%s' in '%s' object with min", compPath, objId);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }


    // Object's permissions

    @Command(description = "Set object's owner id.")
    public String objSetObjectOwner(String objId, String objOwnerId) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        String oldOwner = obj.getInfo().getOwnerId();
        try {
            obj.getInfo().setOwnerId(objOwnerId);
        } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
            return String.format("Object '%s' not connected, can't update owner id", obj.getId());
        } catch (JSLRemoteObject.MissingPermission e) {
            return String.format("Missing permission to object '%s', can't update owner id\n%s", obj.getId(), e.getMessage());
        }

        return String.format("Object '%s' owner updated from '%s' to '%s'", obj.getId(), oldOwner, obj.getInfo().getOwnerId());
    }

    @Command(description = "Add new object's permission.")
    public String objAddPerm(String objId, String srvId, String usrId, String permTypeStr, String connTypeStr) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        JOSPPerm.Type permType = JOSPPerm.Type.valueOf(permTypeStr);
        JOSPPerm.Connection connType = JOSPPerm.Connection.valueOf(connTypeStr);

        try {
            obj.getPerms().addPerm(srvId, usrId, permType, connType);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            return String.format("Object '%s' not connected, can't add permission", obj.getId());
        } catch (JSLRemoteObject.MissingPermission e) {
            return String.format("Missing permission to object '%s', can't add permission\n%s", obj.getId(), e.getMessage());
        }

        return String.format("Object '%s' permission added", obj.getId());
    }

    @Command(description = "Update object's permission.")
    public String objUpdPerm(String objId, String permId, String srvId, String usrId, String permTypeStr, String connTypeStr) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        JOSPPerm.Type permType = JOSPPerm.Type.valueOf(permTypeStr);
        JOSPPerm.Connection connType = JOSPPerm.Connection.valueOf(connTypeStr);

        try {
            obj.getPerms().updPerm(permId, srvId, usrId, permType, connType);
        } catch (JSLRemoteObject.MissingPermission e) {
            return String.format("Missing permission to object '%s', can't update permission\n%s", obj.getId(), e.getMessage());

        } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
            return String.format("Object '%s' not connected, can't update permission", obj.getId());
        }

        return String.format("Object '%s' permission updated", obj.getId());
    }

    @Command(description = "Remove object's permission.")
    public String objRemPerm(String objId, String permId) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        try {
            obj.getPerms().remPerm(permId);

        } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
            return String.format("Object '%s' not connected, can't remove permission", obj.getId());
        } catch (JSLRemoteObject.MissingPermission e) {
            return String.format("Missing permission to object '%s', can't remove permission\n%s", obj.getId(), e.getMessage());
        }

        return String.format("Object '%s' permission removed", obj.getId());
    }


    // Object's listeners

    @Command(description = "Add logger listener to objects manager's events.")
    public String objsMngrAddListeners() {
        objs.addListener(new JSLObjsMngr.ObjsMngrListener() {

            @Override
            public void onObjAdded(JSLRemoteObject obj) {
                System.out.println(PRE + String.format("added '%s' object", obj.getId()) + POST);
            }

            @Override
            public void onObjRemoved(JSLRemoteObject obj) {
                System.out.println(PRE + String.format("removed '%s' object", obj.getId()) + POST);
            }
        });

        return "ok";
    }

    @Command(description = "Add logger listener to object's events.")
    public String objAddListeners(String objId) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        obj.getInfo().addListener(new ObjInfo.RemoteObjectInfoListener() {

            @Override
            public void onNameChanged(JSLRemoteObject obj, String newName, String oldName) {
                System.out.println(PRE + String.format("Name changed object '%s' %-15s > %-15s", obj.getId(), oldName, newName) + POST);
            }

            @Override
            public void onOwnerIdChanged(JSLRemoteObject obj, String newOwnerId, String oldOwnerId) {
                System.out.println(PRE + String.format("OwnerId changed object '%s' %-15s > %-15s", obj.getId(), oldOwnerId, newOwnerId) + POST);
            }

            @Override
            public void onJODVersionChanged(JSLRemoteObject obj, String newJODVersion, String oldJODVersion) {
                System.out.println(PRE + String.format("JODVersion changed object '%s' %-15s > %-15s", obj.getId(), oldJODVersion, newJODVersion) + POST);
            }

            @Override
            public void onModelChanged(JSLRemoteObject obj, String newModel, String oldModel) {
                System.out.println(PRE + String.format("Model changed object '%s' %-15s > %-15s", obj.getId(), oldModel, newModel) + POST);
            }

            @Override
            public void onBrandChanged(JSLRemoteObject obj, String newBrand, String oldBrand) {
                System.out.println(PRE + String.format("Brand changed object '%s' %-15s > %-15s", obj.getId(), oldBrand, newBrand) + POST);
            }

            @Override
            public void onLongDescrChanged(JSLRemoteObject obj, String newLongDescr, String oldLongDescr) {
                System.out.println(PRE + String.format("LongDescr changed object '%s' %-15s > %-15s", obj.getId(), oldLongDescr, newLongDescr) + POST);
            }

        });
        obj.getStruct().addListener(new ObjStruct.RemoteObjectStructListener() {

            @Override
            public void onStructureChanged(JSLRemoteObject obj, JSLRoot newRoot) {
                System.out.println(PRE + String.format("Structure changed object '%s'", obj.getId()) + POST);
            }

        });
        obj.getPerms().addListener(new ObjPerms.RemoteObjectPermsListener() {

            @Override
            public void onPermissionsChanged(JSLRemoteObject obj, List<JOSPPerm> newPerms, List<JOSPPerm> oldPerms) {
                System.out.println(PRE + String.format("Permissions changed object '%s' %-15s > %-15s", obj.getId(), oldPerms.size(), newPerms.size()) + POST);
            }

            @Override
            public void onServicePermChanged(JSLRemoteObject obj, JOSPPerm.Connection connType, JOSPPerm.Type newPermType, JOSPPerm.Type oldPermType) {
                System.out.println(PRE + String.format("Service's permission changed object '%s' %s %-15s > %-15s", obj.getId(), connType, oldPermType, newPermType) + POST);
            }

        });
        obj.getComm().addListener(new ObjComm.RemoteObjectConnListener() {
            @Override
            public void onLocalConnected(JSLRemoteObject obj, JSLLocalClient localClient) {
                System.out.println(PRE + String.format("local object '%s' connected (client id: %s, client addr: %s", obj.getId(), localClient, localClient.getConnectionInfo().getLocalInfo().getAddr().getHostAddress()) + POST);
            }

            @Override
            public void onLocalDisconnected(JSLRemoteObject obj, JSLLocalClient localClient) {
                System.out.println(PRE + String.format("local object '%s' disconnected (client id: %s, client addr: %s", obj.getId(), localClient, localClient.getConnectionInfo().getLocalInfo().getAddr().getHostAddress()) + POST);
            }

            @Override
            public void onCloudConnected(JSLRemoteObject obj) {
                System.out.println(PRE + String.format("cloud object '%s' connected", obj.getId()) + POST);
            }

            @Override
            public void onCloudDisconnected(JSLRemoteObject obj) {
                System.out.println(PRE + String.format("cloud object '%s' disconnected", obj.getId()) + POST);
            }
        });

        return "ok";
    }

    @Command(description = "Add logger listener to object's component events.")
    public String objComponentAddListeners(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStruct().getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        if (comp instanceof JSLBooleanState) {
            ((JSLBooleanState) comp).addListener(new JSLBooleanState.BooleanStateListener() {
                @Override
                public void onStateChanged(JSLBooleanState component, boolean newState, boolean oldState) {
                    System.out.println(PRE + String.format("%s state changed object '%s' %-15s > %-15s", component.getRemoteObject().getId(), component.getName(), oldState, newState) + POST);
                }
            });
        } else if (comp instanceof JSLRangeState) {
            ((JSLRangeState) comp).addListener(new JSLRangeState.RangeStateListener() {
                @Override
                public void onStateChanged(JSLRangeState component, double newState, double oldState) {
                    System.out.println(PRE + String.format("%s state changed object '%s' %-15s > %-15s", component.getRemoteObject().getId(), component.getName(), oldState, newState) + POST);
                }

                @Override
                public void onMinReached(JSLRangeState component, double state, double min) {
                    System.out.println(PRE + String.format("%s min state reached object '%s' %-15s (max: %s)", component.getRemoteObject().getId(), component.getName(), state, min) + POST);
                }

                @Override
                public void onMaxReached(JSLRangeState component, double state, double max) {
                    System.out.println(PRE + String.format("%s max state reached object '%s' %-15s (max: %s)", component.getRemoteObject().getId(), component.getName(), state, max) + POST);
                }
            });
        }

        return "OK";
    }



    private String getCloudObjState(JSLGwS2OClient cloudClient, JSLRemoteObject obj) {
        if (cloudClient.getState().isConnected()) {
            if (obj.getComm().isCloudConnected())
                return "ONLINE";
            else
                return "OFFLINE";
        } else
            return cloudClient.getState().toString();
    }

    private String getLocalObjState(JSLRemoteObject obj) {
        int allClients = ((DefaultObjComm) obj.getComm()).getLocalClients().size();
        return obj.getComm().isLocalConnected() ? String.format("CONNECTED (%d)", allClients) : "DISCONNECTED";
    }

}
