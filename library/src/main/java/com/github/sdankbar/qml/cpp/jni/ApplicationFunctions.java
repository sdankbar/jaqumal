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
package com.github.sdankbar.qml.cpp.jni;

import java.io.File;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.cpp.jni.interfaces.ImageProviderCallback;
import com.github.sdankbar.qml.cpp.jni.interfaces.InvokeCallback;
import com.github.sdankbar.qml.cpp.jni.interfaces.LoggingCallback;

public final class ApplicationFunctions {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationFunctions.class);

	static {
		logger.info("Load Jaqumal.dll");
		// TODO load from classpath
		System.load(new File("C:\\Users\\dankb\\git\\jaqumal\\library\\src\\main\\resources\\win32-x86-64\\Jaqumal.dll")
				.getAbsolutePath());
		logger.info("Finish - Load Jaqumal.dll");
	}

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
	public static void deleteQApplication() {
		// TODO
	}

	/**
	 * Calls QApplication::exec()
	 */
	public static void execQApplication() {
		// TODO
	}

	/**
	 * @return The version of Qt the Jaqumal was compiled with.
	 */
	public static String getCompileQtVersion() {
		// TODO
		return "";
	}

	/**
	 * @return The version of Qt the Jaqumal is linked against.
	 */
	public static String getRuntimeQtVersion() {
		// TODO
		return "";
	}

	/**
	 * Loads the QML file at the given path.
	 *
	 * @param fileName The path to the QML file to load.
	 */
	public static void loadQMLFile(final String fileName) {
		// TODO
	}

	/**
	 * Reloads the QML file at the given path.
	 *
	 * @param fileName The path to the QML file to reload.
	 */
	public static void reloadQMLFile(final String fileName) {
		// TODO
	}

	/**
	 * Unloads the currently loaded QML.
	 */
	public static void unloadQML() {
		// TODO
	}

	/**
	 * Sets the callback to call when C++ wants to log.
	 *
	 * @param c The logging callback.
	 */
	public static void setLoggingCallback(final LoggingCallback c) {
		// TODO
	}

	/**
	 * Calls QApplication::quit()
	 */
	public static void quitQApplication() {
		// TODO
	}

	/**
	 * @param pathToQMLTestFile Path to QML test file or directory containing test
	 *                          files.
	 * @param importPaths       0 or more paths that QML should use when searching
	 *                          for files to import.
	 * @return 0 on test success, 1 on failure.
	 */
	public static int runQMLTest(final String pathToQMLTestFile, final String[] importPaths) {
		// TODO
		return 0;
	}

	/**
	 * Registers a new image provider.
	 *
	 * @param id Identifier of the image provider.
	 * @param c  The provider's callback.
	 */
	public static void addImageProvider(final String id, final ImageProviderCallback c) {
		// TODO
	}

	/**
	 * @return Buffer containing number of screens, then dpi and rectangles.
	 */
	public static ByteBuffer getScreens() {
		// TODO
		return null;
	}

	/**
	 * Invokes a callback on the Qt thread.
	 *
	 * @param callback The callback to call. Caller is responsible to ensuring the
	 *                 InvokeCallback object is not garbage collected.
	 */
	public static void invoke(final InvokeCallback callback) {
		// TODO
	}

	/**
	 * Invokes a callback on the Qt thread after a delay.
	 *
	 * @param callback   The callback to call. Caller is responsible to ensuring the
	 *                   InvokeCallback object is not garbage collected.
	 * @param delayMilli The delay until calling the callback in milliseconds.
	 */
	public static void invokeWithDelay(final InvokeCallback callback, final int delayMilli) {
		// TODO
	}
}
