package com.github.sdankbar.qml.cpp.jni;

import com.github.sdankbar.qml.cpp.jni.interfaces.EventCallback;

public class EventFunctions {
	/**
	 * Adds a callback to be called when an event is created.
	 *
	 * @param c The callback. Caller is responsible to ensuring the EventCallback
	 *          object is not garbage collected.
	 */
	public static native void addEventCallback(EventCallback c);

	// TODO determine argument types
	public static native void sendQMLEvent(String eventName,
			String[] keys/* , Pointer valuesPointer, int keyValuesCount */);

}
