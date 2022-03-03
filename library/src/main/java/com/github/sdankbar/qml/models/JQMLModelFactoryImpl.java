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
package com.github.sdankbar.qml.models;

import java.io.File;
import java.time.Duration;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.JQMLModelFactory;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.QtThread;
import com.github.sdankbar.qml.eventing.builtin.RenderEvent;
import com.github.sdankbar.qml.exceptions.QMLException;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.flat_tree.FlatTreeAccessor;
import com.github.sdankbar.qml.models.flat_tree.JQMLFlatTreeModel;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.github.sdankbar.qml.models.list.JQMLListModelImpl;
import com.github.sdankbar.qml.models.list.JQMLListViewModel;
import com.github.sdankbar.qml.models.list.JQMLListViewModel.SelectionMode;
import com.github.sdankbar.qml.models.list.JQMLXYSeriesModel;
import com.github.sdankbar.qml.models.list.ListAccessor;
import com.github.sdankbar.qml.models.singleton.JQMLConstantsModel;
import com.github.sdankbar.qml.models.singleton.JQMLPerformanceModel;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModelImpl;
import com.github.sdankbar.qml.models.singleton.SingletonMapAccessor;
import com.github.sdankbar.qml.models.table.JQMLTableModel;
import com.github.sdankbar.qml.models.table.JQMLTableModelImpl;
import com.github.sdankbar.qml.persistence.ModelPersistence;
import com.github.sdankbar.qml.utility.JQMLUtilities;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * The JQMLModelFactory allows for the creation of Singleton, List, and Tree
 * models that are available for access by QML.
 *
 * Should not be instantiated by application code.
 */
public class JQMLModelFactoryImpl implements JQMLModelFactory {

	private static <K> void checkForIsSelectedInKeySet(final ImmutableSet<K> keys) {
		for (final K v : keys) {
			if (v.toString().equals("is_selected")) {
				return;
			}
		}
		throw new IllegalArgumentException("Key set must contain \"is_selected\"");
	}

	private final JQMLApplication<?> app;
	private final AtomicReference<Thread> eventLoopThread;
	private final JQMLPerformanceModel perfModel;

	private ModelPersistence persistence = null;

	private final Set<String> modelName = new HashSet<>();

	private final Map<String, JQMLSingletonModel<?>> singletonModels = new HashMap<>();
	private final Map<String, JQMLListModel<?>> listModels = new HashMap<>();
	private final Map<String, JQMLListViewModel<?>> listViewModels = new HashMap<>();
	private final Map<String, JQMLFlatTreeModel<?>> flatTreeModels = new HashMap<>();
	private final Map<String, JQMLTableModel<?>> tableModels = new HashMap<>();
	private final Map<String, JQMLConstantsModel> constantsModels = new HashMap<>();

	/**
	 * Constructs a new factory.
	 *
	 * @param app             The parent JQMLApplication.
	 * @param eventLoopThread A reference to the Qt Thread.
	 */
	public JQMLModelFactoryImpl(final JQMLApplication<?> app, final AtomicReference<Thread> eventLoopThread) {
		this.app = Objects.requireNonNull(app, "app is null");
		this.eventLoopThread = Objects.requireNonNull(eventLoopThread, "eventLoopThread is null");

		perfModel = new JQMLPerformanceModel("PerfModel", this);
		app.getEventDispatcher().register(RenderEvent.class, perfModel);
	}

	private void checkModelName(final String name) {
		if (!modelName.add(Objects.requireNonNull(name, "name is null"))) {
			throw new QMLException("Model with name [" + name + "] already exists");
		}
	}

	@Override
	@QtThread
	public <K extends Enum<K>> JQMLFlatTreeModel<K> createFlatTreeModel(final String name, final Class<K> enumClass,
			final PutMode putMode) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		final JQMLFlatTreeModel<K> m = new JQMLFlatTreeModel<>(name, EnumSet.allOf(enumClass), eventLoopThread,
				new FlatTreeAccessor(), putMode);
		flatTreeModels.put(name, m);
		return m;
	}

	@Override
	@QtThread
	public <K> JQMLFlatTreeModel<K> createFlatTreeModel(final String name, final Set<K> keys, final PutMode putMode) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		final JQMLFlatTreeModel<K> m = new JQMLFlatTreeModel<>(name, keys, eventLoopThread, new FlatTreeAccessor(),
				putMode);
		flatTreeModels.put(name, m);
		return m;
	}

	@QtThread
	@Override
	public <K extends Enum<K>> JQMLListModel<K> createListModel(final String name, final Class<K> enumClass,
			final PutMode putMode) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		final JQMLListModelImpl<K> m = new JQMLListModelImpl<>(name, EnumSet.allOf(enumClass), eventLoopThread,
				new ListAccessor(), putMode);
		listModels.put(name, m);
		return m;
	}

	@QtThread
	@Override
	public <K> JQMLListModel<K> createListModel(final String name, final Set<K> keys, final PutMode putMode) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		final JQMLListModelImpl<K> m = new JQMLListModelImpl<>(name, keys, eventLoopThread, new ListAccessor(),
				putMode);
		listModels.put(name, m);
		return m;
	}

	@QtThread
	@Override
	public <K extends Enum<K>> JQMLMapPool<K> createPool(final String name, final Class<K> enumClass,
			final ImmutableMap<K, JVariant> initialValues, final PutMode putMode) {
		final JQMLListModel<K> model = createListModel(name, enumClass, putMode);
		return new JQMLMapPool<>(model, initialValues);
	}

	@QtThread
	@Override
	public <K> JQMLMapPool<K> createPool(final String name, final Set<K> keys,
			final ImmutableMap<K, JVariant> initialValues, final PutMode putMode) {
		final JQMLListModel<K> model = createListModel(name, keys, putMode);
		return new JQMLMapPool<>(model, initialValues);
	}

	@Override
	@QtThread
	public <K extends Enum<K>> JQMLSingletonModel<K> createSingletonModel(final String name, final Class<K> enumClass,
			final PutMode putMode) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		final JQMLSingletonModel<K> m = new JQMLSingletonModelImpl<>(name, EnumSet.allOf(enumClass), eventLoopThread,
				new SingletonMapAccessor(), putMode);
		singletonModels.put(name, m);
		return m;
	}

	@QtThread
	@Override
	public <K> JQMLSingletonModel<K> createSingletonModel(final String name, final Set<K> keys, final PutMode putMode) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		final JQMLSingletonModel<K> m = new JQMLSingletonModelImpl<>(name, keys, eventLoopThread,
				new SingletonMapAccessor(), putMode);
		singletonModels.put(name, m);
		return m;
	}

	@Override
	@QtThread()
	public JQMLXYSeriesModel createXYSeriesModel(final String name) {
		JQMLUtilities.checkThread(eventLoopThread);

		return new JQMLXYSeriesModel(name, this);
	}

	@Override
	@QtThread()
	public <K extends Enum<K>> JQMLListViewModel<K> createListViewModel(final String modelName, final Class<K> keyClass,
			final SelectionMode mode, final PutMode putMode) {
		final ImmutableSet<K> userKeys = ImmutableSet.copyOf(EnumSet.allOf(keyClass));
		checkForIsSelectedInKeySet(userKeys);
		final JQMLListViewModel<K> m = new JQMLListViewModel<>(modelName, userKeys, app, mode, putMode);
		listViewModels.put(modelName, m);
		return m;
	}

	@Override
	@QtThread()
	public <K> JQMLListViewModel<K> createListViewModel(final String modelName, final ImmutableSet<K> keySet,
			final SelectionMode mode, final PutMode putMode) {
		checkForIsSelectedInKeySet(keySet);
		final JQMLListViewModel<K> m = new JQMLListViewModel<>(modelName, keySet, app, mode, putMode);
		listViewModels.put(modelName, m);
		return m;
	}

	@Override
	@QtThread()
	public <K extends Enum<K>> JQMLTableModel<K> createTableModel(final String modelName, final Class<K> keyClass,
			final PutMode putMode) {
		final ImmutableSet<K> userKeys = ImmutableSet.copyOf(EnumSet.allOf(keyClass));
		final JQMLTableModelImpl<K> m = new JQMLTableModelImpl<>(modelName, userKeys, app, putMode);
		tableModels.put(modelName, m);
		return m;
	}

	@Override
	@QtThread()
	public <K> JQMLTableModel<K> createTableModel(final String modelName, final ImmutableSet<K> keySet,
			final SelectionMode mode, final PutMode putMode) {
		final JQMLTableModelImpl<K> m = new JQMLTableModelImpl<>(modelName, keySet, app, putMode);
		tableModels.put(modelName, m);
		return m;
	}

	@Override
	@QtThread
	public void enablePersistence(final Duration writeDelay, final File persistenceDirectory) {
		if (persistence != null) {
			persistence.shutdown();
		}

		persistence = new ModelPersistence(app.getQMLThreadExecutor(), writeDelay, persistenceDirectory);
	}

	@Override
	public void enableAutoPersistenceForModel(final JQMLSingletonModel<?> model) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		persistence.autoPersistModel(model);
	}

	@Override
	public void enableAutoPersistenceForModel(final JQMLListModel<?> model) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		persistence.autoPersistModel(model, ImmutableSet.of());
	}

	@Override
	public void enableAutoPersistenceForModel(final JQMLListModel<?> model,
			final ImmutableSet<String> rootKeysToPersist) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		persistence.autoPersistModel(model, rootKeysToPersist);
	}

	@Override
	public void enableAutoPersistenceForModel(final JQMLTableModel<?> model) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		persistence.autoPersistModel(model, ImmutableSet.of());
	}

	@Override
	public void enableAutoPersistenceForModel(final JQMLTableModel<?> model,
			final ImmutableSet<String> rootKeysToPersist) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		persistence.autoPersistModel(model, rootKeysToPersist);
	}

	@Override
	public void persistModel(final JQMLSingletonModel<?> model) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		persistence.persistModel(model);
	}

	@Override
	public void persistModel(final JQMLListModel<?> model) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		persistence.persistModel(model, ImmutableSet.of());
	}

	@Override
	public void persistModel(final JQMLTableModel<?> model) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		persistence.persistModel(model, ImmutableSet.of());
	}

	@Override
	public void persistModel(final JQMLListModel<?> model, final ImmutableSet<String> rootKeysToPersist) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		persistence.persistModel(model, rootKeysToPersist);
	}

	@Override
	public void persistModel(final JQMLTableModel<?> model, final ImmutableSet<String> rootKeysToPersist) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		persistence.persistModel(model, rootKeysToPersist);
	}

	@Override
	public boolean restoreModel(final JQMLSingletonModel<?> model) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		return persistence.restoreModel(model);
	}

	@Override
	public boolean restoreModel(final JQMLListModel<?> model) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		return persistence.restoreModel(model, ImmutableSet.of());
	}

	@Override
	public boolean restoreModel(final JQMLTableModel<?> model) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		return persistence.restoreModel(model, ImmutableSet.of());
	}

	@Override
	public boolean restoreModel(final JQMLListModel<?> model, final ImmutableSet<String> rootKeysToPersist) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		return persistence.restoreModel(model, rootKeysToPersist);
	}

	@Override
	public boolean restoreModel(final JQMLTableModel<?> model, final ImmutableSet<String> rootKeysToPersist) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		return persistence.restoreModel(model, rootKeysToPersist);
	}

	@Override
	public void flushPersistence() {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		persistence.flush();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K> Optional<JQMLListModel<K>> getListModel(final String name) {
		try {
			return Optional.ofNullable((JQMLListModel<K>) listModels.get(name));
		} catch (final ClassCastException e) {
			return Optional.empty();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K> Optional<JQMLSingletonModel<K>> getSingletonModel(final String name) {
		try {
			return Optional.ofNullable((JQMLSingletonModel<K>) singletonModels.get(name));
		} catch (final ClassCastException e) {
			return Optional.empty();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K> Optional<JQMLListViewModel<K>> getListViewModel(final String name) {
		try {
			return Optional.ofNullable((JQMLListViewModel<K>) listViewModels.get(name));
		} catch (final ClassCastException e) {
			return Optional.empty();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K> Optional<JQMLFlatTreeModel<K>> getFlatTreeModel(final String name) {
		try {
			return Optional.ofNullable((JQMLFlatTreeModel<K>) flatTreeModels.get(name));
		} catch (final ClassCastException e) {
			return Optional.empty();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K> Optional<JQMLTableModel<K>> getTableModel(final String name) {
		try {
			return Optional.ofNullable((JQMLTableModel<K>) tableModels.get(name));
		} catch (final ClassCastException e) {
			return Optional.empty();
		}
	}

	@Override
	public JQMLConstantsModel createConstantModel(final Class<?> constant) {
		final JQMLConstantsModel model = new JQMLConstantsModel(this, constant);
		constantsModels.put(model.getModelName(), model);
		return model;
	}

	@Override
	public JQMLConstantsModel createEnumModel(final Class<? extends Enum<?>> enumClass) {
		Objects.requireNonNull(enumClass, "enumClass is null");
		final ImmutableMap.Builder<String, JVariant> builder = ImmutableMap.builder();
		for (final Enum<?> s : enumClass.getEnumConstants()) {
			builder.put(s.name(), new JVariant(s.ordinal()));
		}

		final JQMLConstantsModel model = new JQMLConstantsModel(this, enumClass.getSimpleName(), builder.build());
		constantsModels.put(model.getModelName(), model);
		return model;
	}
}
