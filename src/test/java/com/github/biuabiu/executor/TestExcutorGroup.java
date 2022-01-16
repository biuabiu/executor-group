package com.github.biuabiu.executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestExcutorGroup {
	
	@Data
	static class TestSomething {
		private int id;
		public String dothing;
	}
	
	ExecutorGroup<Integer> executorImpl;
	
	@Before
	public void init() {
		// @formatter:off
		executorImpl = ExecutorGroupBuilder
				.<Integer>builder()
				.strategy(SelectorStrategy.MOD )
				.build()
				.init();
		// @formatter:on
	}
	
	@Test
	public void testExecute() {
		
		AtomicReference<String> threadName1 = new AtomicReference<>();
		AtomicReference<String> threadName2 = new AtomicReference<>();
		int count = 10;
		CountDownLatch countDownLatch = new CountDownLatch(count);
		for (int i = 0; i < count; i++) {
			TestSomething test = new TestSomething();
			test.setId(ThreadLocalRandom.current().nextInt(1, 3));
			test.setDothing(i + "");
			
			executorImpl.execute(test.getId(), () -> {
				countDownLatch.countDown();
				
				if (test.getId() % 2 == 0) {
					threadName1.set(Thread.currentThread().getName());
					log.info("{}", test);
				} else {
					threadName2.set(Thread.currentThread().getName());
					log.error("{}", test);
				}
			});
		}
		try {
			countDownLatch.await();
			assertNotEquals(threadName1.get(), threadName2.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSubmit() {
		
		for (int i = 0; i < 10; i++) {
			TestSomething test = new TestSomething();
			test.setId(ThreadLocalRandom.current().nextInt(1, 3));
			test.setDothing(i + "");
			
			CompletableFuture<TestSomething> submit = executorImpl.submit(test.getId(), () -> {
				log.info("i submit = {}", test);
				return test;
			});
			
			assertEquals(test, submit.join());
		}
	}
	
}
