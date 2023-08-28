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

package com.robypomper.comm.configs;

import java.nio.charset.Charset;

public class DataEncodingConfigsDefault implements DataEncodingConfigs {


    // Internal vars

    // data encoding configs
    private Charset charset;
    private byte[] delimiter = null;
    private String delimiterStr = null;


    // Constructors

    public DataEncodingConfigsDefault() {
        this(CHARSET);
    }

    public DataEncodingConfigsDefault(Charset charset) {
        this(charset, DELIMITER);
    }

    public DataEncodingConfigsDefault(Charset charset, String delimiter) {
        setCharset(charset);
        setDelimiter(delimiter);
    }

    public DataEncodingConfigsDefault(Charset charset, byte[] delimiter) {
        setCharset(charset);
        setDelimiter(delimiter);
    }


    // Getter/setters

    @Override
    public byte[] getDelimiter() {
        if (delimiter != null) return delimiter;
        return delimiterStr.getBytes(getCharset());
    }

    @Override
    public String getDelimiterString() {
        if (delimiter != null) return new String(delimiter, getCharset());
        return delimiterStr;
    }

    @Override
    public void setDelimiter(byte[] delimiter) {
        this.delimiter = delimiter;
        if (delimiter != null)
            this.delimiterStr = null;
    }

    @Override
    public void setDelimiter(String delimiter) {
        if (delimiterStr != null)
            this.delimiter = null;
        this.delimiterStr = delimiter;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

}
