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

import com.robypomper.java.JavaJKS;
import com.robypomper.java.JavaSSL;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Abstract Trust Manager customization.
 * <p>
 * This customization allow to add new certificates at runtime via the
 * {@link #addCertificate} methods.
 * <p>
 * This class wrap a {@link X509TrustManager} and use it to manage trusted
 * certificates.
 */
public abstract class AbsCustomTrustManager implements X509TrustManager {

    // Internal vars

    private X509TrustManager trustManager;
    private final Map<String, Certificate> certsMap = new HashMap<>();


    // Constructor

    /**
     * Default constructor that initialize a new empty TrustManager.
     */
    public AbsCustomTrustManager() {
        try {
            reloadTrustManager();
        } catch (UpdateException ignore) {
        }
    }


    // Certificates methods

    /**
     * Add given certificate to the current TrustManager.
     *
     * @param certFile the certificate to load in to the TrustManager.
     */
    public void addCertificate(String alias, File certFile) throws UpdateException {
        CertificateFactory certFactory;
        try {
            certFactory = CertificateFactory.getInstance(JavaJKS.CERT_TYPE);
            Collection<? extends Certificate> certs = certFactory.generateCertificates(new FileInputStream(certFile));
            addCertificate(alias, certs);
        } catch (CertificateException | FileNotFoundException e) {
            throw new UpdateException(String.format("Certificate '%s' can't be loaded because %s.", certFile.getPath(), e.getMessage()), e);
        }
    }

    /**
     * Add given string as certificate to the current TrustManager.
     *
     * @param byteCert the byte array containing the certificate to add in to the
     *                 TrustManager.
     */
    public void addCertificateByte(String alias, byte[] byteCert) throws UpdateException, JavaJKS.LoadingException {
        addCertificate(alias, JavaJKS.loadCertificateFromBytes(byteCert));
    }

    /**
     * Add given certificate to the current TrustManager.
     *
     * @param cert the certificate to load in to the TrustManager.
     */
    public void addCertificate(String alias, Certificate cert) throws UpdateException {
        synchronized (certsMap) {
            certsMap.put(alias, cert);
            reloadTrustManager();
        }
    }

    /**
     * Add given certificate to the current TrustManager.
     *
     * @param certs the certificates list to load in to the TrustManager
     */
    public void addCertificate(String alias, Collection<? extends Certificate> certs) throws UpdateException {
        synchronized (certsMap) {
            int count = 0;
            for (Certificate cert : certs)
                certsMap.put(String.format("%s#%d", alias, ++count), cert);
            reloadTrustManager();
        }
    }

    /**
     * @return a map containing all current TrustManager's alias-cert pairs.
     */
    public Map<String, Certificate> getCertificates() {
        return certsMap;
    }

    // Subclasses utils

    /**
     * @return the internal TrustManager.
     */
    protected X509TrustManager getTrustManager() {
        return trustManager;
    }

    /**
     * Create a trust manager with added certificates and substitute the old one.
     */
    private void reloadTrustManager() throws UpdateException {
        // load keystore from specified cert store (or default)
        KeyStore ks;
        try {
            ks = KeyStore.getInstance(JavaJKS.KEYSTORE_TYPE);
            ks.load(null);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new UpdateException("keystore load", e);
        }

        // add all temporary certs to KeyStore (ts)
        synchronized (certsMap) {
            for (Map.Entry<String, Certificate> cert : certsMap.entrySet())
                try {
                    ks.setCertificateEntry(String.valueOf(UUID.randomUUID()), cert.getValue());
                } catch (KeyStoreException e) {
                    throw new UpdateException("add certificate", e);
                }
        }

        // initialize a new TMF with the ks we just loaded
        TrustManagerFactory tmf;      //TrustManagerFactory.getDefaultAlgorithm()
        try {
            tmf = TrustManagerFactory.getInstance(JavaSSL.TRUSTMNGR_ALG);
            tmf.init(ks);
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new UpdateException("TrustManagerFactory creation", e);
        }

        // acquire X509 trust manager from factory
        TrustManager[] tms = tmf.getTrustManagers();
        for (TrustManager tm : tms)
            if (tm instanceof X509TrustManager) {
                trustManager = (X509TrustManager) tm;
                return;
            }

        throw new UpdateException("no X509TrustManager in TrustManagerFactory");
    }


    // Exception

    /**
     * Exceptions thrown on errors on TrustStore update (reload).
     */
    public static class UpdateException extends Throwable {
        private static final String MSG = "Can't reload TrustStore because %s failed.";

        public UpdateException(String entity) {
            super(String.format(MSG, entity));
        }

        public UpdateException(String entity, Exception e) {
            super(String.format(MSG, entity), e);
        }
    }

}
