/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.file;

import iotserver.context.IotContext;
import iotserver.cookie.IotCookie;
import iotserver.request.HTTPRequest;
import iotserver.response.HTTPResponse;
import iotserver.runner.RequestRunner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

/**
 *
 * @author johanbergman
 */
public class HTTPFile {

    public HTTPResponse readHTTPFile(HTTPRequest httpRequest, Socket runnerSocket, IotContext iotContext,
            String fileName) {
        HTTPResponse httpResponse = new HTTPResponse();

        // TODO Check filename,
        try {
            File fil = new File(fileName);
            if (fil.exists()) {
                String lineBreak = "\r\n";

                OutputStream out = runnerSocket.getOutputStream();

                out.write(("HTTP/1.1 200" + lineBreak).getBytes());
                out.write(("Server: Simple IotServer : 1.0" + lineBreak).getBytes());
                out.write(("Date: " + new Date() + lineBreak).getBytes());

                String[] fileExtensionParts = fileName.split("\\.");
                String contentType = iotContext.getContentType(fileExtensionParts[fileExtensionParts.length - 1]);

                out.write(
                        ("Content-Type: " + contentType
                                + lineBreak).getBytes());

                if (contentType.startsWith("application")) {
                    out.write(("Content-Disposition: attachment; filename=\"" + fil.getName() + "\"" + lineBreak)
                            .getBytes());
                }
                out.write(("Content-Length: " + fil.length() + lineBreak).getBytes());

                out.write(lineBreak.getBytes());
                FileInputStream fis = new FileInputStream(fileName);
                int tLength = 1024;
                byte[] tInData = new byte[tLength];
                int bytes = 0;
                while ((bytes = fis.read(tInData)) > 0) {
                    out.write(tInData, 0, bytes);
                    out.flush();
                    if (bytes < tLength) {
                        break;
                    }
                }

                httpResponse.setReturnCode(-1); // Already sent, don't execute sendresponse.

                fis.close();
                out.flush();
                out.close();
            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
            httpResponse.setReturnCode(404);
            httpResponse.setReturnMessage("File not found!!");
        }

        return httpResponse;
    }

}
