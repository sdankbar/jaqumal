/**
 * The MIT License
 * Copyright © 2020 Stephen Dankbar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.sdankbar.qml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.eventing.NullEventProcessor;

/**
 * Tests the JQMLScheduledExecutorService class.
 */
public class JQMLScheduledExecutorServiceTest {

	/**
	 * Test the execute method.
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void testExecute() throws InterruptedException {
		final String[] args = new String[0];
		final JQMLApplication<NullEventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final CountDownLatch latch = new CountDownLatch(1);
		app.getQMLThreadExecutor().execute(() -> {
			latch.countDown();
			app.quitApp();
		});

		app.execute();

		latch.await(5, TimeUnit.SECONDS);
		// Success if no exception thrown
	}

	/**
	 * Test the invokeAll method.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void testInvokeAll() throws InterruptedException, ExecutionException {
		final String[] args = new String[0];
		final JQMLApplication<NullEventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final CountDownLatch latch = new CountDownLatch(2);

		final AtomicReference<List<Future<Integer>>> fAll = new AtomicReference<>();
		Executors.newSingleThreadExecutor().execute(() -> {
			try {
				fAll.set(app.getQMLThreadExecutor().invokeAll(Arrays.asList(() -> {
					latch.countDown();
					return Integer.valueOf(1);
				}, () -> {
					latch.countDown();
					while (latch.getCount() != 0) {
						// Busy spin
					}
					app.quitApp();
					return Integer.valueOf(2);
				})));
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		});

		app.execute();

		Thread.sleep(100);

		assertEquals(Integer.valueOf(1), fAll.get().get(0).get());
		assertEquals(Integer.valueOf(2), fAll.get().get(1).get());
	}

	/**
	 * Test the invokeAny method.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void testInvokeAny() throws InterruptedException, ExecutionException {
		final String[] args = new String[0];
		final JQMLApplication<NullEventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());

		final AtomicBoolean secondCalled = new AtomicBoolean();
		final AtomicReference<Integer> fAll = new AtomicReference<>();
		Executors.newSingleThreadExecutor().execute(() -> {
			try {
				fAll.set(app.getQMLThreadExecutor().invokeAny(Arrays.asList(() -> {
					Thread.sleep(100);
					app.quitApp();
					return Integer.valueOf(1);
				}, () -> {
					Thread.sleep(5000);
					secondCalled.set(true);
					return Integer.valueOf(2);
				})));
			} catch (final InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});

		app.execute();

		assertEquals(Integer.valueOf(1), fAll.get());
		assertFalse(secondCalled.get());
	}

	/**
	 * Test the schedule method.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void testScheduleCallable() throws InterruptedException, ExecutionException {
		final String[] args = new String[0];
		final JQMLApplication<NullEventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final CountDownLatch latch = new CountDownLatch(1);
		final Future<Integer> f = app.getQMLThreadExecutor().schedule(() -> {
			latch.countDown();
			app.quitApp();

			return Integer.valueOf(1);
		}, 50, TimeUnit.MILLISECONDS);

		final long start = System.currentTimeMillis();
		app.execute();

		assertEquals(Integer.valueOf(1), f.get());
		final long delta = (System.currentTimeMillis() - start);
		assertTrue("delta=" + delta + " not >= 50", delta >= 50);
	}

	/**
	 * Test the schedule method.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void testScheduleFixedDelay() throws InterruptedException, ExecutionException {
		final String[] args = new String[0];
		final JQMLApplication<NullEventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final CountDownLatch latch = new CountDownLatch(3);
		app.getQMLThreadExecutor().scheduleWithFixedDelay(() -> {
			latch.countDown();
			if (latch.getCount() == 0) {
				app.quitApp();
			}
		}, 50, 50, TimeUnit.MILLISECONDS);

		final long start = System.currentTimeMillis();
		app.execute();

		assertEquals(0, latch.getCount());
		final long delta = System.currentTimeMillis() - start;
		assertTrue("delta=" + delta + " not >= 150", delta >= 150);
	}

	/**
	 * Test the schedule method.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void testScheduleFixedRate() throws InterruptedException, ExecutionException {
		final String[] args = new String[0];
		final JQMLApplication<NullEventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final CountDownLatch latch = new CountDownLatch(3);
		app.getQMLThreadExecutor().scheduleAtFixedRate(() -> {
			latch.countDown();
			if (latch.getCount() == 0) {
				app.quitApp();
			}
		}, 50, 50, TimeUnit.MILLISECONDS);

		final long start = System.currentTimeMillis();
		app.execute();

		assertEquals(0, latch.getCount());
		final long delta = System.currentTimeMillis() - start;
		assertTrue("delta=" + delta + " not >= 150", delta >= 150);
	}

	/**
	 * Test the schedule method.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void testScheduleRunnable() throws InterruptedException, ExecutionException {
		final String[] args = new String[0];
		final JQMLApplication<NullEventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final CountDownLatch latch = new CountDownLatch(1);
		final Future<?> f = app.getQMLThreadExecutor().schedule(() -> {
			latch.countDown();
			app.quitApp();
		}, 50, TimeUnit.MILLISECONDS);

		final long start = System.currentTimeMillis();
		app.execute();

		Thread.sleep(100);

		assertEquals(null, f.get());
		assertTrue((System.currentTimeMillis() - start) >= 50);
	}

	/**
	 * Test the submit method.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void testSubmit() throws InterruptedException, ExecutionException {
		final String[] args = new String[0];
		final JQMLApplication<NullEventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final CountDownLatch latch = new CountDownLatch(1);
		final Future<Integer> f = app.getQMLThreadExecutor().submit(() -> {
			latch.countDown();
			app.quitApp();

			return Integer.valueOf(1);
		});

		app.execute();

		assertEquals(Integer.valueOf(1), f.get());
	}

}
