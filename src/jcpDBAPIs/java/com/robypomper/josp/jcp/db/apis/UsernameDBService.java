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

import com.robypomper.josp.jcp.db.apis.entities.UserName;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsernameDBService {

    private final UsernameRepository usernames;

    public UsernameDBService(UsernameRepository usernames) {
        this.usernames = usernames;
    }

    public List<UserName> findAll() {
        return usernames.findAll();
    }

    public Optional<UserName> findById(Long id) {
        return usernames.findById(id);
    }

    public UserName save(UserName stock) throws DataIntegrityViolationException {
        return usernames.save(stock);
    }

    public void deleteById(Long id) {
        usernames.deleteById(id);
    }
}
