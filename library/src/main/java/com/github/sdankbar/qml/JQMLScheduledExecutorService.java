/**
 * The MIT License
 * Copyright © 2019 Stephen Dankbar
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.cpp.ApiInstance;
import com.github.sdankbar.qml.cpp.jni.ApplicationFunctions;
import com.github.sdankbar.qml.cpp.jni.interfaces.InvokeCallback;

/**
 * The JQMLSheduledExecutorService allows for the scheduling of tasks to run on
 * the Qt Thread. Tasks will only be run once JQMLApplication.execute() has been
 * called.
 */
public class JQMLScheduledExecutorService implements ScheduledExecutorService {

	private static class ImmediateTask<T> implements InvokeCallback {

		private final AtomicBoolean isRunning;
		private final Set<ImmediateTask<?>> taskSet;
		private final FutureTask<T> future;

		private final BlockingQueue<T> invokeAnyQueue;

		public ImmediateTask(final AtomicBoolean isRunning, final Set<ImmediateTask<?>> taskSet,
				final FutureTask<T> future, final BlockingQueue<T> invokeAnyQueue) {
			this.isRunning = isRunning;
			this.taskSet = taskSet;
			taskSet.add(this);
			this.future = future;

			this.invokeAnyQueue = invokeAnyQueue;
		}

		@Override
		public void invoke() {
			// TODO check if there is a race condition with invokeAny() then immediately
			// call shutdown().
			if (isRunning.get()) {
				T result = null;
				if (!future.isCancelled()) {
					try {
						future.run();
						result = future.get();
					} catch (final ExecutionException | InterruptedException e) {
						log.error("Error executing Callable", e);
					} catch (final CancellationException e) {
						log.debug("Cancelled Callable", e);
					}
				}

				taskSet.remove(this);

				if (invokeAnyQueue != null && result != null) {
					invokeAnyQueue.offer(result);
				}
			}
		}
	}

	private static class RunnableWrapper<T> implements Callable<T> {

		private final Runnable runnable;
		private final T retValue;

		public RunnableWrapper(final Runnable r, final T retValue) {
			this.runnable = r;
			this.retValue = retValue;
		}

		@Override
		public T call() throws Exception {
			runnable.run();
			return retValue;
		}

	}

	private static final Logger log = LoggerFactory.getLogger(JQMLScheduledExecutorService.class);

	private final Set<ImmediateTask<?>> pendingCallbacks = ConcurrentHashMap.newKeySet();

	private final AtomicBoolean isRunning = new AtomicBoolean(true);
	private final ScheduledExecutorService delayedExecutor = Executors.newSingleThreadScheduledExecutor();

	@Override
	public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
		final long waitTimeNano = unit.toNanos(timeout);
		final long startTime = System.nanoTime();
		long nanosRemaining = waitTimeNano;

		boolean success = true;
		for (final ImmediateTask<?> t : pendingCallbacks) {
			try {
				t.future.get(nanosRemaining, TimeUnit.NANOSECONDS);
			} catch (ExecutionException | TimeoutException e) {
				log.error("Exception awaiting termination", e);
			} catch (final CancellationException e) {
				log.debug("Exception awaiting termination", e);
			}

			nanosRemaining = waitTimeNano - (System.nanoTime() - startTime);
			if (nanosRemaining <= 0) {
				success = false;
				break;
			}
		}
		success = success && delayedExecutor.awaitTermination(nanosRemaining, TimeUnit.NANOSECONDS);

		return success;
	}

	@Override
	public void execute(final Runnable command) {
		throwIfNotRunning();
		submit(command);
	}

	@Override
	public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
		Objects.requireNonNull(tasks, "tasks is null");
		throwIfNotRunning();

		final List<Future<T>> l = tasks.stream().map(t -> submit(t)).collect(Collectors.toList());
		for (final Future<T> f : l) {
			try {
				f.get();
			} catch (final ExecutionException e) {
				log.error("Error executing Callable", e);
			}
		}

		return l;
	}

	@Override
	public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout,
			final TimeUnit unit) throws InterruptedException {
		Objects.requireNonNull(tasks, "tasks is null");
		throwIfNotRunning();

		final List<Future<T>> l = tasks.stream().map(t -> submit(t)).collect(Collectors.toList());

		final long waitTimeNano = unit.toNanos(timeout);
		final long startTime = System.nanoTime();
		long nanosRemaining = waitTimeNano;
		for (final Future<T> f : l) {
			try {
				f.get(nanosRemaining, TimeUnit.NANOSECONDS);
			} catch (final ExecutionException e) {
				log.error("Error executing Callable", e);
			} catch (final TimeoutException e) {
				log.error("Error executing awiting for Callable to complete", e);
				break;
			}

			nanosRemaining = waitTimeNano - (System.nanoTime() - startTime);
			if (nanosRemaining <= 0) {
				break;
			}
		}

		return l;
	}

	@Override
	public <T> T invokeAny(final Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		Objects.requireNonNull(tasks, "tasks is null");
		throwIfNotRunning();
		final BlockingQueue<T> invokeAnyQueue = new ArrayBlockingQueue<>(tasks.size());
		final List<Future<?>> allFutures = tasks.stream().map(t -> submit(t, invokeAnyQueue))
				.collect(Collectors.toList());

		final T result = invokeAnyQueue.take();
		allFutures.forEach(f -> f.cancel(true));
		return result;
	}

	@Override
	public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		Objects.requireNonNull(tasks, "tasks is null");
		throwIfNotRunning();
		final BlockingQueue<T> invokeAnyQueue = new ArrayBlockingQueue<>(tasks.size());
		final List<Future<?>> allFutures = tasks.stream().map(t -> submit(t, invokeAnyQueue))
				.collect(Collectors.toList());

		final T result = invokeAnyQueue.poll(timeout, unit);
		allFutures.forEach(f -> f.cancel(true));
		return result;
	}

	@Override
	public boolean isShutdown() {
		return !isRunning.get();
	}

	@Override
	public boolean isTerminated() {
		return isShutdown() && pendingCallbacks.isEmpty();
	}

	@Override
	public <V> ScheduledFuture<V> schedule(final Callable<V> callable, final long delay, final TimeUnit unit) {
		Objects.requireNonNull(callable, "callable is null");
		throwIfNotRunning();
		return delayedExecutor.schedule(() -> {
			return submit(callable).get();
		}, delay, unit);
	}

	@Override
	public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
		return schedule(new RunnableWrapper<>(command, null), delay, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay, final long period,
			final TimeUnit unit) {
		Objects.requireNonNull(command, "command is null");
		throwIfNotRunning();
		return delayedExecutor.scheduleAtFixedRate(() -> {
			try {
				submit(command).get();
			} catch (InterruptedException | ExecutionException e) {
				log.error("Exception running scheduled task", e);
			}
		}, initialDelay, period, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, final long initialDelay, final long delay,
			final TimeUnit unit) {
		Objects.requireNonNull(command, "command is null");
		throwIfNotRunning();
		return delayedExecutor.scheduleWithFixedDelay(() -> {
			try {
				submit(command).get();
			} catch (InterruptedException | ExecutionException e) {
				log.error("Exception running scheduled task", e);
			}
		}, initialDelay, delay, unit);
	}

	@Override
	public void shutdown() {
		isRunning.set(false);
		delayedExecutor.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		isRunning.set(false);
		// TODO Improve the accuracy of this list since completed tasks may be contained
		final List<Runnable> list = new ArrayList<>(
				pendingCallbacks.stream().map(a -> a.future).collect(Collectors.toList()));
		list.addAll(delayedExecutor.shutdownNow());
		return list;
	}

	@Override
	public <T> Future<T> submit(final Callable<T> task) {
		return submit(task, null);
	}

	private <T> Future<T> submit(final Callable<T> task, final BlockingQueue<T> invokeAnyQueue) {
		Objects.requireNonNull(task, "task is null");
		throwIfNotRunning();

		final FutureTask<T> f = new FutureTask<>(task);
		final ImmediateTask<T> t = new ImmediateTask<>(isRunning, pendingCallbacks, f, invokeAnyQueue);

		ApplicationFunctions.invoke(t);

		return f;
	}

	@Override
	public Future<?> submit(final Runnable task) {
		return submit(task, null);
	}

	@Override
	public <T> Future<T> submit(final Runnable task, final T result) {
		Objects.requireNonNull(task, "task is null");
		throwIfNotRunning();

		final FutureTask<T> f = new FutureTask<>(new RunnableWrapper<>(task, result));
		final ImmediateTask<T> t = new ImmediateTask<>(isRunning, pendingCallbacks, f, null);

		ApplicationFunctions.invoke(t);

		return f;
	}

	private void throwIfNotRunning() {
		if (!isRunning.get()) {
			throw new RejectedExecutionException("Executor has been shutdown");
		}
	}

}
