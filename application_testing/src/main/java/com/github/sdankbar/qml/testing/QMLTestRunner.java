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
package com.github.sdankbar.qml.testing;

import java.io.File;
import java.util.List;
import java.util.Objects;

import com.github.sdankbar.qml.cpp.jni.ApplicationFunctions;

public class QMLTestRunner {

	private final File pathToQMLTests;
	private final String[] importPaths;

	public QMLTestRunner(final File path, final List<File> importPaths) {
		pathToQMLTests = Objects.requireNonNull(path, "path is null");

		Objects.requireNonNull(importPaths, "importPaths is null");
		this.importPaths = new String[importPaths.size()];
		for (int i = 0; i < importPaths.size(); ++i) {
			this.importPaths[i] = importPaths.get(i).getAbsolutePath();
		}
	}

	public void run() {
		final int r = ApplicationFunctions.runQMLTest(pathToQMLTests.getAbsolutePath(), importPaths);
		if (r != 0) {
			throw new AssertionError("QML unit test failure(s)");
		}
	}

}
