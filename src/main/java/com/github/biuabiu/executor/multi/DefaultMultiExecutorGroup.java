package com.github.biuabiu.executor.multi;

import java.util.concurrent.CompletableFuture;

import com.github.biuabiu.executor.ExecutorGroup;
import com.github.biuabiu.executor.Selector;

public class DefaultMultiExecutorGroup<K> implements MultiExecutorGroup<GroupTask<K>> {
	
	private Selector<GroupTask<K>, ExecutorGroup<K>> selector = null;
	
	public DefaultMultiExecutorGroup(Selector<GroupTask<K>, ExecutorGroup<K>> selector) {
		super();
		this.selector = selector;
	}
	
	@Override
	public void execute(GroupTask<K> key) {
		this.submit(key);
	}
	
	@Override
	public CompletableFuture<?> submit(GroupTask<K> key) {
		return selector.select(key).submit(key.k, key.supplier);
	}
	
}
