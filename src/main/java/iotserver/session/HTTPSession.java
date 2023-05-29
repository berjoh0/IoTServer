package iotserver.session;

import java.util.HashMap;

public class HTTPSession {

    private HashMap<String, Object> sessionObject = new HashMap<>();

    public Object getAttribute(String name) {
        return sessionObject.get(name);
    }

    public boolean hasAttribute(String name) {
        return sessionObject.containsKey(name);
    }

    public void setAttribute(String name, Object value) {
        this.sessionObject.put(name, value);
    }

}
