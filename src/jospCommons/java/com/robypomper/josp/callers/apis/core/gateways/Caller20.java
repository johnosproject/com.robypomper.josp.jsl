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

package com.robypomper.josp.callers.apis.core.gateways;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.AbsAPI;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.defs.core.gateways.Params20;
import com.robypomper.josp.defs.core.gateways.Paths20;

import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;


/**
 * JOSP Core - Gateways 2.0
 */
public class Caller20 extends AbsAPI {

    // Internal vars

    private final String instanceId;


    // Constructors

    /**
     * Default constructor.
     *
     * @param jcpClient  the JCP client.
     * @param instanceId the JOD instance id.
     */
    public Caller20(JCPClient2 jcpClient, String instanceId) {
        super(jcpClient);
        this.instanceId = instanceId;
    }


    // Access methods

    /**
     * Request to the JCP object's access info for Gateway O2S connection.
     * <p>
     * Object send his public certificate and instance id to the GW O2S and
     * the JCP respond with the GW O2S's address, port and public certificate.
     *
     * @return the GW O2S access info.
     */
    public Params20.O2SAccessInfo getO2SAccessInfo(Certificate clietnPublicCertificate) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException, CertificateEncodingException {
        Params20.O2SAccessRequest accessRequestParam = new Params20.O2SAccessRequest();
        accessRequestParam.instanceId = instanceId;
        accessRequestParam.clientCertificate = clietnPublicCertificate.getEncoded();

        return jcpClient.execReq(Verb.POST, Paths20.FULL_PATH_GWS_O2S_ACCESS, Params20.O2SAccessInfo.class, accessRequestParam, isSecure());
    }

    /**
     * Request to the JCP service's access info for Gateway S2O connection.
     * <p>
     * Service send his public certificate and instance id to the GW S2O and
     * the JCP respond with the GW S2O's address, port and public certificate.
     *
     * @return the GW S2O access info.
     */
    public Params20.S2OAccessInfo getS2OAccessInfo(Certificate clietnPublicCertificate) throws CertificateEncodingException, JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        Params20.S2OAccessRequest accessRequestParam = new Params20.S2OAccessRequest();
        accessRequestParam.instanceId = instanceId;
        accessRequestParam.clientCertificate = clietnPublicCertificate.getEncoded();

        return jcpClient.execReq(Verb.POST, Paths20.FULL_PATH_GWS_S2O_ACCESS, Params20.S2OAccessInfo.class, accessRequestParam, isSecure());
    }
}
