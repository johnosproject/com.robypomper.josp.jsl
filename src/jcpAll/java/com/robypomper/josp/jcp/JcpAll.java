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

package com.robypomper.josp.jcp;

import com.robypomper.java.JavaSSLIgnoreChecks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * JCP API's service main class.
 * <p>
 * This class start a Spring Boot application that provide JCP API services.
 */
@SpringBootApplication
public class JcpAll {

    public static void main(String[] args) {
        String DISABLE_SSL_CHECKS = System.getenv("DISABLE_SSL_CHECKS");    // NONE, LOCALHOST, ALL
        if (DISABLE_SSL_CHECKS.compareToIgnoreCase("LOCALHOST") == 0)
            JavaSSLIgnoreChecks.disableSSLChecksAndHostVerifierOnLocalHost();
        else if (DISABLE_SSL_CHECKS.compareToIgnoreCase("ALL") == 0)
            JavaSSLIgnoreChecks.disableSSLChecksAndHostVerifierOnAllHost();

        SpringApplication.run(JcpAll.class, args);
    }

}
