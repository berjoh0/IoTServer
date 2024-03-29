/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.runner;

import com.google.gson.JsonObject;

import iotserver.common.Common;
import iotserver.context.IotContext;
import iotserver.cookie.IotCookie;
import iotserver.dynamicClassLoader.ExecuteClass;
import iotserver.file.HTTPFile;
import iotserver.mapping.IoTMapping;
import iotserver.proxy.ProxyRemoteClientCall;
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
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import javax.net.ssl.SSLHandshakeException;

/**
 *
 * @author johanbergman
 */
public class RequestRunner extends Thread {

    private JsonObject httpServerProperties;
    private Socket runnerSocket;
    private IotContext iotContext;
    // private PrintWriter out = null;
    private BufferedOutputStream dataOut = null;
    private Common cmn = new Common(iotContext);

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
            // out = new PrintWriter(runnerSocket.getOutputStream());
            // get binary output stream to client (for requested data)
            dataOut = new BufferedOutputStream(runnerSocket.getOutputStream());

            // String input = in.readLine();
            int tLength = 1024;
            byte[] tInData = new byte[tLength];

            HTTPResponse httpResponse = new HTTPResponse();
            HTTPRequest httpRequest = new HTTPRequest(iotContext, httpResponse);

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
                System.out.println(baos.toString());

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
                                            if (headerName.equalsIgnoreCase("cookie")) {
                                                String[] tCookie = tHeaders[1].split("=");
                                                httpRequest.addCookie(tCookie[0].trim(), tCookie[1]);
                                                httpResponse.addCookie(tCookie[0].trim(), tCookie[1]);
                                            } else {
                                                httpRequest.addHeader(headerName, tHeaders[1]);
                                            }
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

            IoTMapping mapping = executeCall(httpRequest, httpResponse);

            if (mapping.getMapped_type() != IoTMapping.PROXY) {
                bodyIn.close();
                runnerSocket.close();
            }

        } catch (SSLHandshakeException ex) {
        } catch (Exception ex) {
            System.out.println("HTTP Runner error");
            ex.printStackTrace();
        }
    }

    /*
     * private void sendFileResponse(HTTPResponse httpResponse) {
     * try {
     * byte[] retBytes = httpResponse.getBody();
     * int retLength = 0;
     * if (retBytes == null) {
     * retBytes = "".getBytes();
     * } else {
     * retLength = retBytes.length;
     * }
     * 
     * dataOut.write(retBytes, 0, retLength);
     * dataOut.flush();
     * 
     * out.close();
     * dataOut.close();
     * 
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * 
     * }
     */

    private void sendInvalidMethod(HTTPRequest httpRequest, HTTPResponse httpResponse, String method) {
        httpResponse.setReturnCode(405);
        httpResponse.setReturnMessage("Method not implemented!!!");

        cmn.sendResponse(httpRequest, httpResponse, runnerSocket, dataOut);
    }

    private void sendError(HTTPRequest httpRequest, HTTPResponse httpResponse, Exception ex) {
        httpResponse.setReturnCode(405);
        httpResponse.setReturnMessage("Error:" + ex.getMessage());

        cmn.sendResponse(httpRequest, httpResponse, runnerSocket, dataOut);
    }

    private void sendInvalidMapping(HTTPRequest httpRequest, HTTPResponse httpResponse) {
        httpResponse.setReturnCode(404);
        httpResponse.setReturnMessage("Mapping not found!!!");

        cmn.sendResponse(httpRequest, httpResponse, runnerSocket, dataOut);
    }

    private IoTMapping executeCall(HTTPRequest httpRequest, HTTPResponse httpResponse) {

        IoTMapping mapping = iotContext.getMapping(httpRequest);

        try {

            if (mapping == null) {
                sendInvalidMapping(httpRequest, httpResponse);
                return mapping;
            } else {
                switch (mapping.getMapped_type()) {
                    case IoTMapping.PATH:
                        // Read file
                        cmn.sendResponse(httpRequest,
                                new HTTPFile().readHTTPFile(httpRequest, httpResponse, runnerSocket, iotContext,
                                        mapping,
                                        mapping.buildMapped_path(httpRequest.getUrl())),
                                runnerSocket, dataOut);
                        break;
                    case IoTMapping.CLASS:
                        cmn.sendResponse(httpRequest,
                                new ExecuteClass().execute(mapping.getMapped_path(), httpRequest, httpResponse),
                                runnerSocket, dataOut);
                        break;
                    case IoTMapping.PACKAGE:
                        cmn.sendResponse(httpRequest,
                                new ExecuteClass().executePackage(
                                        mapping.getMapped_path() + (httpRequest.getUrl().equals(mapping.getMapped_key())
                                                ? "." + mapping.getMapped_defaultPage()
                                                : ""),
                                        httpRequest, httpResponse),
                                runnerSocket, dataOut);
                        break;
                    case IoTMapping.PROXY:
                        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                        for (Iterator<Thread> it = threadSet.iterator(); it.hasNext();) {
                            Thread th = it.next();

                            if (th.getThreadGroup().getName().equals("ProxyRemoteClients")) {
                                Thread th2 = new Thread(th.getThreadGroup(),
                                        new ProxyRemoteClientCall(httpRequest, httpResponse, runnerSocket, dataOut,
                                                iotContext),
                                        "ProxyRemoteClient" + httpRequest.getQueryString());

                                th2.start();
                                ;
                                break;
                            }
                        }

                        /*
                         * while (true) {
                         * Thread.sleep(5000);
                         * System.out.println(getName() + " waiting");
                         * }
                         */
                        // Wait for remote call,

                }

            }
        } catch (Exception e) {
            sendError(httpRequest, httpResponse, e);
        }

        return mapping;
    }

}
