/*******************************************************************************
 * The John Cloud Platform is the set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jcp.jslwebbridge.webbridge;

import com.robypomper.josp.jsl.JSLSettings_002;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JSLParams {

    // Internal vars

    public final String jslVersion;
    public final boolean useSSL;
    public final String urlAPIs;
    public final String urlAuth;
    public final String clientCallback;


    // Constructors

    public JSLParams(@Value("${jsl.version:2.0.0}") String jslVersion,
                     @Value("${" + JSLSettings_002.JCP_SSL + ".public:" + JSLSettings_002.JCP_SSL_DEF + "}") boolean useSSL,
                     @Value("${" + JSLSettings_002.JCP_URL_APIS + ":" + JSLSettings_002.JCP_URL_DEF_APIS + "}") String urlAPIs,
                     @Value("${" + JSLSettings_002.JCP_URL_AUTH + ":" + JSLSettings_002.JCP_URL_DEF_AUTH + "}") String urlAuth,
                     @Value("${" + JSLSettings_002.JCP_CLIENT_CALLBACK + ":}") String clientCallback) {

        this.jslVersion = jslVersion;
        this.useSSL = useSSL;
        this.urlAPIs = urlAPIs;
        this.urlAuth = urlAuth;
        this.clientCallback = clientCallback;
    }

}
