package iotserver.proxy;

import java.io.BufferedOutputStream;
import java.net.Socket;

import iotserver.common.Common;
import iotserver.context.IotContext;
import iotserver.request.HTTPRequest;
import iotserver.response.HTTPResponse;
import iotserver.runner.RequestRunner;

public class ProxyRemoteClientCall extends Thread {

    private Socket runnerSocket;
    private IotContext iotContext;
    // private PrintWriter out = null;
    private BufferedOutputStream dataOut = null;
    private Common cmn = new Common(iotContext);
    private HTTPRequest httpRequest;
    private HTTPResponse httpResponse;

    public ProxyRemoteClientCall(HTTPRequest httpRequest, HTTPResponse httpResponse, Socket runnerSocket,
            BufferedOutputStream dataOut, IotContext iotContext) {
        this.iotContext = iotContext;
        this.runnerSocket = runnerSocket;
        this.dataOut = dataOut;
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }

    @Override
    public void run() {

        synchronized (this) {

            try {
                System.out.println("remoteProxyCall");

                try {
                    this.wait();
                } catch (Exception e) {
                    // TODO: handle exception
                }
                ProxyRemoteCallInstruction proxyClientInfo = iotContext.getRemoteProxyClientContext("1234");
                if (proxyClientInfo != null) {

                    System.out.println("waiting " + this.getName());
                    httpResponse.setReturnCode(200);
                    httpResponse.setBody(proxyClientInfo.getBody().getBytes());
                    cmn.sendResponse(httpRequest, httpResponse, runnerSocket, dataOut);
                    dataOut.close();
                    runnerSocket.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
