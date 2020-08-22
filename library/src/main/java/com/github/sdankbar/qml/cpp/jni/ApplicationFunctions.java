package com.github.sdankbar.qml.cpp.jni;

import java.nio.ByteBuffer;

import com.github.sdankbar.qml.cpp.jni.interfaces.ImageProviderCallback;
import com.github.sdankbar.qml.cpp.jni.interfaces.InvokeCallback;
import com.github.sdankbar.qml.cpp.jni.interfaces.LoggingCallback;

public final class ApplicationFunctions {

	private ApplicationFunctions() {
		// Empty Implementation
	}

	/**
	 * Creates a new QApplication.
	 *
	 * @param argc Command line argument count
	 * @param argv Command line arguments
	 */
	public static native void createQApplication(String[] argv);

	/**
	 * Deletes the current QApplication.
	 */
	public static native void deleteQApplication();

	/**
	 * Calls QApplication::exec()
	 */
	public static native void execQApplication();

	/**
	 * @return The version of Qt the Jaqumal was compiled with.
	 */
	public static native String getCompileQtVersion();

	/**
	 * @return The version of Qt the Jaqumal is linked against.
	 */
	public static native String getRuntimeQtVersion();

	/**
	 * Loads the QML file at the given path.
	 *
	 * @param fileName The path to the QML file to load.
	 */
	public static native void loadQMLFile(String fileName);

	/**
	 * Reloads the QML file at the given path.
	 *
	 * @param fileName The path to the QML file to reload.
	 */
	public static native void reloadQMLFile(String fileName);

	/**
	 * Unloads the currently loaded QML.
	 */
	public static native void unloadQML();

	/**
	 * Sets the callback to call when C++ wants to log.
	 *
	 * @param c The logging callback.
	 */
	public static native void setLoggingCallback(LoggingCallback c);

	/**
	 * Calls QApplication::quit()
	 */
	public static native void quitQApplication();

	/**
	 * @param pathToQMLTestFile Path to QML test file or directory containing test
	 *                          files.
	 * @param importPaths       0 or more paths that QML should use when searching
	 *                          for files to import.
	 * @return 0 on test success, 1 on failure.
	 */
	public static native int runQMLTest(String pathToQMLTestFile, String[] importPaths);

	/**
	 * Registers a new image provider.
	 *
	 * @param id Identifier of the image provider.
	 * @param c  The provider's callback.
	 */
	public static native void addImageProvider(String id, ImageProviderCallback c);

	/**
	 * @return Buffer containing number of screens, then dpi and rectangles.
	 */
	public static native ByteBuffer getScreens();

	/**
	 * Invokes a callback on the Qt thread.
	 *
	 * @param callback The callback to call. Caller is responsible to ensuring the
	 *                 InvokeCallback object is not garbage collected.
	 */
	public static native void invoke(InvokeCallback callback);

	/**
	 * Invokes a callback on the Qt thread after a delay.
	 *
	 * @param callback   The callback to call. Caller is responsible to ensuring the
	 *                   InvokeCallback object is not garbage collected.
	 * @param delayMilli The delay until calling the callback in milliseconds.
	 */
	public static native void invokeWithDelay(InvokeCallback callback, int delayMilli);
}
