/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.listeners;

import com.google.gson.JsonObject;
import iotserver.context.IotContext;
import iotserver.runner.RequestRunner;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author johanbergman
 */
public class HTTPServer implements Runnable {

    private JsonObject httpServerProperties;
    private IotContext iotContext;

    public HTTPServer(IotContext iotContext, JsonObject httpServerProperties) {
        this.httpServerProperties = httpServerProperties;
        this.iotContext = iotContext;
    }

    @Override
    public void run() {

        try {
            int port = httpServerProperties.get("port").getAsInt();
            ServerSocket serverConnect = new ServerSocket(port);
            System.out.println("Http server Listening for connections on port : " + port + "\n");
            while (true) {
                Socket inSock = serverConnect.accept();
                RequestRunner newRunner = new RequestRunner(inSock, iotContext, httpServerProperties);
                Thread newThread = new Thread(newRunner);
                newThread.start();
            }
        } catch (IOException iOException) {
            System.out.println("HTTP Server error");
            iOException.printStackTrace();
        }
    }

}
