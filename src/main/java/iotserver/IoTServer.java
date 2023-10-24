/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import iotserver.context.IotContext;
import iotserver.database.IoTDatabase;
import iotserver.listeners.HTTPSServer;
import iotserver.listeners.HTTPServer;
import iotserver.listeners.ProxyClientServer;
import iotserver.mapping.IoTMappings;
import iotserver.proxy.ProxyRemoteClientManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

/**
 *
 * @author johanbergman
 */
public class IoTServer {

    private static IoTMappings iotM;
    private static IoTDatabase iotDB;
    private static IoTContentType iotContentType;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Properties file not entered");
            return;
        }
        // Load properties file.
        byte[] propBytes;
        try {
            FileInputStream fis = new FileInputStream(args[0]);
            propBytes = fis.readAllBytes();
            fis.close();

        } catch (FileNotFoundException ex) {
            System.out.println("Properties file not found");
            ex.printStackTrace();
            return;
        } catch (IOException ex) {
            System.out.println("Properties file cannot be read");
            ex.printStackTrace();
            return;
        }

        JsonObject prop = JsonParser.parseString(new String(propBytes)).getAsJsonObject();

        System.out.println("prop: " + prop);

        // Get database
        if (prop.has("database")) {
            iotDB = new IoTDatabase(prop.getAsJsonObject("database"));
            if (!iotDB.openDatabase(true)) {
                return;
            }
        }

        // Get mappings
        if (prop.has("mapping")) {
            iotM = new IoTMappings(prop.getAsJsonObject("mapping"));
        }

        // Get contentTypes
        if (prop.has("content-type")) {
            iotContentType = new IoTContentType(prop.getAsJsonObject("content-type"));
        }

        IotContext iotContext = new IotContext(iotM, iotContentType, iotDB);

        ThreadGroup proxyClients = new ThreadGroup("ProxyClients");
        ThreadGroup httpListeners = new ThreadGroup("HttpListeners");
        new Thread(new ThreadGroup("ProxyRemoteClients"),
                new ProxyRemoteClientManager(iotContext),
                "ProxyRemoteManager").start();

        // Start servers
        if (prop.has("http")) {
            JsonObject httpObj = prop.get("http").getAsJsonObject();
            System.out.println("http: " + httpObj);
            HTTPServer mainHttpServer = new HTTPServer(iotContext, httpObj);
            new Thread(httpListeners, mainHttpServer, "HTTPListener").start();
        }
        if (prop.has("https")) {
            JsonObject httpsObj = prop.get("https").getAsJsonObject();
            System.out.println("https: " + httpsObj);
            HTTPSServer mainHttpsServer = new HTTPSServer(iotContext, httpsObj);
            new Thread(httpListeners, mainHttpsServer, "HTTPSListener").start();
        }
        // Start remote proxy clients
        if (prop.has("proxy-client")) {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                // TODO: handle exception
            }

            for (Iterator<JsonElement> it = prop.getAsJsonArray("proxy-client").iterator(); it.hasNext();) {
                JsonElement pxc = it.next();
                new Thread(proxyClients, new ProxyClientServer(iotContext, pxc.getAsJsonObject())).start();
                ;
            }

        }

    }

}
