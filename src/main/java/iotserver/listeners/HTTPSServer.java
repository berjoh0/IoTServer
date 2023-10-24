/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.listeners;

import com.google.gson.JsonObject;
import iotserver.context.IotContext;
import iotserver.runner.RequestRunner;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

/**
 *
 * @author johanbergman
 */
public class HTTPSServer extends Thread {

    private JsonObject httpsServerProperties;
    private IotContext iotContext;

    public HTTPSServer(IotContext iotContext, JsonObject httpsServerProperties) {
        this.httpsServerProperties = httpsServerProperties;
        this.iotContext = iotContext;
    }

    @Override
    public void run() {

        try {
            int port = httpsServerProperties.get("port").getAsInt();
            String keyStorePath = httpsServerProperties.get("keyStorePath").getAsString();
            String keyStorePassword = httpsServerProperties.get("keyStorePassword").getAsString();
            String keyStoreAlgorithm = httpsServerProperties.get("keyStoreAlgorithm").getAsString();
            String sslAlgorithm = httpsServerProperties.get("sslAlgorithm").getAsString();

            KeyStore jks = getKeystore(keyStorePath, keyStoreAlgorithm, keyStorePassword);

            SSLServerSocketFactory sslssf = getSSLServerSocketFactory(jks, keyStorePassword, sslAlgorithm);
            if (sslssf != null) {
                // var suits = sslssf.getDefaultCipherSuites();
                SSLServerSocket sslServerSocket = (SSLServerSocket) sslssf.createServerSocket(port);

                System.out.println("Https server Listening for connections on port : " + port + "\n");
                while (true) {
                    Socket inSock = sslServerSocket.accept();

                    RequestRunner newRunner = new RequestRunner(inSock, iotContext, httpsServerProperties);
                    Thread newThread = new Thread(this.getThreadGroup(), newRunner,
                            "HTTPS_" + (int) (Math.random() * 10000));
                    newThread.start();
                }
            }

        } catch (IOException iOException) {
            System.out.println("HTTP Server error");
            iOException.printStackTrace();
        }
    }

    private KeyStore getKeystore(String path, String algorithm, String filePassword) {
        try {
            File f = new File(path);

            if (!f.exists()) {
                throw new RuntimeException("Cert file not found.");
            }

            FileInputStream keyFile = new FileInputStream(f);
            KeyStore keystore = KeyStore.getInstance(algorithm);
            keystore.load(keyFile, filePassword.toCharArray());
            keyFile.close();

            return keystore;
        } catch (Exception e) {
            System.out.println("Error: getKeystore");
            e.printStackTrace();
        }

        return null;
    }

    private SSLServerSocketFactory getSSLServerSocketFactory(KeyStore trustKey, String keyStorePassword,
            String sslAlgorithm) {
        try {
            /*
             * TrustManagerFactory tmf =
             * TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
             * tmf.init(trustKey);
             */

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(trustKey, keyStorePassword.toCharArray());

            SSLContext context = SSLContext.getInstance(sslAlgorithm);// "SSL" "TLS"
            context.init(kmf.getKeyManagers(), null, null);

            return context.getServerSocketFactory();
        } catch (Exception e) {
            System.out.println("getSSLServerSocketFactory Error");
            e.printStackTrace();
        }

        return null;
    }

}
