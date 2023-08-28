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
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;


/**
 * Utils class to manage {@link SSLContext} and his {@link TrustManager} and
 * {@link KeyManager}.
 */
public class JavaSSL {

    // Class constants

    public static final String TRUSTMNGR_ALG = "PKIX";          // TrustManagerFactory.getDefaultAlgorithm() => PKIX
    public static final String KEYMNGR_ALG = "SunX509";         // KeyManagerFactory.getDefaultAlgorithm() = > SunX509
    public static final String SSL_PROTOCOL = "TLS";


    // Generators

    /**
     * Generate a {@link SSLContext} starting from given Java Key Store and (if
     * provided) {@link TrustManager}.
     * <p>
     * If the <code>trustManager</code> param is null, then a TrustManager
     * will created based on given KeyStore.
     *
     * @param keyStore     the KeyStore containing the certificates (local and peer)
     *                     and local key pairs.
     * @param ksPass       the string containing the key store password.
     * @param trustManager the TrustManager to use with in the generated SSLContext.
     *                     if it's null, then a default TrustManager is created.
     * @return the generated {@link SSLContext}.
     */
    public static SSLContext generateSSLContext(KeyStore keyStore, String ksPass, TrustManager trustManager) throws GenerationException {
        //log.trace(Mrk_Commons.COMM_SSL_UTILS, String.format("Generating SSL context from key store and%strust store", trustManager != null ? "" : " empty "));

        if (ksPass == null) ksPass = "";
        try {
            TrustManager[] tms = trustManager != null ? new TrustManager[]{trustManager} : generateTrustManagers(keyStore);
            KeyManager[] kms = generateKeyManager(keyStore, ksPass);

            SSLContext ctx = SSLContext.getInstance(SSL_PROTOCOL);
            ctx.init(kms, tms, null);
            return ctx;

        } catch (NoSuchAlgorithmException e) {
            throw new GenerationException(String.format("Can't generate SSL context with algorithm %s", SSL_PROTOCOL), e);
        } catch (GenerationException | KeyManagementException e) {
            throw new GenerationException(String.format("Can't generate SSL context for server because %s", e.getMessage()), e);
        }
    }

    /**
     * Generate a default {@link TrustManager} based on given key store.
     * <p>
     * The default trust manager is generated with {@value TRUSTMNGR_ALG} algorithm.
     *
     * @param keyStore the key store to use for generated TrustManager.
     * @return the array containing the default {@link TrustManager}.
     */
    private static TrustManager[] generateTrustManagers(KeyStore keyStore) throws GenerationException {
        //log.trace(Mrk_Commons.COMM_SSL_UTILS, "Generating trust manager from key store");

        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TRUSTMNGR_ALG);
            tmf.init(keyStore);
            return tmf.getTrustManagers();

        } catch (NoSuchAlgorithmException e) {
            throw new GenerationException(String.format("Can't create new TrustManagerFactory with algorithm '%s'", TRUSTMNGR_ALG), e);
        } catch (KeyStoreException e) {
            throw new GenerationException(String.format("Can't initialize TrustManagerFactory with given key store because %s", e.getMessage()), e);
        }
    }

    /**
     * Generate a default {@link KeyManager} based on given key store.
     * <p>
     * The KeyManager is generated with {@value KEYMNGR_ALG} algorithm and contains
     * keys from given Key Store.
     *
     * @param keyStore the key store to use for populating the Key Manager.
     * @return the array containing the default {@link KeyManager}.
     */
    private static KeyManager[] generateKeyManager(KeyStore keyStore, String ksPass) throws GenerationException {
        //log.trace(Mrk_Commons.COMM_SSL_UTILS, "Generating key manager from key store");

        KeyManagerFactory kmf;
        try {
            kmf = KeyManagerFactory.getInstance(KEYMNGR_ALG);
            kmf.init(keyStore, ksPass.toCharArray());
            return kmf.getKeyManagers();

        } catch (NoSuchAlgorithmException e) {
            throw new GenerationException(String.format("Can't create new KeyManagerFactory with algorithm '%s'", KEYMNGR_ALG), e);
        } catch (UnrecoverableKeyException | KeyStoreException e) {
            throw new GenerationException(String.format("Can't initialize KeyManagerFactory with given key store because %s", e.getMessage()), e);
        }
    }


    // SSL Sessions

    /**
     * Return the field value used as peer id.
     * <p>
     * This method return the CommonName field from the peer certificate.
     *
     * @param peerSocket connection with the peer.
     * @return the peer id.
     */
    public static String getPeerId(SSLSocket peerSocket) throws PeerException {
        //log.trace(Mrk_Commons.COMM_SSL_UTILS, "Getting SSL session from peer socket");

        SSLSession session = peerSocket.getSession();
        try {
            return extractCN(session.getPeerCertificates());

        } catch (SSLPeerUnverifiedException e) {
            throw new PeerException("Can't read peer's id because not received peer certificate chain", e);
        }
    }

    private static String extractCN(Certificate[] certsChain) throws PeerException {
        if (certsChain.length < 1)
            throw new PeerException("Can't read peer's id because peer certificate chain is empty");

        String principal = ((X509Certificate) certsChain[certsChain.length - 1]).getSubjectX500Principal().getName();
        if (principal.isEmpty())
            throw new PeerException("Can't read peer's id because peer certificate's subject is empty");

        return principal.substring(principal.indexOf("CN=") + 3, principal.indexOf(",", principal.indexOf("CN=") + 3));
    }


    // Exceptions

    /**
     * Exceptions thrown on errors during JKS generation processes.
     */
    public static class GenerationException extends Throwable {
        public GenerationException(String msg, Throwable e) {
            super(msg, e);
        }
    }

    /**
     * Exceptions thrown on errors during JKS generation processes.
     */
    public static class PeerException extends Throwable {
        public PeerException(String msg) {
            super(msg);
        }

        public PeerException(String msg, Throwable e) {
            super(msg, e);
        }
    }

}
