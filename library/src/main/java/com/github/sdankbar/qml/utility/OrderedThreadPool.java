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

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderedThreadPool<T> {

	private static final Logger logger = LoggerFactory.getLogger(OrderedThreadPool.class);

	private enum State {
		RUNNING, COMPLETE, ERROR;
	}

	private static final class Job<T> {
		private volatile State jobState = State.RUNNING;
		private volatile T result;
		private final Consumer<T> consumer;

		public Job(final Consumer<T> consumer) {
			this.consumer = consumer;
		}

		public boolean isFinished() {
			return jobState == State.COMPLETE || jobState == State.ERROR;
		}
	}

	private final ExecutorService parallelExecutor;
	private final ExecutorService sequentialExecutor;

	private final Queue<Job<T>> futureQueue = new ConcurrentLinkedQueue<>();

	public OrderedThreadPool(final ExecutorService parallelExecutor, final ExecutorService sequentialExecutor) {
		this.parallelExecutor = Objects.requireNonNull(parallelExecutor, "parallelExecutor is null");
		this.sequentialExecutor = Objects.requireNonNull(sequentialExecutor, "sequentialExecutor is null");
	}

	public void submit(final Supplier<T> parallelProcessing, final Consumer<T> sequentialProcessing) {
		final Job<T> j = new Job<>(sequentialProcessing);
		futureQueue.add(j);

		parallelExecutor.execute(() -> {
			try {
				j.result = parallelProcessing.get();
				j.jobState = State.COMPLETE;
			} catch (final Exception e) {
				logger.warn("Exception processing job", e);
				j.jobState = State.ERROR;
			}

			sequentialExecutor.submit(() -> checkQueue());
		});
	}

	private void checkQueue() {
		final Job<T> front = futureQueue.peek();
		if (front != null) {
			if (front.jobState == State.COMPLETE) {
				futureQueue.poll();
				front.consumer.accept(front.result);
				checkIfAdditionalScheduleNeeded();
			} else if (front.jobState == State.ERROR) {
				futureQueue.poll();
				checkIfAdditionalScheduleNeeded();
			} else {
				// Head job not complete yet.
			}
		}
	}

	private void checkIfAdditionalScheduleNeeded() {
		// Check if the next item on the queue is already finished and if it is,
		// schedule to work it.
		final Job<T> front2 = futureQueue.peek();
		if (front2 != null && front2.isFinished()) {
			sequentialExecutor.submit(() -> checkQueue());
		}
	}

}
