/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.sensors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import iotserver.request.HTTPRequest;
import iotserver.response.HTTPResponse;

/**
 *
 * @author johanbergman
 */
public class sensor {

    public HTTPResponse doGET(HTTPRequest httpRequest) {
        HTTPResponse httpResponse = new HTTPResponse();

        Object retSensors = httpRequest.getApplicationAttribute("Sensors");

        JsonObject retObj = new JsonObject();
        if (retSensors != null) {
            retObj = (JsonObject) retSensors;
        } else {
            retObj.addProperty("Sensors", "Not Set");
        }

        httpResponse.setContentType("application/Json");
        httpResponse.setBody(retObj);

        return httpResponse;
    }

    public HTTPResponse doPOST(HTTPRequest httpRequest) {
        HTTPResponse httpResponse = new HTTPResponse();

        JsonObject body = JsonParser.parseString(httpRequest.getBody()).getAsJsonObject();

        JsonArray bodyArray = body.getAsJsonArray("sensor");

        for (int i = 0; i < bodyArray.size(); i++) {
            System.out.println("body:" + bodyArray.get(i));
        }
        httpRequest.setApplicationAttribute("Sensors", body);

        httpResponse.setReturnCode(200);

        return httpResponse;
    }
}
