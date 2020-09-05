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
	 * @param key  Key/Role name that changed.
	 * @param data New value.
	 */
	void invoke(String key, JVariant data);
}