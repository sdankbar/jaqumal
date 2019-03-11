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
package com.github.sdankbar.qml.cpp.jna.singleton;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

/**
 * Interface used by JNA to call ListModel functions. This is not part of
 * ListQMLAPIFast because JNA direct mapping does not support arrays of strings.
 */
public interface SingletonQMLAPI extends Library {

	/**
	 * Interface for getting callbacks from the C++ Singleton model that a value has
	 * changed.
	 */
	public interface MapChangeCallback extends Callback {
		/**
		 * Called when a value changes in the map.
		 *
		 * @param key          Key/Role name that changed.
		 * @param newValueData Pointer to the new value.
		 * @param dataLength   Length of the data pointed to by newValueData.
		 */
		void invoke(String key, Pointer newValueData, int dataLength);
	}

	/**
	 * Creates a new SingletonModel.
	 *
	 * @param modelName   The name of the model.
	 * @param roleNames   An array of role names.
	 * @param roleIndices An array of the indices for each role. Maps to the names
	 *                    in roleNames.
	 * @param length      Length of roleNames and roleIndices.
	 * @return A Pointer to the new model.
	 */
	Pointer createGenericObjectModel(String modelName, String[] roleNames, int length);

}
