package com.readutf.inari.core.utils;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class MultiTaskUtil {

    public static <T> List<T> collectAll(ExecutorService service, List<Supplier<T>> tasks) {

        try {
            List<CompletableFuture<T>> futures = tasks.stream().map(task -> CompletableFuture.supplyAsync(task, service)).toList();
            CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            all.get();

            return futures.stream().map(tCompletableFuture -> tCompletableFuture.getNow(null)).toList();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }


    }

}
