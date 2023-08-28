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

import com.robypomper.comm.behaviours.ByeMsgConfigs;
import com.robypomper.comm.behaviours.HeartBeatConfigs;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.exception.ServerStartupException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.charset.Charset;

public abstract class ServerAbsTCP extends ServerAbs {

    // Constructors

    protected ServerAbsTCP(String localId, int bindPort, String protoName) {
        this(localId, null, bindPort, protoName);
    }

    protected ServerAbsTCP(String localId, InetAddress bindAddr, int bindPort, String protoName) {
        this(localId, bindAddr, bindPort, protoName,
                DataEncodingConfigs.CHARSET, DataEncodingConfigs.DELIMITER,
                HeartBeatConfigs.TIMEOUT_MS, HeartBeatConfigs.TIMEOUT_HB_MS, HeartBeatConfigs.ENABLE_HB_RES,
                ByeMsgConfigs.ENABLE, ByeMsgConfigs.BYE_MSG);
    }

    protected ServerAbsTCP(String localId, InetAddress bindAddr, int bindPort, String protoName,
                           Charset charset, byte[] delimiter,
                           int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                           Boolean enableByeMsg, byte[] byeMsg) {
        super(localId, bindAddr, bindPort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
    }

    protected ServerAbsTCP(String localId, InetAddress bindAddr, int bindPort, String protoName,
                           Charset charset, String delimiter,
                           int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                           Boolean enableByeMsg, String byeMsg) {
        super(localId, bindAddr, bindPort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
    }


    // Connection methods

    @Override
    protected ServerSocket generateBindedServerSocket(InetAddress bindAddr, int bindPort) throws ServerStartupException {
        try {
            return new ServerSocket(bindPort, 50, bindAddr);

        } catch (IOException e) {
            throw new ServerStartupException(this, bindAddr, bindPort, e);
        }
    }

}
