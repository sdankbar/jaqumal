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
package com.github.sdankbar.qml.models;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import com.github.sdankbar.qml.JQMLUtilities;

/**
 * Abstract model for all models to be made available to QML.
 */
public abstract class AbstractJQMLModel {
	protected static final int USER_ROLE_STARTING_INDEX = 256;

	private final AtomicReference<Thread> eventLoopThread;

	/**
	 * Constructor.
	 *
	 * @param eventLoopThread Reference to the Qt thread.
	 */
	public AbstractJQMLModel(final AtomicReference<Thread> eventLoopThread) {
		this.eventLoopThread = Objects.requireNonNull(eventLoopThread, "eventLoopThread is null");
	}

	protected void verifyEventLoopThread() {
		JQMLUtilities.checkThread(eventLoopThread);
	}
}
