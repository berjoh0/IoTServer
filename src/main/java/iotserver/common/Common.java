package iotserver.common;

import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import iotserver.context.IotContext;
import iotserver.cookie.IotCookie;
import iotserver.request.HTTPRequest;
import iotserver.response.HTTPResponse;

public class Common {

    IotContext iotContext;

    public Common(IotContext iotContext) {
        this.iotContext = iotContext;
    }

    public void sendResponse(HTTPRequest httpRequest, HTTPResponse httpResponse, Socket runnerSocket,
            BufferedOutputStream dataOut) {
        try {

            if (httpResponse.getReturnCode() != -1) {

                if (httpResponse.getReturnCode() != 200 && httpResponse.getBody() == null) {
                    // load default error file
                    httpResponse.setBody(("Error::" + httpResponse.getReturnCode()).getBytes());
                }
                byte[] retBytes = httpResponse.getBody();
                int retLength = 0;
                if (retBytes == null) {
                    retBytes = "".getBytes();
                } else {
                    retLength = retBytes.length;
                }

                PrintWriter out = new PrintWriter(runnerSocket.getOutputStream());

                out.println("HTTP/1.1 " + httpResponse.getReturnCode() + " " + httpResponse.getReturnMessage());
                out.println("Server: Simple IotServer : 1.0");
                out.println("Date: " + new Date());

                // Headers
                for (String headerName : httpResponse.listHeaderKeys()) {
                    String headerValue = httpResponse.getHeader(headerName);
                    out.println(headerName + ": " + headerValue);
                }
                // Cookies
                for (String cookieName : httpResponse.listCookieKeys()) {
                    IotCookie cookieValue = httpResponse.getCookie(cookieName);
                    out.println("Set-Cookie: " + cookieName + "=" + cookieValue.getValue() + "; Path=/"
                            + httpRequest.getUrlParts()[0]);
                }

                if (!httpResponse.getContentType().isEmpty()) {
                    out.println("Content-type: " + httpResponse.getContentType());
                }
                out.println("Content-length: " + retLength);
                out.println(); // blank line between headers and content, very important !
                out.flush();
                dataOut.write(retBytes, 0, retLength);
                dataOut.flush();

                out.close();
                dataOut.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
