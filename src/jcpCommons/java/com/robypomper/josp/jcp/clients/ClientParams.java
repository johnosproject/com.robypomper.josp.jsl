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

package com.robypomper.josp.jcp.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClientParams {

    //@formatter:off
    @Value("${jcp.client.id}")                  public String clientId;
    @Value("${jcp.client.secret}")              public String clientSecret;
    @Value("${jcp.client.callback:'NA'}")       public String clientCallBack;
    @Value("${jcp.client.ssl.private}")         public boolean sslPrivate;
    @Value("${jcp.client.ssl.public}")          public boolean sslPublic;
    //@Value("${jcp.client.auth.private}")        public String authHostPrivate;
    @Value("${jcp.client.auth.public}")         public String authHostPublic;
    @Value("${jcp.client.auth.port}")           public String authPort;
    @Value("${jcp.client.apis.private:'NA'}")   public String apisHostPrivate;
    @Value("${jcp.client.apis.public:'NA'}")    public String apisHostPublic;
    @Value("${jcp.client.apis.port:'NA'}")      public String apisPort;
    @Value("${jcp.client.jslWB.private:'NA'}")  public String jslWBHostPrivate;
    @Value("${jcp.client.jslWB.public:'NA'}")   public String jslWBHostPublic;
    @Value("${jcp.client.jslWB.port:'NA'}")     public String jslWBPort;
    @Value("${jcp.client.fe.private:'NA'}")     public String feHostPrivate;
    @Value("${jcp.client.fe.public:'NA'}")      public String feHostPublic;
    @Value("${jcp.client.fe.port:'NA'}")        public String fePort;
    //@formatter:on

}
