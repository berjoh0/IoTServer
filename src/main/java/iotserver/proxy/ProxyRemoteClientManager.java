package iotserver.proxy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import iotserver.context.IotContext;
import iotserver.runner.RequestRunner;

public class ProxyRemoteClientManager extends Thread {

    private IotContext iotContext;

    public ProxyRemoteClientManager(IotContext iotContext) {
        this.iotContext = iotContext;
    }

    HashMap<String, RequestRunner> remoteClients = new HashMap<String, RequestRunner>();

    @Override
    public void run() {

        int i = 0;
        synchronized (this) {

            try {
                while (true) {
                    Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                    for (Iterator<Thread> it = threadSet.iterator(); it.hasNext();) {
                        Thread th = it.next();

                        if (th.getName().startsWith("ProxyRemoteClient")) {
                            System.out.println("Exists" + th.getName());
                            if (i > 2) {
                                iotContext.addRemoteProxyCallInstruction("1234", "a call with better data");
                                th.interrupt();
                                i = 0;
                            }
                        }
                    }
                    Thread.sleep(5000);
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addRemoteClient(RequestRunner rr) {
        remoteClients.put("1234", rr);
    }

}
