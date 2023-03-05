/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver;

import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author johanbergman
 */
public class IoTContentType extends HashMap<String, String> {

    public IoTContentType(JsonObject contentTypes) {
        for (Iterator<String> it = contentTypes.keySet().iterator(); it.hasNext();) {
            String fileExtension = it.next().toString();
            String contentType = contentTypes.get(fileExtension).getAsString();

            put(fileExtension, contentType);
        }
    }

    public String getContentType(String fileExtension) {
        if (containsKey(fileExtension)) {
            return get(fileExtension).toString();
        } else {
            System.out.println("ContentType missing: " + fileExtension);
            return "application/octet-stream";
        }
    }
}
