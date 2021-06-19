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
package com.github.sdankbar.qml.utility;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPool that allows submitted tasks to be preempted to the front of the
 * workqueue.
 */
public class PreemptingThreadPool {

	private final LinkedBlockingDeque<Runnable> workQueue = new LinkedBlockingDeque<>();
	private final ThreadPoolExecutor pool;

	public PreemptingThreadPool(final int threadCount) {
		pool = new ThreadPoolExecutor(threadCount, threadCount, Integer.MAX_VALUE, TimeUnit.SECONDS, workQueue);
	}

	public Future<?> submit(final Runnable r) {
		return pool.submit(r);
	}

	public <T> Future<T> submit(final Callable<T> c) {
		return pool.submit(c);
	}

	/**
	 * If the task behind the Future has not been started yet, moves it to the front
	 * of the task queue.
	 *
	 * @param f Future of the work to move to the front of the queue.
	 */
	public void preempt(final Future<?> f) {
		Objects.requireNonNull(f, "f is null");
		final boolean remove = workQueue.remove(f);
		if (remove) {
			workQueue.addFirst((RunnableFuture<?>) f);
		}
	}

	public void preempt(final Collection<Future<?>> c) {
		Objects.requireNonNull(c, "c is null");
		final boolean remove = workQueue.removeAll(c);
		if (remove) {
			for (final Future<?> f : c) {
				workQueue.addFirst((RunnableFuture<?>) f);
			}
		}
	}
}
