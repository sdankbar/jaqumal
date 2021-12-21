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
package com.github.sdankbar.qml.dev_tools;

import java.io.File;
import java.time.Duration;

public class JQMLDevelopmentTools {

	public void pressKey(final String k) {
		pressKey(k, Duration.ZERO);
	}

	public void pressKey(final String k, final Duration delay) {
		wait(delay);
		// TODO
	}

	public void releaseKey(final String k) {
		releaseKey(k, Duration.ZERO);
	}

	public void releaseKey(final String k, final Duration delay) {
		wait(delay);
		// TODO
	}

	public void mousePress(final int x, final int y) {
		mousePress(x, y, Duration.ZERO);
	}

	public void mousePress(final int x, final int y, final Duration delay) {
		wait(delay);
		// TODO
	}

	public void mouseRelease(final int x, final int y) {
		mouseRelease(x, y, Duration.ZERO);
	}

	public void mouseRelease(final int x, final int y, final Duration delay) {
		wait(delay);
		// TODO
	}

	public void mouseMove(final int x, final int y) {
		mouseMove(x, y, Duration.ZERO);
	}

	public void mouseMove(final int x, final int y, final Duration delay) {
		wait(delay);
		// TODO
	}

	public void compareWindowToImage(final File path) {
		compareWindowToImage(path, Duration.ZERO);
	}

	public void compareWindowToImage(final File path, final Duration delay) {
		wait(delay);
		// TODO
	}

	private void wait(final Duration delay) {
		// TODO
	}
}
