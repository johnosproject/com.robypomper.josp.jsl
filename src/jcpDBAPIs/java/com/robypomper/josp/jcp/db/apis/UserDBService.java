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

package com.robypomper.josp.jcp.db.apis;

import com.robypomper.josp.jcp.db.apis.entities.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;
import java.util.Optional;


@Service
@SessionScope
public class UserDBService {

    // Internal vars

    private final UserRepository users;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<User> user = Optional.empty();


    // Constructor

    public UserDBService(UserRepository users) {
        this.users = users;
    }


    // Access methods

    public List<User> findAll() {
        return users.findAll();
    }

    public Optional<User> get(String usrId) {
        if (!user.isPresent() || user.get().getUsrId().compareTo(usrId) != 0)
            user = users.findById(usrId);
        return user;
    }

    public User save(User stock) throws DataIntegrityViolationException {
        return users.save(stock);
    }

    public long count() {
        return users.count();
    }

}
