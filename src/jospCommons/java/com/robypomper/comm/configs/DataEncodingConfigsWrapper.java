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

import com.robypomper.comm.client.ClientWrapper;

import java.nio.charset.Charset;

public class DataEncodingConfigsWrapper implements DataEncodingConfigs {

    // Internal vars

    // wrapper
    private final ClientWrapper clientWrapper;
    // data encoding configs
    private Charset charset;
    private byte[] delimiter = null;
    private String delimiterStr = null;


    // Constructors

    public DataEncodingConfigsWrapper(ClientWrapper clientWrapper) {
        this(clientWrapper, CHARSET);
    }

    public DataEncodingConfigsWrapper(ClientWrapper clientWrapper, Charset charset) {
        this(clientWrapper, charset, DELIMITER);
    }

    public DataEncodingConfigsWrapper(ClientWrapper clientWrapper, Charset charset, String delimiter) {
        this.clientWrapper = clientWrapper;

        setCharset(charset);
        setDelimiter(delimiter);
    }

    public DataEncodingConfigsWrapper(ClientWrapper clientWrapper, Charset charset, byte[] delimiter) {
        this.clientWrapper = clientWrapper;

        setCharset(charset);
        setDelimiter(delimiter);
    }


    // Getter/setters

    public byte[] getDelimiter() {
        if (delimiter != null) return delimiter;
        return delimiterStr.getBytes(getCharset());
    }

    public String getDelimiterString() {
        if (delimiter != null) return new String(delimiter, getCharset());
        return delimiterStr;
    }

    public void setDelimiter(byte[] delimiter) {
        if (clientWrapper.getWrapper() != null)
            clientWrapper.getWrapper().getDataEncodingConfigs().setDelimiter(delimiter);

        this.delimiter = delimiter;
    }

    public void setDelimiter(String delimiter) {
        if (clientWrapper.getWrapper() != null)
            clientWrapper.getWrapper().getDataEncodingConfigs().setDelimiter(delimiter);

        this.delimiterStr = delimiter;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        if (clientWrapper.getWrapper() != null)
            clientWrapper.getWrapper().getDataEncodingConfigs().setCharset(charset);

        this.charset = charset;
    }

}
