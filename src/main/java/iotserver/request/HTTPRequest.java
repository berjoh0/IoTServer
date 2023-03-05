/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.request;

import iotserver.context.IotContext;
import iotserver.cookie.IotCookie;
import java.util.HashMap;

/**
 *
 * @author johanbergman
 */
public class HTTPRequest {

    private IotContext iotContext;
    private String method = "";
    private String url = "";
    private String[] urlParts;
    private HashMap<String, IotCookie> cookies = new HashMap<String, IotCookie>();
    private String queryString = "";
    private String body = "";
    private int contentLength = 0;
    private HashMap headers = new HashMap();

    public HTTPRequest(IotContext iotContext) {
        this.iotContext = iotContext;
    }

    public void setServerAttribute(String key, Object value) {
        iotContext.setServerAttribute(key, value);
    }

    public Object getServerAttribute(String key) {
        return iotContext.getServerAttribute(key);
    }

    public boolean containsServerAttribute(String key) {
        return iotContext.containsServerAttribute(key);
    }

    private void setApplication(String application) {
        iotContext.setApplication(application);
    }

    public void setApplicationAttribute(String key, Object value) {
        iotContext.setApplicationAttribute(key, value);
    }

    public Object getApplicationAttribute(String key) {
        return iotContext.getApplicationAttribute(key);
    }

    public boolean containsApplicationAttribute(String key) {
        return iotContext.containsApplicationAttribute(key);
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        if (url.contains("?")) {
            this.setQueryString(url.substring(url.indexOf("?") + 1));
            url = url.substring(0, url.indexOf("?"));
        }
        this.url = url;

        setUrlParts(this.url.split("/"));
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @param body the body to append
     */
    public void appendBody(String body) {
        this.body += body;
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
     * @param headerName the name to set
     * @param headerValue the value to set
     */
    public void addHeader(String headerName, String headerValue) {
        //Check for cookies
        if (headerName.equalsIgnoreCase("cookie")) {

        } else {
            this.headers.put(headerName, headerValue);
        }
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
     * @return the queryString
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * @param queryString the queryString to set
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    /**
     * @return the urlParts
     */
    public String[] getUrlParts() {
        return urlParts;
    }

    /**
     * @param urlParts the urlParts to set
     */
    private void setUrlParts(String[] urlParts) {
        this.urlParts = urlParts;
        setApplication(this.urlParts[0]);
    }

    /**
     * @return the cookies
     */
    public HashMap<String, IotCookie> getCookies() {
        return cookies;
    }

    /**
     * @param name the cookie to get
     * @return cookie
     */
    public IotCookie getCookie(String name) {
        return cookies.get(name);
    }

    /**
     * @param name of cookie
     * @param value of cookie
     */
    private void addCookie(String name, String value) {
        this.cookies.put(name, new IotCookie(name, value));
    }
}
