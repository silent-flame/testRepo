package org.example.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.example.mdc.MDCExecutorService.threadFactory;


@RestController
@Slf4j
public class Controller {
    private final AtomicInteger counter = new AtomicInteger(0);
    /*   private ExecutorService executorService = Executors.newFixedThreadPool(10, runnable ->
               new Thread("Thread-" + counter.getAndIncrement() + "--" + Controller.class.getName()));*/
    private ExecutorService executorService = Executors.newFixedThreadPool(10, threadFactory("MyThreadPool"));

//    private final MDCExecutorService executorService = new MDCExecutorService(10, "MyThreadPool");


    @GetMapping("/url")
    public String ping() {

        /*try (MDC.MDCCloseable mdcCloseable = MDC.putCloseable(TRACE_ID, UUID.randomUUID().toString())) {
            executorService.submit(() -> log.info("GET Request"));
        } catch (Exception e) {
        }*/
        try {


//            CompletableFuture<Void> result = executorService.run(UUID.randomUUID().toString(), () -> log.info("GET Request"));
           /* CompletableFuture<Void> result =*/
            executorService.submit ( () -> {
                /*for (int i = 0; i < 20; i++) {*/
                    log.info("GET Request");
//                }
                throw new RuntimeException("Test error");
            },null);
//            Void voidResult = executeSync(result);

            System.out.println("System output");
        } catch (Exception e) {
            log.error("Error", e);
        }
        return "Hello " + counter.getAndIncrement();
    }

/*
    @PreDestroy
    public void destroy() {
        log.info("Destroy method");
        executorService.destroy();
    }*/
}