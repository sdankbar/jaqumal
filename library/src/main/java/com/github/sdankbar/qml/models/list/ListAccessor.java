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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import com.github.sdankbar.qml.JQMLExceptionHandling;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.jna.list.ListQMLAPIFast;
import com.github.sdankbar.qml.cpp.memory.SharedJavaCppMemory;
import com.github.sdankbar.qml.models.MapAccessor;

/**
 * Implementation of MapAccessor that is used to modify one of the maps in a
 * JQMLListModel.
 */
public class ListAccessor extends MapAccessor {

	private int listIndex;

	/**
	 * Constructor.
	 *
	 * @param javaToCppMemory Memory used for Java to C++ communication.
	 * @param cppToJavaMemory Memory used for C++ to Java communication.
	 */
	public ListAccessor(final SharedJavaCppMemory javaToCppMemory, final SharedJavaCppMemory cppToJavaMemory) {
		super(javaToCppMemory, cppToJavaMemory);
	}

	private void checkIndex() {
		if (listIndex < 0) {
			throw new IllegalStateException("Map is no longer valid due to its removal from the list model");
		}
	}

	@Override
	public void clear() {
		checkIndex();
		ListQMLAPIFast.clearAllGenericListModelData(modelPointer, listIndex);
		JQMLExceptionHandling.checkExceptions();
	}

	/**
	 * Creates a copy of this ListAccessor with a different list index.
	 *
	 * @param listIndex Index into the list model that the new ListAccessor will
	 *                  modify.
	 * @return The copied accessor.
	 */
	public ListAccessor copy(final int listIndex) {
		final ListAccessor a = new ListAccessor(javaToCppMemory, cppToJavaMemory);
		a.setModelPointer(modelPointer);
		a.setListIndex(listIndex);
		return a;
	}

	@Override
	public Optional<JVariant> get(final int roleIndex, final IntByReference length) {
		checkIndex();

		final Pointer received = ListQMLAPIFast.getGenericListModelData(modelPointer, listIndex, roleIndex, length);
		JQMLExceptionHandling.checkExceptions();
		return deserialize(received, length.getValue());
	}

	/**
	 * @return The index into the list model that this accessor works on.
	 */
	public int getIndex() {
		return listIndex;
	}

	@Override
	public Optional<JVariant> remove(final int roleIndex, final IntByReference length) {
		checkIndex();

		final Optional<JVariant> existingValue = get(roleIndex, length);

		ListQMLAPIFast.clearGenericListModelData(modelPointer, listIndex, roleIndex);
		JQMLExceptionHandling.checkExceptions();

		return existingValue;
	}

	@Override
	public void set(final JVariant value, final int roleIndex) {
		checkIndex();

		value.serialize(javaToCppMemory);
		ListQMLAPIFast.setGenericListModelData(modelPointer, listIndex, javaToCppMemory.getPointer(), roleIndex);
		JQMLExceptionHandling.checkExceptions();
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
		ListQMLAPIFast.setGenericListModelDataMulti(modelPointer, listIndex, javaToCppMemory.getPointer(), array,
				valuesMap.size());
		JQMLExceptionHandling.checkExceptions();
	}

	/**
	 * @param listIndex The index indo the list model that this accessor works on.
	 */
	public void setListIndex(final int listIndex) {
		this.listIndex = listIndex;
	}
}
