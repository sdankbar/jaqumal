package com.github.sdankbar.qml.cpp.jni.interfaces;

/**
 * Callback for when the C++/QML layer requests to log a message.
 */
public interface LoggingCallback {
	/**
	 * Callback to log a message.
	 *
	 * @param type             The logging type, trace=0, debug=1, info=2, warn=3,
	 *                         error=4.
	 * @param formattedMessage The message to log.
	 */
	void invoke(int type, String formattedMessage);
}