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
package com.github.sdankbar.qml.cpp.jni;

import java.awt.image.BufferedImage;

import com.github.sdankbar.qml.JScreen;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.jni.interfaces.ImageProviderCallback;
import com.github.sdankbar.qml.cpp.jni.interfaces.InvokeCallback;
import com.github.sdankbar.qml.cpp.jni.interfaces.LoggingCallback;
import com.github.sdankbar.qml.utility.LibraryUtilities;

public final class ApplicationFunctions {

	static {
		LibraryUtilities.loadLibrary("Jaqumal");
	}

	private ApplicationFunctions() {
		// Empty Implementation
	}

	/**
	 * Creates a new QApplication.
	 *
	 * @param argv Command line arguments
	 */
	public static native void createQApplication(String[] argv);

	/**
	 * Deletes the current QApplication.
	 */
	public static native void deleteQApplication();

	public static native void enableEventLogging();

	/**
	 * Calls QApplication::exec()
	 */
	public static native void execQApplication();

	public static native void pollQAplicationEvents();

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
	public static native void loadQMLFile(final String fileName);

	/**
	 * Reloads the QML file at the given path.
	 *
	 * @param fileName The path to the QML file to reload.
	 */
	public static native void reloadQMLFile(final String fileName);

	/**
	 * Unloads the currently loaded QML.
	 */
	public static native void unloadQMLFile();

	/**
	 * Sets the callback to call when C++ wants to log.
	 *
	 * @param c The logging callback.
	 */
	public static native void setLoggingCallback(final LoggingCallback c);

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
	public static native int runQMLTests(final String pathToQMLTestFile, final String[] importPaths);

	/**
	 * Registers a new image provider.
	 *
	 * @param id Identifier of the image provider.
	 * @param c  The provider's callback.
	 */
	public static native void addImageProvider(final String id, final ImageProviderCallback c);

	/**
	 * @return Array of screens.
	 */
	public static native JScreen[] getScreens();

	/**
	 * Invokes a callback on the Qt thread.
	 *
	 * @param callback The callback to call. Caller is responsible to ensuring the
	 *                 InvokeCallback object is not garbage collected.
	 */
	public static native void invoke(final InvokeCallback callback);

	public static native void setWindowsIcon(BufferedImage icon);

	/**
	 * @param rccFile Path to the rcc file to load and register.
	 * @param mapRoot Resource in the location tree to register.
	 * @return True if successfully registered, false on error.
	 */
	public static native boolean registerResource(String rccFile, String mapRoot);

	/**
	 * @param length  Length of rccData.
	 * @param rccData Binary rcc data to register..
	 * @param mapRoot Resource in the location tree to register.
	 * @return True if successfully registered, false on error.
	 */
	public static native boolean registerResource(int length, byte[] rccData, String mapRoot);

	public static native void addImportPath(String importPath);

	public static native void injectMousePressIntoApplication(int x, int y, int button, int buttons, int modifiers);

	public static native void injectMouseReleaseIntoApplication(int x, int y, int button, int buttons, int modifiers);

	public static native void injectMouseDoubleClickIntoApplication(int x, int y, int button, int buttons,
			int modifiers);

	public static native void injectMouseMoveIntoApplication(int x, int y, int button, int buttons, int modifiers);

	public static native void injectWheelIntoApplication(int x, int y, int pixelX, int pixelY, int angleX, int angleY,
			int buttons, int modifiers, int phase, boolean inverted);

	public static native void injectKeyPressIntoApplication(int key, int modifiers, String text, boolean autoRep,
			int count);

	public static native void injectKeyReleaseIntoApplication(int key, int modifiers, String text, boolean autoRep,
			int count);

	public static native boolean compareImageToActiveWindow(BufferedImage i, double ratiodB);

	public static native void generateDeltaBetweenImageAndActiveWindow(String fileName, BufferedImage i);

	public static native void saveScreenshot(String path);

	public static native JVariant renderPainterInstructionsToImage(int length, byte[] data, int width, int height);
}
