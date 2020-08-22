package com.github.sdankbar.qml.cpp.jni.list;

import com.github.sdankbar.qml.JVariant;

public final class ListModelFunctions {

	private ListModelFunctions() {}
	
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

	public static native void putRootValueIntoListModel(long modelPointer, String key, JVariant data);

	public static native void removeRootValueFromListModel(long modelPointer, String key);

	public static native void reorderGenericListModel(long modelPointer, int[] ordering);

	public static native void setGenericListModelData(long modelPointer, int row);
}
