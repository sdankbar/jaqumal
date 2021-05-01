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
import java.time.Duration;
import java.util.Set;

import com.github.sdankbar.qml.exceptions.QMLException;
import com.github.sdankbar.qml.exceptions.QMLThreadingException;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.JQMLMapPool;
import com.github.sdankbar.qml.models.flat_tree.JQMLFlatTreeModel;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.github.sdankbar.qml.models.list.JQMLListViewModel;
import com.github.sdankbar.qml.models.list.JQMLListViewModel.SelectionMode;
import com.github.sdankbar.qml.models.list.JQMLXYSeriesModel;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * The JQMLModelFactory allows for the creation of Singleton, List, and Tree
 * models that are available for access by QML.
 *
 *
 */
public interface JQMLModelFactory {

	/**
	 * Creates a new JQMLFlatTreeModel with Class&lt;K&gt; being its key.
	 *
	 * @param name      Name of the model.
	 * @param enumClass Class of the Enum that is used as the new model's key.
	 * @return The new model.
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 */
	@QtThread
	<K extends Enum<K>> JQMLFlatTreeModel<K> createFlatTreeModel(final String name, final Class<K> enumClass,
			final PutMode putMode);

	/**
	 * Creates a new JQMLFlatTreeModel with Class&lt;K&gt; being its key.
	 *
	 * @param name Name of the model.
	 * @param keys The set of keys that can be used by the new model.
	 * @return The new model.
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 */
	@QtThread
	<K> JQMLFlatTreeModel<K> createFlatTreeModel(final String name, final Set<K> keys, final PutMode putMode);

	/**
	 * Creates a new JQMLListModel with Class&lt;K&gt; being its key.
	 *
	 * @param name      Name of the model.
	 * @param enumClass Class of the Enum that is used as the new model's key.
	 * @return The new model.
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 */
	@QtThread
	<K extends Enum<K>> JQMLListModel<K> createListModel(final String name, final Class<K> enumClass,
			final PutMode putMode);

	/**
	 * Creates a new JQMLListModel with Class&lt;K&gt; being its key.
	 *
	 * @param name Name of the model.
	 * @param keys The set of keys that can be used by the new model.
	 * @return The new model.
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 */
	@QtThread
	<K> JQMLListModel<K> createListModel(final String name, final Set<K> keys, final PutMode putMode);

	/**
	 * Creates a JQMLListModel and wraps it in a JQMLMapPool that manages it.
	 *
	 * @param name          Name of the model.
	 * @param enumClass     Class of the Enum that is used as the new model's key.
	 * @param initialValues Map of the initial values put into a model's item when
	 *                      allocated and released.
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 * @return The new pool.
	 */
	@QtThread
	<K extends Enum<K>> JQMLMapPool<K> createPool(final String name, final Class<K> enumClass,
			final ImmutableMap<K, JVariant> initialValues, PutMode putMode);

	/**
	 * Creates a JQMLListModel and wraps it in a JQMLMapPool that manages it.
	 *
	 * @param name          Name of the model.
	 * @param keys          The set of keys that can be used by the new model.
	 * @param initialValues Map of the initial values put into a model's item when
	 *                      allocated and released.
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 * @return The new pool.
	 */
	@QtThread
	<K> JQMLMapPool<K> createPool(final String name, final Set<K> keys, final ImmutableMap<K, JVariant> initialValues,
			PutMode putMode);

	/**
	 * Creates a new JQMLSingletonModel with Class&lt;K&gt; being its key.
	 *
	 * @param name      Name of the model.
	 * @param enumClass Class of the Enum that is used as the new model's key.
	 * @return The new model.
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 */
	@QtThread
	<K extends Enum<K>> JQMLSingletonModel<K> createSingletonModel(final String name, final Class<K> enumClass,
			final PutMode putMode);

	/**
	 * Creates a new JQMLSingletonModel with Class&lt;K&gt; being its key.
	 *
	 * @param name Name of the model.
	 * @param keys The set of keys that can be used by the new model.
	 * @return The new model.
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 */
	@QtThread
	<K> JQMLSingletonModel<K> createSingletonModel(final String name, final Set<K> keys, final PutMode putMode);

	/**
	 * Creates a new JQMLXYSeriesModel which can be used for line/scatter graphs.
	 *
	 * @param name The name of the model.
	 * @return The new model.
	 */
	@QtThread()
	JQMLXYSeriesModel createXYSeriesModel(final String name);

	/**
	 * Creates a new model.
	 *
	 * @param modelName Name of the model. Must be unique.
	 * @param keyClass  Class of the Enum that is used as the new model's key.
	 * @param mode      The selection mode for the model.
	 * @param putMode   Specifies how put operations behave.
	 * @return The new model.
	 */
	<K extends Enum<K>> JQMLListViewModel<K> createListViewModel(final String modelName, final Class<K> keyClass,
			final SelectionMode mode, final PutMode putMode);

	/**
	 * Creates a new model.
	 *
	 * @param modelName Name of the model. Must be unique.
	 * @param keySet    The set of keys that can be used by the new model.
	 * @param mode      The selection mode for the model.
	 * @param putMode   Specifies how put operations behave.
	 * @return The new model.
	 */
	<K> JQMLListViewModel<K> createListViewModel(final String modelName, final ImmutableSet<K> keySet,
			final SelectionMode mode, final PutMode putMode);

	void enablePersistence(final Duration writeDelay, final File persistenceDirectory);

	void enableAutoPersistenceForModel(final JQMLSingletonModel<?> model);

	void enableAutoPersistenceForModel(final JQMLListModel<?> model);

	void persistModel(final JQMLSingletonModel<?> model);

	void persistModel(final JQMLListModel<?> model);

	boolean restoreModel(final JQMLSingletonModel<?> model);

	boolean restoreModel(final JQMLListModel<?> model);
}
