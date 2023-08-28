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

package com.robypomper.josp.jcp.base.controllers;

import com.robypomper.josp.jcp.defs.base.errors.Params20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@ControllerAdvice
@RestController
public class ControllerError implements ErrorController {

    // Class constants

    public static final String PATH_ERROR = "/error";


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(ControllerError.class);


    // Error handlers

    @RequestMapping(value = PATH_ERROR)
    public Object handleError(final HttpServletRequest request) {
        // Error info
        HttpStatus status = generateHttpStatus(request);
        String type = "HTTP_" + status;
        String msg = extractOriginalError(request, status);
        String details = "";

        // Request info
        String headerAccept = request.getHeader("accept");
        String reqUrl = extractOriginalUri(request, status);

        String originalClient = extractOriginalClientAddr(request);
        //String reqSource = String.format("method: %s, src: %s:%d, remoteUser: %s", request.getMethod(), request.getRemoteAddr(), request.getRemotePort(), request.getRemoteUser());
        String reqSource = String.format("method: %s, src: %s, remoteUser: %s", request.getMethod(), originalClient, request.getRemoteUser());
        ControllerError.log.warn(String.format("INCOMING REQUEST ERROR [%d-%s on '%s']: '%s' (%s)'", status.value(), type, reqUrl, msg, reqSource));

        // Prepare response
        if (headerAccept!=null && headerAccept.contains("text/html"))
            return generateHtmlResponse(request.getRequestURI(), status, type, msg, details);
//2021-10-16T11:59:31.022+02:00	com.robypomper.comm.exception.PeerConnectionException: Error on get JCP GWs's access info from JCP APIs
        return generateDefaultResponse(request.getRequestURI(), status, type, msg, details);
    }

    private static String extractOriginalClientAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    public String getErrorPath() {
        return PATH_ERROR;
    }

    // Exception handlers

    @ExceptionHandler(Throwable.class)
    public Object handleException(final HttpServletRequest request, final Throwable ex) {
        // Error info
        HttpStatus status = generateHttpStatus(ex);
        String type = ex != null ? ex.getClass().getSimpleName() : "N/A";
        String msg = ex != null ? ex.getMessage() : "N/A";
        String details = ex != null ? concatenateCauses(ex) : "N/A";

        // Request info
        String headerAccept = request.getHeader("accept");
        String reqUrl = request.getRequestURI();
        if (status == HttpStatus.NOT_FOUND) {
            Object notFound_Path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
            if (notFound_Path != null)
                reqUrl = notFound_Path.toString();
        }

        String reqSource = String.format("method: %s, src: %s:%d, remoteUser: %s", request.getMethod(), request.getRemoteAddr(), request.getRemotePort(), request.getRemoteUser());
        ControllerError.log.warn(String.format("REQUEST EXCEPTION [%d-%s on '%s']: '%s' (%s)'", status.value(), type, reqUrl, msg, reqSource));

        // Prepare response
        if (headerAccept!=null && headerAccept.contains("text/html"))
            return generateHtmlResponse(request.getRequestURI(), status, type, msg, details);

        return generateDefaultResponse(request.getRequestURI(), status, type, msg, details);
    }


    private static HttpStatus generateHttpStatus(Throwable ex) {
        if (ex == null)
            return HttpStatus.INTERNAL_SERVER_ERROR;

        switch (ex.getClass().getSimpleName()) {

            case "ResponseStatusException":
                return ((ResponseStatusException) ex).getStatus();

            case "RequestRejectedException":
            case "HttpRequestMethodNotSupportedException":
            case "IllegalArgumentException":
                return HttpStatus.BAD_REQUEST;

            case "AccessDeniedException":
                return HttpStatus.FORBIDDEN;

            case "GWNotAvailableException":
            case "Exception":
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private static HttpStatus generateHttpStatus(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode != null)
            return HttpStatus.resolve(Integer.parseInt(statusCode.toString()));

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private static String generateMessage(HttpServletRequest request, HttpStatus status) {
        if (status == HttpStatus.NOT_FOUND) {
            Object notFound_Path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
            if (notFound_Path != null)
                return String.format("Path '%s' not found.", notFound_Path);
        } else {
            Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
            if (errorMessage != null)
                return errorMessage.toString();
        }

        return "Unknown error";
    }

    private static String extractOriginalUri(HttpServletRequest request, HttpStatus status) {
        Object originalUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (originalUri != null)
            return originalUri.toString();

        return "Unknown original Uri";
    }

    private static String extractOriginalError(HttpServletRequest request, HttpStatus status) {
        Object originalError = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        if (originalError != null)
            return originalError.toString();

        return "Unknown original Error";
    }

    private static String concatenateCauses(Throwable ex) {
        if (ex == null)
            return "";

        String current = String.format("    -> (%s) %s", ex.getClass().getSimpleName(), ex.getMessage());
        String next = concatenateCauses(ex.getCause());
        if (!next.isEmpty())
            current += "\n" + next;
        return current;
    }

    private static ResponseEntity<String> generateHtmlResponse(String reqUrl, HttpStatus status, String type, String message, String details) {
        String error = String.format("<center><h1>Error %d</h1><p>Error <b>%s</b> on request '%s':<br>%s</p></center><!--\n%s\n-->", status.value(), type, reqUrl, message, details);
        //return new ResponseEntity<String>
        return ResponseEntity.status(status).body(error);
    }

    private static ResponseEntity<Params20.Error> generateDefaultResponse(String reqUrl, HttpStatus status, String type, String message, String details) {
        Params20.Error error = new Params20.Error(reqUrl, type, status.value(), message, details, new HashMap<>());
        return ResponseEntity.status(status).body(error);
    }

}