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
package com.github.sdankbar.qml.cpp.jni.singleton;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.jni.interfaces.MapChangeCallback;

public class SingletonModelFunctions {

	/**
	 * Creates a new SingletonModel.
	 *
	 * @param modelName The name of the model.
	 * @param roleNames An array of role names.
	 * @return A Pointer to the new model.
	 */
	public static native long createGenericObjectModel(String modelName, String[] roleNames);

	/**
	 * Clears all data from the Map.
	 *
	 * @param modelPointer Pointer to the model.
	 */
	public static native void clearGenericObjectModel(long modelPointer);

	/**
	 * Clears the data for the role.
	 *
	 * @param modelPointer Pointer to the model.
	 * @param role         Index of role to clear.
	 */
	public static native void clearGenericObjectModelRole(long modelPointer, int role);

	public static native JVariant getGenericObjectModelData(long modelPointer, int roleIndex);

	public static native boolean isGenericObjectModelRolePresent(long modelPointer, int role);

	public static native void registerValueChangedCallback(long modelPointer, MapChangeCallback callback);

	public static native void setGenericObjectModelData(long modelPointer);

	public static native void assignGenericObjectModelData(long modelPointer);

	private SingletonModelFunctions() {
		// Empty Implementation
	}

}
