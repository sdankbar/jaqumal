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
package com.github.sdankbar.qml.models.flat_tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.jni.flat_tree.FlatTreeQMLAPIFast;
import com.github.sdankbar.qml.cpp.memory.SharedJavaCppMemory;
import com.github.sdankbar.qml.models.MapAccessor;
import com.github.sdankbar.qml.models.TreePath;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * Implementation of MapAccessor that is used to modify one of the Maps in a
 * FlatTreeModel.
 */
public class FlatTreeAccessor extends MapAccessor {

	private TreePath path;

	/**
	 * Constructor.
	 *
	 * @param javaToCppMemory Memory used for Java to C++ communication.
	 * @param cppToJavaMemory Memory used for C++ to Java communication.
	 */
	public FlatTreeAccessor(final SharedJavaCppMemory javaToCppMemory, final SharedJavaCppMemory cppToJavaMemory) {
		super(javaToCppMemory, cppToJavaMemory);
	}

	private void checkIndex() {
		if (path == null) {
			throw new IllegalStateException("Map is no longer valid due to its removal from the list model");
		}
	}

	@Override
	public void clear() {
		checkIndex();

		final Pointer indiciesMem = path.serialize();
		FlatTreeQMLAPIFast.clearAllGenericFlatTreeModelData(Pointer.nativeValue(modelPointer), path.toArray());

	}

	/**
	 * Creates a copy of this FlatTreeAccessor with a different TreePath.
	 *
	 * @param p TreePath into the tree model that the new FlatTreeAccessor will
	 *          modify.
	 * @return The copied accessor.
	 */
	public FlatTreeAccessor copy(final TreePath p) {
		final FlatTreeAccessor a = new FlatTreeAccessor(javaToCppMemory, cppToJavaMemory);
		a.setTreePath(p);
		a.setModelPointer(modelPointer);
		return a;
	}

	@Override
	public Optional<JVariant> get(final int roleIndex, final IntByReference length) {
		checkIndex();

		final JVariant received = FlatTreeQMLAPIFast.getGenericFlatTreeModelData(Pointer.nativeValue(modelPointer),
				path.toArray(), roleIndex);
		return Optional.ofNullable(received);
	}

	/**
	 * @return This accessor's TreePath into its parent model.
	 */
	public TreePath getTreePath() {
		return path;
	}

	@Override
	public Optional<JVariant> remove(final int roleIndex, final IntByReference length) {
		checkIndex();

		final Optional<JVariant> oldValue = get(roleIndex, length);

		final Pointer indiciesMem = path.serialize();
		FlatTreeQMLAPIFast.clearGenericFlatTreeModelData(Pointer.nativeValue(modelPointer), path.toArray(), roleIndex);

		return oldValue;
	}

	@Override
	public void set(final JVariant value, final int roleIndex) {
		checkIndex();

		value.serialize(javaToCppMemory);
		// TODO
		FlatTreeQMLAPIFast.setGenericFlatTreeModelData(Pointer.nativeValue(modelPointer), path.toArray());

	}

	@Override
	public void set(final Map<Integer, JVariant> valuesMap) {
		final List<JVariant> variantList = new ArrayList<>(valuesMap.size());
		final int[] array = new int[valuesMap.size()];
		int i = 0;
		for (final Map.Entry<Integer, JVariant> e : valuesMap.entrySet()) {
			variantList.add(e.getValue());
			array[i] = e.getKey().intValue();
			++i;
		}

		checkIndex();

		JVariant.serialize(variantList, javaToCppMemory);

		final Pointer indiciesMem = path.serialize();
		// TODO
		FlatTreeQMLAPIFast.setGenericFlatTreeModelData(Pointer.nativeValue(modelPointer), path.toArray());

	}

	/**
	 * @param p TreePath into the tree model that this FlatTreeAccessor will modify.
	 */
	public void setTreePath(final TreePath p) {
		path = p;
	}

}
