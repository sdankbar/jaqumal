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
package com.github.sdankbar.qml.cpp.jna.flat_tree;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * Interface to call C++ functions for controlling Flat Tree models.
 */
public class FlatTreeQMLAPIFast {

	static {
		Native.register("Jaqumal");
	}

	/**
	 * Appends data to the child node list given by indices.
	 *
	 * @param modelPointer Pointer to the model to be modified.
	 * @param indicies     Serialized TreePath containing the node to add the data
	 *                     to.
	 * @param elementCount Count of the number of indexes in indices.
	 * @param data         The data to add.
	 * @param roleIndex    The role the data will be attached to.
	 * @return The index the new node.
	 */
	public static native int appendGenericFlatTreeModelData(Pointer modelPointer, Pointer indicies, int elementCount,
			Pointer data, int roleIndex);

	/**
	 * Appends multiple data objects to the child node list given by indices.
	 *
	 * @param modelPointer Pointer to the model to be modified.
	 * @param indicies     Serialized TreePath containing the node to add the data
	 *                     to.
	 * @param elementCount Count of the number of indexes in indices.
	 * @param data         The data to add.
	 * @param roleIndices  The roles the data will be attached to.
	 * @param valueCount   The number of roles in roleIndices.
	 * @return The index the new node.
	 */
	public static native int appendGenericFlatTreeModelDataMulti(Pointer modelPointer, Pointer indicies,
			int elementCount, Pointer data, int[] roleIndices, int valueCount);

	/**
	 * Removes all roles at the node given by indices.
	 *
	 * @param modelPointer Pointer to the model to be modified.
	 * @param indicies     Serialized TreePath containing the node to clear the data
	 *                     from.
	 * @param elementCount Count of the number of indexes in indices.
	 */
	public static native void clearAllGenericFlatTreeModelData(Pointer modelPointer, Pointer indicies,
			int elementCount);

	/**
	 * Removes data for the specified role for the node given by indices.
	 *
	 * @param modelPointer Pointer to the model to be modified.
	 * @param indicies     Serialized TreePath containing the node to clear the data
	 *                     from.
	 * @param elementCount Count of the number of indexes in indices.
	 * @param roleIndex    Role to clear
	 */
	public static native void clearGenericFlatTreeModelData(Pointer modelPointer, Pointer indicies, int elementCount,
			int roleIndex);

	public static native void eraseGenericFlatTreeModelData(Pointer modelPointer, Pointer indicies, int elementCount);

	public static native Pointer getGenericFlatTreeModelData(Pointer modelPointer, Pointer indicies, int elementCount,
			int roleIndex, IntByReference length);

	public static native int getGenericFlatTreeModelSize(Pointer modelPointer, Pointer indicies, int elementCount);

	public static native void insertGenericFlatTreeModelData(Pointer modelPointer, Pointer indicies, int elementCount,
			Pointer data, int roleIndex);

	public static native void insertGenericFlatTreeModelDataMulti(Pointer modelPointer, Pointer indicies,
			int elementCount, Pointer data, int[] roleIndices, int valueCount);

	public static native boolean isGenericFlatTreeModelRolePresent(Pointer modelPointer, Pointer indicies,
			int elementCount, int roleIndex);

	public static native void reorderGenericFlatTreeModel(Pointer modelPointer, Pointer index, int elementCount,
			int[] ordering, int length);

	public static native void setGenericFlatTreeModelData(Pointer modelPointer, Pointer indicies, int elementCount,
			Pointer data, int roleIndex);

	public static native void setGenericFlatTreeModelDataMulti(Pointer modelPointer, Pointer indicies, int elementCount,
			Pointer data, int[] roleIndicies, int valueCount);

	private FlatTreeQMLAPIFast() {
		// Empty Implementation
	}

}
