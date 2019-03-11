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
package com.github.sdankbar.qml.models.singleton;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.jna.Pointer;

import com.github.sdankbar.qml.JQMLExceptionHandling;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.ApiInstance;
import com.github.sdankbar.qml.cpp.jna.singleton.SingletonQMLAPIFast;
import com.github.sdankbar.qml.cpp.jna.singleton.SingletonQMLAPI.MapChangeCallback;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel;
import com.github.sdankbar.qml.models.interfaces.ChangeListener;

/**
 * A model that is available to QML. Represents a single Map from the key type
 * K, to a JVariant.
 *
 * @param <K> The type of the K in the Map.
 */
public class JQMLSingletonModel<K> extends AbstractJQMLMapModel<K> {

	private static class MapChangeListener implements MapChangeCallback {

		private final List<ChangeListener> listeners = new ArrayList<>();

		public void addListener(final ChangeListener l) {
			listeners.add(l);
		}

		public boolean hasListeners() {
			return !listeners.isEmpty();
		}

		@Override
		public void invoke(final String key, final Pointer newValue, final int length) {
			if (newValue == null || Pointer.nativeValue(newValue) == 0) {
				for (final ChangeListener l : listeners) {
					l.valueChanged(key, null);
				}
			} else {
				final ByteBuffer buffer = newValue.getByteBuffer(0, length);
				buffer.order(ByteOrder.nativeOrder());
				final Optional<JVariant> v = JVariant.deserialize(buffer);
				if (v.isPresent()) {
					for (final ChangeListener l : listeners) {
						l.valueChanged(key, v.get());
					}
				}
			}
		}

	}

	private final SingletonMapAccessor mapAccessor;
	private final MapChangeListener changeCallback = new MapChangeListener();
	private final Pointer modelPointer;

	/**
	 * Model constructor.
	 *
	 * @param modelName       The name of the model. This is the name that the model
	 *                        is made available to QML as.
	 * @param keys            Set of keys this model can use.
	 * @param eventLoopThread Reference to the Qt Thread.
	 * @param accessor        Accessor this model will use to to access the C++
	 *                        portion of this model.
	 */
	public JQMLSingletonModel(final String modelName, final Set<K> keys, final AtomicReference<Thread> eventLoopThread,
			final SingletonMapAccessor accessor) {
		super(modelName, keys, eventLoopThread, accessor);
		this.mapAccessor = accessor;

		int i = 0;
		final String[] roleArray = new String[keys.size()];
		for (final K k : keys) {
			final String name = k.toString();
			roleArray[i] = name;
			indexLookup.put(name, Integer.valueOf(i++));
		}

		modelPointer = ApiInstance.SINGLETON_LIB_INSTANCE.createGenericObjectModel(modelName, roleArray,
				roleArray.length);
		JQMLExceptionHandling.checkExceptions();

		mapAccessor.setModelPointer(modelPointer);
	}

	/**
	 * Registers a listener to listen for changes to this model's values.
	 *
	 * @param l The change listener.
	 */
	public void registerChangeListener(final ChangeListener l) {
		verifyEventLoopThread();
		if (!changeCallback.hasListeners()) {
			SingletonQMLAPIFast.registerValueChangedCallback(modelPointer, changeCallback);
			JQMLExceptionHandling.checkExceptions();
		}
		changeCallback.addListener(Objects.requireNonNull(l, "l is null"));
	}
}
