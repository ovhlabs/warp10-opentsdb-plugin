package io.warp10.plugins.opentsdb;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import io.warp10.warp.sdk.AbstractWarp10Plugin;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

/**
 * Created by rcoligno on 12/22/16.
 */
public class OpenTSDBWarp10Plugin extends AbstractWarp10Plugin implements Runnable {

    private int     port;
    private String  host;
    private int     idleTimeout;
    private int     maxThreads;
    private int     acceptors;
    private int     selectors;
    private URL     url;
    private BlockingQueue<Runnable> queue;
    // Configuration hierarchy namespace
    private final static String ns = "plugin.opentsdb.";

    @Override
    public void run() {
        System.out.println("[OpenTSDB plugin] Run");
        Server server = new Server(new QueuedThreadPool(maxThreads, 8, idleTimeout, queue));
        ServerConnector connector = new ServerConnector(server, acceptors, selectors);
        connector.setIdleTimeout(idleTimeout);
        connector.setPort(port);
        connector.setHost(host);
        connector.setName("OpenTSDB input protocol");

        server.setConnectors(new Connector[] { connector });
        HandlerList handlers = new HandlerList();
        handlers.addHandler((Handler) new OpenTSDBPutHandler(url));
        server.setHandler(handlers);

        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(Properties properties) {
        System.out.println("[OpenTSDB plugin] Init");

        this.acceptors      = Integer.parseInt(properties.getProperty(ns+"acceptors", "4"));
        this.selectors      = Integer.parseInt(properties.getProperty(ns+"selectors", "2"));
        this.maxThreads     = Integer.parseInt(properties.getProperty(ns+"jetty.threadpool", "8"));
        this.idleTimeout    = Integer.parseInt(properties.getProperty(ns+"idle.timeout", "30000"));
        this.port           = Integer.parseInt(properties.getProperty(ns+"port", "9091"));
        this.host           = properties.getProperty(ns+"host", "127.0.0.1");

        try {
            this.url = new URL(properties.getProperty(ns+"warp10.endpoint"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (properties.containsKey(ns+"jetty.maxqueuesize")) {
            int queuesize = Integer.parseInt(properties.getProperty(ns+"jetty.maxqueuesize"));
            queue = new BlockingArrayQueue<Runnable>(queuesize);
        }

        Thread t = new Thread(this);
        t.setDaemon(true);
        t.setName("[OpenTSDB plugin] " + host + ":" + port);
        t.start();
    }
}
