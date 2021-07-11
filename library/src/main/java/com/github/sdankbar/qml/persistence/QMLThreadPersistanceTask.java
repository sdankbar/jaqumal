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
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;
import com.github.sdankbar.qml.models.table.JQMLTableModel;
import com.google.common.io.Files;

public class QMLThreadPersistanceTask implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(QMLThreadPersistanceTask.class);

	private final File persistenceDirectory;

	private final Map<String, QMLThreadPersistanceTask> scheduled;

	private final JQMLSingletonModel<?> singletonModel;
	private final JQMLListModel<?> listModel;
	private final JQMLTableModel<?> tableModel;

	private ScheduledFuture<?> qtThreadFuture = null;

	private boolean isRunning = false;

	public QMLThreadPersistanceTask(final File persistenceDirectory, final JQMLSingletonModel<?> singletonModel,
			final Map<String, QMLThreadPersistanceTask> scheduled) {
		this.persistenceDirectory = Objects.requireNonNull(persistenceDirectory, "persistenceDirectory is null");
		this.scheduled = Objects.requireNonNull(scheduled, "scheduled is null");
		this.singletonModel = Objects.requireNonNull(singletonModel, "singletonModel is null");
		listModel = null;
		tableModel = null;

		scheduled.put(singletonModel.getModelName(), this);
	}

	public QMLThreadPersistanceTask(final File persistenceDirectory, final JQMLListModel<?> listModel,
			final Map<String, QMLThreadPersistanceTask> scheduled) {
		this.persistenceDirectory = Objects.requireNonNull(persistenceDirectory, "persistenceDirectory is null");
		this.scheduled = Objects.requireNonNull(scheduled, "scheduled is null");
		singletonModel = null;
		this.listModel = Objects.requireNonNull(listModel, "listModel is null");
		tableModel = null;

		scheduled.put(listModel.getModelName(), this);
	}

	public QMLThreadPersistanceTask(final File persistenceDirectory, final JQMLTableModel<?> tableModel,
			final Map<String, QMLThreadPersistanceTask> scheduled) {
		this.persistenceDirectory = Objects.requireNonNull(persistenceDirectory, "persistenceDirectory is null");
		this.scheduled = Objects.requireNonNull(scheduled, "scheduled is null");
		singletonModel = null;
		listModel = null;
		this.tableModel = Objects.requireNonNull(tableModel, "listModel is null");

		scheduled.put(tableModel.getModelName(), this);
	}

	public void setFuture(final ScheduledFuture<?> future) {
		qtThreadFuture = Objects.requireNonNull(future, "future is null");
	}

	public void finishImmediately() {
		if (!isRunning) {
			// Not run yet so cancel and run synchronously
			qtThreadFuture.cancel(false);
			run();
		}
	}

	@Override
	public void run() {
		isRunning = true;
		if (singletonModel != null) {
			qtThreadSaveModel(singletonModel);
		} else if (listModel != null) {
			qtThreadSaveModel(listModel);
		} else {// tableModel != null
			qtThreadSaveModel(tableModel);
		}
	}

	private void qtThreadSaveModel(final JQMLSingletonModel<?> model) {
		scheduled.remove(model.getModelName());
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			model.serialize(stream);
			saveModel(model.getModelName(), stream.toByteArray());
		} catch (final IOException e) {
			log.warn("Failed to persist " + model.getModelName(), e);
		}
	}

	private void qtThreadSaveModel(final JQMLListModel<?> model) {
		scheduled.remove(model.getModelName());
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			model.serialize(stream, null);
			saveModel(model.getModelName(), stream.toByteArray());
		} catch (final IOException e) {
			log.warn("Failed to persist " + model.getModelName(), e);
		}
	}

	private void qtThreadSaveModel(final JQMLTableModel<?> model) {
		scheduled.remove(model.getModelName());
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			model.serialize(stream);
			saveModel(model.getModelName(), stream.toByteArray());
		} catch (final IOException e) {
			log.warn("Failed to persist " + model.getModelName(), e);
		}
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
