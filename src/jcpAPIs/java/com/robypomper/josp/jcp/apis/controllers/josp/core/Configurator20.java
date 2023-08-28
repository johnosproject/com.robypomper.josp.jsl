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

package com.robypomper.josp.jcp.apis.controllers.josp.core;

import com.robypomper.josp.defs.core.Versions;
import com.robypomper.josp.jcp.base.spring.SwaggerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.plugins.Docket;


/**
 * JOSP Core
 */
@Configuration(Versions.API_NAME)
public class Configurator20 {

    @Autowired
    private SwaggerConfigurer swagger;

    // Docs configs

    @Bean("swaggerConfig" + Versions.API_NAME)
    public Docket swaggerConfig() {
        return swagger.createAPIsGroup(Versions.API_NAME, Versions.API_PATH_BASE, Versions.VER_JCP_APIs_2_0, Versions.API_NAME);
    }

}