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

package com.robypomper.josp.clients;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.states.HTTPClientState;

import java.util.Date;
import java.util.Map;

public interface HTTPClient {

    // Getter state

    /**
     * @return the JCP Client's status.
     */
    HTTPClientState getState();

    /**
     * @return true only if the HTTP server is reachable.
     */
    boolean isAvailable();

    /**
     * @return true when the check availability is started.
     */
    boolean isAvailableChecking();

    /**
     * @return the last date when the HTTP server was checked and it was available.
     */
    Date getLastAvailable();

    /**
     * @return the last date when a request was executed successfully
     */
    Date getLastRequest();

    /**
     * @return the last date when a request was executed and an error occurs.
     */
    Date getLastRequestError();


    // Availability

    void startCheckAvailability();

    void stopCheckAvailability();


    // Availability listeners

    void addAvailabilityListener(AvailabilityListener listener);

    void removeAvailabilityListener(AvailabilityListener listener);


    // Connection listeners interfaces

    interface AvailabilityListener {

        void onAvailable(HTTPClient httpClient);

        void onNotAvailable(HTTPClient httpClient);

    }


    // Headers and sessions

    void addDefaultHeader(String headerName, String headerValue);

    void removeDefaultHeader(String headerName);

    /**
     * @return true only if the JCP Client had negotiated a session with
     * the designed JCP service.
     */
    boolean isSessionSet();


    // Exec requests

    void execReq(Verb reqType, String path) throws RequestException, ResponseException;

    void execReq(Verb reqType, String path, Map<String, String> params) throws RequestException, ResponseException;

    void execReq(Verb reqType, String path, Object objParam) throws RequestException, ResponseException;

    <T> T execReq(Verb reqType, String path, Class<T> reqObject) throws RequestException, ResponseException;

    <T> T execReq(Verb reqType, String path, Class<T> reqObject, Map<String, String> params) throws RequestException, ResponseException;

    <T> T execReq(Verb reqType, String path, Class<T> reqObject, Object objParam) throws RequestException, ResponseException;


    // Request exceptions

    class RequestException extends Throwable {

        public RequestException(String msg) {
            super(msg);
        }

        public RequestException(String msg, Throwable cause) {
            super(msg, cause);
        }

    }


    // Response exceptions

    class ResponseException extends Throwable {

        protected final String fullUrl;

        public ResponseException(String msg, String fullUrl) {
            this(msg, fullUrl, null);
        }

        public ResponseException(String msg, String fullUrl, Throwable cause) {
            super(msg, cause);
            this.fullUrl = fullUrl;
        }

    }

    class ResponseParsingException extends ResponseException {

        private static final String MSG = "Error on '%s' url response parsing ('%s').";

        public ResponseParsingException(String fullUrl, Throwable cause) {
            super(String.format(MSG, fullUrl, cause.getMessage()), fullUrl, cause);
        }

    }

    class ResponseCode extends ResponseException {

        protected final int code;

        public ResponseCode(String msg, String fullUrl, int code) {
            this(msg, fullUrl, code, null);
        }

        public ResponseCode(String msg, String fullUrl, int code, Throwable cause) {
            super(msg, fullUrl, cause);
            this.code = code;
        }

    }

    class BadRequest_400 extends ResponseCode {

        private static final String MSG = "Server received Bad request for '%s' resource.";

        public BadRequest_400(String fullUrl) {
            super(String.format(MSG, fullUrl), fullUrl, 400);
        }

    }

    class NotAuthorized_403 extends ResponseCode {

        private static final String MSG = "Client NOT authorized to access to '%s' resource.";

        public NotAuthorized_403(String fullUrl) {
            super(String.format(MSG, fullUrl), fullUrl, 403);
        }

    }

    class NotFound_404 extends ResponseCode {

        private static final String MSG = "Resource '%s' NOT found.";

        public NotFound_404(String fullUrl) {
            super(String.format(MSG, fullUrl), fullUrl, 404);
        }

    }

    class Conflict_409 extends ResponseCode {

        private static final String MSG = "Conflict on elaborating request on '%s' resource.";

        public Conflict_409(String fullUrl) {
            super(String.format(MSG, fullUrl), fullUrl, 409);
        }

    }

    class Error_Code extends ResponseCode {

        private static final String MSG = "Response error '%s' code on '%s' resource.";

        public Error_Code(String fullUrl, int code) {
            super(String.format(MSG, code, fullUrl), fullUrl, code);
        }

        public Error_Code(String fullUrl, int code, String body) {
            super(String.format(MSG + " Response body: '%s'", code, fullUrl, body), fullUrl, code);
        }

        public Error_Code(String fullUrl, int code, Throwable cause) {
            super(String.format(MSG, code, fullUrl), fullUrl, code, cause);
        }

    }

}
