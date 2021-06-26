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
package com.github.sdankbar.qml.persistence;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.QtThread;
import com.github.sdankbar.qml.models.interfaces.ChangeListener;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;
import com.github.sdankbar.qml.models.table.JQMLTableModel;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

public class ModelPersistence {

	private static final Logger log = LoggerFactory.getLogger(ModelPersistence.class);

	private final ScheduledExecutorService ioExecutor = Executors.newSingleThreadScheduledExecutor();

	private final ScheduledExecutorService qtExecutor;
	private ScheduledFuture<?> pendingFuture = null;

	private final Duration writeDelay;
	private final File persistenceDirectory;

	private final Set<String> scheduledModels = new HashSet<>();

	private final Map<JQMLSingletonModel<?>, ChangeListener> autoPersistedSingletonModels = new HashMap<>();
	private final Map<JQMLListModel<?>, Runnable> autoPersistedListModels = new HashMap<>();
	private final Map<JQMLTableModel<?>, Runnable> autoPersistedTableModels = new HashMap<>();

	public ModelPersistence(final ScheduledExecutorService qtExecutor, final Duration writeDelay,
			final File persistenceDirectory) {
		this.qtExecutor = Objects.requireNonNull(qtExecutor, "qtExecutor is null");
		this.writeDelay = Objects.requireNonNull(writeDelay, "writeDelay is null");
		Preconditions.checkArgument(!writeDelay.isNegative(), "writeDelay is negative");
		this.persistenceDirectory = Objects.requireNonNull(persistenceDirectory, "persistenceDirectory is null");
	}

	public void shutdown() {
		for (final Entry<JQMLSingletonModel<?>, ChangeListener> m : autoPersistedSingletonModels.entrySet()) {
			m.getKey().unregisterChangeListener(m.getValue());
		}
		for (final Entry<JQMLListModel<?>, Runnable> m : autoPersistedListModels.entrySet()) {
			m.getKey().unregisterModelChangedListener(m.getValue());
		}

		if (pendingFuture != null) {
			pendingFuture.cancel(false);
		}
		ioExecutor.shutdown();
		scheduledModels.clear();
		// Do not shutdown qtExecutor
	}

	@QtThread
	public <K> void autoPersistModel(final JQMLSingletonModel<K> model) {
		final ChangeListener l = (final String key, final JVariant newValue) -> scheduleSave(model);
		autoPersistedSingletonModels.put(model, l);
		model.registerChangeListener(l);
	}

	@QtThread
	public <K> void autoPersistModel(final JQMLListModel<K> model) {
		final Runnable l = () -> scheduleSave(model);
		autoPersistedListModels.put(model, l);
		model.registerModelChangedListener(l);
	}

	@QtThread
	public <K> void autoPersistModel(final JQMLTableModel<K> model) {
		final Runnable l = () -> scheduleSave(model);
		autoPersistedTableModels.put(model, l);
		model.registerModelChangedListener(l);
	}

	@QtThread
	public void persistModel(final JQMLSingletonModel<?> model) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			model.serialize(stream);
			saveModel(model.getModelName(), stream.toByteArray());
		} catch (final IOException e) {
			log.warn("Failed to persist " + model.getModelName(), e);
		}
	}

	@QtThread
	public void persistModel(final JQMLListModel<?> model) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			model.serialize(stream, null);
			saveModel(model.getModelName(), stream.toByteArray());
		} catch (final IOException e) {
			log.warn("Failed to persist " + model.getModelName(), e);
		}
	}

	@QtThread
	public void persistModel(final JQMLTableModel<?> model) {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			model.serialize(stream);
			saveModel(model.getModelName(), stream.toByteArray());
		} catch (final IOException e) {
			log.warn("Failed to persist " + model.getModelName(), e);
		}
	}

	@QtThread
	public boolean restoreModel(final JQMLSingletonModel<?> model) {
		try (FileInputStream s = new FileInputStream(new File(persistenceDirectory, model.getModelName() + ".json"))) {
			model.deserialize(s);
			return true;
		} catch (final IOException e) {
			log.info("No data restored to " + model.getModelName(), e);
			return false;
		}
	}

	@QtThread
	public boolean restoreModel(final JQMLListModel<?> model) {
		try (FileInputStream s = new FileInputStream(new File(persistenceDirectory, model.getModelName() + ".json"))) {
			model.deserialize(s);
			return true;
		} catch (final IOException e) {
			log.info("No data restored to " + model.getModelName(), e);
			return false;
		}
	}

	@QtThread
	public boolean restoreModel(final JQMLTableModel<?> model) {
		try (FileInputStream s = new FileInputStream(new File(persistenceDirectory, model.getModelName() + ".json"))) {
			model.deserialize(s);
			return true;
		} catch (final IOException e) {
			log.info("No data restored to " + model.getModelName(), e);
			return false;
		}
	}

	private void scheduleSave(final JQMLSingletonModel<?> model) {
		if (!scheduledModels.contains(model.getModelName())) {
			scheduledModels.add(model.getModelName());

			if (writeDelay.isZero()) {
				qtThreadSaveModel(model);
			} else {
				pendingFuture = qtExecutor.schedule(() -> {
					pendingFuture = null;
					qtThreadSaveModel(model);
				}, writeDelay.toMillis(), TimeUnit.MILLISECONDS);
			}
		}
	}

	private void qtThreadSaveModel(final JQMLSingletonModel<?> model) {
		scheduledModels.remove(model.getModelName());
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			model.serialize(stream);
			saveModelThreaded(model.getModelName(), stream.toByteArray());
		} catch (final IOException e) {
			log.warn("Failed to persist " + model.getModelName(), e);
		}
	}

	private void scheduleSave(final JQMLListModel<?> model) {
		if (!scheduledModels.contains(model.getModelName())) {
			scheduledModels.add(model.getModelName());

			if (writeDelay.isZero()) {
				qtThreadSaveModel(model);
			} else {
				pendingFuture = qtExecutor.schedule(() -> {
					pendingFuture = null;
					qtThreadSaveModel(model);
				}, writeDelay.toMillis(), TimeUnit.MILLISECONDS);
			}
		}
	}

	private void scheduleSave(final JQMLTableModel<?> model) {
		if (!scheduledModels.contains(model.getModelName())) {
			scheduledModels.add(model.getModelName());

			if (writeDelay.isZero()) {
				qtThreadSaveModel(model);
			} else {
				pendingFuture = qtExecutor.schedule(() -> {
					pendingFuture = null;
					qtThreadSaveModel(model);
				}, writeDelay.toMillis(), TimeUnit.MILLISECONDS);
			}
		}
	}

	private void qtThreadSaveModel(final JQMLListModel<?> model) {
		scheduledModels.remove(model.getModelName());
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			model.serialize(stream, null);
			saveModelThreaded(model.getModelName(), stream.toByteArray());
		} catch (final IOException e) {
			log.warn("Failed to persist " + model.getModelName(), e);
		}
	}

	private void qtThreadSaveModel(final JQMLTableModel<?> model) {
		scheduledModels.remove(model.getModelName());
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			model.serialize(stream);
			saveModelThreaded(model.getModelName(), stream.toByteArray());
		} catch (final IOException e) {
			log.warn("Failed to persist " + model.getModelName(), e);
		}
	}

	private void saveModelThreaded(final String modelName, final byte[] data) {
		ioExecutor.execute(() -> {
			saveModel(modelName, data);
		});
	}

	private void saveModel(final String modelName, final byte[] data) {
		try {
			persistenceDirectory.mkdirs();
			Files.write(data, new File(persistenceDirectory, modelName + ".json"));
		} catch (final IOException e) {
			log.warn("Failed to persist " + modelName, e);
		}
	}
}
