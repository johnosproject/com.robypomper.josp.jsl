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

package com.robypomper.discovery;

import java.net.InetAddress;
import java.util.List;


public class DiscoveryService {

    // Public final vars

    public final String name;
    public final String type;
    public final String intf;
    public final String proto;
    public final InetAddress address;
    public final Integer port;
    public final String extra;


    // Constructor

    public DiscoveryService(String name, String type, String intf) {
        this(name, type, intf, null, null, null, null);
    }

    public DiscoveryService(String name, String type, String intf, String proto, InetAddress address, Integer port, String extra) {
        this.name = name;
        this.type = type;
        this.intf = intf;
        this.proto = proto;
        this.address = address;
        this.port = port;
        this.extra = extra;
    }


    // Services comparison methods

    public boolean alreadyIn(List<DiscoveryService> services) {
        return extractFrom(services) != null;
    }

    public DiscoveryService extractFrom(List<DiscoveryService> services) {
        String currType = type.endsWith(".") ? type.substring(0, type.length() - 1) : type;
        currType = currType.endsWith("local") ? currType.substring(0, currType.length() - "local".length()) : currType;
        currType = currType.endsWith(".") ? currType.substring(0, currType.length() - 1) : currType;

        for (DiscoveryService other : services) {
            if (
                    other.name.equalsIgnoreCase(name)
                            && other.type.startsWith(currType)
                            && other.intf.equalsIgnoreCase(intf)
                            && (proto == null || other.proto.equalsIgnoreCase(proto))
                            && (address == null || other.address.getHostAddress().equalsIgnoreCase(address.getHostAddress()))
                            && (port == null || other.port.equals(port))
                            && (extra == null || other.extra.equalsIgnoreCase(extra))
            )
                return other;
        }
        return null;
    }


    // To String override

    @Override
    public String toString() {
        return name + "@" + intf + "//" + address + ":" + port;
    }

}
