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
package com.github.sdankbar.qml;

import com.github.sdankbar.qml.cpp.ApiInstance;
import com.github.sdankbar.qml.cpp.jna.CppInterface.ExceptionCallback;
import com.github.sdankbar.qml.exceptions.QMLException;

/**
 * Exception thrown for errors related to Qt and QML.
 */
public class JQMLExceptionHandling {

	private static class Callback implements ExceptionCallback {

		@Override
		public void invoke(final String exceptionMessage) {
			lastException = new QMLException(exceptionMessage);
		}

	}

	private static QMLException lastException = null;

	private static Callback exceptionCallback = new Callback();

	/**
	 * Checks and throws any pending exceptions created by the C++ layer.
	 */
	public static void checkExceptions() {
		if (lastException != null) {
			final QMLException temp = lastException;
			lastException = null;
			throw temp;
		}
	}

	/**
	 * Registers this class with the C++ layer so it can create exceptions.
	 */
	public static void register() {
		ApiInstance.LIB_INSTANCE.setExceptionCallback(exceptionCallback);
	}

	private JQMLExceptionHandling() {

	}
}
