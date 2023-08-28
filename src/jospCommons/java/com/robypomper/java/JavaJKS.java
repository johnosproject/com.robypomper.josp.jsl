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

import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;
import java.util.Map;


/**
 * Utils class to manage Java Key Stores (JKS).
 */
public class JavaJKS {

    // Class constants

    public static final String KEYSTORE_TYPE = "PKCS12";        // KeyStore.getDefaultType() => jks
    public static final String CERT_TYPE = "X.509";
    public static final String SIGING_ALG = "RSA";
    public static final int KEY_SIZE = 2048;
    public static final int CERT_VALIDITY_DAYS = 3650;


    // JKS, Keys, cert generators

    /**
     * Generate new Java Key Store containing the Key Pair and the corresponding
     * Certificate Chain.
     * <p>
     * The certificate chain contain only the Key Pair certificate with given id.
     *
     * @param certificateID the string used as certificate commonName (this will
     *                      be visible also to other peer side).
     * @param ksPass        the JKS password.
     * @param certAlias     the alias associated to the generated certificate.
     * @return a valid Key Store with Key Pair and Certificate Chain.
     */
    public static KeyStore generateKeyStore(String certificateID, String ksPass, String certAlias) throws GenerationException {
        if (ksPass.length() < 6)
            throw new GenerationException(String.format("Error on generating keystore for '%s', password must be longer than 6 char", certificateID));

        File tmpKeyStoreFile;
        try {
            tmpKeyStoreFile = File.createTempFile("tmpks", "");
            if (tmpKeyStoreFile.exists()) //noinspection ResultOfMethodCallIgnored
                tmpKeyStoreFile.delete();
        } catch (IOException e) {
            throw new GenerationException(String.format("Error on generating keystore for '%s' commonName because [%s] %s", certificateID, e.getClass().getSimpleName(), e.getMessage()), null);
        }

        KeyStore ks;
        try {
            String genKeyStoreDir = Paths.get(System.getProperty("java.home"), "bin/keytool").toString();
            if (System.getProperty("os.name").startsWith("Windows"))
                genKeyStoreDir = JavaFiles.toWindowsPath(genKeyStoreDir);
            String genKeyStore = genKeyStoreDir + String.format(" -genkey -noprompt -keyalg %s -keysize %d -validity %d -alias %s -dname 'CN=%s,OU=com.robypomper.comm,O=John,L=Trento,S=TN,C=IT' -keystore %s -deststoretype pkcs12 -storepass '%s' -keypass '%s'", SIGING_ALG, KEY_SIZE, CERT_VALIDITY_DAYS, certAlias, certificateID, tmpKeyStoreFile.getAbsolutePath(), ksPass, ksPass);
            String output = JavaExecProcess.execCmd(genKeyStore, JavaExecProcess.DEF_TIMEOUT * 5).trim();
            if (!tmpKeyStoreFile.exists()) {
                throw new GenerationException(String.format("Error on generating keystore for '%s' commonName, temporary keystore not create because '%s'", certificateID, output));
            }

            ks = loadKeyStore(tmpKeyStoreFile.getAbsolutePath(), ksPass);

        } catch (IOException | LoadingException | JavaExecProcess.ExecStillAliveException e) {
            if (tmpKeyStoreFile.exists()) //noinspection ResultOfMethodCallIgnored
                tmpKeyStoreFile.delete();
            throw new GenerationException(String.format("Error on generating keystore for '%s' commonName because [%s] %s", certificateID, e.getClass().getSimpleName(), e.getMessage()), e);
        }

        if (tmpKeyStoreFile.exists()) //noinspection ResultOfMethodCallIgnored
            tmpKeyStoreFile.delete();
        return ks;
    }


    // JKS import and exports

    /**
     * Load {@link KeyStore} from given path and use given password.
     *
     * @param ksPath the file path containing the KeyStore to load.
     * @param ksPass the string containing the KeyStore password.
     * @return the loaded KeyStore.
     */
    public static KeyStore loadKeyStore(String ksPath, String ksPass) throws LoadingException {
        //log.trace(Mrk_Commons.COMM_SSL_UTILS, String.format("Loading KeyStore from '%s' file", ksPath));

        try {
            KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
            InputStream keyStoreInputStream = new FileInputStream(ksPath);
            ks.load(keyStoreInputStream, ksPass.toCharArray());
            return ks;
        } catch (Exception e) {
            throw new LoadingException(String.format("Error loading key store from '%s' file because %s", ksPath, e.getMessage()), e);
        }
    }

    /**
     * Save given KeyStore to given file path and use given password.
     *
     * @param ks     the KeyStore to store.
     * @param ksPath the file path where to store the KeyStore.
     * @param ksPass the string containing the KeyStore password.
     */
    public static void storeKeyStore(KeyStore ks, String ksPath, String ksPass) throws LoadingException {
        //log.trace(Mrk_Commons.COMM_SSL_UTILS, String.format("Storing KeyStore on '%s' file", ksPath));

        try {
            dirExistOrCreate(ksPath);
            FileOutputStream keyStoreOutputStream = new FileOutputStream(ksPath);
            ks.store(keyStoreOutputStream, ksPass.toCharArray());
        } catch (Exception e) {
            throw new LoadingException(String.format("Error storing key store from '%s' file because %s", ksPath, e.getMessage()), e);
        }
    }

    /**
     * Save <code>ksAlias</code> certificate contained in <code>ksFile</code>
     * to <code>exportCertFile</code>given certificate chain as public certificate to the KeyStore.
     */
    public static void exportCertificate(KeyStore keyStore, String exportCertFile, String ksAlias) throws GenerationException {
        //log.trace(Mrk_Commons.COMM_SSL_UTILS, String.format("Exporting certificate '%s' on '%s' file", ksAlias, exportCertFile));

        try {
            Certificate cert = keyStore.getCertificate(ksAlias);
            byte[] buf = cert.getEncoded();

            dirExistOrCreate(exportCertFile);
            FileOutputStream os = new FileOutputStream(new File(exportCertFile));
            os.write(buf);
            os.close();

        } catch (IOException | KeyStoreException | CertificateEncodingException e) {
            throw new GenerationException(String.format("Error exporting certificate to file %s because %s", exportCertFile, e.getMessage()), e);
        }
    }

    /**
     * Save <code>ksAlias</code> certificate contained in <code>ksFile</code>
     * to <code>exportCertFile</code>given certificate chain as public certificate to the KeyStore.
     */
    public static Certificate extractCertificate(KeyStore keyStore, String ksAlias) throws GenerationException {
        //log.trace(Mrk_Commons.COMM_SSL_UTILS, String.format("Extracting certificate '%s'", ksAlias));

        try {
            return keyStore.getCertificate(ksAlias);

        } catch (KeyStoreException e) {
            throw new GenerationException(String.format("Error extracting certificate because %s", e.getMessage()), e);
        }
    }

    /**
     * Load the Certificate from given string.
     *
     * @param file the file containing the certificate.
     * @return the loaded certificate.
     */
    public static Certificate loadCertificateFromFile(File file) throws LoadingException {
        //log.trace(Mrk_Commons.COMM_SSL_UTILS, String.format("Loading certificate from '%s' file", file.getPath()));

        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            return loadCertificateFromBytes(fileBytes);

        } catch (Exception e) {
            throw new LoadingException(String.format("Error loading certificate from bytes because %s", e.getMessage()), e);
        }
    }

    /**
     * Load the Certificate from given string.
     *
     * @param bytesCert the byte array containing the certificate.
     * @return the loaded certificate.
     */
    public static Certificate loadCertificateFromBytes(byte[] bytesCert) throws LoadingException {
        //log.trace(Mrk_Commons.COMM_SSL_UTILS, "Loading certificate from bytes");

        try {
            CertificateFactory certFactory = CertificateFactory.getInstance(CERT_TYPE);
            InputStream inputStream = new ByteArrayInputStream(bytesCert);
            return certFactory.generateCertificate(inputStream);

        } catch (Exception e) {
            throw new LoadingException(String.format("Error loading certificate from bytes because %s", e.getMessage()), e);
        }
    }

    /**
     * Copy all certificates from given {@link javax.net.ssl.TrustManager} to given
     * {@link KeyStore}.
     * <p>
     * It copies all certificates without duplicates. Certificates are duplicated
     * if their alias is already present in the destination.
     *
     * @param keyStore     the certificates source.
     * @param trustManager the certificates destination.
     */
    public static void copyCertsFromTrustManagerToKeyStore(KeyStore keyStore, AbsCustomTrustManager trustManager) throws StoreException {
        //log.trace(Mrk_Commons.COMM_SSL_UTILS, "Synchronizing certificate from trust manager to key store");

        for (Map.Entry<String, Certificate> aliasAndCert : trustManager.getCertificates().entrySet())
            try {
                keyStore.setCertificateEntry(aliasAndCert.getKey(), aliasAndCert.getValue());
            } catch (KeyStoreException e) {
                if (!e.getMessage().startsWith("Cannot overwrite own certificate"))
                    throw new StoreException(String.format("Error coping certificate from trust manager to key store because %s", e.getMessage()), e);
            }
    }

    /**
     * Copy all certificates from given {@link KeyStore} to given
     * {@link javax.net.ssl.TrustManager}.
     * <p>
     * It copies all certificates without duplicates. Certificates are duplicated
     * if their alias is already present in the destination.
     *
     * @param keyStore     the certificates destination.
     * @param trustManager the certificates source.
     */
    public static void copyCertsFromKeyStoreToTrustManager(KeyStore keyStore, AbsCustomTrustManager trustManager) {
        //log.trace(Mrk_Commons.COMM_SSL_UTILS, "Synchronizing certificate from key store to trust manager");

        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = keyStore.getCertificate(alias);
                if (cert == null) {
                    //log.error(Mrk_Commons.COMM_SSL_UTILS, String.format("Can't find certificate for alias '%s'", alias));
                    continue;
                }
                trustManager.addCertificate(alias, cert);
            }

        } catch (KeyStoreException | AbsCustomTrustManager.UpdateException e) {
            e.printStackTrace();
        }
    }


    // Utils methods

    /**
     * Create directory for given path.
     * <p>
     * The path can define a directory or a file. If it represent a file, then
     * this method will create the parent directory.
     *
     * @param path string containing the path to create.
     */
    private static void dirExistOrCreate(String path) throws IOException {
        File f = new File(path).getAbsoluteFile();
        File dir = f.isDirectory() ? f : f.getParentFile();
        if (!dir.exists())
            if (!dir.mkdirs())
                throw new IOException(String.format("Can't create directory for path %s", path));

    }


    // Exception

    /**
     * Exceptions thrown on errors during JKS generation processes.
     */
    public static class GenerationException extends Throwable {
        public GenerationException(String msg) {
            super(msg);
        }

        public GenerationException(String msg, Throwable e) {
            super(msg, e);
        }
    }

    /**
     * Exceptions thrown on errors during JKS loading processes.
     */
    public static class LoadingException extends Throwable {
        public LoadingException(String msg, Throwable e) {
            super(msg, e);
        }
    }

    /**
     * Exceptions thrown on errors during JKS storing processes.
     */
    public static class StoreException extends Throwable {
        public StoreException(String msg, Throwable e) {
            super(msg, e);
        }
    }

}
