package com.github.biuabiu.executor.multi;

import static java.util.stream.IntStream.range;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.biuabiu.executor.ExecutorGroup;
import com.github.biuabiu.executor.ExecutorGroupBuilder;
import com.github.biuabiu.executor.Selector;

import lombok.Builder;

@Builder
public class MultiExecutorGroupBuilder<K> {
	
	class DefaultMultiExecutorGroupSelector implements Selector<GroupTask<K>, ExecutorGroup<K>> {
		private List<ExecutorGroup<K>> groups;
		
		public DefaultMultiExecutorGroupSelector(List<ExecutorGroup<K>> groups) {
			super();
			this.groups = groups;
		}
		
		@Override
		public ExecutorGroup<K> select(GroupTask<K> k) {
			return groups.get(Math.abs(System.identityHashCode(k.thread) % (groups.size())));
		}
	}
	
	private int poolCount;
	
	private Function<Integer, ExecutorGroup<K>> builder;
	
	public MultiExecutorGroup<GroupTask<K>> init() {
		if (poolCount <= 0) {
			this.poolCount = 1;
		}
		if (Objects.isNull(builder)) {
			builder = i -> ExecutorGroupBuilder.<K>builder().build().init();
		}
		// @formatter:off
		List<ExecutorGroup<K>> groups = range(0, poolCount)
				.mapToObj(builder::apply)
				.collect(Collectors.toList());
		// @formatter:on
		return new DefaultMultiExecutorGroup<>(new DefaultMultiExecutorGroupSelector(groups));
	}
	
}
