package org.example.concurrent;


import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class CompletableFutures {
    public static <T> CompletableFuture<T> exception(Throwable t) {
        CompletableFuture<T> completableFuture = new CompletableFuture<T>();
        completableFuture.completeExceptionally(t);
        return completableFuture;
    }

    @SneakyThrows
    public static <R> R executeSync(CompletableFuture<R> completableFuture) {
        try {
            return completableFuture.join();
        } catch (CompletionException e) {
            throw retrieveException(e);
        }
    }

    public static Throwable retrieveException(CompletionException e) {
        Throwable error = e;
        while (error instanceof CompletionException) {
            error = error.getCause();
        }
        return error;
    }

}