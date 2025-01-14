package com.github.biuabiu.executor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class DefaultExecutorGroup<K> implements ExecutorGroup<K> {
	
	private Selector<K, ExecutorAdapter> selector = null;
	
	public DefaultExecutorGroup(Supplier<Selector<K, ExecutorAdapter>> selector) {
		this.selector = selector.get();
	}
	
	@Override
	public void execute(K key, Runnable command) {
		this.selector.select(key).execute(command);
	}
	
	@Override
	public <V> CompletableFuture<V> submit(K key, CompletableFuture<V> supplier) {
		return this.submit(key, supplier);
	}
	
	@Override
	public <V> CompletableFuture<V> submit(K key, Supplier<V> supplier) {
		return CompletableFuture.supplyAsync(supplier, this.selector.select(key));
	}
	
}
