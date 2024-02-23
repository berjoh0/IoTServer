package iotserver.time;

import java.sql.Timestamp;

import com.google.gson.JsonObject;

import iotserver.request.HTTPRequest;
import iotserver.response.HTTPResponse;

/**
 *
 * @author johanbergman
 */
public class Time {
    public HTTPResponse doGET(HTTPRequest httpRequest, HTTPResponse httpResponse) {

        JsonObject retObj = new JsonObject();
        long curTimeMillis = System.currentTimeMillis();

        retObj.addProperty("timestampmillis", "" + curTimeMillis);
        retObj.addProperty("timestamp", new Timestamp(curTimeMillis).toString());

        httpResponse.setReturnCode(200);

        httpResponse.setContentType("application/Json");
        httpResponse.setBody(retObj);

        return httpResponse;

    }
}
