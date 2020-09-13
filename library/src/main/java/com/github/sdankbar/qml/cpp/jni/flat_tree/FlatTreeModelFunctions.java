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
package com.github.sdankbar.qml.cpp.jni.flat_tree;

import com.github.sdankbar.qml.JVariant;

/**
 * Interface to call C++ functions for controlling Flat Tree models.
 */
public class FlatTreeModelFunctions {

	/**
	 * Creates a new FlatTreeModel.
	 *
	 * @param modelName   The name of the model.
	 * @param roleNames   An array of role names.
	 * @param roleIndices An array of the indices for each role. Maps to the names
	 *                    in roleNames.
	 * @param length      Length of roleNames and roleIndices.
	 * @return A Pointer to the new model.
	 */
	public static native long createGenericFlatTreeModel(String modelName, String[] roleNames, int[] roleIndices);

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
	public static native int appendGenericFlatTreeModelData(long modelPointer, int[] indicies);

	/**
	 * Removes all roles at the node given by indices.
	 *
	 * @param modelPointer Pointer to the model to be modified.
	 * @param indicies     Serialized TreePath containing the node to clear the data
	 *                     from.
	 * @param elementCount Count of the number of indexes in indices.
	 */
	public static native void clearAllGenericFlatTreeModelData(long modelPointer, int[] indicies);

	/**
	 * Removes data for the specified role for the node given by indices.
	 *
	 * @param modelPointer Pointer to the model to be modified.
	 * @param indicies     Serialized TreePath containing the node to clear the data
	 *                     from.
	 * @param elementCount Count of the number of indexes in indices.
	 * @param roleIndex    Role to clear
	 */
	public static native void clearGenericFlatTreeModelData(long modelPointer, int[] indices, int roleIndex);

	public static native void eraseGenericFlatTreeModelData(long modelPointer, int[] indices);

	public static native JVariant getGenericFlatTreeModelData(long modelPointer, int[] indices, int roleIndex);

	public static native int getGenericFlatTreeModelSize(long modelPointer, int[] indices);

	public static native void insertGenericFlatTreeModelData(long modelPointer, int[] indices);

	public static native boolean isGenericFlatTreeModelRolePresent(long modelPointer, int[] indices, int roleIndex);

	public static native void reorderGenericFlatTreeModel(long modelPointer, int[] indices, int[] ordering);

	public static native void setGenericFlatTreeModelData(long modelPointer, int[] indices);

	public static native void assignGenericFlatTreeModelData(long modelPointer, int[] indices);

	private FlatTreeModelFunctions() {
		// Empty Implementation
	}

}
