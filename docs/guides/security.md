# JOSP Service Library -Guides: Security

...



### Create a Java KeyStore

The JSL instance, by default, looks for the `local_ks.jks` file (into the execution
dir) in order to load his SSL certificate for a secure communication. If no
certificate is found, then the JSL instance generates a new one and saves it.

The generated certificate (a self-signed certificate) contains the service's
full id (`{srv-id}/{user_id}/{instance_id}`) as the Common Name (CN) and the
default password `123456` (defined by `JSLLocalClientSSLShare::KS_PASS`).
The default behavior correspond to the 2nd level of JOSP security: "SSLShare Instance",
a private SSL communication with certificate auto-sharing.

In order to configure other JOSP Security levels, you must provide your own
certificate. With the full or partial service id as the CN, you can generate
a new certificate into `new_ks.jks` file, using the `keytool` command (part of
the Java Development Kit):

```shell
file		./new_ks.jks
password	johnny123
cert. id	test-client-srv/00000-00000-00000/9999             <-- it must be always on {srv-id}/{user_id}/{instance_id}. instance_id can be a random number
alias		test-android-srv-LocalCert


keytool -genkey -noprompt -keyalg RSA -keysize 2048 -validity 3650 \
    -alias test-android-srv-LocalCert \
    -dname 'CN=test-client-srv/00000-00000-00000/9999,OU=com.robypomper.comm,O=John,L=Trento,S=TN,C=IT' \
    -keystore ./new_ks.jks \
    -deststoretype pkcs12 \
    -storepass 'johnny123' -keypass 'johnny123'
```

Note that the generated Java KeyStore must follow the following rules:
- alias:
  a string composed by the service id (`jsl.srv.id` property into `jsl.yml`
  configs file) and the word `-LocalCert` (e.g., `test-android-srv-LocalCert`)
- dname:
  it must contain a CN with the full or partial service id, other fields
  can be set as you want (e.g., `CN=test-android-srv/00000-00000-00000/9999,...`)
- storepass and keypass:
  use the same password for both and then set it to the
  `jsl.comm.local.ks.pass` property into `jsl.yml` configs file.

If you set a partial service id as the CN, then the JSL instance can connect to
the JOD daemon using the 3tr or 4th JOSP Security levels (SSL Comp or SSL Share Comp).
Otherwise, if you set the full service id, then the JSL service can use the 1st or 2nd
JOSP Security levels (SSL Instance or SSL Instance Share).

The difference between the classic SSL and the SSL Share security levels is that
the first must pre-share the certificates, while the second one uses an internal
mechanism to auto-share the certificates.




keytool -genkey -noprompt -keyalg RSA -keysize 2048 -validity 3650 \
-alias test-client-srv-LocalCert \
-dname 'CN=test-client-srv,OU=com.robypomper.comm,O=John,L=Trento,S=TN,C=IT' \
-keystore ./local_partial_ks.jks \
-deststoretype pkcs12 \
-storepass '123456' -keypass '123456'