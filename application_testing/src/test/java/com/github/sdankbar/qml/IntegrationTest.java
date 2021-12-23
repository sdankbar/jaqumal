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
		tools.mouseMove(277, 142, 0, 0, 0, Duration.ofMillis(467));
		tools.mouseMove(64, 2, 0, 0, 0, Duration.ofMillis(369));
		tools.mouseMove(43, 10, 0, 0, 0, Duration.ofMillis(279));
		tools.mousePress(43, 10, 1, 1, 0, Duration.ofMillis(48));
		tools.mouseRelease(43, 10, 1, 0, 0, Duration.ofMillis(87));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_01_50_33_280.png"), Duration.ofMillis(579));
		tools.mouseMove(43, 11, 0, 0, 0, Duration.ofMillis(513));
		tools.mouseMove(47, 36, 0, 0, 0, Duration.ofMillis(253));
		tools.mousePress(48, 38, 1, 1, 0, Duration.ofMillis(73));
		tools.mouseRelease(48, 38, 1, 0, 0, Duration.ofMillis(53));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_01_50_34_863.png"), Duration.ofMillis(664));
		tools.mouseMove(55, 38, 0, 0, 0, Duration.ofMillis(384));
		tools.mouseMove(229, 86, 0, 0, 0, Duration.ofMillis(251));
		tools.mouseMove(247, 86, 0, 0, 0, Duration.ofMillis(252));
		tools.mouseMove(269, 89, 0, 0, 0, Duration.ofMillis(252));
		tools.mousePress(273, 90, 1, 1, 0, Duration.ofMillis(290));
		tools.mouseRelease(273, 90, 1, 0, 0, Duration.ofMillis(93));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_01_50_37_409.png"), Duration.ofMillis(1024));
		tools.mouseMove(274, 88, 0, 0, 0, Duration.ofMillis(510));
		tools.mouseMove(278, 64, 0, 0, 0, Duration.ofMillis(305));
		tools.mousePress(278, 64, 1, 1, 0, Duration.ofMillis(37));
		tools.mouseRelease(278, 64, 1, 0, 0, Duration.ofMillis(83));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_01_50_39_095.png"), Duration.ofMillis(751));
		tools.mouseMove(274, 66, 0, 0, 0, Duration.ofMillis(381));
		tools.mouseMove(101, 183, 0, 0, 0, Duration.ofMillis(254));
		tools.mousePress(60, 190, 1, 0, 0, Duration.ofMillis(50));
		tools.mouseRelease(60, 190, 1, 0, 0, Duration.ofMillis(353));
		tools.pressKey(49, 0, "1", false, 1, Duration.ofMillis(525));
		tools.releaseKey(49, 0, "1", false, 1, Duration.ofMillis(69));
		tools.pressKey(50, 0, "2", false, 1, Duration.ofMillis(106));
		tools.releaseKey(50, 0, "2", false, 1, Duration.ofMillis(67));
		tools.pressKey(51, 0, "3", false, 1, Duration.ofMillis(126));
		tools.releaseKey(51, 0, "3", false, 1, Duration.ofMillis(71));
		tools.pressKey(52, 0, "4", false, 1, Duration.ofMillis(286));
		tools.releaseKey(52, 0, "4", false, 1, Duration.ofMillis(64));
		tools.pressKey(53, 0, "5", false, 1, Duration.ofMillis(218));
		tools.releaseKey(53, 0, "5", false, 1, Duration.ofMillis(72));
		tools.compareWindowToImage(new File(screenshotDir, "screenshot_01_50_42_944.png"), Duration.ofMillis(1257));
		tools.pollEventQueue(Duration.ofMillis(695));
	}

}
