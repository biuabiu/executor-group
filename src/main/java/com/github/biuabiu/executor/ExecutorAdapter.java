package com.github.biuabiu.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.ttl.TtlRunnable;

public class ExecutorAdapter extends ThreadPoolExecutor {
	
	private boolean useTTL;
	private int seq;
	
	ExecutorAdapter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler,
			boolean useTTL, int seq) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		this.useTTL = useTTL;
		this.seq = seq;
		
	}
	
	@Override
	public void execute(Runnable command) {
		super.execute(useTTL ? TtlRunnable.get(command) : command);
	}
	
	public String name() {
		return "ExecutorAdapter-" + seq;
	}
	
}
