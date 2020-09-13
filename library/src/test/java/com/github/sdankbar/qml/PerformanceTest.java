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

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;

/**
 * Tests the performance of the QML models.
 *
 */
public class PerformanceTest {

	/**
	 *
	 */
	public interface EventProcessor {
		// Empty Implementation
	}

	private enum Roles {
		R1;
	}

	/**
	 *
	 */
	@After
	public void cleanup() {
		JQMLApplication.delete();
	}

	/**
	 *
	 */
	@Test
	public void getSetValues() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("model", Roles.class,
				PutMode.RETURN_NULL);

		for (int iter = 0; iter < 5; ++iter) {

			final long start = System.nanoTime();
			for (int i = 0; i < 500000; ++i) {
				model.put(Roles.R1, new JVariant(i));
				final JVariant v = model.get(Roles.R1);

				assertEquals(i, v.asInteger());
			}
			final long end = System.nanoTime();

			System.out.println("Took " + (end - start) / 1000 + " microseconds");
		}
	}

}
