package com.github.sdankbar.qml.cpp.jni.interfaces;

import java.nio.ByteBuffer;

import com.github.sdankbar.qml.JVariant;

/**
 * Interface for those classes wishing to be called back when the C++ or QML
 * layers create an Event.
 */
public interface EventCallback {
	/**
	 * Called when an Event is created.
	 *
	 * @param type   The type of the event.
	 * @param data   The data for the event. The data stored is specific for an
	 *               Event type.
	 * @param length The length of the array pointed to by data.
	 * @return The result of the Event or null if no result.
	 */
	JVariant invoke(String type, ByteBuffer data);
}