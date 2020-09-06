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
import java.util.Optional;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.jni.list.ListModelFunctions;
import com.github.sdankbar.qml.models.MapAccessor;

/**
 * Implementation of MapAccessor that is used to modify one of the maps in a
 * JQMLListModel.
 */
public class ListAccessor extends MapAccessor {

	private int listIndex;

	private void checkIndex() {
		if (listIndex < 0) {
			throw new IllegalStateException("Map is no longer valid due to its removal from the list model");
		}
	}

	@Override
	public void clear() {
		checkIndex();
		ListModelFunctions.clearAllGenericListModelData(modelPointer, listIndex);

	}

	/**
	 * Creates a copy of this ListAccessor with a different list index.
	 *
	 * @param listIndex Index into the list model that the new ListAccessor will
	 *                  modify.
	 * @return The copied accessor.
	 */
	public ListAccessor copy(final int listIndex) {
		final ListAccessor a = new ListAccessor();
		a.setModelPointer(modelPointer);
		a.setListIndex(listIndex);
		return a;
	}

	@Override
	public Optional<JVariant> get(final int roleIndex) {
		checkIndex();

		final JVariant received = ListModelFunctions.getGenericListModelData(modelPointer, listIndex, roleIndex);

		return Optional.ofNullable(received);
	}

	/**
	 * @return The index into the list model that this accessor works on.
	 */
	public int getIndex() {
		return listIndex;
	}

	@Override
	public Optional<JVariant> remove(final int roleIndex) {
		checkIndex();

		final Optional<JVariant> existingValue = get(roleIndex);

		ListModelFunctions.clearGenericListModelData(modelPointer, listIndex, roleIndex);

		return existingValue;
	}

	@Override
	public void set(final JVariant value, final int roleIndex) {
		checkIndex();

		value.sendToQML(roleIndex);
		ListModelFunctions.setGenericListModelData(modelPointer, listIndex);
	}

	@Override
	public void set(final Map<Integer, JVariant> valuesMap) {
		for (final Map.Entry<Integer, JVariant> e : valuesMap.entrySet()) {
			e.getValue().sendToQML(e.getKey().intValue());
		}

		checkIndex();

		ListModelFunctions.setGenericListModelData(modelPointer, listIndex);

	}

	/**
	 * @param listIndex The index into the list model that this accessor works on.
	 */
	public void setListIndex(final int listIndex) {
		this.listIndex = listIndex;
	}
}
