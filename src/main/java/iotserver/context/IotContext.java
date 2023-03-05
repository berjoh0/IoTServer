/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.context;

import iotserver.IoTContentType;
import iotserver.IoTMappings;
import iotserver.request.HTTPRequest;
import java.util.HashMap;

/**
 *
 * @author johanbergman
 */
public class IotContext {

    private static IoTMappings iotMappings;
    private static IoTContentType iotContentType;

    private static HashMap<String, Object> serverContext = new HashMap<String, Object>();
    private static HashMap<String, Object> applicationContext = new HashMap<String, Object>();

    private String application = "";
    private HashMap<String, Object> currentApplicationContext;

    public IotContext(IoTMappings iotMappings, IoTContentType iotContentType) {
        this.iotMappings = iotMappings;
        this.iotContentType = iotContentType;
    }

    public IoTMappings.Mapping getMapping(HTTPRequest httpRequest) {
        return iotMappings.getMapping(httpRequest);
    }

    public String getContentType(String fileExtension) {
        return iotContentType.getContentType(fileExtension);
    }

    public void setServerAttribute(String key, Object value) {
        serverContext.put(key, value);
    }

    public Object getServerAttribute(String key) {
        return serverContext.get(key);
    }

    public boolean containsServerAttribute(String key) {
        return serverContext.containsKey(key);
    }

    public void setApplication(String application) {
        this.application = application;

        Object tApplicationContext = applicationContext.get(this.application);
        if (tApplicationContext == null) {
            currentApplicationContext = new HashMap<String, Object>();
        } else {
            currentApplicationContext = (HashMap<String, Object>) tApplicationContext;
        }
    }

    public boolean containsApplicationAttribute(String key) {
        return currentApplicationContext.containsKey(key);
    }

    public void setApplicationAttribute(String key, Object value) {
        currentApplicationContext.put(key, value);
        applicationContext.put(application, currentApplicationContext);
    }

    public Object getApplicationAttribute(String key) {
        return currentApplicationContext.get(key);
    }

}
