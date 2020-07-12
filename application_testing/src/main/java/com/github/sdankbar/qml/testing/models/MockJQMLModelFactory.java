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
package com.github.sdankbar.qml.testing.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.github.sdankbar.qml.JQMLModelFactory;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.exceptions.QMLException;
import com.github.sdankbar.qml.models.JQMLMapPool;
import com.github.sdankbar.qml.models.flat_tree.JQMLFlatTreeModel;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.github.sdankbar.qml.models.list.JQMLXYSeriesModel;
import com.github.sdankbar.qml.models.singleton.JQMLButtonModel;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;
import com.github.sdankbar.qml.models.singleton.JQMLTextInputModel;
import com.google.common.collect.ImmutableMap;

public class MockJQMLModelFactory implements JQMLModelFactory {

	private final Map<String, JQMLSingletonModel<?>> singletonModels = new HashMap<>();
	private final Map<String, JQMLListModel<?>> listModels = new HashMap<>();
	private final Set<String> modelName = new HashSet<>();

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
	public <K extends Enum<K>> JQMLFlatTreeModel<K> createFlatTreeModel(final String name, final Class<K> enumClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> JQMLFlatTreeModel<K> createFlatTreeModel(final String name, final Set<K> keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K extends Enum<K>> JQMLListModel<K> createListModel(final String name, final Class<K> enumClass) {
		checkModelName(name);
		final JQMLListModel<K> temp = new MockJQMLListModel<>(name);
		listModels.put(name, temp);
		return temp;
	}

	@Override
	public <K> JQMLListModel<K> createListModel(final String name, final Set<K> keys) {
		checkModelName(name);
		final JQMLListModel<K> temp = new MockJQMLListModel<>(name);
		listModels.put(name, temp);
		return temp;
	}

	@Override
	public <K extends Enum<K>> JQMLMapPool<K> createPool(final String name, final Class<K> enumClass,
			final ImmutableMap<K, JVariant> initialValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> JQMLMapPool<K> createPool(final String name, final Set<K> keys,
			final ImmutableMap<K, JVariant> initialValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K extends Enum<K>> JQMLSingletonModel<K> createSingletonModel(final String name, final Class<K> enumClass) {
		checkModelName(name);
		final JQMLSingletonModel<K> temp = new MockJQMLSingletonModel<>(name);
		singletonModels.put(name, temp);
		return temp;
	}

	@Override
	public <K> JQMLSingletonModel<K> createSingletonModel(final String name, final Set<K> keys) {
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

	public JQMLListModel<?> getListModel(final String name) {
		return listModels.get(name);
	}

	public JQMLSingletonModel<?> getSingletonModel(final String name) {
		return singletonModels.get(name);
	}

}
