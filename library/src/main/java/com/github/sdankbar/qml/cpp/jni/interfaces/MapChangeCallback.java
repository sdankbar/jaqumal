package com.github.sdankbar.qml.cpp.jni.interfaces;

import com.github.sdankbar.qml.JVariant;

/**
 * Interface for getting callbacks from the C++ Singleton model that a value has
 * changed.
 */
public interface MapChangeCallback {
	/**
	 * Called when a value changes in the map.
	 *
	 * @param key          Key/Role name that changed.
	 * @param newValueData Pointer to the new value.
	 * @param dataLength   Length of the data pointed to by newValueData.
	 */
	void invoke(String key, JVariant data);
}