package com.github.onsdigital.babbage.util.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static com.github.onsdigital.babbage.logging.LogEvent.logEvent;

/**
 * Class for building Closable Http clients to be used by Babbage
 */
public class BabbageHttpClient implements AutoCloseable {

    protected final CloseableHttpClient httpClient;
    protected final URI host;
    private final PoolingHttpClientConnectionManager connectionManager;
    private final IdleConnectionMonitorThread monitorThread;

    public BabbageHttpClient(String host, ClientConfiguration configuration) {
        this.host = resolveHostUri(host);
        this.connectionManager = new PoolingHttpClientConnectionManager();
        HttpClientBuilder customClientBuilder = HttpClients.custom();
        configure(customClientBuilder, configuration);
        this.httpClient = customClientBuilder.setConnectionManager(connectionManager)
                .build();

        logEvent()
                .info("Starting monitor thread");
        this.monitorThread = new IdleConnectionMonitorThread(connectionManager);
        this.monitorThread.start();
        Runtime.getRuntime().addShutdownHook(new BabbageHttpClient.ShutdownHook());
    }

    private void configure(HttpClientBuilder customClientBuilder, ClientConfiguration configuration) {
        Integer connectionNumber = configuration.getMaxTotalConnection();
        if (connectionNumber != null) {
            connectionManager.setMaxTotal(connectionNumber);
            connectionManager.setDefaultMaxPerRoute(connectionNumber);
        }
        if (configuration.isDisableRedirectHandling()) {
            customClientBuilder.disableRedirectHandling();
        }
    }

    private URI resolveHostUri(String host) {
        URI givenHost = URI.create(host);
        URIBuilder builder = new URIBuilder();
        if (StringUtils.startsWithIgnoreCase(host, "http")) {
            builder.setScheme(givenHost.getScheme());
            builder.setHost(givenHost.getHost());
            builder.setPort(givenHost.getPort());
            builder.setPath(givenHost.getPath());
            builder.setUserInfo(givenHost.getUserInfo());
        } else {
            builder.setScheme("http");
            builder.setHost(host);
        }
        try {
            return builder.build();
        } catch (URISyntaxException e) {
            logEvent(e)
                    .error("Error building uri");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        logEvent()
                .host(host.getHost())
                .info("Shutting down connection pool");
        httpClient.close();
        logEvent()
                .host(host.getHost())
                .info("Successfully shut down connection pool");
        monitorThread.shutdown();
    }

    private class IdleConnectionMonitorThread extends Thread {

        private boolean shutdown;
        private HttpClientConnectionManager connMgr;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
        }

        @Override
        public void run() {
            logEvent()
                    .host(host.getHost())
                    .info("Running connection pool monitor");
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        // Close expired connections every 5 seconds
                        logEvent()
                                .info("Closing expired connections");
                        connMgr.closeExpiredConnections();
                        // Close connections
                        // that have been idle longer than 30 sec
                        connMgr.closeIdleConnections(60, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                logEvent(ex)
                        .host(host.getHost())
                        .error("Connection pool monitor failed");
            }
        }

        public void shutdown() {
            logEvent()
                    .host(host.getHost())
                    .info("Shutting down connection pool monitor");
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }

    }

    private class ShutdownHook extends Thread {
        @Override
        public void run() {
            try {
                logEvent()
                        .host(host.getHost())
                        .info("Closing http client");
                if (httpClient != null) {
                    close();
                }
            } catch (Exception e) {
                logEvent(e)
                        .host(host.getHost())
                        .error("Falied shutting down http client");
            }
        }
    }

}
