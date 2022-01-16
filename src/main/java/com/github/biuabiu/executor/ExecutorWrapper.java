package com.github.biuabiu.executor;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;

@Getter
public class ExecutorWrapper {
	
	private ExecutorAdapter executor;
	
	private AtomicInteger runCount = new AtomicInteger();
	
	public ExecutorWrapper(ExecutorAdapter executor) {
		this.executor = executor;
	}
	
	public int idle() {
		return runCount.get();
	}
}
