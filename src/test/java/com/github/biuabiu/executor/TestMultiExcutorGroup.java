package com.github.biuabiu.executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import com.github.biuabiu.executor.multi.GroupTask;
import com.github.biuabiu.executor.multi.MultiExecutorGroup;
import com.github.biuabiu.executor.multi.MultiExecutorGroupBuilder;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestMultiExcutorGroup {
	
	@Data
	static class TestSomething {
		private int id;
		public String dothing;
	}
	
	MultiExecutorGroup<GroupTask<Integer>> executorImpl;
	
	@Before
	public void init() {
		// @formatter:off
		Function<Integer, ExecutorGroup<Integer>> builder = t -> ExecutorGroupBuilder.<Integer>builder()
				.count(3)
				.strategy(SelectorStrategy.MOD)
				.build()
				.init();
		
		executorImpl = MultiExecutorGroupBuilder.<Integer>builder()
				.builder(builder)
				.build()
				.init();
		// @formatter:on
	}
	
	@Test
	@SneakyThrows
	public void testPoolRunnable() {
		// 模拟线程池提交任务
		ExecutorService pool = Executors.newFixedThreadPool(2);
		int count = 5;
		CountDownLatch latch = new CountDownLatch(count);
		
		for (int i = 0; i < count; i++) {
			pool.execute(() -> {
				this.testExecute();
				latch.countDown();
			});
		}
		latch.await();
	}
	
	@Test
	public void testExecute() {
		
		AtomicReference<String> threadName1 = new AtomicReference<>();
		AtomicReference<String> threadName2 = new AtomicReference<>();
		AtomicReference<String> threadName3 = new AtomicReference<>();
		int count = 10;
		CountDownLatch countDownLatch = new CountDownLatch(count);
		for (int i = 0; i < count; i++) {
			TestSomething test = new TestSomething();
			test.setId(ThreadLocalRandom.current().nextInt(1, 4));
			test.setDothing(i + "");
			
			executorImpl.execute(new GroupTask<>(Thread.currentThread().hashCode(), test.getId(), () -> {
				countDownLatch.countDown();
				if (test.getId() == 01) {
					threadName1.set(Thread.currentThread().getName());
					log.info("{}", test);
				} else if (test.getId() == 2) {
					threadName2.set(Thread.currentThread().getName());
					log.error("{}", test);
				} else if (test.getId() == 3) {
					threadName3.set(Thread.currentThread().getName());
					log.error("{}", test);
				}
				return null;
			}));
		}
		try {
			countDownLatch.await();
			assertNotEquals(threadName1.get(), threadName2.get());
			assertNotEquals(threadName3.get(), threadName2.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSubmit() {
		
		for (int i = 0; i < 10; i++) {
			TestSomething test = new TestSomething();
			test.setId(ThreadLocalRandom.current().nextInt(1, 5));
			test.setDothing(i + "");
			CompletableFuture<?> submit = executorImpl.submit(new GroupTask<Integer>(Thread.currentThread().hashCode(),test.getId(), () -> {
				log.info("i submit,id= {},dothing={}", test.getId(), test.getDothing());
				return test;
			}));
			assertEquals(test, submit.join());
		}
	}
	
}
