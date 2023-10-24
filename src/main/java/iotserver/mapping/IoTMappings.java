/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.mapping;

import com.google.gson.JsonObject;

import iotserver.request.HTTPRequest;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author johanbergman
 */
public class IoTMappings extends HashMap<String, IoTMapping> {

    public IoTMappings(JsonObject mappings) {
        for (Iterator<String> it = mappings.keySet().iterator(); it.hasNext();) {
            String mapp_key = it.next().toString();
            JsonObject mapp_value = mappings.getAsJsonObject(mapp_key);

            if (mapp_value.has("path")) {
                this.put(mapp_key, new IoTMapping(mapp_key, mapp_value.get("path").getAsString(),
                        mapp_value.get("defaultPage").getAsString(), IoTMapping.PATH));
            } else if (mapp_value.has("package")) {
                this.put(mapp_key,
                        new IoTMapping(mapp_key, mapp_value.get("package").getAsString(),
                                mapp_value.get("defaultClass").getAsString(), IoTMapping.PACKAGE));
            } else if (mapp_value.has("class")) {
                this.put(mapp_key,
                        new IoTMapping(mapp_key, mapp_value.get("class").getAsString(),
                                mapp_value.get("defaultClass").getAsString(), IoTMapping.CLASS));
            } else if (mapp_value.has("remoteURL")) {
                this.put(mapp_key,
                        new IoTMapping(mapp_key, mapp_value.get("remoteURL").getAsString(),
                                "", IoTMapping.PROXY));
            }
        }
    }

    public IoTMapping getMapping(HTTPRequest httpRequest) {
        String urlPart0 = httpRequest.getUrlParts()[0];

        if (urlPart0.equals("proxy")) {
            return new IoTMapping("proxy", "iotserver.proxy", "server", IoTMapping.PROXY);
        } else if (this.containsKey(urlPart0)) {
            return (IoTMapping) this.get(urlPart0);
        } else if (this.containsKey("")) {
            return (IoTMapping) this.get("");
        } else {
            return null;
        }
    }

}
