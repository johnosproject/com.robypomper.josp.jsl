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

package com.robypomper.josp.test.mocks.jsl;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.clients.JCPAPIsClientSrv;

import java.util.Map;

public class MockJCPClient_Service extends JCPAPIsClientSrv {

    public MockJCPClient_Service() {
        super(false, null, null, null, null, null, null);
    }

    public void setServiceId(String srvId) {
    }

    public void setUserId(String usrId) {
    }

    public void setLoginCodeAndReconnect(String loginCode) {
    }

    @Override
    protected void storeTokens() {
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void connect() {
    }

    @Override
    public void disconnect() {
    }

    @Override
    public void addConnectionListener(ConnectionListener listener) {
    }

    @Override
    public void removeConnectionListener(ConnectionListener listener) {
    }

    @Override
    public boolean isConnecting() {
        return false;
    }

    @Override
    public boolean isClientCredentialFlowEnabled() {
        return false;
    }

    @Override
    public boolean isAuthCodeFlowEnabled() {
        return false;
    }

    @Override
    public void setLoginCode(String loginCode) {
    }

    @Override
    public void userLogout() {
    }

    @Override
    public void addLoginListener(LoginListener listener) {
    }

    @Override
    public void removeLoginListener(LoginListener listener) {
    }

    @Override
    public void addDefaultHeader(String headerName, String headerValue) {
    }

    @Override
    public void removeDefaultHeader(String headerName) {
    }

    @Override
    public boolean isSessionSet() {
        return false;
    }

    @Override
    public void execReq(Verb reqType, String path) throws ConnectionException, RequestException, ResponseException {
    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path) throws ConnectionException, RequestException, ResponseException {
    }

    @Override
    public void execReq(Verb reqType, String path, boolean secure) throws ConnectionException, RequestException, ResponseException {
    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path, boolean secure) throws ConnectionException, RequestException, ResponseException {
    }

    @Override
    public void execReq(Verb reqType, String path, Map<String, String> params, boolean secure) throws ConnectionException, RequestException, ResponseException {
    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path, Map<String, String> params, boolean secure) throws ConnectionException, RequestException, ResponseException {
    }

    @Override
    public void execReq(Verb reqType, String path, Object objParam, boolean secure) throws ConnectionException, RequestException, ResponseException {
    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path, Object objParam, boolean secure) throws ConnectionException, RequestException, ResponseException {
    }

    @Override
    public <T> T execReq(Verb reqType, String path, Class<T> reqObject, boolean secure) throws ConnectionException, RequestException, ResponseException {
        return null;
    }

    @Override
    public <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, boolean secure) throws ConnectionException, RequestException, ResponseException {
        return null;
    }

    @Override
    public <T> T execReq(Verb reqType, String path, Class<T> reqObject, Map<String, String> params, boolean secure) throws ConnectionException, RequestException, ResponseException {
        return null;
    }

    @Override
    public <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, Map<String, String> params, boolean secure) throws ConnectionException, RequestException, ResponseException {
        return null;
    }

    @Override
    public <T> T execReq(Verb reqType, String path, Class<T> reqObject, Object objParam, boolean secure) throws ConnectionException, RequestException, ResponseException {
        return null;
    }

    @Override
    public <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, Object objParam, boolean secure) throws ConnectionException, RequestException, ResponseException {
        return null;
    }
}
