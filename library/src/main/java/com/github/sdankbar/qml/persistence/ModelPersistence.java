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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
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
import com.google.common.collect.ImmutableList;

public class ModelPersistence {

	private static final Logger log = LoggerFactory.getLogger(ModelPersistence.class);

	private final ScheduledExecutorService qtExecutor;

	private final Duration writeDelay;
	private final File persistenceDirectory;

	private final Map<String, QMLThreadPersistanceTask> taskMap = new HashMap<>();

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

	@QtThread
	public void shutdown() {
		for (final Entry<JQMLSingletonModel<?>, ChangeListener> m : autoPersistedSingletonModels.entrySet()) {
			m.getKey().unregisterChangeListener(m.getValue());
		}
		for (final Entry<JQMLListModel<?>, Runnable> m : autoPersistedListModels.entrySet()) {
			m.getKey().unregisterModelChangedListener(m.getValue());
		}
		for (final Entry<JQMLTableModel<?>, Runnable> m : autoPersistedTableModels.entrySet()) {
			m.getKey().unregisterModelChangedListener(m.getValue());
		}

		flush();
	}

	@QtThread
	public void flush() {
		for (final QMLThreadPersistanceTask task : ImmutableList.copyOf(taskMap.values())) {
			task.finishImmediately();
		}
		taskMap.clear();
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
		final QMLThreadPersistanceTask task = new QMLThreadPersistanceTask(persistenceDirectory, model, taskMap);
		task.run();
	}

	@QtThread
	public void persistModel(final JQMLListModel<?> model) {
		final QMLThreadPersistanceTask task = new QMLThreadPersistanceTask(persistenceDirectory, model, taskMap);
		task.run();
	}

	@QtThread
	public void persistModel(final JQMLTableModel<?> model) {
		final QMLThreadPersistanceTask task = new QMLThreadPersistanceTask(persistenceDirectory, model, taskMap);
		task.run();
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
		final String name = model.getModelName();
		if (!taskMap.containsKey(name)) {
			final QMLThreadPersistanceTask task = new QMLThreadPersistanceTask(persistenceDirectory, model, taskMap);

			if (writeDelay.isZero()) {
				task.run();
			} else {
				final ScheduledFuture<?> f = qtExecutor.schedule(task, writeDelay.toMillis(), TimeUnit.MILLISECONDS);
				task.setFuture(f);
			}
		}
	}

	private void scheduleSave(final JQMLListModel<?> model) {
		final String name = model.getModelName();
		if (!taskMap.containsKey(name)) {
			final QMLThreadPersistanceTask task = new QMLThreadPersistanceTask(persistenceDirectory, model, taskMap);

			if (writeDelay.isZero()) {
				task.run();
			} else {
				final ScheduledFuture<?> f = qtExecutor.schedule(task, writeDelay.toMillis(), TimeUnit.MILLISECONDS);
				task.setFuture(f);
			}
		}
	}

	private void scheduleSave(final JQMLTableModel<?> model) {
		final String name = model.getModelName();
		if (!taskMap.containsKey(name)) {
			final QMLThreadPersistanceTask task = new QMLThreadPersistanceTask(persistenceDirectory, model, taskMap);

			if (writeDelay.isZero()) {
				task.run();
			} else {
				final ScheduledFuture<?> f = qtExecutor.schedule(task, writeDelay.toMillis(), TimeUnit.MILLISECONDS);
				task.setFuture(f);
			}
		}
	}
}
