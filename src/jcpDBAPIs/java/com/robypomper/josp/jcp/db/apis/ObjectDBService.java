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

import com.robypomper.josp.jcp.db.apis.entities.Object;
import com.robypomper.josp.jcp.db.apis.entities.ObjectId;
import com.robypomper.josp.jcp.db.apis.entities.ObjectStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ObjectDBService {

    // Internal vars

    private final ObjectRepository objects;
    private final ObjectIdRepository objectsId;
    private final ObjectOwnerRepository objectsOwner;
    private final ObjectStatusRepository objectsStatus;


    // Constructor

    public ObjectDBService(ObjectRepository objects,
                           ObjectIdRepository objectsId,
                           ObjectOwnerRepository objectsOwner,
                           ObjectStatusRepository objectsStatus) {
        this.objects = objects;
        this.objectsId = objectsId;
        this.objectsOwner = objectsOwner;
        this.objectsStatus = objectsStatus;
    }


    // Access methods

    public List<Object> findAll() {
        return objects.findAll();
    }

    public Optional<Object> find(String objId) throws DataIntegrityViolationException {
        return objects.findById(objId);
    }

    public ObjectId save(ObjectId objId) throws DataIntegrityViolationException {
        return objectsId.save(objId);
    }

    public Object save(Object obj) throws DataIntegrityViolationException {
        assert obj.getOwner() != null;
        assert obj.getOwner().getObjId() != null;
        assert obj.getInfo() != null;
        assert obj.getInfo().getObjId() != null;

        return objects.save(obj);
    }

    public ObjectStatus save(ObjectStatus objStatus) throws DataIntegrityViolationException {
        return objectsStatus.save(objStatus);
    }

    public long count() {
        return objects.count();
    }

    public long countActive() {
        return objects.countByActive(true);
    }

    public long countInactive() {
        return objects.countByActive(false);
    }

    public long countOnline() {
        return objectsStatus.countByOnline(true);
    }

    public long countOffline() {
        return objectsStatus.countByOnline(false);
    }

    public long countOwners() {
        return objectsOwner.countOwners();
    }

}
