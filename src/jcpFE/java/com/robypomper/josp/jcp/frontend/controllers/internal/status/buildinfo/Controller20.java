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

package com.robypomper.josp.jcp.frontend.controllers.internal.status.buildinfo;


import com.robypomper.jcpFE.BuildInfoJcpFE;
import com.robypomper.josp.jcp.defs.base.internal.status.buildinfo.Params20;
import com.robypomper.josp.jcp.defs.base.internal.status.buildinfo.Paths20;
import io.swagger.annotations.Api;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;


/**
 * JCP All - Status / Build Info 2.0 (Front End)
 */
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
@Profile("jcp-frontend")
public class Controller20 extends com.robypomper.josp.jcp.base.controllers.internal.status.buildinfo.Controller20 {

    static Params20.BuildInfo current = Params20.BuildInfo.clone(BuildInfoJcpFE.current);

    @Override
    protected Params20.BuildInfo getInstanceReqSubClass() {
        return current;
    }

}
