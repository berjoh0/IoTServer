package iotserver.api;

import com.google.gson.JsonObject;

import iotserver.request.HTTPRequest;
import iotserver.response.HTTPResponse;
import iotserver.session.HTTPSession;

public class api {
    public HTTPResponse doGET(HTTPRequest httpRequest, HTTPResponse httpResponse) {
        HTTPSession session = httpRequest.getSession(true);
        // JsonObject obj = new JsonObject();
        // obj.addProperty("API", "Avaible");
        // httpResponse.setBody(obj);
        var html = "<html>" +
                "<body>" +
                "APIs" +
                "</body>" +
                "</html>";
        httpResponse.setBody(html.getBytes());
        return httpResponse;
    }

    public HTTPResponse doPOST(HTTPRequest httpRequest, HTTPResponse httpResponse) {
        return httpResponse;
    }

}
