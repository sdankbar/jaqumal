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
import java.util.HashSet;
import java.util.Objects;
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
import com.github.sdankbar.qml.models.singleton.JQMLPerformanceModel;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModelImpl;
import com.github.sdankbar.qml.models.singleton.SingletonMapAccessor;
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

		return new JQMLFlatTreeModel<>(name, EnumSet.allOf(enumClass), eventLoopThread, new FlatTreeAccessor(),
				putMode);
	}

	@Override
	@QtThread
	public <K> JQMLFlatTreeModel<K> createFlatTreeModel(final String name, final Set<K> keys, final PutMode putMode) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		return new JQMLFlatTreeModel<>(name, keys, eventLoopThread, new FlatTreeAccessor(), putMode);
	}

	@QtThread
	@Override
	public <K extends Enum<K>> JQMLListModel<K> createListModel(final String name, final Class<K> enumClass,
			final PutMode putMode) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		return new JQMLListModelImpl<>(name, EnumSet.allOf(enumClass), eventLoopThread, new ListAccessor(), putMode);
	}

	@QtThread
	@Override
	public <K> JQMLListModel<K> createListModel(final String name, final Set<K> keys, final PutMode putMode) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		return new JQMLListModelImpl<>(name, keys, eventLoopThread, new ListAccessor(), putMode);
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

		return new JQMLSingletonModelImpl<>(name, EnumSet.allOf(enumClass), eventLoopThread, new SingletonMapAccessor(),
				putMode);
	}

	@QtThread
	@Override
	public <K> JQMLSingletonModel<K> createSingletonModel(final String name, final Set<K> keys, final PutMode putMode) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		return new JQMLSingletonModelImpl<>(name, keys, eventLoopThread, new SingletonMapAccessor(), putMode);
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
		return new JQMLListViewModel<>(modelName, userKeys, app, mode, putMode);
	}

	@Override
	@QtThread()
	public <K> JQMLListViewModel<K> createListViewModel(final String modelName, final ImmutableSet<K> keySet,
			final SelectionMode mode, final PutMode putMode) {
		checkForIsSelectedInKeySet(keySet);
		return new JQMLListViewModel<>(modelName, keySet, app, mode, putMode);
	}

	@Override
	@QtThread
	public void enablePersistence(final Duration writeDelay, final File persistenceDirectory) {
		Preconditions.checkArgument(persistence == null, "Persistence has already been enabled");
		persistence = new ModelPersistence(app.getQMLThreadExecutor(), writeDelay, persistenceDirectory);
	}

	public void enablePersistenceForModel(final JQMLSingletonModel<?> model) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		persistence.addModel(model);
	}

	public void enablePersistenceForModel(final JQMLListModel<?> model) {
		Preconditions.checkArgument(persistence != null, "Persistence has not been enabled");
		persistence.addModel(model);
	}

}
