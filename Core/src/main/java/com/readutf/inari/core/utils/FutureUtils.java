package com.readutf.inari.core.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class FutureUtils {

    public static <T> CompletableFuture<List<T>> completeAll(Collection<CompletableFuture<T>> futures) {

        AtomicInteger completedTasks = new AtomicInteger(0);

        CompletableFuture<List<T>> allDone = new CompletableFuture<>();

        for (CompletableFuture<T> future : futures) {
            future.thenAccept(t -> {
                synchronized (completedTasks) {
                    if(completedTasks.incrementAndGet() == futures.size()) {
                        allDone.complete(futures.stream().map(CompletableFuture::join).toList());
                    }
                }
            });
        }

        return allDone;
    }

}
