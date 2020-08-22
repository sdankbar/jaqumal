package com.github.sdankbar.qml.cpp.jni.interfaces;

/**
 * Interface for having Java code called from the Qt Event Loop Thread.
 */
public interface InvokeCallback {
	/**
	 * Called from the Qt Event Loop Thread.
	 */
	void invoke();
}