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

import java.io.File;

import org.junit.Test;

import com.github.sdankbar.qml.testing.QMLTestRunner;
import com.google.common.collect.ImmutableList;

/**
 * Test the QMLTestRunner class.
 */
public class QMLTestRunnerTest {

	/**
	 * Test successful unit tests.
	 */
	@Test
	public void testRun() {
		final QMLTestRunner runner = new QMLTestRunner(new File("src/test/qml_success"), ImmutableList.of());
		runner.run();
	}

	/**
	 * Test failed unit tests
	 */
	@Test(expected = AssertionError.class)
	public void testRunFail() {
		final QMLTestRunner runner = new QMLTestRunner(new File("src/test/qml_failure"), ImmutableList.of());
		runner.run();
	}

	/**
	 * Test successful unit tests that import.
	 */
	@Test
	public void testRunWithImport() {
		final QMLTestRunner runner = new QMLTestRunner(new File("src/test/qml_success2"),
				ImmutableList.of(new File("src/main/qml")));
		runner.run();
	}
}
