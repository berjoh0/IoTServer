package iotserver.proxy;

import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.cert.X509Certificate;

import iotserver.context.IotContext;
import iotserver.mapping.IoTMapping;
import iotserver.request.HTTPRequest;
import iotserver.response.HTTPResponse;

public class proxyCall {

    public HTTPResponse execute(HTTPRequest httpRequest, HTTPResponse httpResponse, Socket runnerSocket,
            IotContext iotContext,
            IoTMapping mapping) {

        try {

            URL url = new URL(mapping.getMapped_path());

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
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);

            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            if (con != null) {

                InputStream is = con.getInputStream();

                // OutputStream os = runnerSocket.getOutputStream();

                final byte[] inData = new byte[1024];

                String retData = "";

                int bytes_read;
                while ((bytes_read = is.read(inData)) != -1) {

                    retData += new String(inData, 0, bytes_read);
                    // os.write(inData, 0, bytes_read);
                    // os.flush();
                }

                httpResponse.setReturnCode(200);

                httpResponse.setBody(retData.getBytes());
                is.close();
                // os.close();

                System.out.println("retData: " + retData);

            }

        } catch (Exception e) {
            httpResponse.setReturnCode(400);
            e.printStackTrace();
        }
        return httpResponse;
    }

}
