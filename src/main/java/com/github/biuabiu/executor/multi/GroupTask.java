package com.github.biuabiu.executor.multi;

import java.util.function.Supplier;

public class GroupTask<K> {
	
	public K k;
	
	public Thread thread;
	
	Supplier<?> supplier;
	
	public <V> GroupTask(K k, Supplier<V> supplier) {
		super();
		this.k = k;
		this.thread = Thread.currentThread();
		this.supplier = supplier;
	}
	
}
