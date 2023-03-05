/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.file;

import iotserver.context.IotContext;
import iotserver.response.HTTPResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

/**
 *
 * @author johanbergman
 */
public class HTTPFile {

    public HTTPResponse readHTTPFile(IotContext iotContext, String fileName) {
        HTTPResponse httpResponse = new HTTPResponse();

        //TODO Check filename
        try {
            FileInputStream fis = new FileInputStream(fileName);
            int tLength = 1024;
            byte[] tInData = new byte[tLength];
            int bytes = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((bytes = fis.read(tInData)) > 0) {
                baos.write(tInData, 0, bytes);
                if (bytes < tLength) {
                    break;
                }
            }

            httpResponse.setBody(baos.toByteArray());
            httpResponse.setContentLength();
            httpResponse.setReturnCode(200);
            String[] fileExtensionParts = fileName.split("\\.");
            httpResponse.setContentType(iotContext.getContentType(fileExtensionParts[fileExtensionParts.length - 1]));

            fis.close();
            baos.close();

        } catch (Exception e) {
            e.printStackTrace();
            httpResponse.setReturnCode(404);
            httpResponse.setReturnMessage("File not found!!");
        }

        return httpResponse;
    }
}
