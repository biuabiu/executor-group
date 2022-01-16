package com.github.biuabiu.executor;

public interface Selector<K, V> {
	
	V select(K k);
}
