/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.info;

import lombok.Data;
import lombok.Getter;

/**
 * Definitions class dedicated to project's contacts.
 */
public class JCPContacts {

    // Constants

    public static final String WWW_RP = "robypomper.com";
    public static final String MAIL_AT_RP = "%s@" + WWW_RP;
    public static final String URL_RP = "https://www." + WWW_RP;

    public static final String WWW_JOHN = "johnosproject.com";
    public static final String MAIL_AT_JOHN = "%s@" + WWW_JOHN;
    public static final String URL_JOHN = "https://www." + WWW_JOHN;

    public static final String WWW_CODAX = "codax.it";
    public static final String MAIL_AT_CODAX = "%s@" + WWW_CODAX;
    public static final String URL_CODAX = "https://www." + WWW_CODAX;


    // Static declarations and initializations

    @Getter
    private static final Contact john;
    @Getter
    private static final Contact roby;

    static {
        john = new Contact("John O.S. Project","",String.format(MAIL_AT_JOHN,"info"),URL_JOHN);
        roby = new Contact("Roberto","Pompermaier",String.format(MAIL_AT_JOHN,"robypomper"),URL_JOHN);
    }


    // Data types

    @Data
    public static class Contact {

        private final String name;
        private final String surname;
        private final String email;
        private final String url;

        public String getFullName() {
            return String.format("%s %s", getName(), getSurname());
        }
    }

}
