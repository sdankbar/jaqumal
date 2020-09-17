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

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

import com.github.sdankbar.qml.cpp.jni.ApplicationFunctions;
import com.github.sdankbar.qml.cpp.jni.EventFunctions;
import com.github.sdankbar.qml.cpp.jni.interfaces.EventCallback;
import com.github.sdankbar.qml.eventing.Event;
import com.github.sdankbar.qml.eventing.EventDispatcher;
import com.github.sdankbar.qml.eventing.EventFactory;
import com.github.sdankbar.qml.eventing.builtin.BuiltinEventFactory;
import com.github.sdankbar.qml.eventing.builtin.BuiltinEventProcessor;
import com.github.sdankbar.qml.exceptions.QMLException;
import com.github.sdankbar.qml.images.JQMLImageProvider;
import com.github.sdankbar.qml.images.JQMLImageProviderWrapper;
import com.github.sdankbar.qml.models.JQMLModelFactoryImpl;
import com.github.sdankbar.qml.utility.QMLRequestParser;
import com.github.sdankbar.qml.utility.JQMLUtilities;
import com.google.common.collect.ImmutableList;

/**
 * Starting point for creating a Jaqumal application.
 *
 * @param <EType> The EventProcessor type.
 */
public class JQMLApplication<EType> {

	private class ApplicationEventListener implements EventCallback {

		@Override
		public boolean invoke(final String type, final ByteBuffer buffer) {
			try {
				buffer.order(ByteOrder.nativeOrder());
				final Optional<JVariant> result = handleEvent(type, buffer);
				if (result.isPresent()) {
					result.get().sendToQML(0);
					return true;
				} else {
					return false;
				}
			} catch (final Exception e) {
				log.warn("Exception constructing event " + type, e);
				return false;
			}
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
		ApplicationFunctions.deleteQApplication();
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

	private final List<JQMLImageProviderWrapper> imageProviders = new ArrayList<>();

	@SuppressWarnings("unused")
	// Used by C++ code
	private final JQMLLogging logger;

	private JQMLApplication(final String[] argv, final EventFactory<EType> factory) {
		this.factory = Objects.requireNonNull(factory, "factory is null");
		Objects.requireNonNull(argv, "argv is null");

		ApplicationFunctions.createQApplication(argv);

		EventFunctions.addEventCallback(listener);

		modelFactory = new JQMLModelFactoryImpl(this, eventLoopThread);

		logger = new JQMLLogging();

		log.info("Qt Compile version={} Qt Runtime version={}", ApplicationFunctions.getCompileQtVersion(),
				ApplicationFunctions.getRuntimeQtVersion());
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
					executor.submit(ApplicationFunctions::quitQApplication);
				} catch (final RejectedExecutionException e) {
					log.debug("Executor has already been shutdown", e);
				}
				while (eventLoopThread.get() != null || SINGLETON_EXISTS.get()) {
					Thread.yield();
				}
			}
		};
		Runtime.getRuntime().addShutdownHook(shutdownThread);

		ApplicationFunctions.execQApplication();
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

	private Optional<JVariant> handleEvent(final String type, final ByteBuffer data) {
		final QMLRequestParser parser = new QMLRequestParser(data);
		final Event<BuiltinEventProcessor> builtinEvent = builtinFactory.create(type, parser);
		if (builtinEvent != null) {
			return dispatcher.submitBuiltin(builtinEvent);
		} else {
			final Event<EType> event = factory.create(type, parser);
			if (event != null) {
				return dispatcher.submit(event);
			} else {
				log.warn("Event creation failed: {}", type);
				return Optional.empty();
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

		ApplicationFunctions.loadQMLFile(filePath);

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

		ApplicationFunctions.loadQMLFile(filePath);
	}

	/**
	 * Instructs Qt to exit its EventLoop.
	 */
	@QtThread
	public void quitApp() {
		verifyEventLoopThread();

		ApplicationFunctions.quitQApplication();
	}

	/**
	 * Registers an image provider. See QQuickImageProvider.
	 *
	 * @param provider   The image provider.
	 * @param providerID The ID of the image provider.
	 */
	@QtThread
	public void registerImageProvider(final JQMLImageProvider provider, final String providerID) {
		imageProviders.add(new JQMLImageProviderWrapper(providerID, provider));
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

		ApplicationFunctions.reloadQMLFile(filePath);
	}

	/**
	 * @return List of the current screens. See QApplication::screens() and QScreen.
	 */
	@QtThread
	public ImmutableList<JScreen> screens() {
		return ImmutableList.copyOf(ApplicationFunctions.getScreens());
	}

	private void verifyEventLoopThread() {
		JQMLUtilities.checkThread(eventLoopThread);
	}

}
