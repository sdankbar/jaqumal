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

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.cpp.ApiInstance;
import com.github.sdankbar.qml.cpp.jna.CppInterface.EventCallback;
import com.github.sdankbar.qml.eventing.Event;
import com.github.sdankbar.qml.eventing.EventDispatcher;
import com.github.sdankbar.qml.eventing.EventFactory;
import com.github.sdankbar.qml.eventing.EventParser;
import com.github.sdankbar.qml.eventing.builtin.BuiltinEventFactory;
import com.github.sdankbar.qml.eventing.builtin.BuiltinEventProcessor;
import com.github.sdankbar.qml.exceptions.QMLException;
import com.sun.jna.Pointer;

/**
 * Starting point for creating a Jaqumal application.
 *
 * @param <EType> The EventProcessor type.
 */
public class JQMLApplication<EType> {

	private class ApplicationEventListener implements EventCallback {

		@Override
		public void invoke(final String type, final Pointer data, final int length) {
			final ByteBuffer buffer = data.getByteBuffer(0, length);

			handleEvent(type, buffer);
		}

	}

	private static final Logger log = LoggerFactory.getLogger(JQMLApplication.class);

	private static AtomicBoolean SINGLETON_EXISTS = new AtomicBoolean();

	/**
	 * Creates a new JQMLApplication. Only 1 can exist at a time in an address
	 * space. If an attempt is made to make another, an IllegalStateException it
	 * thrown.
	 *
	 * @param argv    Arguments to pass to the QApplication.
	 * @param factory Factory for building user defined events that are generated in
	 *                QML code.
	 * @return The created JQMLApplication.
	 */
	public static <EType> JQMLApplication<EType> create(final String[] argv, final EventFactory<EType> factory) {
		if (SINGLETON_EXISTS.getAndSet(true)) {
			throw new IllegalStateException("JQMLApplication already exists");
		}
		return new JQMLApplication<>(argv, factory);
	}

	/**
	 * Deletes the singleton JQMLApplication instance. Most applications will only
	 * need this for unit testing.
	 */
	@QtThread
	public static void delete() {
		ApiInstance.LIB_INSTANCE.deleteQApplication();
		SINGLETON_EXISTS.set(false);
	}

	private final ApplicationEventListener listener = new ApplicationEventListener();
	private final EventFactory<EType> factory;
	private final BuiltinEventFactory builtinFactory = new BuiltinEventFactory();
	private final EventDispatcher<EType> dispatcher = new EventDispatcher<>();

	private final AtomicReference<Thread> eventLoopThread = new AtomicReference<>();

	private final List<FileAlterationMonitor> fileMonitors = new ArrayList<>();

	private final ScheduledExecutorService executor = new JQMLScheduledExecutorService();
	private final JQMLModelFactory modelFactory;

	@SuppressWarnings("unused")
	// Used by C++ code
	private final JQMLLogging logger;

	private JQMLApplication(final String[] argv, final EventFactory<EType> factory) {
		this.factory = Objects.requireNonNull(factory, "factory is null");
		Objects.requireNonNull(argv, "argv is null");
		JQMLExceptionHandling.register();// Do very early so that exception handling is immediately available.

		ApiInstance.LIB_INSTANCE.createQApplication(argv.length, argv);
		JQMLExceptionHandling.checkExceptions();

		ApiInstance.LIB_INSTANCE.addEventCallback(listener);
		JQMLExceptionHandling.checkExceptions();

		modelFactory = new JQMLModelFactory(this, eventLoopThread);

		logger = new JQMLLogging();
	}

	/**
	 * Blocks the current thread, processing Qt Events until the EventLoop exits.
	 *
	 * See QApplication::exec()
	 */
	public void execute() {
		eventLoopThread.set(Thread.currentThread());

		final AtomicBoolean shutdownRunning = new AtomicBoolean(false);
		final Thread shutdownThread = new Thread() {

			@Override
			public void run() {
				shutdownRunning.set(true);
				try {
					executor.submit(() -> ApiInstance.LIB_INSTANCE.quitQApplication());
				} catch (final RejectedExecutionException e) {
					log.debug("Executor has already been shutdown");
				}
				while (eventLoopThread.get() != null || SINGLETON_EXISTS.get()) {
					Thread.yield();
				}
			}
		};
		Runtime.getRuntime().addShutdownHook(shutdownThread);

		ApiInstance.LIB_INSTANCE.execQApplication();
		eventLoopThread.set(null);

		for (final FileAlterationMonitor m : fileMonitors) {
			try {
				m.stop();
			} catch (final Exception e) {
				log.error("Failed to stop FileMonitor", e);
			}
		}
		fileMonitors.clear();

		executor.shutdownNow();
		try {
			executor.awaitTermination(1, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			log.error("Interrupted while attempting to shutdown executor", e);
		}

		delete();

		if (!shutdownRunning.get()) {
			Runtime.getRuntime().removeShutdownHook(shutdownThread);
		}

		log.info("JQMLApplication.execute() complete");
	}

	/**
	 * @return The application's event dispatcher.
	 */
	public EventDispatcher<EType> getEventDispatcher() {
		return dispatcher;
	}

	/**
	 * @return A reference to the JQMLMOdelFactory for this application.
	 */
	public JQMLModelFactory getModelFactory() {
		return modelFactory;
	}

	/**
	 * @return A reference to the ScheduledExecutorServer for this application. Can
	 *         be used to execute on the Qt Event Loop.
	 */
	public ScheduledExecutorService getQMLThreadExecutor() {
		return executor;
	}

	private void handleEvent(final String type, final ByteBuffer data) {
		final EventParser parser = new EventParser(data);
		final Event<BuiltinEventProcessor> builtinEvent = builtinFactory.create(type, parser);
		if (builtinEvent != null) {
			dispatcher.submitBuiltin(builtinEvent);
		} else {
			final Event<EType> event = factory.create(type, parser);
			if (event != null) {
				dispatcher.submit(event);
			} else {
				log.warn("Event creation failed: {}", type);
			}
		}
	}

	/**
	 * Commands Qt to load the QML file at the file path. Also watches for any
	 * changes to files in the parent directory of filePath and will reload the QML
	 * file at filePath if a change is detected.
	 *
	 * @param filePath Path to the QML file to load. @throws
	 */
	@QtThread
	public void loadAndWatchQMLFile(final String filePath) {
		Objects.requireNonNull(filePath, "filePath is null");

		verifyEventLoopThread();

		ApiInstance.LIB_INSTANCE.loadQMLFile(filePath);
		JQMLExceptionHandling.checkExceptions();

		final FileAlterationObserver observer = new FileAlterationObserver(Paths.get(filePath).getParent().toString());
		final FileAlterationMonitor monitor = new FileAlterationMonitor(250);
		final FileAlterationListener listener = new FileAlterationListener() {

			@Override
			public void onDirectoryChange(final File directory) {
				// Empty Implementation
			}

			@Override
			public void onDirectoryCreate(final File directory) {
				// Empty Implementation
			}

			@Override
			public void onDirectoryDelete(final File directory) {
				// Empty Implementation
			}

			@Override
			public void onFileChange(final File file) {
				if (file.getName().endsWith(".qml")) {
					executor.submit(() -> reloadQMLFile(filePath));
				}
			}

			@Override
			public void onFileCreate(final File file) {
				// Empty Implementation
			}

			@Override
			public void onFileDelete(final File file) {
				// Empty Implementation
			}

			@Override
			public void onStart(final FileAlterationObserver observer) {
				// Empty Implementation
			}

			@Override
			public void onStop(final FileAlterationObserver observer) {
				// Empty Implementation
			}
		};
		observer.addListener(listener);
		monitor.addObserver(observer);
		try {
			monitor.start();
		} catch (final Exception e) {
			throw new QMLException("Failed to wait file: " + filePath, e);
		}

		fileMonitors.add(monitor);
	}

	/**
	 * Commands Qt to load the QML file at the file path.
	 *
	 * @param filePath Path to the QML file to load.
	 */
	@QtThread
	public void loadQMLFile(final String filePath) {
		Objects.requireNonNull(filePath, "filePath is null");
		verifyEventLoopThread();

		ApiInstance.LIB_INSTANCE.loadQMLFile(filePath);
		JQMLExceptionHandling.checkExceptions();
	}

	/**
	 * Instructs Qt to exit its EventLoop.
	 */
	@QtThread
	public void quitApp() {
		verifyEventLoopThread();

		ApiInstance.LIB_INSTANCE.quitQApplication();
		JQMLExceptionHandling.checkExceptions();
	}

	/**
	 * Requests that Qt reload the QML file at filePath.
	 *
	 * @param filePath Path to the file to reload.
	 */
	@QtThread
	public void reloadQMLFile(final String filePath) {
		Objects.requireNonNull(filePath, "filePath is null");
		verifyEventLoopThread();

		ApiInstance.LIB_INSTANCE.reloadQMLFile(filePath);
		JQMLExceptionHandling.checkExceptions();
	}

	private void verifyEventLoopThread() {
		JQMLUtilities.checkThread(eventLoopThread);
	}

}
