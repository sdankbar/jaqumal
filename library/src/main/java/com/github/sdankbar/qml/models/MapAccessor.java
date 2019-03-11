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
package com.github.sdankbar.qml.models;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.memory.SharedJavaCppMemory;

/**
 * Abstract class for modifying maps contained in QML models.
 */
public abstract class MapAccessor {

	protected final SharedJavaCppMemory javaToCppMemory;
	protected final SharedJavaCppMemory cppToJavaMemory;

	protected Pointer modelPointer;

	protected MapAccessor(final SharedJavaCppMemory javaToCppMemory, final SharedJavaCppMemory cppToJavaMemory) {
		this.javaToCppMemory = Objects.requireNonNull(javaToCppMemory, "javaToCppMemory is null");
		this.cppToJavaMemory = Objects.requireNonNull(cppToJavaMemory, "cppToJavaMemory is null");
	}

	/**
	 * Remove all values from the map.
	 */
	public abstract void clear();

	protected Optional<JVariant> deserialize(final Pointer p, final int length) {
		Preconditions.checkArgument(length >= 0, "length must be positive.");

		if (p == null || Pointer.nativeValue(p) == 0) {
			return Optional.empty();
		} else if (Pointer.nativeValue(cppToJavaMemory.getPointer()) == Pointer.nativeValue(p)) {
			return JVariant.deserialize(cppToJavaMemory.getBuffer(0));
		} else {
			final ByteBuffer buffer = p.getByteBuffer(0, length);
			buffer.order(ByteOrder.nativeOrder());
			return JVariant.deserialize(buffer);
		}
	}

	/**
	 * Return the map's value for the role.
	 *
	 * @param roleIndex Index of the role to return the value of.
	 * @param length    Reference to an integer that will be populated with length
	 *                  of the serialized data being returned.
	 * @return Value of the role or Optional.empty()
	 */
	public abstract Optional<JVariant> get(final int roleIndex, IntByReference length);

	/**
	 * @return A reference to the shared memory used to send data between Java and
	 *         C++,
	 */
	public SharedJavaCppMemory getJavaToCppMemory() {
		return javaToCppMemory;
	}

	/**
	 * Removes the map's value for the role.
	 *
	 * @param roleIndex Index of the role to remove.
	 * @param length    Reference to an integer that will be populated with length
	 *                  of the serialized data being returned.
	 * @return Existing Value of the role or Optional.empty() if it had no value.
	 */
	public abstract Optional<JVariant> remove(int roleIndex, final IntByReference length);

	/**
	 * Puts a new value into the map.
	 *
	 * @param value     The value to put.
	 * @param roleIndex The Index of the role to modify.
	 */
	public abstract void set(JVariant value, int roleIndex);

	/**
	 * Puts all of the values in valuesMap into the map.
	 *
	 * @param valuesMap Map to insert into this map.
	 */
	public abstract void set(Map<Integer, JVariant> valuesMap);

	/**
	 * Sets the pointer to the C++ model behind this map.
	 *
	 * @param modelPointer Pointer to the C++ model.
	 */
	public void setModelPointer(final Pointer modelPointer) {
		this.modelPointer = Objects.requireNonNull(modelPointer, "modelPointer is null");
	}

}
