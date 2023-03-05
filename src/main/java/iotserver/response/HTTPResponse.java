/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.response;

import com.google.gson.JsonObject;
import iotserver.cookie.IotCookie;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author johanbergman
 */
public class HTTPResponse {

    private String method = "";
    private int returnCode = 0;
    private String returnMessage = "";
    private byte[] body;
    private int contentLength = 0;
    private String contentType = "";
    private HashMap<String, String> headers = new HashMap<String, String>();
    private HashMap<String, IotCookie> cookies = new HashMap<String, IotCookie>();

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @return the body
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(byte[] body) {
        this.body = body;
        this.setContentLength();
    }

    /**
     * @param body the body to set type set to application/json length set to
     * object length
     */
    public void setBody(JsonObject body) {
        this.body = body.toString().getBytes();
        this.setContentType("application/json");
        this.setContentLength();
    }

    /**
     * @return the contentLength
     */
    public int getContentLength() {
        return contentLength;
    }

    /**
     * @param contentLength the contentLength to set
     */
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    /**
     */
    public void setContentLength() {
        this.contentLength = body.length;
    }

    /**
     * @param headerName the name to set
     * @param headerValue the value to set
     */
    public void addHeader(String headerName, String headerValue) {
        this.headers.put(headerName, headerValue);
    }

    /**
     * @param headerName the name to get
     */
    public String getHeader(String headerName) {
        if (this.headers.containsKey(headerName)) {
            return this.headers.get(headerName).toString();
        } else {
            return null;
        }
    }

    /**
     * @param headerName the name to check if exists
     */
    public boolean hasHeader(String headerName) {
        return this.headers.containsKey(headerName);
    }

    /**
     */
    public Set<String> listHeaderKeys() {
        return this.headers.keySet();
    }

    /**
     * @param name the name to set
     * @param value the value to set
     */
    public void addCookie(String name, String value) {
        this.cookies.put(name, new IotCookie(name, value));
    }

    /**
     * @param name the name to get
     */
    public IotCookie getCookie(String name) {
        return this.cookies.get(name);
    }

    /**
     * @param cookieName the name to check if exists
     */
    public boolean hasCookie(String cookieName) {
        return this.cookies.containsKey(cookieName);
    }

    /**
     * @return the returnCode
     */
    /**
     */
    public Set<String> listCookieKeys() {
        return this.cookies.keySet();
    }

    /**
     * @return the returnCode
     */
    public int getReturnCode() {
        return returnCode;
    }

    /**
     * @param returnCode the returnCode to set
     */
    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    /**
     * @return the returnMessage
     */
    public String getReturnMessage() {
        return returnMessage;
    }

    /**
     * @param returnMessage the returnMessage to set
     */
    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
