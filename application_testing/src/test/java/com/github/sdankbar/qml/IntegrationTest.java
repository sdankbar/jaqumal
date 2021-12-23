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
import org.junit.Before;
import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;

/**
 * Example of using the development tools to test a Jaqumal application.
 */
public class IntegrationTest {

	private JQMLApplication<?> app;
	private final String screenshotDir = "src/test/resources/integrationTestScreenshots/";

	@Before
	public void setup() {
		app = JQMLApplication.create(new String[0], new NullEventFactory<>());
		app.loadQMLFile("src/test/java/com/github/sdankbar/qml/IntegrationTest.qml");
	}

	@After
	public void finish() {
		app.getDevelopmentTools().endIntegrationTest();
	}

	@Test
	public void test_run() {
		final JQMLDevelopmentTools tools = app.getDevelopmentTools();
		tools.startIntegrationTest();
		tools.mouseMove(288, 164, 0, 0, 0, Duration.ofMillis(645));
		tools.mouseMove(94, 94, 0, 0, 0, Duration.ofMillis(382));
		tools.mouseMove(43, 20, 0, 0, 0, Duration.ofMillis(252));
		tools.mousePress(43, 8, 1, 1, 0, Duration.ofMillis(365));
		tools.mouseRelease(43, 8, 1, 0, 0, Duration.ofMillis(106));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_02_52_55_154.png"), Duration.ofMillis(1015));
		tools.mouseMove(44, 10, 0, 0, 0, Duration.ofMillis(524));
		tools.mouseMove(48, 33, 0, 0, 0, Duration.ofMillis(253));
		tools.mousePress(48, 35, 1, 1, 0, Duration.ofMillis(163));
		tools.mouseRelease(48, 35, 1, 0, 0, Duration.ofMillis(80));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_02_52_57_474.png"), Duration.ofMillis(1274));
		tools.mouseMove(53, 35, 0, 0, 0, Duration.ofMillis(486));
		tools.mouseMove(235, 86, 0, 0, 0, Duration.ofMillis(252));
		tools.mouseMove(282, 92, 0, 0, 0, Duration.ofMillis(252));
		tools.mouseMove(313, 92, 0, 0, 0, Duration.ofMillis(259));
		tools.mousePress(320, 92, 1, 1, 0, Duration.ofMillis(275));
		tools.mouseRelease(320, 92, 1, 0, 0, Duration.ofMillis(108));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_02_53_00_607.png"), Duration.ofMillis(1501));
		tools.mouseMove(320, 91, 0, 0, 0, Duration.ofMillis(582));
		tools.mouseMove(314, 62, 0, 0, 0, Duration.ofMillis(279));
		tools.mousePress(314, 61, 1, 1, 0, Duration.ofMillis(204));
		tools.mouseRelease(314, 61, 1, 0, 0, Duration.ofMillis(114));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_02_53_02_800.png"), Duration.ofMillis(1014));
		tools.mouseMove(313, 62, 0, 0, 0, Duration.ofMillis(694));
		tools.mouseMove(116, 185, 0, 0, 0, Duration.ofMillis(252));
		tools.mousePress(75, 193, 1, 0, 0, Duration.ofMillis(482));
		tools.mouseRelease(75, 193, 1, 0, 0, Duration.ofMillis(482));
		tools.pressKey(49, 0, "1", false, 1, Duration.ofMillis(495));
		tools.releaseKey(49, 0, "1", false, 1, Duration.ofMillis(87));
		tools.pressKey(50, 0, "2", false, 1, Duration.ofMillis(208));
		tools.releaseKey(50, 0, "2", false, 1, Duration.ofMillis(80));
		tools.pressKey(51, 0, "3", false, 1, Duration.ofMillis(159));
		tools.releaseKey(51, 0, "3", false, 1, Duration.ofMillis(87));
		tools.pressKey(52, 0, "4", false, 1, Duration.ofMillis(196));
		tools.releaseKey(52, 0, "4", false, 1, Duration.ofMillis(93));
		tools.pressKey(53, 0, "5", false, 1, Duration.ofMillis(176));
		tools.releaseKey(53, 0, "5", false, 1, Duration.ofMillis(82));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_02_53_07_017.png"), Duration.ofMillis(1126));
		tools.mouseMove(76, 191, 0, 0, 0, Duration.ofMillis(672));
		tools.mouseMove(130, 111, 0, 0, 0, Duration.ofMillis(267));
		tools.mouseMove(157, 95, 0, 0, 0, Duration.ofMillis(313));
		tools.mousePress(162, 87, 1, 1, 0, Duration.ofMillis(289));
		tools.mouseRelease(162, 87, 1, 0, 0, Duration.ofMillis(135));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_02_53_10_217.png"), Duration.ofMillis(1524));
		tools.pressKey(16777219, 0, "", false, 1, Duration.ofMillis(914));
		tools.releaseKey(16777219, 0, "", false, 1, Duration.ofMillis(91));
		tools.pressKey(16777219, 0, "", false, 1, Duration.ofMillis(153));
		tools.releaseKey(16777219, 0, "", false, 1, Duration.ofMillis(100));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_02_53_13_060.png"), Duration.ofMillis(1586));
		tools.pollEventQueue(Duration.ofMillis(1520));
	}

}
