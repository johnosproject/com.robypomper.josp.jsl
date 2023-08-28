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

package com.robypomper.josp.jcp.frontend.controllers.pub;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.FileNotFoundException;
import java.io.InputStream;

@RestController
public class SinglePageAppController {

    @RequestMapping(value = {
            "/objects/**",
            "/service/**",
            "/jcp/**",
            "/stats/**"})
    public ResponseEntity<StreamingResponseBody> export() throws FileNotFoundException {
        InputStream indexPageStream = this.getClass().getClassLoader().getResourceAsStream("public/index.html");

        StreamingResponseBody responseBody = outputStream -> {
            int numberOfBytesToWrite;
            byte[] data = new byte[1024];
            while ((numberOfBytesToWrite = indexPageStream.read(data, 0, data.length)) != -1)
                outputStream.write(data, 0, numberOfBytesToWrite);

            indexPageStream.close();
        };

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(responseBody);
    }

}