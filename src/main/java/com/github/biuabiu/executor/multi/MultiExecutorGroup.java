package com.github.biuabiu.executor.multi;

import java.util.concurrent.CompletableFuture;

public interface MultiExecutorGroup<K> {
	
	void execute(K key);
	
	CompletableFuture<?> submit(K key);
	
}