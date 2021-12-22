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

import java.time.Duration;

import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.eventing.NullEventProcessor;

public class IntegrationTest {

	@Test
	public void testRun() {
		final String[] args = new String[0];
		final JQMLApplication<NullEventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		app.loadQMLFile("src/test/java/com/github/sdankbar/qml/IntegrationTest.qml");

		final String screenshotDir = "TODO";
		final JQMLDevelopmentTools tools = app.getDevelopmentTools();
		tools.startIntegrationTest();
		tools.mousePress(362, 91, 1, 1, 0, Duration.ofMillis(555));
		tools.mouseRelease(362, 91, 1, 0, 0, Duration.ofMillis(229));
		tools.mouseMove(362, 90, 0, 0, 0, Duration.ofMillis(234));
		tools.mouseMove(359, 69, 0, 0, 0, Duration.ofMillis(281));
		tools.mousePress(360, 67, 1, 1, 0, Duration.ofMillis(135));
		tools.mouseRelease(360, 67, 1, 0, 0, Duration.ofMillis(81));
		tools.mouseMove(358, 67, 0, 0, 0, Duration.ofMillis(283));
		tools.mouseMove(326, 86, 0, 0, 0, Duration.ofMillis(267));
		tools.mousePress(322, 87, 1, 1, 0, Duration.ofMillis(170));
		tools.mouseRelease(322, 87, 1, 0, 0, Duration.ofMillis(74));
		tools.mouseMove(322, 86, 0, 0, 0, Duration.ofMillis(238));
		tools.mouseMove(322, 67, 0, 0, 0, Duration.ofMillis(252));
		tools.mouseMove(320, 62, 0, 0, 0, Duration.ofMillis(260));
		tools.mousePress(320, 63, 1, 1, 0, Duration.ofMillis(30));
		tools.mouseRelease(320, 63, 1, 0, 0, Duration.ofMillis(67));
		tools.mouseMove(309, 61, 0, 0, 0, Duration.ofMillis(283));
		tools.mouseMove(20, 1, 0, 0, 0, Duration.ofMillis(771));
		tools.mousePress(17, 5, 1, 1, 0, Duration.ofMillis(165));
		tools.mouseRelease(17, 5, 1, 0, 0, Duration.ofMillis(67));
		tools.mouseMove(17, 7, 0, 0, 0, Duration.ofMillis(204));
		tools.mouseMove(17, 27, 0, 0, 0, Duration.ofMillis(268));
		tools.mousePress(17, 28, 1, 1, 0, Duration.ofMillis(108));
		tools.mouseRelease(17, 28, 1, 0, 0, Duration.ofMillis(76));
		tools.mouseMove(19, 29, 0, 0, 0, Duration.ofMillis(226));
		tools.mousePress(45, 40, 1, 1, 0, Duration.ofMillis(319));
		tools.mouseRelease(45, 40, 1, 0, 0, Duration.ofMillis(60));
		tools.mouseMove(45, 39, 0, 0, 0, Duration.ofMillis(211));
		tools.mouseMove(50, 14, 0, 0, 0, Duration.ofMillis(258));
		tools.mousePress(50, 9, 1, 1, 0, Duration.ofMillis(166));
		tools.mouseRelease(50, 9, 1, 0, 0, Duration.ofMillis(67));
		tools.pollEventQueue(Duration.ofMillis(972));
		tools.endIntegrationTest();
	}

}
