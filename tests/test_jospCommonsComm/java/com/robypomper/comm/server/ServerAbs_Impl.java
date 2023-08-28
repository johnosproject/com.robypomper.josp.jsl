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

package com.robypomper.comm.server;

import com.robypomper.comm.exception.ServerStartupException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.charset.Charset;

public class ServerAbs_Impl extends ServerAbs {

    public String lastDataRx;

    public ServerAbs_Impl(String localId, int bindPort, String protoName) {
        super(localId, bindPort, protoName);
    }

    protected ServerAbs_Impl(String localId, InetAddress bindAddr, int bindPort, String protoName) {
        super(localId, bindAddr, bindPort, protoName);
    }

    protected ServerAbs_Impl(String localId, InetAddress bindAddr, int bindPort, String protoName, Charset charset, byte[] delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, byte[] byeMsg) {
        super(localId, bindAddr, bindPort, protoName, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg);
    }

    protected ServerAbs_Impl(String localId, InetAddress bindAddr, int bindPort, String protoName, Charset charset, String delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, String byeMsg) {
        super(localId, bindAddr, bindPort, protoName, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg);
    }

    @Override
    protected ServerSocket generateBindedServerSocket(InetAddress bindAddr, int bindPort) throws ServerStartupException {
        try {
            return new ServerSocket(bindPort, 50, bindAddr);

        } catch (IOException e) {
            throw new ServerStartupException(this, bindAddr, bindPort, e);
        }
    }

    @Override
    public boolean processData(ServerClient client, byte[] data) {
        return false;
    }

    @Override
    public boolean processData(ServerClient client, String data) {
        lastDataRx = data;
        return true;
    }

}
