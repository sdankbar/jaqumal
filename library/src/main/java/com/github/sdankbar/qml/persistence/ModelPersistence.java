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
import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.models.interfaces.ChangeListener;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.github.sdankbar.qml.models.list.ListListener;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

public class ModelPersistence {

	private final ScheduledExecutorService ioExecutor = Executors.newSingleThreadScheduledExecutor();

	private final ScheduledExecutorService qtExecutor;
	private final Duration writeDelay;
	private final File persistenceDirectory;

	private final Set<String> scheduledModels = new HashSet<>();

	public ModelPersistence(final ScheduledExecutorService qtExecutor, final Duration writeDelay,
			final File persistenceDirectory) {
		this.qtExecutor = Objects.requireNonNull(qtExecutor, "qtExecutor is null");
		this.writeDelay = Objects.requireNonNull(writeDelay, "writeDelay is null");
		Preconditions.checkArgument(!writeDelay.isNegative(), "writeDelay is negative");
		this.persistenceDirectory = Objects.requireNonNull(persistenceDirectory, "persistenceDirectory is null");
	}

	public <K> void addModel(final JQMLSingletonModel<K> model) {
		model.registerChangeListener(new ChangeListener() {

			@Override
			public void valueChanged(final String key, final JVariant newValue) {
				scheduleSave(model);
			}
		});
	}

	public <K> void addModel(final JQMLListModel<K> model) {
		model.registerListener(new ListListener<K>() {

			@Override
			public void added(final int index, final Map<K, JVariant> map) {
				scheduleSave(model);
			}

			@Override
			public void removed(final int index, final Map<K, JVariant> map) {
				scheduleSave(model);
			}
		});
	}

	private void scheduleSave(final JQMLSingletonModel<?> model) {
		if (!scheduledModels.contains(model.getModelName())) {
			scheduledModels.add(model.getModelName());

			if (writeDelay.isZero()) {
				qtThreadSaveModel(model);
			} else {
				qtExecutor.schedule(() -> {
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
			saveModel(model.getModelName(), stream.toByteArray());
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void scheduleSave(final JQMLListModel<?> model) {
		if (!scheduledModels.contains(model.getModelName())) {
			scheduledModels.add(model.getModelName());

			if (writeDelay.isZero()) {
				qtThreadSaveModel(model);
			} else {
				qtExecutor.schedule(() -> {
					qtThreadSaveModel(model);
				}, writeDelay.toMillis(), TimeUnit.MILLISECONDS);
			}
		}
	}

	private void qtThreadSaveModel(final JQMLListModel<?> model) {
		scheduledModels.remove(model.getModelName());
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			model.serialize(stream);
			saveModel(model.getModelName(), stream.toByteArray());
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveModel(final String modelName, final byte[] data) {
		ioExecutor.execute(() -> {
			persistenceDirectory.mkdirs();
			try {
				Files.write(data, new File(persistenceDirectory, modelName + ".json"));
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
