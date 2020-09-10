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

import java.util.Map;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.models.interfaces.ChangeListener;

/**
 * A model that is available to QML. Represents a single Map from the key type
 * K, to a JVariant.
 *
 * @param <K> The type of the K in the Map.
 */
public interface JQMLSingletonModel<K> extends Map<K, JVariant> {

	/**
	 * Registers a listener to listen for changes to this model's values.
	 *
	 * @param l The change listener.
	 */
	void registerChangeListener(final ChangeListener l);

	/**
	 * @return The name of the QML model this map is a part of.
	 */
	String getModelName();

}
