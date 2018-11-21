package com.github.onsdigital.babbage.search.external;

import com.github.onsdigital.babbage.search.external.requests.base.SearchClosable;
import com.github.onsdigital.babbage.search.external.requests.base.ShutdownThread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;

public class SearchClientExecutorService implements SearchClosable {

    private final ExecutorService executorService;

    private static SearchClientExecutorService INSTANCE;

    public static SearchClientExecutorService getInstance() {
        if (INSTANCE == null) {
            synchronized (SearchClientExecutorService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SearchClientExecutorService();
                    Runtime.getRuntime().addShutdownHook(new ShutdownThread(INSTANCE));
                }
            }
        }
        return INSTANCE;
    }

    private SearchClientExecutorService() {
        executorService = Executors.newFixedThreadPool(appConfig().externalSearch().executorCount());
    }

    public <T> Future<T> submit(Callable<T> task) {
        return this.executorService.submit(task);
    }

    @Override
    public void close() {
        this.executorService.shutdown();
    }

}