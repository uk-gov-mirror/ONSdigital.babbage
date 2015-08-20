package com.github.onsdigital.babbage.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by bren on 23/07/15.
 */
public class ThreadContextTest {

    private final int TEST_THREAD_COUNT = 10;

    @Test
    public void test() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(TEST_THREAD_COUNT);
        List<Future> taskList = submitTasks(executor);
        executor.shutdown();
        executor.awaitTermination(1l, TimeUnit.SECONDS);
        getResults(taskList);
    }

    private List<Future> submitTasks(ExecutorService executor) {
        List<Future> taskList = new ArrayList<>();
        for (int i = 0; i < TEST_THREAD_COUNT; i++) {
            Runnable worker = new TestThread(new Integer(i));
            taskList.add(executor.submit(worker));
        }
        return taskList;
    }

    private void getResults(List<Future> taskList) throws InterruptedException, ExecutionException {
        for (Iterator<Future> iterator = taskList.iterator(); iterator.hasNext(); ) {
            Future next =  iterator.next();
            next.get();
        }
    }

    private class TestThread implements Runnable {

        private final String TEST_KEY = "testData";
        private final Integer id;

        public TestThread(Integer id) {
            this.id = id;
        }

        @Override
        public void run() {
            ThreadContext.addData(TEST_KEY, id);
            Object data = ThreadContext.getData(TEST_KEY);
            Assert.assertEquals(id, data);
        }
    }

}
