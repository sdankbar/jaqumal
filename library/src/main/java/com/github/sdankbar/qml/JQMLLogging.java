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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.cpp.ApiInstance;
import com.github.sdankbar.qml.cpp.jna.CppInterface.LoggingCallback;

/**
 * Class allows QML to access an SLF4j instance for the purposes of logging.
 */
public class JQMLLogging {

	private static class LogCallback implements LoggingCallback {

		@Override
		public void invoke(final int type, final String formattedMessage) {
			switch (type) {
			case 0:
				logger.trace(formattedMessage);
				break;
			case 1:
				logger.debug(formattedMessage);
				break;
			case 2:
				logger.info(formattedMessage);
				break;
			case 3:
				logger.warn(formattedMessage);
				break;
			case 4:
				logger.error(formattedMessage);
				break;
			default:
				break;
			}
		}

	}

	private static final Logger logger = LoggerFactory.getLogger(JQMLLogging.class);

	private final LogCallback c = new LogCallback();

	/**
	 * Constructor
	 */
	public JQMLLogging() {
		ApiInstance.LIB_INSTANCE.setLoggingCallback(c);
	}

}
