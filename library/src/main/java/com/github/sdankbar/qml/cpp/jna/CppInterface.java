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
package com.github.sdankbar.qml.cpp.jna;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

/**
 * C++ library interface. Used to make calls in Java into the C++ library.
 * Provides methods to interact with a QApplication and related functionality.
 */
public interface CppInterface extends Library {

	/**
	 * Interface for those classes wishing to be called back when the C++ or QML
	 * layers create an Event.
	 */
	public interface EventCallback extends Callback {
		/**
		 * Called when an Event is created.
		 *
		 * @param type   The type of the vent.
		 * @param data   The data for the event. The data stored is specific for an
		 *               Event type.
		 * @param length The length of the array pointed to by data.
		 */
		void invoke(String type, Pointer data, int length);
	}

	/**
	 * Interface for those classes wishing to be called back when an exception is
	 * created in C++ that must propogate up to the Java layer.
	 */
	public interface ExceptionCallback extends Callback {
		/**
		 * Called when C++ creates an exception that should propogate up to the Java
		 * layer.
		 *
		 * @param exceptionMessage The exeception's message.
		 */
		void invoke(String exceptionMessage);
	}

	/**
	 * Interface for having Java code called from the Qt Event Loop Thread.
	 */
	public interface InvokeCallback extends Callback {
		/**
		 * Called from the Qt Event Loop Thread.
		 */
		void invoke();
	}

	/**
	 * Callback for when the C++/QML layer requests to log a message.
	 */
	public interface LoggingCallback extends Callback {
		/**
		 * Callback to log a message.
		 *
		 * @param type             The logging type, trace=0, debug=1, info=2, warn=3,
		 *                         error=4.
		 * @param formattedMessage The message to log.
		 */
		void invoke(int type, String formattedMessage);
	}

	/**
	 * Adds a callback to be called when an event is created.
	 *
	 * @param c The callback. Caller is responsible to ensuring the EventCallback
	 *          object is not garbage collected.
	 */
	void addEventCallback(EventCallback c);

	/**
	 * Creates a new QApplication.
	 *
	 * @param argc Command line argument count
	 * @param argv Command line arguments
	 */
	void createQApplication(int argc, String[] argv);

	/**
	 * Deletes the current QApplication.
	 */
	void deleteQApplication();

	/**
	 * Calls QApplication::exec()
	 */
	void execQApplication();

	Pointer getBoundingRect(String fontToString, String text);

	Pointer getBoundingRect2(String fontToString, int x, int y, int w, int h, int alignFlags, int textFlags,
			String text);

	String getQFontInfo(String fontToString);

	String getQFontMetrics(String fontToString);

	/**
	 * \return The value of QFront::toString() after returning QFont constructed
	 * from the various parameters passed to this function.
	 */
	String getQFontToString(String family, int pointSize, int pixelSize, boolean bold, boolean italic, boolean overline,
			boolean strikeout, boolean underline, boolean fixedPitch, boolean kerning, int fontWeight,
			double wordSpacing, double letteringSpacing, int letterSpacingType, int capitalization,
			int hintingPreference, int stretch, int style, String styleName, int styleHint, int styleStrategy);

	int getStringWidth(String fontToString, String text);

	Pointer getTightBoundingRect(String fontToString, String text);

	boolean inFont(String fontToString, int character);

	/**
	 * Invokes a callback on the Qt thread.
	 *
	 * @param callback The callback to call. Caller is responsible to ensuring the
	 *                 InvokeCallback object is not garbage collected.
	 */
	void invoke(InvokeCallback callback);

	/**
	 * Invokes a callback on the Qt thread after a delay.
	 *
	 * @param callback   The callback to call. Caller is responsible to ensuring the
	 *                   InvokeCallback object is not garbage collected.
	 * @param delayMilli The delay until calling the callback in milliseconds.
	 */
	void invokeWithDelay(InvokeCallback callback, int delayMilli);

	/**
	 * Loads the QML file at the given path.
	 *
	 * @param fileName The path to the QML file to load.
	 */
	void loadQMLFile(String fileName);

	/**
	 * Calls QApplication::quit()
	 */
	void quitQApplication();

	/**
	 * Reloads the QML file at the given path.
	 *
	 * @param fileName The path to the QML file to reload.
	 */
	void reloadQMLFile(String fileName);

	void sendQMLEvent(String eventName, String[] keys, Pointer valuesPointer, int keyValuesCount);

	/**
	 * Sets the callback that the C++ side will call if an error condition is
	 * encountered.
	 *
	 * @param c The callback to call on C++ error.
	 */
	void setExceptionCallback(ExceptionCallback c);

	/**
	 * Sets the callback to call when C++ wants to log.
	 *
	 * @param c The logging callback.
	 */
	void setLoggingCallback(LoggingCallback c);

	/**
	 * Provides C++ with a pointer to use when data needs to be passed from C++ to
	 * Java.
	 *
	 * @param cppToJava Pointer to memory to use.
	 * @param length    The size of the memory block cppToJava points to.
	 */
	void setSharedMemory(Pointer cppToJava, int length);
}
