package com.github.biuabiu.executor;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

import lombok.Builder;

@Builder
public class ExecutorGroupBuilder<K> {
	
	class DefaultSelector implements Selector<K, ExecutorAdapter> {
		private final Map<K, ExecutorWrapper> mapping = new ConcurrentHashMap<>();
		
		@Override
		public ExecutorAdapter select(K key) {
			ExecutorWrapper wrap = mapping.computeIfAbsent(key, k -> strategy.apply(k, groups.get()));
			wrap.getRunCount().incrementAndGet();
			if (cleanKey) mapping.remove(key, wrap);
			return wrap.getExecutor();
		}
	}
	
	private final AtomicReference<List<ExecutorWrapper>> groups = new AtomicReference<>();
	
	private int count;
	private int size;
	private boolean cleanKey;
	private boolean useTTL;
	private ThreadFactory threadFactory;
	private RejectedExecutionHandler handler;
	private BiFunction<Object, List<ExecutorWrapper>, ExecutorWrapper> strategy;
	
	public ExecutorGroup<K> init() {
		return new DefaultExecutorGroup<>(this::smartInit);
	}
	
	private ExecutorAdapter executor(int seq) {
		ThreadFactory threadFactory = this.threadFactory;
		if (Objects.isNull(threadFactory)) {
			threadFactory = Executors.defaultThreadFactory();
		}
		LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(size);
		return new ExecutorAdapter(1, 1, 0L, MILLISECONDS, workQueue, threadFactory, handler, useTTL, seq);
	}
	
	private Selector<K, ExecutorAdapter> smartInit() {
		if (count <= 0) {
			this.count = 1 << 1;
		}
		if (size <= 0) {
			this.size = 1 << 10;
		}
		if (Objects.isNull(handler)) {
			this.handler = new ExecutorAdapter.AbortPolicy();
		}
		if (Objects.isNull(strategy)) {
			this.strategy = SelectorStrategy.IDLE;
		}
		// @formatter:off
		List<ExecutorWrapper> groups = range(0, count)
				.mapToObj(this::executor)
				.map(ExecutorWrapper::new)
				.collect(toList());
		// @formatter:on
		this.groups.set(Collections.unmodifiableList(groups));
		return new DefaultSelector();
	}
}
