/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import iotserver.context.IotContext;
import iotserver.listeners.HTTPSServer;
import iotserver.listeners.HTTPServer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author johanbergman
 */
public class IoTServer {

    private static IoTMappings iotM;
    private static IoTContentType iotContentType;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Properties file not entered");
            return;
        }
        //Load properties file.
        byte[] propBytes;
        try {
            FileInputStream fis = new FileInputStream(args[0]);
            propBytes = fis.readAllBytes();

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

        //Get mappings
        if (prop.has("mapping")) {
            iotM = new IoTMappings(prop.getAsJsonObject("mapping"));
        }

        //Get contentTypes
        if (prop.has("content-type")) {
            iotContentType = new IoTContentType(prop.getAsJsonObject("content-type"));
        }

        IotContext iotContext = new IotContext(iotM, iotContentType);

        //Start servers
        if (prop.has("http")) {
            JsonObject httpObj = prop.get("http").getAsJsonObject();
            System.out.println("http: " + httpObj);
            HTTPServer mainHttpServer = new HTTPServer(iotContext, httpObj);
            new Thread(mainHttpServer).start();
        }
        if (prop.has("https")) {
            JsonObject httpsObj = prop.get("https").getAsJsonObject();
            System.out.println("https: " + httpsObj);
            HTTPSServer mainHttpsServer = new HTTPSServer(iotContext, httpsObj);
            new Thread(mainHttpsServer).start();
        }

    }

}
