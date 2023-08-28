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

import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.exception.PeerException;
import com.robypomper.comm.peer.PeerAbs;

import java.util.Arrays;

public class ByeMsgDefault extends ByeMsgConfigsDefault implements ByeMsgImpl {

    // Class constants

    public static final String BYE_MSG = "bye";


    // Internal vars

    // peer configs
    private final PeerAbs peer;


    // Constructors

    public ByeMsgDefault(PeerAbs peer, DataEncodingConfigs dataEncoding) {
        this(peer, dataEncoding, ENABLE, BYE_MSG);
    }

    public ByeMsgDefault(PeerAbs peer, DataEncodingConfigs dataEncoding, byte[] byeMsg) {
        this(peer, dataEncoding, ENABLE, byeMsg);
    }

    public ByeMsgDefault(PeerAbs peer, DataEncodingConfigs dataEncoding, String byeMsg) {
        this(peer, dataEncoding, ENABLE, byeMsg);
    }

    public ByeMsgDefault(PeerAbs peer, DataEncodingConfigs dataEncoding, boolean enable, byte[] byeMsg) {
        super(dataEncoding, enable, byeMsg);
        this.peer = peer;
    }

    public ByeMsgDefault(PeerAbs peer, DataEncodingConfigs dataEncoding, boolean enable, String byeMsg) {
        super(dataEncoding, enable, byeMsg);
        this.peer = peer;
    }


    // Messages methods

    @Override
    public boolean processByeMsg(byte[] data) {
        if (!Arrays.equals(BYE_MSG.getBytes(peer.getDataEncodingConfigs().getCharset()), data))
            return false;

        emitOnBye(peer, this);

        return true;
    }

    @Override
    public void sendByeMsg() throws PeerException {
        peer.sendData(BYE_MSG.getBytes(peer.getDataEncodingConfigs().getCharset()));
    }

}
