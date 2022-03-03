package com.github.biuabiu.executor.multi;

import java.util.function.Supplier;

public class GroupTask<K> {
	
	public K k;
	
	public Integer key;
	
	Supplier<?> supplier;
	
	public <V> GroupTask(Integer key, K k, Supplier<V> supplier) {
		super();
		assert key != null;
		this.key = key;
		this.k = k;
		this.supplier = supplier;
	}
	
}
