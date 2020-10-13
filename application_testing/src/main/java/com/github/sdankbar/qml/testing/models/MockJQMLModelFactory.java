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
package com.github.sdankbar.qml.testing.models;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.JQMLModelFactory;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.QtThread;
import com.github.sdankbar.qml.exceptions.QMLException;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.JQMLMapPool;
import com.github.sdankbar.qml.models.flat_tree.JQMLFlatTreeModel;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.github.sdankbar.qml.models.list.JQMLListViewModel;
import com.github.sdankbar.qml.models.list.JQMLListViewModel.SelectionMode;
import com.github.sdankbar.qml.models.list.JQMLXYSeriesModel;
import com.github.sdankbar.qml.models.singleton.JQMLButtonModel;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;
import com.github.sdankbar.qml.models.singleton.JQMLTextInputModel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class MockJQMLModelFactory implements JQMLModelFactory {

	private final Map<String, JQMLSingletonModel<?>> singletonModels = new HashMap<>();
	private final Map<String, JQMLListModel<?>> listModels = new HashMap<>();
	private final Map<String, JQMLListViewModel<?>> listViewModels = new HashMap<>();
	private final Set<String> modelName = new HashSet<>();

	private final JQMLApplication<?> app;

	public MockJQMLModelFactory(final JQMLApplication<?> app) {
		this.app = Objects.requireNonNull(app, "app is null");
	}

	private void checkModelName(final String name) {
		if (!modelName.add(Objects.requireNonNull(name, "name is null"))) {
			throw new QMLException("Model with name [" + name + "] already exists");
		}
	}

	@Override
	public JQMLButtonModel createButtonModel(final String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K extends Enum<K>> JQMLFlatTreeModel<K> createFlatTreeModel(final String name, final Class<K> enumClass,
			final PutMode putMode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> JQMLFlatTreeModel<K> createFlatTreeModel(final String name, final Set<K> keys, final PutMode putMode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K extends Enum<K>> JQMLListModel<K> createListModel(final String name, final Class<K> enumClass,
			final PutMode putMode) {
		checkModelName(name);
		final JQMLListModel<K> temp = new MockJQMLListModel<>(name);
		listModels.put(name, temp);
		return temp;
	}

	@Override
	public <K> JQMLListModel<K> createListModel(final String name, final Set<K> keys, final PutMode putMode) {
		checkModelName(name);
		final JQMLListModel<K> temp = new MockJQMLListModel<>(name);
		listModels.put(name, temp);
		return temp;
	}

	@Override
	public <K extends Enum<K>> JQMLMapPool<K> createPool(final String name, final Class<K> enumClass,
			final ImmutableMap<K, JVariant> initialValues, final PutMode putMode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> JQMLMapPool<K> createPool(final String name, final Set<K> keys,
			final ImmutableMap<K, JVariant> initialValues, final PutMode putMode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K extends Enum<K>> JQMLSingletonModel<K> createSingletonModel(final String name, final Class<K> enumClass,
			final PutMode putMode) {
		checkModelName(name);
		final JQMLSingletonModel<K> temp = new MockJQMLSingletonModel<>(name);
		singletonModels.put(name, temp);
		return temp;
	}

	@Override
	public <K> JQMLSingletonModel<K> createSingletonModel(final String name, final Set<K> keys, final PutMode putMode) {
		checkModelName(name);
		final JQMLSingletonModel<K> temp = new MockJQMLSingletonModel<>(name);
		singletonModels.put(name, temp);
		return temp;
	}

	@Override
	public JQMLTextInputModel createTextInputModel(final String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JQMLXYSeriesModel createXYSeriesModel(final String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public <K> JQMLListModel<K> getListModel(final String name) {
		return (JQMLListModel<K>) listModels.get(name);
	}

	public <K> JQMLSingletonModel<K> getSingletonModel(final String name) {
		return (JQMLSingletonModel<K>) singletonModels.get(name);
	}

	public <K> JQMLListViewModel<K> getListViewModel(final String name) {
		return (JQMLListViewModel<K>) listViewModels.get(name);
	}

	void reset() {
		singletonModels.clear();
		listModels.clear();
		listViewModels.clear();
		modelName.clear();
	}

	@Override
	@QtThread()
	public <K extends Enum<K>> JQMLListViewModel<K> createListViewModel(final String modelName, final Class<K> keyClass,
			final SelectionMode mode, final PutMode putMode) {
		final ImmutableSet<K> userKeys = ImmutableSet.copyOf(EnumSet.allOf(keyClass));
		final JQMLListViewModel<K> model = new JQMLListViewModel<>(modelName, userKeys, app, mode, putMode);
		listViewModels.put(modelName, model);
		return model;
	}

	@Override
	@QtThread()
	public <K> JQMLListViewModel<K> createListViewModel(final String modelName, final ImmutableSet<K> keySet,
			final SelectionMode mode, final PutMode putMode) {
		final JQMLListViewModel<K> model = new JQMLListViewModel<>(modelName, keySet, app, mode, putMode);
		listViewModels.put(modelName, model);
		return model;
	}

}
