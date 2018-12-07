/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nix.jingxun.addp.rpc.remoting.netty;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.TLS_CLIENT_AUTHSERVER;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.TLS_CLIENT_CERTPATH;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.TLS_CLIENT_KEYPASSWORD;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.TLS_CLIENT_KEYPATH;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.TLS_CLIENT_TRUSTCERTPATH;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.TLS_SERVER_AUTHCLIENT;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.TLS_SERVER_CERTPATH;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.TLS_SERVER_KEYPASSWORD;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.TLS_SERVER_KEYPATH;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.TLS_SERVER_NEED_CLIENT_AUTH;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.TLS_SERVER_TRUSTCERTPATH;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.TLS_TEST_MODE_ENABLE;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.tlsClientAuthServer;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.tlsClientCertPath;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.tlsClientKeyPassword;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.tlsClientKeyPath;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.tlsClientTrustCertPath;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.tlsServerAuthClient;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.tlsServerCertPath;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.tlsServerKeyPassword;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.tlsServerKeyPath;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.tlsServerNeedClientAuth;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.tlsServerTrustCertPath;
import static com.nix.jingxun.addp.rpc.remoting.netty.TlsSystemConfig.tlsTestModeEnable;

@Slf4j
public class TlsHelper {

    public interface DecryptionStrategy {
        /**
         * Decrypt the target encrpted private key file.
         *
         * @param privateKeyEncryptPath A pathname string
         * @param forClient tells whether it's a client-side key file
         * @return An input stream for a decrypted key file
         * @throws IOException if an I/O error has occurred
         */
        InputStream decryptPrivateKey(String privateKeyEncryptPath, boolean forClient) throws IOException;
    }


    private static DecryptionStrategy decryptionStrategy = new DecryptionStrategy() {
        @Override
        public InputStream decryptPrivateKey(final String privateKeyEncryptPath,
            final boolean forClient) throws IOException {
            return new FileInputStream(privateKeyEncryptPath);
        }
    };


    public static void registerDecryptionStrategy(final DecryptionStrategy decryptionStrategy) {
        TlsHelper.decryptionStrategy = decryptionStrategy;
    }

    public static SslContext buildSslContext(boolean forClient) throws IOException, CertificateException {
        File configFile = new File(TlsSystemConfig.tlsConfigFile);
        extractTlsConfigFromFile(configFile);
        logTheFinalUsedTlsConfig();

        SslProvider provider;
        if (OpenSsl.isAvailable()) {
            provider = SslProvider.OPENSSL;
            log.info("Using OpenSSL provider");
        } else {
            provider = SslProvider.JDK;
            log.info("Using JDK SSL provider");
        }

        if (forClient) {
            if (tlsTestModeEnable) {
                return SslContextBuilder
                    .forClient()
                    .sslProvider(SslProvider.JDK)
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            } else {
                SslContextBuilder sslContextBuilder = SslContextBuilder.forClient().sslProvider(SslProvider.JDK);


                if (!tlsClientAuthServer) {
                    sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                } else {
                    if (!isNullOrEmpty(tlsClientTrustCertPath)) {
                        sslContextBuilder.trustManager(new File(tlsClientTrustCertPath));
                    }
                }

                return sslContextBuilder.keyManager(
                    !isNullOrEmpty(tlsClientCertPath) ? new FileInputStream(tlsClientCertPath) : null,
                    !isNullOrEmpty(tlsClientKeyPath) ? decryptionStrategy.decryptPrivateKey(tlsClientKeyPath, true) : null,
                    !isNullOrEmpty(tlsClientKeyPassword) ? tlsClientKeyPassword : null)
                    .build();
            }
        } else {

            if (tlsTestModeEnable) {
                SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
                return SslContextBuilder
                    .forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey())
                    .sslProvider(SslProvider.JDK)
                    .clientAuth(ClientAuth.OPTIONAL)
                    .build();
            } else {
                SslContextBuilder sslContextBuilder = SslContextBuilder.forServer(
                    !isNullOrEmpty(tlsServerCertPath) ? new FileInputStream(tlsServerCertPath) : null,
                    !isNullOrEmpty(tlsServerKeyPath) ? decryptionStrategy.decryptPrivateKey(tlsServerKeyPath, false) : null,
                    !isNullOrEmpty(tlsServerKeyPassword) ? tlsServerKeyPassword : null)
                    .sslProvider(provider);

                if (!tlsServerAuthClient) {
                    sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                } else {
                    if (!isNullOrEmpty(tlsServerTrustCertPath)) {
                        sslContextBuilder.trustManager(new File(tlsServerTrustCertPath));
                    }
                }

                sslContextBuilder.clientAuth(parseClientAuthMode(tlsServerNeedClientAuth));
                return sslContextBuilder.build();
            }
        }
    }

    private static void extractTlsConfigFromFile(final File configFile) {
        if (!(configFile.exists() && configFile.isFile() && configFile.canRead())) {
            log.info("Tls config file doesn't exist, skip it");
            return;
        }

        Properties properties;
        properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configFile);
            properties.load(inputStream);
        } catch (IOException ignore) {
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                }
            }
        }

        tlsTestModeEnable = Boolean.parseBoolean(properties.getProperty(TLS_TEST_MODE_ENABLE, String.valueOf(tlsTestModeEnable)));
        tlsServerNeedClientAuth = properties.getProperty(TLS_SERVER_NEED_CLIENT_AUTH, tlsServerNeedClientAuth);
        tlsServerKeyPath = properties.getProperty(TLS_SERVER_KEYPATH, tlsServerKeyPath);
        tlsServerKeyPassword = properties.getProperty(TLS_SERVER_KEYPASSWORD, tlsServerKeyPassword);
        tlsServerCertPath = properties.getProperty(TLS_SERVER_CERTPATH, tlsServerCertPath);
        tlsServerAuthClient = Boolean.parseBoolean(properties.getProperty(TLS_SERVER_AUTHCLIENT, String.valueOf(tlsServerAuthClient)));
        tlsServerTrustCertPath = properties.getProperty(TLS_SERVER_TRUSTCERTPATH, tlsServerTrustCertPath);

        tlsClientKeyPath = properties.getProperty(TLS_CLIENT_KEYPATH, tlsClientKeyPath);
        tlsClientKeyPassword = properties.getProperty(TLS_CLIENT_KEYPASSWORD, tlsClientKeyPassword);
        tlsClientCertPath = properties.getProperty(TLS_CLIENT_CERTPATH, tlsClientCertPath);
        tlsClientAuthServer = Boolean.parseBoolean(properties.getProperty(TLS_CLIENT_AUTHSERVER, String.valueOf(tlsClientAuthServer)));
        tlsClientTrustCertPath = properties.getProperty(TLS_CLIENT_TRUSTCERTPATH, tlsClientTrustCertPath);
    }

    private static void logTheFinalUsedTlsConfig() {
        log.info("Log the final used tls related configuration");
        log.info("{} = {}", TLS_TEST_MODE_ENABLE, tlsTestModeEnable);
        log.info("{} = {}", TLS_SERVER_NEED_CLIENT_AUTH, tlsServerNeedClientAuth);
        log.info("{} = {}", TLS_SERVER_KEYPATH, tlsServerKeyPath);
        log.info("{} = {}", TLS_SERVER_KEYPASSWORD, tlsServerKeyPassword);
        log.info("{} = {}", TLS_SERVER_CERTPATH, tlsServerCertPath);
        log.info("{} = {}", TLS_SERVER_AUTHCLIENT, tlsServerAuthClient);
        log.info("{} = {}", TLS_SERVER_TRUSTCERTPATH, tlsServerTrustCertPath);

        log.info("{} = {}", TLS_CLIENT_KEYPATH, tlsClientKeyPath);
        log.info("{} = {}", TLS_CLIENT_KEYPASSWORD, tlsClientKeyPassword);
        log.info("{} = {}", TLS_CLIENT_CERTPATH, tlsClientCertPath);
        log.info("{} = {}", TLS_CLIENT_AUTHSERVER, tlsClientAuthServer);
        log.info("{} = {}", TLS_CLIENT_TRUSTCERTPATH, tlsClientTrustCertPath);
    }

    private static ClientAuth parseClientAuthMode(String authMode) {
        if (null == authMode || authMode.trim().isEmpty()) {
            return ClientAuth.NONE;
        }

        for (ClientAuth clientAuth : ClientAuth.values()) {
            if (clientAuth.name().equals(authMode.toUpperCase())) {
                return clientAuth;
            }
        }

        return ClientAuth.NONE;
    }

    /**
     * Determine if a string is {@code null} or {@link String#isEmpty()} returns {@code true}.
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
