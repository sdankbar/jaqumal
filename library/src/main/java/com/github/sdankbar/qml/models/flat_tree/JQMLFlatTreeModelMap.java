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
package com.github.sdankbar.qml.models.flat_tree;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.github.sdankbar.qml.models.AbstractJQMLMapModel;
import com.github.sdankbar.qml.models.TreePath;

/**
 * Implementation of the Map interface that provides accessors/modifiers to a
 * map in a flat tree model.
 *
 * @param <K> Type of the Map's key.
 */
public class JQMLFlatTreeModelMap<K> extends AbstractJQMLMapModel<K> {

	private final FlatTreeAccessor accessor;

	/**
	 * Constructor
	 *
	 * @param modelName       The name of the containing flat tree model.
	 * @param keys            Set of all possible keys.
	 * @param eventLoopThread Reference to the Qt thread.
	 * @param accessor        Accessor used be update the C++ portion of the model.
	 * @param indexLookup     Map from a Key's toString() value to its integer
	 *                        index.
	 * @param putMode         Specifies how put operations behave.
	 */
	public JQMLFlatTreeModelMap(final String modelName, final Set<K> keys,
			final AtomicReference<Thread> eventLoopThread, final FlatTreeAccessor accessor,
			final Map<String, Integer> indexLookup, final PutMode putMode) {
		super(modelName, keys, eventLoopThread, accessor, putMode);
		this.accessor = Objects.requireNonNull(accessor, "accessor is null");
		this.indexLookup.putAll(Objects.requireNonNull(indexLookup, "indexLookup is null"));
	}

	/**
	 * @return The TreePath into the flat tree model this Map is at.
	 */
	public TreePath getIndex() {
		return accessor.getTreePath();
	}

	/**
	 * @param p The TreePath into the flat tree model this Map is at.
	 */
	public void setIndex(final TreePath p) {
		accessor.setTreePath(p);
	}

}
