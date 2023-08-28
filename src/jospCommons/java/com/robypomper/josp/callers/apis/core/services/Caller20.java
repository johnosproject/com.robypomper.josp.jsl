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

package com.robypomper.josp.callers.apis.core.services;

import com.robypomper.josp.clients.AbsAPISrv;
import com.robypomper.josp.clients.JCPAPIsClientSrv;


/**
 * JOSP Core - Services 2.0
 */
public class Caller20 extends AbsAPISrv {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public Caller20(JCPAPIsClientSrv jcpClient) {
        super(jcpClient);
        //registerToJCP();
    }


    //// Registration methods
    //
    //public void registerToJCP() {
    //    try {
    //        jcpClient.execReq(Verb.POST, Paths20.FULL_PATH_REGISTER, Params20.SrvName.class, isSecure());
    //
    //    } catch (JCPClient2.RequestException | JCPClient2.AuthenticationException | JCPClient2.ConnectionException | JCPClient2.ResponseException ignore) {
    //        jcpClient.addConnectionListener(new JCPClient2.ConnectionListener() {
    //            @Override
    //            public void onConnected(JCPClient2 jcpClient) {
    //                registerToJCP();
    //            }
    //
    //            @Override
    //            public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {
    //            }
    //
    //            @Override
    //            public void onAuthenticationFailed(JCPClient2 jcpClient, Throwable t) {
    //            }
    //
    //            @Override
    //            public void onDisconnected(JCPClient2 jcpClient) {
    //            }
    //        });
    //    }
    //}

}
