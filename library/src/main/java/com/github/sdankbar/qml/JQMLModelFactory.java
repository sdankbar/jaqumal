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
package com.github.sdankbar.qml;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.github.sdankbar.qml.cpp.ApiInstance;
import com.github.sdankbar.qml.cpp.memory.SharedJavaCppMemory;
import com.github.sdankbar.qml.eventing.builtin.RenderEvent;
import com.github.sdankbar.qml.exceptions.QMLException;
import com.github.sdankbar.qml.exceptions.QMLThreadingException;
import com.github.sdankbar.qml.models.JQMLMapPool;
import com.github.sdankbar.qml.models.flat_tree.FlatTreeAccessor;
import com.github.sdankbar.qml.models.flat_tree.JQMLFlatTreeModel;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.github.sdankbar.qml.models.list.JQMLXYSeriesModel;
import com.github.sdankbar.qml.models.list.ListAccessor;
import com.github.sdankbar.qml.models.singleton.JQMLButtonModel;
import com.github.sdankbar.qml.models.singleton.JQMLPerformanceModel;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;
import com.github.sdankbar.qml.models.singleton.JQMLTextInputModel;
import com.github.sdankbar.qml.models.singleton.SingletonMapAccessor;
import com.google.common.collect.ImmutableMap;

/**
 * The JQMLModelFactory allows for the creation of Singleton, List, and Tree
 * models that are available for access by QML.
 *
 *
 */
public class JQMLModelFactory {

	private final JQMLApplication<?> app;
	private final AtomicReference<Thread> eventLoopThread;
	private final JQMLPerformanceModel perfModel;

	private final SharedJavaCppMemory cppToJava = new SharedJavaCppMemory(16 * 1024 * 1024);
	private final SharedJavaCppMemory javaToCpp = new SharedJavaCppMemory(16 * 1024 * 1024);

	private final Set<String> modelName = new HashSet<>();

	/**
	 * Constructs a new factory.
	 *
	 * @param app             The parent JQMLApplication.
	 * @param eventLoopThread A reference to the Qt Thread.
	 */
	JQMLModelFactory(final JQMLApplication<?> app, final AtomicReference<Thread> eventLoopThread) {
		this.app = Objects.requireNonNull(app, "app is null");
		this.eventLoopThread = Objects.requireNonNull(eventLoopThread, "eventLoopThread is null");

		ApiInstance.LIB_INSTANCE.setSharedMemory(cppToJava.getPointer(), cppToJava.getSize());

		perfModel = new JQMLPerformanceModel("PerfModel", this);
		app.getEventDispatcher().register(RenderEvent.class, perfModel);
	}

	private void checkModelName(final String name) {
		if (!modelName.add(Objects.requireNonNull(name, "name is null"))) {
			throw new QMLException("Model with name [" + name + "] already exists");
		}
	}

	/**
	 * Creates a new JQMLButtonModel with the specified name.
	 *
	 * @param name Name of the model.
	 * @return The new model
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 */
	@QtThread()
	public JQMLButtonModel createButtonModel(final String name) {
		JQMLUtilities.checkThread(eventLoopThread);

		return new JQMLButtonModel(name, this, app.getEventDispatcher());
	}

	/**
	 * Creates a new JQMLFlatTreeModel with Class<K> being its key.
	 *
	 * @param name      Name of the model.
	 * @param enumClass Class of the Enum that is used as the new model's key.
	 * @return The new model.
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 */
	@QtThread
	public <K extends Enum<K>> JQMLFlatTreeModel<K> createFlatTreeModel(final String name, final Class<K> enumClass) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		return new JQMLFlatTreeModel<>(name, EnumSet.allOf(enumClass), eventLoopThread,
				new FlatTreeAccessor(javaToCpp, cppToJava));
	}

	/**
	 * Creates a new JQMLFlatTreeModel with Class<K> being its key.
	 *
	 * @param name Name of the model.
	 * @param keys The set of keys that can be used by the new model.
	 * @return The new model.
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 */
	@QtThread
	public <K> JQMLFlatTreeModel<K> createFlatTreeModel(final String name, final Set<K> keys) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		return new JQMLFlatTreeModel<>(name, keys, eventLoopThread, new FlatTreeAccessor(javaToCpp, cppToJava));
	}

	/**
	 * Creates a new JQMLListModel with Class<K> being its key.
	 *
	 * @param name      Name of the model.
	 * @param enumClass Class of the Enum that is used as the new model's key.
	 * @return The new model.
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 */
	@QtThread
	public <K extends Enum<K>> JQMLListModel<K> createListModel(final String name, final Class<K> enumClass) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		return new JQMLListModel<>(name, EnumSet.allOf(enumClass), eventLoopThread,
				new ListAccessor(javaToCpp, cppToJava));
	}

	/**
	 * Creates a new JQMLListModel with Class<K> being its key.
	 *
	 * @param name Name of the model.
	 * @param keys The set of keys that can be used by the new model.
	 * @return The new model.
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 */
	@QtThread
	public <K> JQMLListModel<K> createListModel(final String name, final Set<K> keys) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		return new JQMLListModel<>(name, keys, eventLoopThread, new ListAccessor(javaToCpp, cppToJava));
	}

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
	public <K extends Enum<K>> JQMLMapPool<K> createPool(final String name, final Class<K> enumClass,
			final ImmutableMap<K, JVariant> initialValues) {
		final JQMLListModel<K> model = createListModel(name, enumClass);
		return new JQMLMapPool<>(model, initialValues);
	}

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
	public <K> JQMLMapPool<K> createPool(final String name, final Set<K> keys,
			final ImmutableMap<K, JVariant> initialValues) {
		final JQMLListModel<K> model = createListModel(name, keys);
		return new JQMLMapPool<>(model, initialValues);
	}

	/**
	 * Creates a new JQMLSingletonModel with Class<K> being its key.
	 *
	 * @param name      Name of the model.
	 * @param enumClass Class of the Enum that is used as the new model's key.
	 * @return The new model.
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 */
	@QtThread
	public <K extends Enum<K>> JQMLSingletonModel<K> createSingletonModel(final String name, final Class<K> enumClass) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		return new JQMLSingletonModel<>(name, EnumSet.allOf(enumClass), eventLoopThread,
				new SingletonMapAccessor(javaToCpp, cppToJava));
	}

	/**
	 * Creates a new JQMLSingletonModel with Class<K> being its key.
	 *
	 * @param name Name of the model.
	 * @param keys The set of keys that can be used by the new model.
	 * @return The new model.
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 */
	@QtThread
	public <K> JQMLSingletonModel<K> createSingletonModel(final String name, final Set<K> keys) {
		JQMLUtilities.checkThread(eventLoopThread);
		checkModelName(name);

		return new JQMLSingletonModel<>(name, keys, eventLoopThread, new SingletonMapAccessor(javaToCpp, cppToJava));
	}

	/**
	 * Creates a new JQMLTextInputModel.
	 *
	 * @param name Name of the model.
	 * @return The new model
	 * @throws QMLThreadingException Thrown if not called from the Qt Thread once
	 *                               JQMLApplication.execute() is called.
	 * @throws QMLException          Thrown if a model already exists with name.
	 */
	@QtThread
	public JQMLTextInputModel createTextInputModel(final String name) {
		JQMLUtilities.checkThread(eventLoopThread);

		final JQMLTextInputModel m = new JQMLTextInputModel(name, this, app.getEventDispatcher());

		return m;
	}

	/**
	 * Creates a new JQMLXYSeriesModel which can be used for line/scatter graphs.
	 *
	 * @param name The name of the model.
	 * @return The new model.
	 */
	@QtThread()
	public JQMLXYSeriesModel createXYSeriesModel(final String name) {
		JQMLUtilities.checkThread(eventLoopThread);

		return new JQMLXYSeriesModel(name, this);
	}

}
