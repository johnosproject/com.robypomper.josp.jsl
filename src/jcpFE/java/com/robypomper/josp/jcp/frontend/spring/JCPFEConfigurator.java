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

package com.robypomper.josp.jcp.frontend.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


@Configuration
public class JCPFEConfigurator extends HandlerInterceptorAdapter implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/frontend").setViewName("forward:/frontend/index.html");
        registry.addViewController("/frontend/").setViewName("forward:/frontend/index.html");
        registry.addViewController("/frontend/{spring:\\w+}").setViewName("forward:/frontend/index.html");
        registry.addViewController("/frontend/{spring:\\w+}/**{spring:?!(\\.js|\\.css|\\.png)$}").setViewName("forward:/frontend/index.html");
        registry.addViewController("/frontend/**/{spring:\\w+}").setViewName("forward:/frontend/index.html");
    }

}
