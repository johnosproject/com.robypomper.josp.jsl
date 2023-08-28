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

package com.robypomper.comm.behaviours;

import com.robypomper.comm.client.ClientWrapper;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.peer.Peer;

public class ByeMsgConfigsWrapper extends ByeMsgConfigsDefault {

    // Internal vars

    // server
    private final ClientWrapper clientWrapper;


    // Constructors

    public ByeMsgConfigsWrapper(ClientWrapper clientWrapper, DataEncodingConfigs dataEncoding) {
        this(clientWrapper, dataEncoding, ENABLE, BYE_MSG);
    }

    public ByeMsgConfigsWrapper(ClientWrapper clientWrapper, DataEncodingConfigs dataEncoding, byte[] byeMsg) {
        this(clientWrapper, dataEncoding, ENABLE, byeMsg);
    }

    public ByeMsgConfigsWrapper(ClientWrapper clientWrapper, DataEncodingConfigs dataEncoding, String byeMsg) {
        this(clientWrapper, dataEncoding, ENABLE, byeMsg);
    }

    public ByeMsgConfigsWrapper(ClientWrapper clientWrapper, DataEncodingConfigs dataEncoding, boolean enable, byte[] byeMsg) {
        super(dataEncoding, enable, byeMsg);

        this.clientWrapper = clientWrapper;
    }

    public ByeMsgConfigsWrapper(ClientWrapper clientWrapper, DataEncodingConfigs dataEncoding, boolean enable, String byeMsg) {
        super(dataEncoding, enable, byeMsg);

        this.clientWrapper = clientWrapper;
    }

    private final ByeMsgListener clientByeMsgListener = new ByeMsgListener() {

        @Override
        public void onBye(Peer peer, ByeMsgImpl byeMsg) {
            emitOnBye(peer, byeMsg);
        }

    };


    // Getter/setters

    @Override
    public void enable(boolean enable) {
        if (clientWrapper != null)
            if (clientWrapper.getWrapper() != null) {
                clientWrapper.getWrapper().getByeConfigs().enable(enable);
                if (isEnable())
                    clientWrapper.getWrapper().getByeConfigs().addListener(clientByeMsgListener);
                else
                    clientWrapper.getWrapper().getByeConfigs().removeListener(clientByeMsgListener);
            }

        super.enable(enable);
    }

    @Override
    public void setByeMsg(byte[] byeMsg) {
        if (clientWrapper != null)
            if (clientWrapper.getWrapper() != null)
                clientWrapper.getWrapper().getByeConfigs().setByeMsg(byeMsg);

        super.setByeMsg(byeMsg);
    }

    @Override
    public void setByeMsg(String byeMsg) {
        if (clientWrapper != null)
            if (clientWrapper.getWrapper() != null)
                clientWrapper.getWrapper().getByeConfigs().setByeMsg(byeMsg);

        super.setByeMsg(byeMsg);
    }

}
