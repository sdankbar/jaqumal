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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

import javax.imageio.ImageIO;

import com.github.sdankbar.qml.cpp.jni.ApplicationFunctions;

public class JQMLDevelopmentTools {

	private final JQMLApplication<?> app;

	JQMLDevelopmentTools(final JQMLApplication<?> app) {
		this.app = Objects.requireNonNull(app, "app is null");
	}

	public void startIntegrationTest() {
		app.startIntegrationTest();
	}

	public void endIntegrationTest() {
		app.endIntegrationTest();
	}

	public void pressKey(final int key, final int modifiers, final String text, final boolean autoRep, final int count,
			final Duration delay) {
		pollEventQueue(delay);
		ApplicationFunctions.injectKeyPressIntoApplication(key, modifiers, text, autoRep, count);
	}

	public void releaseKey(final int key, final int modifiers, final String text, final boolean autoRep,
			final int count, final Duration delay) {
		pollEventQueue(delay);
		ApplicationFunctions.injectKeyReleaseIntoApplication(key, modifiers, text, autoRep, count);
	}

	public void mousePress(final int x, final int y, final int button, final int buttons, final int modifiers,
			final Duration delay) {
		pollEventQueue(delay);
		ApplicationFunctions.injectMousePressIntoApplication(x, y, button, buttons, modifiers);
	}

	public void mouseRelease(final int x, final int y, final int button, final int buttons, final int modifiers,
			final Duration delay) {
		pollEventQueue(delay);
		ApplicationFunctions.injectMouseReleaseIntoApplication(x, y, button, buttons, modifiers);
	}

	public void mouseMove(final int x, final int y, final int button, final int buttons, final int modifiers,
			final Duration delay) {
		pollEventQueue(delay);
		ApplicationFunctions.injectMouseMoveIntoApplication(x, y, button, buttons, modifiers);
	}

	public void wheel(final int x, final int y, final int pixelX, final int pixelY, final int angleX, final int angleY,
			final int buttons, final int modifiers, final int phase, final boolean inverted, final Duration delay) {
		pollEventQueue(delay);
		ApplicationFunctions.injectWheelIntoApplication(x, y, pixelX, pixelY, angleX, angleY, buttons, modifiers, phase,
				inverted);
	}

	public void compareWindowToImage(final File path, final Duration delay) {
		pollEventQueue(delay);

		try {
			final BufferedImage i = ImageIO.read(path);
			for (int j = 0; j < 10; ++j) {
				if (ApplicationFunctions.compareImageToActiveWindow(i)) {
					return;
				} else {
					pollEventQueue(Duration.ofMillis(100));
				}
			}
			throw new AssertionError("Active window does not match " + path);
		} catch (final IOException e) {
			throw new AssertionError("Unable to load comparison image", e);
		}
	}

	public void pollEventQueue(final Duration delay) {
		// Poll at least once.
		ApplicationFunctions.pollQAplicationEvents();

		final long start = System.currentTimeMillis();
		long now = start;
		while ((now - start) < delay.toMillis()) {
			ApplicationFunctions.pollQAplicationEvents();
			try {
				Thread.sleep(1);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			now = System.currentTimeMillis();
		}
	}
}
