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

package com.robypomper.java;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;


/**
 * Utils class to avoid SSL host verifier checks.
 */
public class JavaSSLIgnoreChecks {

    // Class constants

    private static final HostnameVerifier LOCALHOST = (hostname, sslSession) -> hostname.equals("localhost");
    private static final HostnameVerifier ALLHOSTS = (hostname, session) -> true;


    // Disable SSL Checks

    /**
     * Disable SSL checks for given connection.
     *
     * @param conn connection to disable the hostname verifier.
     */
    public static void disableSSLChecks(HttpsURLConnection conn) {
        conn.setSSLSocketFactory(JavaSSLIgnoreChecks.newSSLContextInstanceWithDisableSSLChecks().getSocketFactory());
    }


    // Disable SSL Hostname verifier

    /**
     * Disable hostname verifier on localhost for given connection.
     *
     * @param conn connection to disable the hostname verifier.
     */
    public static void disableSSLHostVerifierOnLocalHost(HttpsURLConnection conn) {
        conn.setHostnameVerifier(JavaSSLIgnoreChecks.LOCALHOST);
    }

    /**
     * Disable hostname verifier on all hostnames for given connection.
     *
     * @param conn connection to disable the hostname verifier.
     */
    public static void disableSSLHostVerifierOnAllHost(HttpsURLConnection conn) {
        conn.setHostnameVerifier(JavaSSLIgnoreChecks.ALLHOSTS);
    }


    // Disable SSL Checks and Hostname verifier

    /**
     * Disable SSL checks and hostname verifier on localhost for all
     * {@link HttpsURLConnection} instances.
     */
    public static void disableSSLChecksAndHostVerifierOnLocalHost() {
        disableSSLChecksAndHostVerifier(LOCALHOST);
    }

    /**
     * Disable SSL checks and hostname verifier on localhost for given
     * connection.
     */
    public static void disableSSLChecksAndHostVerifierOnLocalHost(HttpsURLConnection conn) {
        disableSSLChecks(conn);
        disableSSLHostVerifierOnLocalHost(conn);
    }

    /**
     * Disable SSL checks and hostname verifier on all hostnames for all
     * {@link HttpsURLConnection} instances.
     */
    public static void disableSSLChecksAndHostVerifierOnAllHost() {
        disableSSLChecksAndHostVerifier(ALLHOSTS);
    }

    /**
     * Disable SSL checks and hostname verifier on all hostnames for given
     * connection.
     */
    public static void disableSSLChecksAndHostVerifierOnAllHost(HttpsURLConnection conn) {
        disableSSLChecks(conn);
        disableSSLHostVerifierOnAllHost(conn);
    }


    // SSL utils

    private static void disableSSLChecksAndHostVerifier(HostnameVerifier hostVerifier) {
        SSLContext sc = newSSLContextInstanceWithDisableSSLChecks();

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(hostVerifier);
    }

    private static SSLContext newSSLContextInstanceWithDisableSSLChecks() {
        SSLContext sc;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }

                    }
            }, new java.security.SecureRandom());

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            return JavaAssertions.makeAssertion_Failed(e, "SSLContext initialization with standard params should not throw exceptions", null);
        }

        return sc;
    }

}
