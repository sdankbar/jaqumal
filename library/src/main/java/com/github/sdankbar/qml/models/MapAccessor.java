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

import java.util.Optional;

import com.github.sdankbar.qml.JVariant;

/**
 * Abstract class for modifying maps contained in QML models.
 */
public abstract class MapAccessor {

	protected long modelPointer;

	/**
	 * Assigns the passed in map to this map. Equivalent to clear() followed by
	 * putAll().
	 *
	 * @param roles Array of the role integers.
	 * @param data  Array of the JVariants.
	 */
	public abstract void assign(int[] roles, JVariant[] data);

	/**
	 * Remove all values from the map.
	 */
	public abstract void clear();

	/**
	 * Return the map's value for the role.
	 *
	 * @param roleIndex Index of the role to return the value of.
	 * @param length    Reference to an integer that will be populated with length
	 *                  of the serialized data being returned.
	 * @return Value of the role or Optional.empty()
	 */
	public abstract Optional<JVariant> get(final int roleIndex);

	/**
	 * Removes the map's value for the role.
	 *
	 * @param roleIndex Index of the role to remove.
	 * @param length    Reference to an integer that will be populated with length
	 *                  of the serialized data being returned.
	 * @return Existing Value of the role or Optional.empty() if it had no value.
	 */
	public abstract Optional<JVariant> remove(int roleIndex);

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
	 * @param roles Array of the role integers.
	 * @param data  Array of the JVariants.
	 */
	public abstract void set(int[] roles, JVariant[] data);

	/**
	 * Sets the pointer to the C++ model behind this map.
	 *
	 * @param modelPointer Pointer to the C++ model.
	 */
	public void setModelPointer(final long modelPointer) {
		this.modelPointer = modelPointer;
	}

	protected void sendToQML(final int[] roles, final JVariant[] data) {
		final int size = roles.length;
		for (int i = 0; i < size; ++i) {
			data[i].sendToQML(roles[i]);
		}
	}

}
