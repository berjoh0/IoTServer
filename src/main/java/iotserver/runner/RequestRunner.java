/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.runner;

import com.google.gson.JsonObject;

import iotserver.context.IotContext;
import iotserver.cookie.IotCookie;
import iotserver.dynamicClassLoader.ExecuteClass;
import iotserver.file.HTTPFile;
import iotserver.mapping.IoTMapping;
import iotserver.request.HTTPRequest;
import iotserver.response.HTTPResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;
import javax.net.ssl.SSLHandshakeException;

/**
 *
 * @author johanbergman
 */
public class RequestRunner implements Runnable {

    private JsonObject httpServerProperties;
    private Socket runnerSocket;
    private IotContext iotContext;
    private PrintWriter out = null;
    private BufferedOutputStream dataOut = null;

    public RequestRunner(Socket runnerSocket, IotContext iotContext, JsonObject httpServerProperties) {
        this.runnerSocket = runnerSocket;
        this.httpServerProperties = httpServerProperties;
        this.iotContext = iotContext;
    }

    @Override
    public void run() {
        DataInputStream bodyIn = null;

        try {
            // in = new BufferedReader(new
            // InputStreamReader(runnerSocket.getInputStream()));
            bodyIn = new DataInputStream(runnerSocket.getInputStream());

            // we get character output stream to client (for headers)
            out = new PrintWriter(runnerSocket.getOutputStream());
            // get binary output stream to client (for requested data)
            dataOut = new BufferedOutputStream(runnerSocket.getOutputStream());

            // String input = in.readLine();
            int tLength = 1024;
            byte[] tInData = new byte[tLength];

            HTTPRequest httpRequest = new HTTPRequest(iotContext);

            boolean firstLineRead = false;
            boolean bodyNext = false;

            while (true) {
                int bytes = 0;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((bytes = bodyIn.read(tInData)) > 0) {
                    baos.write(tInData, 0, bytes);
                    if (bytes < tLength) {
                        break;
                    }
                }

                byte[] inDta = baos.toByteArray();

                if (inDta.length > 0) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(inDta)));
                    String tLine;
                    while ((tLine = br.readLine()) != null) {
                        // System.out.println("t:" + tLine);
                        if (!firstLineRead) {
                            // Get method, url
                            StringTokenizer parser = new StringTokenizer(tLine);
                            httpRequest.setMethod(parser.nextElement().toString());
                            httpRequest.setUrl(parser.nextElement().toString());
                            firstLineRead = true;
                        } else {
                            if (bodyNext) {
                                httpRequest.appendBody(tLine + System.lineSeparator());
                            } else if (tLine.isBlank() && !bodyNext) {
                                bodyNext = true;
                            } else {
                                String[] tHeaders = tLine.split(":", 2);
                                if (tHeaders.length > 1) {
                                    String headerName = tHeaders[0];
                                    switch (headerName) {
                                        case "Content-Length":
                                            httpRequest.setContentLength(Integer.parseInt(tHeaders[1].trim()));
                                            break;
                                        default:
                                            httpRequest.addHeader(headerName, tHeaders[1]);
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }

                if ((httpRequest.getContentLength() > 0 && !httpRequest.getBody().isBlank())
                        || httpRequest.getContentLength() == 0) {
                    break;
                }
            }

            executeCall(httpRequest);

            bodyIn.close();
            runnerSocket.close();

        } catch (SSLHandshakeException ex) {
        } catch (Exception ex) {
            System.out.println("HTTP Runner error");
            ex.printStackTrace();
        }
    }

    private void sendResponse(HTTPRequest httpRequest, HTTPResponse httpResponse) {
        try {
            byte[] retBytes = httpResponse.getBody();
            int retLength = 0;
            if (retBytes == null) {
                retBytes = "".getBytes();
            } else {
                retLength = retBytes.length;
            }

            out.println("HTTP/1.1 " + httpResponse.getReturnCode() + " " + httpResponse.getReturnMessage());
            out.println("Server: Simple IotServer : 1.0");
            out.println("Date: " + new Date());

            // Headers
            for (String headerName : httpResponse.listHeaderKeys()) {
                String headerValue = httpResponse.getHeader(headerName);
                out.println(headerName + ": " + headerValue);
            }
            // Cookies
            for (String cookieName : httpResponse.listCookieKeys()) {
                IotCookie cookieValue = httpResponse.getCookie(cookieName);
                out.println("Set-Cookie: " + cookieName + "=" + cookieValue.getValue() + "; Path=/"
                        + httpRequest.getUrlParts()[0]);
            }

            if (!httpResponse.getContentType().isEmpty()) {
                out.println("Content-type: " + httpResponse.getContentType());
            }
            out.println("Content-length: " + retLength);
            out.println(); // blank line between headers and content, very important !
            out.flush();
            dataOut.write(retBytes, 0, retLength);
            dataOut.flush();

            out.close();
            dataOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendFileResponse(HTTPResponse httpResponse) {
        try {
            byte[] retBytes = httpResponse.getBody();
            int retLength = 0;
            if (retBytes == null) {
                retBytes = "".getBytes();
            } else {
                retLength = retBytes.length;
            }

            dataOut.write(retBytes, 0, retLength);
            dataOut.flush();

            out.close();
            dataOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendInvalidMethod(HTTPRequest httpRequest, String method) {
        HTTPResponse httpR = new HTTPResponse();
        httpR.setReturnCode(405);
        httpR.setReturnMessage("Method not implemented!!!");

        sendResponse(httpRequest, httpR);
    }

    private void sendError(HTTPRequest httpRequest, Exception ex) {
        HTTPResponse httpR = new HTTPResponse();
        httpR.setReturnCode(405);
        httpR.setReturnMessage("Error:" + ex.getMessage());

        sendResponse(httpRequest, httpR);
    }

    private void sendInvalidMapping(HTTPRequest httpRequest) {
        HTTPResponse httpR = new HTTPResponse();
        httpR.setReturnCode(404);
        httpR.setReturnMessage("Mapping not found!!!");

        sendResponse(httpRequest, httpR);
    }

    private void executeCall(HTTPRequest httpRequest) {

        // SetCookies
        IoTMapping mapping = iotContext.getMapping(httpRequest);

        try {

            if (mapping == null) {
                sendInvalidMapping(httpRequest);
                return;
            } else {
                switch (mapping.getMapped_type()) {
                    case IoTMapping.PATH:
                        // Read file
                        sendResponse(httpRequest,
                                new HTTPFile().readHTTPFile(iotContext,
                                        mapping.buildMapped_path(httpRequest.getUrl())));
                        break;
                    case IoTMapping.CLASS:
                        sendResponse(httpRequest, new ExecuteClass().execute(mapping.getMapped_path(), httpRequest));
                        break;
                    case IoTMapping.PACKAGE:
                        sendResponse(httpRequest,
                                new ExecuteClass().executePackage(mapping.getMapped_path(), httpRequest));
                        break;
                }

            }
        } catch (Exception e) {
            sendError(httpRequest, e);
        }
    }

}
