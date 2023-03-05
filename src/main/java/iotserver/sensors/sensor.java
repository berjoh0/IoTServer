/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.sensors;

import java.util.Iterator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import iotserver.database.tables.TblSensorValues;
import iotserver.request.HTTPRequest;
import iotserver.response.HTTPResponse;

/**
 *
 * @author johanbergman
 */
public class Sensor {

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
        try {

            JsonObject body = JsonParser.parseString(httpRequest.getBody()).getAsJsonObject();

            JsonObject sensorVals = body.getAsJsonObject("sensor");

            TblSensorValues tblSensor = new TblSensorValues(httpRequest.getDatabase());

            for (Iterator<String> it = sensorVals.keySet().iterator(); it.hasNext();) {
                String mapp_key = it.next().toString();
                String mapp_value = sensorVals.get(mapp_key).getAsString();
                // System.out.println("val: " + mapp_key + "=" + mapp_value);
                tblSensor.setValue(mapp_key, mapp_value);
            }

            if (!tblSensor.insertValues()) {
                // TODO Save not OK
            }

            httpRequest.setApplicationAttribute("Sensors", body);

            httpResponse.setReturnCode(200);
        } catch (Exception e) {
            httpResponse.setReturnCode(405);
            httpResponse.setReturnMessage("Invalid sensor format: " + e.getMessage());
        }

        return httpResponse;
    }
}
