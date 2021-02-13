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
package com.github.sdankbar.qml;

import org.junit.Test;

import com.github.sdankbar.qml.PerformanceTest.EventProcessor;
import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.exceptions.QMLException;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;

/**
 * Tests the JQMLExceptionHandling class
 */
public class JQMLExceptionHandlingTest {

	/**
	 *
	 */
	@Test(expected = QMLException.class)
	public void test() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());

		JQMLApplication.delete();

		app.getModelFactory().createSingletonModel("Test", PutMode.class, PutMode.RETURN_NULL);
	}

}
