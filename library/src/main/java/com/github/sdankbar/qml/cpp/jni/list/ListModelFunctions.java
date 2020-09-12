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
package com.github.sdankbar.qml.cpp.jni.list;

import com.github.sdankbar.qml.JVariant;

public final class ListModelFunctions {

	private ListModelFunctions() {
		// Empty Implementation
	}

	/**
	 * Creates a new ListModel.
	 *
	 * @param modelName   The name of the model.
	 * @param roleNames   An array of role names.
	 * @param roleIndices An array of the indices for each role. Maps to the names
	 *                    in roleNames.
	 * @param length      Length of roleNames and roleIndices.
	 * @return A Pointer to the new model.
	 */
	public static native long createGenericListModel(String modelName, String[] roleNames, int[] roleIndices);

	public static native int appendGenericListModelData(long modelPointer);

	public static native void clearAllGenericListModelData(long modelPointer, int index);

	public static native void clearGenericListModelData(long modelPointer, int index, int roleIndex);

	public static native void eraseGenericListModelData(long modelPointer, int index);

	public static native JVariant getGenericListModelData(long modelPointer, int index, int roleIndex);

	public static native int getGenericListModelSize(long modelPointer);

	public static native JVariant getRootValueFromListModel(long modelPointer, String key);

	public static native void insertGenericListModelData(long modelPointer, int index);

	public static native boolean isGenericListModelRolePresent(long modelPointer, int index, int roleIndex);

	public static native void putRootValueIntoListModel(long modelPointer, String key);

	public static native void removeRootValueFromListModel(long modelPointer, String key);

	public static native void reorderGenericListModel(long modelPointer, int[] ordering);

	public static native void setGenericListModelData(long modelPointer, int row);

	public static native void assignGenericListModelData(long modelPointer, int row);

	public static native void lockDataChangedSignal(long modelPointer);

	public static native void unlockDataChangedSignal(long modelPointer);
}
