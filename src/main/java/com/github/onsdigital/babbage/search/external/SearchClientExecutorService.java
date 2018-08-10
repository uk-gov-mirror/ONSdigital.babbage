package com.github.onsdigital.babbage.search.external;

import com.github.onsdigital.babbage.configuration.Configuration;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SearchClientExecutorService {

    private ExecutorService executorService;

    private static SearchClientExecutorService INSTANCE = new SearchClientExecutorService();

    public static SearchClientExecutorService getInstance() {
        return INSTANCE;
    }

    private SearchClientExecutorService() {
        executorService = Executors.newFixedThreadPool(Configuration.SEARCH_SERVICE.SEARCH_NUM_EXECUTORS);
        Runtime.getRuntime().addShutdownHook(new Shutdown(this.executorService));
    }

    public <T> Future<T> submit(Callable<T> task) {
        return this.executorService.submit(task);
    }

    static class Shutdown extends Thread {

        private ExecutorService executorService;

        public Shutdown(ExecutorService executorService) {
            this.executorService = executorService;
        }

        @Override
        public void run() {
            executorService.shutdown();
        }

    }

}
