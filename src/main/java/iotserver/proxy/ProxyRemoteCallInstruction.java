package iotserver.proxy;

public class ProxyRemoteCallInstruction {

    public ProxyRemoteCallInstruction(String id, String body) {
        this.id = id;
        this.body = body;
    }

    private String id;
    private String body;

    public String getId() {
        return id;
    }

    public String getBody() {
        return body;
    }
}
