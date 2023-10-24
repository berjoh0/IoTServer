
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.listeners;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iotserver.context.IotContext;
import iotserver.runner.RequestRunner;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author johanbergman
 */
public class ProxyClientServer extends Thread {

    private JsonObject proxyClientProperties;
    private IotContext iotContext;

    public ProxyClientServer(IotContext iotContext, JsonObject proxyClientProperties) {
        this.proxyClientProperties = proxyClientProperties;
        this.iotContext = iotContext;
    }

    @Override
    public void run() {
        try {

            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    } };

            // Ignore differences between given hostname and certificate hostname
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting trust manager
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(hv);
            } catch (Exception e) {
            }
            String token = proxyClientProperties.get("token").getAsString();
            String uri = proxyClientProperties.get("remoteURL").getAsString();
            if (token.isEmpty() || uri.isEmpty()) {
                return;
            }
            uri += uri.endsWith("/") ? "" : "/" + "proxy?token=" + token;
            URL url = new URL(uri);

            // URL url = new URL(proxyClientProperties.get("remoteURL").getAsString());
            // HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
            while (true) {
                HttpsURLConnection httpscon = (HttpsURLConnection) url.openConnection();
                httpscon.setRequestMethod("GET");
                httpscon.setRequestProperty("User-Agent", "IotServerProxy");
                // Wait for remote call.
                int responseCode = httpscon.getResponseCode();
                System.out.println("PROXY Response Code :: " + responseCode);
            }

            // Do call
        } catch (IOException iOException) {
            System.out.println("HTTP Server error");
            iOException.printStackTrace();
        } catch (Exception iOException) {
            System.out.println("HTTP Server error");
            iOException.printStackTrace();
        }
    }

}
