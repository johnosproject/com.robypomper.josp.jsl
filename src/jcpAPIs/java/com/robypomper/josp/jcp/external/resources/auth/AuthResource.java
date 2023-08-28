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

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.jcp.db.apis.entities.Service;
import com.robypomper.josp.jcp.db.apis.entities.ServiceDetails;
import com.robypomper.josp.jcp.db.apis.entities.User;
import com.robypomper.josp.jcp.db.apis.entities.UserProfile;

/**
 * Interface for authentication resource.
 * <p>
 * This interface implementations can be used to access auth's server resources
 * and methods.
 */
public interface AuthResource {

    // User Q&M

    /**
     * Request <code>usrId</code> user to auth server and cast to {@link User}
     * instance including {@link UserProfile}
     * field.
     *
     * @param usrId the user id.
     * @return instance of {@link Service} object populated with auth's user info.
     */
    User queryUser(String usrId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.RequestException, JCPClient2.ResponseException;

    /**
     * Request <code>srvId</code> service to auth server and cast to {@link Service}
     * instance including {@link ServiceDetails} field.
     *
     * @param srvId the service id.
     * @return instance of {@link Service} object populated with auth's service info.
     */
    Service queryService(String srvId) throws JCPClient2.ConnectionException, JCPClient2.RequestException;

}
