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

package com.robypomper.josp.jcp.external.resources.auth;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.defs.auth.keycloak.Paths20;
import com.robypomper.josp.jcp.clients.JCPClientsMngr;
import com.robypomper.josp.jcp.db.apis.entities.Service;
import com.robypomper.josp.jcp.db.apis.entities.ServiceDetails;
import com.robypomper.josp.jcp.db.apis.entities.User;
import com.robypomper.josp.jcp.db.apis.entities.UserProfile;
import org.keycloak.representations.idm.UserRepresentation;


/**
 * Keycloak implementation of {@link AuthResource} interface.
 */
public class AuthKeycloak implements AuthResource {

    // Internal vars

    private final JCPClientsMngr clientMngr;


    // Constructor

    public AuthKeycloak(JCPClientsMngr clientMngr) {
        this.clientMngr = clientMngr;
    }


    // User Q&M

    /**
     * {@inheritDoc}
     */
    public User queryUser(String usrId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.RequestException, JCPClient2.ResponseException {
        UserRepresentation kcUser = clientMngr.getJCPAPIsClient().execReq(true, Verb.GET, Paths20.FULL_PATH_USER + "/" + usrId, UserRepresentation.class, true);

        UserProfile profile = new UserProfile();
        User user = new User();

        profile.setUsrId(kcUser.getId());
        profile.setEmail(kcUser.getEmail());
        profile.setName(kcUser.getFirstName());
        profile.setSurname(kcUser.getLastName());

        user.setUsrId(kcUser.getId());
        user.setUsername(kcUser.getUsername());
        user.setProfile(profile);

        return user;
    }

    /**
     * {@inheritDoc}
     */
    public Service queryService(String srvId) throws JCPClient2.ConnectionException, JCPClient2.RequestException {
        // No KeyCloack api to get client info
        //ClientRepresentation kcClient = client.execReq(true, Verb.GET, APIAuth.FULL_PATH_CLIENTS + "/" + srvId, ClientRepresentation.class, true);
        //System.out.println("WAR: empty service info are generated, because keycloak don't provide client's info");

        ServiceDetails details = new ServiceDetails();
        Service service = new Service();

        details.setSrvId(srvId);
        details.setEmail("");
        details.setWeb("");
        details.setCompany("");

        service.setSrvId(srvId);
        service.setSrvName(srvId.replace("-", " "));
        service.setDetails(details);

        return service;
    }

}
