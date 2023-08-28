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

import com.robypomper.josp.jcp.db.apis.entities.Service;
import com.robypomper.josp.jcp.db.apis.entities.ServiceStatus;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;


@org.springframework.stereotype.Service
public class ServiceDBService {

    // Internal vars

    private final ServiceRepository services;
    private final ServiceStatusRepository servicesStatus;

    // Constructor

    public ServiceDBService(ServiceRepository services, ServiceStatusRepository servicesStatus) {
        this.services = services;
        this.servicesStatus = servicesStatus;
    }


    // Access methods

    public List<Service> findAll() {
        return services.findAll();
    }

    public Optional<Service> find(String srvId) {
        return services.findById(srvId);
    }

    public Optional<ServiceStatus> findStatus(String fullSrvId) {
        return servicesStatus.findById(fullSrvId);
    }

    public Service save(Service srv) throws DataIntegrityViolationException {
        return services.save(srv);
    }

    public ServiceStatus save(ServiceStatus srvStatus) throws DataIntegrityViolationException {
        return servicesStatus.save(srvStatus);
    }

    public long count() {
        return services.count();
    }

    public long countOnline() {
        return servicesStatus.countServicesOnline();
    }

    public long countOffline() {
        return count() - countOnline();
    }

    public long countInstances() {
        return servicesStatus.count();
    }

    public long countInstancesOnline() {
        return servicesStatus.countByOnline(true);
    }

    public long countInstancesOffline() {
        return servicesStatus.countByOnline(false);
    }

}
