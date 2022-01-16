package com.github.biuabiu.executor;

import static java.util.Comparator.comparingInt;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

public enum SelectorStrategy implements BiFunction<Object, List<ExecutorWrapper>, ExecutorWrapper> {
	// @formatter:off
	IDLE {
		@Override
		public ExecutorWrapper apply(Object t, List<ExecutorWrapper> u) {
			return u.stream()
					.min(comparingInt(ExecutorWrapper::idle))
					.orElseThrow(IllegalStateException::new);
		}
	},
	// @formatter:on
	MOD {
		@Override
		public ExecutorWrapper apply(Object t, List<ExecutorWrapper> u) {
			return u.get(Math.abs(t.hashCode() % (u.size())));
		}
	},
	MIXED {
		@Override
		public ExecutorWrapper apply(Object t, List<ExecutorWrapper> u) {
			ExecutorWrapper wrapper = MOD.apply(t, u);
			ExecutorAdapter v = wrapper.getExecutor();
			BlockingQueue<Runnable> queue = v.getQueue();
			int size = queue.size();
			int remainingCapacity = queue.remainingCapacity();
			int impreciseCapacity = remainingCapacity + size;
			
			if (size >= impreciseCapacity * 0.75) {
				wrapper = IDLE.apply(t, u);
			}
			return wrapper;
		}
	},
	RANDOM {
		@Override
		public ExecutorWrapper apply(Object t, List<ExecutorWrapper> u) {
			return u.get(ThreadLocalRandom.current().nextInt(u.size()));
		}
	},;
	
}