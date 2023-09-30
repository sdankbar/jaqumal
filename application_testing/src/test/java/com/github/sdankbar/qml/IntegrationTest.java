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
		tools.mouseMove(59, 2, 0, 0, 0, Duration.ofMillis(1026));
		tools.mouseMove(46, 10, 0, 0, 0, Duration.ofMillis(295));
		tools.mousePress(46, 10, 1, 1, 0, Duration.ofMillis(238));
		tools.mouseRelease(46, 10, 1, 0, 0, Duration.ofMillis(102));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_20_08_00_728.png"), Duration.ofMillis(1051));
		tools.mouseMove(46, 11, 0, 0, 0, Duration.ofMillis(179));
		tools.mouseMove(46, 30, 0, 0, 0, Duration.ofMillis(252));
		tools.mousePress(45, 34, 1, 1, 0, Duration.ofMillis(297));
		tools.mouseRelease(45, 34, 1, 0, 0, Duration.ofMillis(87));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_20_08_02_311.png"), Duration.ofMillis(605));
		tools.mouseMove(52, 34, 0, 0, 0, Duration.ofMillis(193));
		tools.mouseMove(292, 83, 0, 0, 0, Duration.ofMillis(317));
		tools.mouseMove(247, 100, 0, 0, 0, Duration.ofMillis(251));
		tools.mousePress(250, 86, 1, 1, 0, Duration.ofMillis(304));
		tools.mouseRelease(250, 86, 1, 0, 0, Duration.ofMillis(74));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_20_08_04_233.png"), Duration.ofMillis(783));
		tools.mouseMove(240, 90, 0, 0, 0, Duration.ofMillis(244));
		tools.mouseMove(240, 65, 0, 0, 0, Duration.ofMillis(266));
		tools.mousePress(250, 60, 1, 1, 0, Duration.ofMillis(328));
		tools.mouseRelease(250, 60, 1, 0, 0, Duration.ofMillis(93));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_20_08_05_949.png"), Duration.ofMillis(785));
		tools.mouseMove(236, 67, 0, 0, 0, Duration.ofMillis(379));
		tools.mouseMove(89, 186, 0, 0, 0, Duration.ofMillis(253));
		tools.mousePress(71, 186, 1, 1, 0, Duration.ofMillis(237));
		tools.mouseRelease(71, 186, 1, 0, 0, Duration.ofMillis(107));
		tools.pressKey(49, 0, "1", false, 1, Duration.ofMillis(758));
		tools.releaseKey(49, 0, "1", false, 1, Duration.ofMillis(94));
		tools.pressKey(50, 0, "2", false, 1, Duration.ofMillis(169));
		tools.releaseKey(50, 0, "2", false, 1, Duration.ofMillis(80));
		tools.pressKey(51, 0, "3", false, 1, Duration.ofMillis(188));
		tools.releaseKey(51, 0, "3", false, 1, Duration.ofMillis(78));
		tools.pressKey(52, 0, "4", false, 1, Duration.ofMillis(170));
		tools.releaseKey(52, 0, "4", false, 1, Duration.ofMillis(80));
		tools.pressKey(53, 0, "5", false, 1, Duration.ofMillis(148));
		tools.releaseKey(53, 0, "5", false, 1, Duration.ofMillis(84));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_20_08_09_731.png"), Duration.ofMillis(957));
		tools.mouseMove(73, 181, 0, 0, 0, Duration.ofMillis(909));
		tools.mouseMove(139, 85, 0, 0, 0, Duration.ofMillis(259));
		tools.mouseMove(157, 91, 0, 0, 0, Duration.ofMillis(266));
		tools.mousePress(170, 85, 1, 1, 0, Duration.ofMillis(221));
		tools.mouseRelease(170, 85, 1, 0, 0, Duration.ofMillis(114));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_20_08_12_508.png"), Duration.ofMillis(1007));
		tools.mouseMove(155, 94, 0, 0, 0, Duration.ofMillis(561));
		tools.mouseMove(50, 183, 0, 0, 0, Duration.ofMillis(259));
		tools.pressKey(16777219, 0, "", false, 1, Duration.ofMillis(762));
		tools.releaseKey(16777219, 0, "", false, 1, Duration.ofMillis(74));
		tools.pressKey(16777219, 0, "", false, 1, Duration.ofMillis(193));
		tools.releaseKey(16777219, 0, "", false, 1, Duration.ofMillis(65));
		tools.pressKey(16777219, 0, "", false, 1, Duration.ofMillis(221));
		tools.releaseKey(16777219, 0, "", false, 1, Duration.ofMillis(65));
		tools.pollEventQueue(Duration.ofMillis(2130));
	}

}
