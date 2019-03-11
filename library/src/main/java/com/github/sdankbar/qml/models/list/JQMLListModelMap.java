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
package com.github.sdankbar.qml.models.list;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.github.sdankbar.qml.models.AbstractJQMLMapModel;

/**
 * Implementation of the Map interface that provides accessors/modifiers to a
 * map in a list model.
 *
 * @param <K> Type of the Map's key.
 */
public class JQMLListModelMap<K> extends AbstractJQMLMapModel<K> {

	private final ListAccessor mapAccessor;

	/**
	 * Constructor
	 *
	 * @param modelName       The name of the containing list model.
	 * @param keys            Set of all possible keys.
	 * @param eventLoopThread Reference to the Qt thread.
	 * @param accessor        Accessor used be update the C++ portion of the model.
	 * @param indexLookup     Map from a Key's toString() value to its integer
	 *                        index.
	 */
	public JQMLListModelMap(final String modelName, final Set<K> keys, final AtomicReference<Thread> eventLoopThread,
			final ListAccessor accessor, final Map<String, Integer> indexLookup) {
		super(modelName, keys, eventLoopThread, accessor);
		this.mapAccessor = accessor;

		this.indexLookup.putAll(indexLookup);
	}

	/**
	 * @return The index into the list model.
	 */
	public int getIndex() {
		return mapAccessor.getIndex();
	}

	/**
	 * @param listIndex The index into the list model.
	 */
	public void setIndex(final int listIndex) {
		mapAccessor.setListIndex(listIndex);
	}

}
