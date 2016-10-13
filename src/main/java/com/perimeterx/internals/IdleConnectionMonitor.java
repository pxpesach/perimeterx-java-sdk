package com.perimeterx.internals;

import org.apache.http.conn.HttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by shikloshi on 13/10/2016.
 */
public class IdleConnectionMonitor implements Runnable {

    private final HttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public IdleConnectionMonitor(HttpClientConnectionManager connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    System.err.println("Going to check for closed connections");
                    wait(5000);
                    // close expired connection
                    connMgr.closeExpiredConnections();
                    connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException e) {
            // terminated
        }
    }

    public void shutdown() {
        System.err.println("Shutting down monitor");
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
