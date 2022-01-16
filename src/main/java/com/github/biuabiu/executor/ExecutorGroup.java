package com.github.biuabiu.executor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface ExecutorGroup<K> {
	
	void execute(K key, Runnable command);
	
	<V> CompletableFuture<V> submit(K key, CompletableFuture<V> supplier);
	
	<V> CompletableFuture<V> submit(K key, Supplier<V> supplier);
}
