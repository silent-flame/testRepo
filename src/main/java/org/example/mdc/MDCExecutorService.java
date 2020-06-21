package org.example.mdc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.example.mdc.MDCUtils.TRACE_ID;

@RequiredArgsConstructor
@Slf4j
public class MDCExecutorService {
    private final ExecutorService executorService;

    public MDCExecutorService(int poolSize, String poolName) {
        this.executorService = Executors.newFixedThreadPool(poolSize, threadFactory(poolName));
    }

    public CompletableFuture<Void> run(String key, Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try (MDC.MDCCloseable mdcCloseable = MDC.putCloseable(TRACE_ID, key)) {
                runnable.run();
            }
        }, executorService);
    }

    public <R> CompletableFuture<R> execute(String key, Supplier<R> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try (MDC.MDCCloseable mdcCloseable = MDC.putCloseable(TRACE_ID, key)) {
                return supplier.get();
            }
        }, executorService);
    }

    private CompletableFuture<Void> run(Map<String, String> mdcContextMap, Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (mdcContextMap != null) {
                    MDC.setContextMap(mdcContextMap);
                }
                runnable.run();
            } finally {
                MDC.clear();
            }
        }, executorService);
    }

    private <R> CompletableFuture<R> execute(Map<String, String> mdcContextMap, Supplier<R> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (mdcContextMap != null) {
                    MDC.setContextMap(mdcContextMap);
                }
                return supplier.get();
            } finally {
                MDC.clear();
            }
        }, executorService);
    }

    public void destroy() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            log.error("Error while shutdown executor service");
        }
    }

    public static ThreadFactory threadFactory(String poolName) {
        return new DefaultThreadFactory(poolName);
    }

    public static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public DefaultThreadFactory(String poolName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "threadPool-" +
                    poolNumber.getAndIncrement() +
                    "-poolName-" + poolName + "-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}