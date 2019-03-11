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
package com.github.sdankbar.qml.cpp.jna.list;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class ListQMLAPIFast {

	static {
		Native.register("Jaqumal");
	}

	public static native int appendGenericListModelData(Pointer modelPointer, Pointer data, int roleIndex);

	public static native int appendGenericListModelDataMulti(Pointer modelPointer, Pointer data, int[] roleIndex,
			int valueCount);

	public static native void clearAllGenericListModelData(Pointer modelPointer, int index);

	public static native void clearGenericListModelData(Pointer modelPointer, int index, int roleIndex);

	public static native void eraseGenericListModelData(Pointer modelPointer, int index);

	public static native Pointer getGenericListModelData(Pointer modelPointer, int index, int roleIndex,
			IntByReference length);

	public static native int getGenericListModelSize(Pointer modelPointer);

	public static native void insertGenericListModelData(Pointer modelPointer, int index, Pointer data, int roleIndex);

	public static native void insertGenericListModelDataMulti(Pointer modelPointer, int index, Pointer data,
			int[] roleIndices, int valueCount);

	public static native boolean isGenericListModelRolePresent(Pointer modelPointer, int index, int roleIndex);

	public static native void reorderGenericListModel(Pointer modelPointer, int[] ordering, int length);

	public static native void setGenericListModelData(Pointer modelPointer, int row, Pointer data, int roleIndex);

	public static native void setGenericListModelDataMulti(Pointer modelPointer, int row, Pointer data,
			int[] roleIndices, int valueCount);

	private ListQMLAPIFast() {
		// Empty Implementation
	}

}
