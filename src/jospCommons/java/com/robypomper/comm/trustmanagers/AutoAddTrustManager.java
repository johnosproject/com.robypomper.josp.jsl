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

package com.robypomper.comm.trustmanagers;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * TrustManager that add and accept any certificate to check.
 */
public class AutoAddTrustManager extends AbsCustomTrustManager {

    // X509TrustManager impl

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            checkTrusted(chain, authType);
        } catch (UpdateException ignore) {
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            checkTrusted(chain, authType);
        } catch (UpdateException ignore) {
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return getTrustManager().getAcceptedIssuers();
    }


    // Utils

    /**
     * Check if given certificate chain is trusted by current TrustManager, if
     * not then add the certificate to the TrustManager and re-check the
     * certificate.
     *
     * @param chain    the certificate chain to check.
     * @param authType authentication type based on certificate type.
     */
    public void checkTrusted(X509Certificate[] chain, String authType) throws CertificateException, UpdateException {
        if (getTrustManager().getAcceptedIssuers().length == 0)
            addCertificate("Client#" + chain[0].getSubjectDN(), chain[0]);

        try {
            getTrustManager().checkClientTrusted(chain, authType);

        } catch (CertificateException cx) {
            addCertificate("Client#" + chain[0].getSubjectDN(), chain[0]);
            getTrustManager().checkClientTrusted(chain, authType);
        }
    }

}
