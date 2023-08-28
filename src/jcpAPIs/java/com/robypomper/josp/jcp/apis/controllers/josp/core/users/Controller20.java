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

package com.robypomper.josp.jcp.apis.controllers.josp.core.users;

import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.defs.core.users.Params20;
import com.robypomper.josp.defs.core.users.Paths20;
import com.robypomper.josp.jcp.base.controllers.ControllerImpl;
import com.robypomper.josp.jcp.base.spring.SecurityUser;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import com.robypomper.josp.jcp.db.apis.UserDBService;
import com.robypomper.josp.jcp.db.apis.entities.User;
import com.robypomper.josp.jcp.external.resources.auth.AuthDefault;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Optional;


/**
 * JOSP Core - Users 2.0
 */
@SuppressWarnings("unused")
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends ControllerImpl {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(Controller20.class);
    @Autowired
    private AuthDefault authDefault;
    @Autowired
    private UserDBService userService;
    @Autowired
    private HttpSession httpSession;


    // User methods

    /**
     * Return current user id and username.
     * <p>
     * This method afterwards check if the user is registered in the JCP db. If
     * not, the it query the auth's server and register current user in the JCP
     * db.
     *
     * @return current user representation.
     */
    @GetMapping(path = Paths20.FULL_PATH_CURRENT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = Paths20.DESCR_PATH_CURRENT,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User's id and username", response = Params20.User.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 404, message = "User with specified id not found"),
            @ApiResponse(code = 500, message = "Authorization not setup"),
            @ApiResponse(code = 503, message = "Authorization server not available"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public ResponseEntity<Params20.User> getCurrent() {
        try {
            String usrId = SecurityUser.getUserID();
            User user = getOrRegisterUser(usrId);
            Collection<String> roles = SecurityUser.getUserRoles();
            boolean isAuthenticated = SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
            boolean isAdmin = roles.contains("mng");
            boolean isMaker = roles.contains("maker");
            boolean isDeveloper = roles.contains("devs");

            Params20.User usrName = new Params20.User();
            usrName.usrId = user.getUsrId();
            usrName.username = user.getUsername();
            usrName.authenticated = isAuthenticated;
            usrName.admin = isAdmin;
            usrName.maker = isMaker;
            usrName.developer = isDeveloper;

            updateUserRoles(user, isAdmin, isMaker, isDeveloper);

            return ResponseEntity.ok(usrName);

        } catch (SecurityUser.UserNotAuthenticated e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated.", e);
        } catch (SecurityUser.AuthNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authorization not setup.", e);
        } catch (JCPClient2.AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't connect to remote service because authentication problems.", e);
        } catch (JCPClient2.ConnectionException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't connect to remote service.", e);
        } catch (JCPClient2.RequestException | JCPClient2.ResponseException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error on executing remote request.", e);
        }
    }


    // User register

    /**
     * Looks for <code>userId</code> on JCP db, if can't find it then create a
     * new user querying the auth server, store it on JCP db and finally return
     * current user representation.
     *
     * @param usrId the <code>usrId</code> to search in the JCP db or to register.
     * @return the {@link User} object stored on the JCP db.
     */
    private User getOrRegisterUser(String usrId) throws JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.RequestException, JCPClient2.ResponseException {
        Optional<User> optUser = userService.get(usrId);

        if (optUser.isPresent())
            return optUser.get();

        User newUser = authDefault.queryUser(usrId);
        return userService.save(newUser);
    }


    private void updateUserRoles(User user, boolean isAdmin, boolean isMaker, boolean isDeveloper) {
        boolean changed = false;
        if (user.isAdmin() != isAdmin) {
            changed = true;
            user.setAdmin(isAdmin);
        }

        if (user.isMaker() != isMaker) {
            changed = true;
            user.setMaker(isMaker);
        }

        if (user.isDeveloper() != isDeveloper) {
            changed = true;
            user.setDeveloper(isDeveloper);
        }

        if (changed)
            userService.save(user);
    }

}
