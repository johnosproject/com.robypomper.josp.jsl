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

package com.robypomper.comm.connection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * Tested methods:
 * - getLastConnection/Disconnection
 * - getHeartBeat
 * - getData
 * <p>
 * Indirect tested methods:
 * - constructors: on all methods
 * - updateOnConnected()/updateOnConnected(): on all connection methods
 * - updateOnHeartBeatSuccess()/updateOnHeartBeatFail(): on all heartbeat methods
 * - updateOnDataRx()/updateOnDataTx(): on all data methods
 */
public class ConnectionStatsDefault_Test {

    // Class constants

    private static final byte[] DATA = "data example".getBytes();

    // connection/disconnection

    @Test
    public void METHOD_connection_ON_nothing() {
        ConnectionStatsDefault connStats = new ConnectionStatsDefault();

        Assertions.assertNull(connStats.getLastConnection());
        Assertions.assertNull(connStats.getLastDisconnection());
    }

    @Test
    public void METHOD_connection_ON_updOnConnected() {
        ConnectionStatsDefault connStats = new ConnectionStatsDefault();
        connStats.updateOnConnected();

        Assertions.assertTrue(checkDate(connStats.getLastConnection(), new Date(), 100));
        Assertions.assertNull(connStats.getLastDisconnection());
    }

    @Test
    public void METHOD_connection_ON_updOnDisconnected() {
        ConnectionStatsDefault connStats = new ConnectionStatsDefault();
        connStats.updateOnDisconnected();

        Assertions.assertNull(connStats.getLastConnection());
        Assertions.assertTrue(checkDate(connStats.getLastDisconnection(), new Date(), 100));
    }


    // heartbeat

    @Test
    public void METHOD_heartbeat_ON_nothing() {
        ConnectionStatsDefault connStats = new ConnectionStatsDefault();

        Assertions.assertNull(connStats.getLastHeartBeat());
        Assertions.assertNull(connStats.getLastHeartBeatFailed());
        Assertions.assertEquals(0, connStats.getHeartBeatReceived());
    }

    @Test
    public void METHOD_heartbeat_ON_success() {
        ConnectionStatsDefault connStats = new ConnectionStatsDefault();
        connStats.updateOnHeartBeatSuccess();

        Assertions.assertTrue(checkDate(connStats.getLastHeartBeat(), new Date(), 100));
        Assertions.assertNull(connStats.getLastHeartBeatFailed());
        Assertions.assertEquals(1, connStats.getHeartBeatReceived());
    }

    @Test
    public void METHOD_heartbeat_ON_successMultiple() {
        ConnectionStatsDefault connStats = new ConnectionStatsDefault();
        connStats.updateOnHeartBeatSuccess();
        connStats.updateOnHeartBeatSuccess();
        connStats.updateOnHeartBeatSuccess();

        Assertions.assertTrue(checkDate(connStats.getLastHeartBeat(), new Date(), 100));
        Assertions.assertNull(connStats.getLastHeartBeatFailed());
        Assertions.assertEquals(3, connStats.getHeartBeatReceived());
    }

    @Test
    public void METHOD_heartbeat_ON_fail() {
        ConnectionStatsDefault connStats = new ConnectionStatsDefault();
        connStats.updateOnHeartBeatFail();

        Assertions.assertNull(connStats.getLastHeartBeat());
        Assertions.assertTrue(checkDate(connStats.getLastHeartBeatFailed(), new Date(), 100));
        Assertions.assertEquals(0, connStats.getHeartBeatReceived());
    }


    // data

    @Test
    public void METHOD_data_ON_nothing() {
        ConnectionStatsDefault connStats = new ConnectionStatsDefault();

        Assertions.assertNull(connStats.getLastDataRx());
        Assertions.assertEquals(0, connStats.getBytesRx());
        Assertions.assertNull(connStats.getLastDataTx());
        Assertions.assertEquals(0, connStats.getBytesTx());
    }

    @Test
    public void METHOD_data_ON_rx() {
        ConnectionStatsDefault connStats = new ConnectionStatsDefault();
        connStats.updateOnDataRx(DATA);

        Assertions.assertTrue(checkDate(connStats.getLastDataRx(), new Date(), 100));
        Assertions.assertEquals(DATA.length, connStats.getBytesRx());
        Assertions.assertNull(connStats.getLastDataTx());
        Assertions.assertEquals(0, connStats.getBytesTx());
    }

    @Test
    public void METHOD_data_ON_rxMultiple() {
        ConnectionStatsDefault connStats = new ConnectionStatsDefault();
        connStats.updateOnDataRx(DATA);
        connStats.updateOnDataRx(DATA);
        connStats.updateOnDataRx(DATA);

        Assertions.assertTrue(checkDate(connStats.getLastDataRx(), new Date(), 100));
        Assertions.assertEquals(DATA.length * 3, connStats.getBytesRx());
        Assertions.assertNull(connStats.getLastDataTx());
        Assertions.assertEquals(0, connStats.getBytesTx());
    }

    @Test
    public void METHOD_data_ON_tx() {
        ConnectionStatsDefault connStats = new ConnectionStatsDefault();
        connStats.updateOnDataTx(DATA);

        Assertions.assertNull(connStats.getLastDataRx());
        Assertions.assertEquals(0, connStats.getBytesRx());
        Assertions.assertTrue(checkDate(connStats.getLastDataTx(), new Date(), 100));
        Assertions.assertEquals(DATA.length, connStats.getBytesTx());
    }

    @Test
    public void METHOD_data_ON_txMultiple() {
        ConnectionStatsDefault connStats = new ConnectionStatsDefault();
        connStats.updateOnDataTx(DATA);
        connStats.updateOnDataTx(DATA);
        connStats.updateOnDataTx(DATA);

        Assertions.assertNull(connStats.getLastDataRx());
        Assertions.assertEquals(0, connStats.getBytesRx());
        Assertions.assertTrue(checkDate(connStats.getLastDataTx(), new Date(), 100));
        Assertions.assertEquals(DATA.length * 3, connStats.getBytesTx());
    }


    // Others

    public static boolean checkDate(Date date1, Date date2, int toleranceMs) {
        return Math.abs(date2.getTime() - date1.getTime()) < toleranceMs;
    }

}
