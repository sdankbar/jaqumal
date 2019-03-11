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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import com.github.sdankbar.qml.JQMLExceptionHandling;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.jna.singleton.SingletonQMLAPIFast;
import com.github.sdankbar.qml.cpp.memory.SharedJavaCppMemory;
import com.github.sdankbar.qml.models.MapAccessor;

/**
 * Implementation of MapAccessor that is used to modify the map in an
 * SingletonModel.
 */
public class SingletonMapAccessor extends MapAccessor {

	/**
	 * Constructor.
	 *
	 * @param javaToCppMemory Memory used for Java to C++ communication.
	 * @param cppToJavaMemory Memory used for C++ to Java communication.
	 */
	public SingletonMapAccessor(final SharedJavaCppMemory javaToCppMemory, final SharedJavaCppMemory cppToJavaMemory) {
		super(javaToCppMemory, cppToJavaMemory);
	}

	@Override
	public void clear() {
		SingletonQMLAPIFast.clearGenericObjectModel(modelPointer);
		JQMLExceptionHandling.checkExceptions();
	}

	@Override
	public Optional<JVariant> get(final int roleIndex, final IntByReference length) {
		final Pointer received = SingletonQMLAPIFast.getGenericObjectModelData(modelPointer, roleIndex, length);
		JQMLExceptionHandling.checkExceptions();
		return deserialize(received, length.getValue());
	}

	@Override
	public Optional<JVariant> remove(final int roleIndex, final IntByReference length) {
		final Optional<JVariant> existingValue = get(roleIndex, length);

		SingletonQMLAPIFast.clearGenericObjectModelRole(modelPointer, roleIndex);
		JQMLExceptionHandling.checkExceptions();

		return existingValue;
	}

	@Override
	public void set(final JVariant value, final int roleIndex) {
		value.serialize(javaToCppMemory);
		SingletonQMLAPIFast.setGenericObjectModelData(modelPointer, javaToCppMemory.getPointer(), roleIndex);
		JQMLExceptionHandling.checkExceptions();
	}

	@Override
	public void set(final Map<Integer, JVariant> valuesMap) {
		final List<JVariant> values = new ArrayList<>(valuesMap.size());
		final int[] roles = new int[valuesMap.size()];
		int i = 0;
		for (final Map.Entry<Integer, JVariant> e : valuesMap.entrySet()) {
			values.add(e.getValue());
			roles[i] = e.getKey().intValue();
			++i;
		}

		JVariant.serialize(values, javaToCppMemory);
		SingletonQMLAPIFast.setGenericObjectModelDataMulti(modelPointer, javaToCppMemory.getPointer(), roles,
				valuesMap.size());
		JQMLExceptionHandling.checkExceptions();
	}

}
