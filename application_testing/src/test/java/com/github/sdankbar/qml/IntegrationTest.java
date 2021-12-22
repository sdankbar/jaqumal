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

import java.io.File;
import java.time.Duration;

import org.junit.After;
import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.eventing.NullEventProcessor;

public class IntegrationTest {

	private JQMLApplication<NullEventProcessor> app;

	@After
	public void finish() {
		app.endIntegrationTest();
	}

	@Test
	public void testRun() {
		final String[] args = new String[0];
		app = JQMLApplication.create(args, new NullEventFactory<>());
		app.loadQMLFile("src/test/java/com/github/sdankbar/qml/IntegrationTest.qml");

		final String screenshotDir = "src/test/resources/";
		final JQMLDevelopmentTools tools = app.getDevelopmentTools();
		tools.startIntegrationTest();
		tools.mouseMove(206, 134, 0, 0, 0, Duration.ofMillis(429));
		tools.mouseMove(190, 31, 0, 0, 0, Duration.ofMillis(357));
		tools.mouseMove(71, 2, 0, 0, 0, Duration.ofMillis(253));
		tools.mousePress(52, 7, 1, 1, 0, Duration.ofMillis(491));
		tools.mouseRelease(52, 7, 1, 0, 0, Duration.ofMillis(86));
		tools.compareWindowToImage(new File(screenshotDir, "integrationTestScreenshots/screenshot_22_59_38_399.png"),
				Duration.ofMillis(1006));
		tools.mouseMove(52, 9, 0, 0, 0, Duration.ofMillis(398));
		tools.mouseMove(47, 24, 0, 0, 0, Duration.ofMillis(260));
		tools.mousePress(49, 34, 1, 1, 0, Duration.ofMillis(327));
		tools.mouseRelease(49, 34, 1, 0, 0, Duration.ofMillis(88));
		tools.compareWindowToImage(new File(screenshotDir, "integrationTestScreenshots/screenshot_22_59_40_469.png"),
				Duration.ofMillis(842));
		tools.mouseMove(55, 36, 0, 0, 0, Duration.ofMillis(478));
		tools.mouseMove(201, 60, 0, 0, 0, Duration.ofMillis(259));
		tools.mouseMove(273, 61, 0, 0, 0, Duration.ofMillis(253));
		tools.mouseMove(277, 61, 0, 0, 0, Duration.ofMillis(259));
		tools.mousePress(282, 61, 1, 1, 0, Duration.ofMillis(174));
		tools.mouseRelease(282, 61, 1, 0, 0, Duration.ofMillis(59));
		tools.compareWindowToImage(new File(screenshotDir, "integrationTestScreenshots/screenshot_22_59_42_793.png"),
				Duration.ofMillis(842));
		tools.mouseMove(282, 62, 0, 0, 0, Duration.ofMillis(597));
		tools.mouseMove(282, 92, 0, 0, 0, Duration.ofMillis(274));
		tools.mousePress(282, 93, 1, 1, 0, Duration.ofMillis(118));
		tools.mouseRelease(282, 93, 1, 0, 0, Duration.ofMillis(73));
		tools.compareWindowToImage(new File(screenshotDir, "integrationTestScreenshots/screenshot_22_59_44_755.png"),
				Duration.ofMillis(900));
		tools.pollEventQueue(Duration.ofMillis(1282));
	}

}
