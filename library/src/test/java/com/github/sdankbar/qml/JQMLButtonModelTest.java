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

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.models.singleton.JQMLButtonModel;

/**
 * Tests the JQMLButtonModel.
 */
public class JQMLButtonModelTest {

	/**
	 *
	 */
	public static interface EventProcessor {
		// Empty
	}

	/**
	 *
	 */
	@After
	public void cleanup() {
		JQMLApplication.delete();
	}

	/**
	 * @throws InterruptedException e
	 */
	@Test
	public void testChangeListeners() throws InterruptedException {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLButtonModel model = app.getModelFactory().createButtonModel("other");

		final AtomicBoolean focusChanged = new AtomicBoolean();
		final AtomicBoolean hoverChanged = new AtomicBoolean();
		final AtomicBoolean pressedChanged = new AtomicBoolean();
		model.registerFocusChanged((v) -> {
			focusChanged.set(true);
		});
		model.registerHoverChanged((v) -> {
			hoverChanged.set(true);
		});
		model.registerPressedChanged((v) -> {
			pressedChanged.set(true);
		});
	}
}
