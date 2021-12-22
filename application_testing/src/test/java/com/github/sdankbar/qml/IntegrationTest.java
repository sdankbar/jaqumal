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
		final JQMLDevelopmentTools tools = app.getDevolopmentTools();
		tools.startIntegrationTest();

		tools.mouseMove(140, 5, 0, 0, 0, Duration.ofMillis(790));
		tools.mouseMove(138, 8, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(136, 12, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(135, 15, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(133, 18, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(130, 22, 0, 0, 0, Duration.ofMillis(9));
		tools.mouseMove(129, 25, 0, 0, 0, Duration.ofMillis(5));
		tools.mouseMove(127, 27, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(126, 30, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(124, 32, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(123, 34, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(122, 36, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(120, 39, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(119, 41, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(118, 42, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(117, 44, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(115, 45, 0, 0, 0, Duration.ofMillis(8));
		tools.mouseMove(115, 46, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(114, 46, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(114, 47, 0, 0, 0, Duration.ofMillis(13));
		tools.mouseMove(113, 47, 0, 0, 0, Duration.ofMillis(15));
		tools.mouseMove(112, 47, 0, 0, 0, Duration.ofMillis(47));
		tools.mouseMove(111, 47, 0, 0, 0, Duration.ofMillis(23));
		tools.mouseMove(110, 47, 0, 0, 0, Duration.ofMillis(11));
		tools.mouseMove(109, 47, 0, 0, 0, Duration.ofMillis(14));
		tools.mouseMove(107, 46, 0, 0, 0, Duration.ofMillis(8));
		tools.mouseMove(105, 45, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(103, 44, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(101, 44, 0, 0, 0, Duration.ofMillis(8));
		tools.mouseMove(98, 42, 0, 0, 0, Duration.ofMillis(4));
		tools.mouseMove(96, 41, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(94, 41, 0, 0, 0, Duration.ofMillis(8));
		tools.mouseMove(92, 40, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(90, 39, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(89, 39, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(88, 38, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(88, 37, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(87, 37, 0, 0, 0, Duration.ofMillis(8));
		tools.mouseMove(86, 37, 0, 0, 0, Duration.ofMillis(26));
		tools.mouseMove(86, 36, 0, 0, 0, Duration.ofMillis(62));
		tools.mouseMove(86, 35, 0, 0, 0, Duration.ofMillis(34));
		tools.mouseMove(86, 34, 0, 0, 0, Duration.ofMillis(41));
		tools.mouseMove(86, 33, 0, 0, 0, Duration.ofMillis(41));
		tools.mousePress(86, 33, 1, 1, 0, Duration.ofMillis(143));
		tools.mouseRelease(86, 33, 1, 0, 0, Duration.ofMillis(87));
		tools.mouseMove(89, 33, 0, 0, 0, Duration.ofMillis(261));
		tools.mouseMove(94, 34, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(101, 36, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(108, 38, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(115, 40, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(122, 41, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(129, 42, 0, 0, 0, Duration.ofMillis(8));
		tools.mouseMove(135, 44, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(141, 46, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(146, 48, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(151, 49, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(155, 51, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(160, 51, 0, 0, 0, Duration.ofMillis(12));
		tools.mouseMove(164, 52, 0, 0, 0, Duration.ofMillis(3));
		tools.mouseMove(168, 53, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(171, 54, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(174, 55, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(176, 55, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(177, 55, 0, 0, 0, Duration.ofMillis(8));
		tools.mouseMove(178, 56, 0, 0, 0, Duration.ofMillis(5));
		tools.mouseMove(180, 57, 0, 0, 0, Duration.ofMillis(12));
		tools.mouseMove(181, 58, 0, 0, 0, Duration.ofMillis(9));
		tools.mouseMove(182, 59, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(183, 59, 0, 0, 0, Duration.ofMillis(14));
		tools.mouseMove(184, 60, 0, 0, 0, Duration.ofMillis(14));
		tools.mouseMove(185, 60, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(186, 60, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(186, 61, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(187, 61, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(187, 62, 0, 0, 0, Duration.ofMillis(14));
		tools.mouseMove(188, 62, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(189, 62, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(189, 63, 0, 0, 0, Duration.ofMillis(48));
		tools.mouseMove(190, 63, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(191, 63, 0, 0, 0, Duration.ofMillis(21));
		tools.mouseMove(192, 63, 0, 0, 0, Duration.ofMillis(9));
		tools.mouseMove(193, 64, 0, 0, 0, Duration.ofMillis(5));
		tools.mouseMove(194, 64, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(195, 64, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(197, 65, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(198, 65, 0, 0, 0, Duration.ofMillis(9));
		tools.mouseMove(199, 65, 0, 0, 0, Duration.ofMillis(4));
		tools.mouseMove(200, 66, 0, 0, 0, Duration.ofMillis(8));
		tools.mouseMove(203, 66, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(204, 66, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(205, 67, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(207, 67, 0, 0, 0, Duration.ofMillis(8));
		tools.mouseMove(208, 67, 0, 0, 0, Duration.ofMillis(5));
		tools.mouseMove(210, 68, 0, 0, 0, Duration.ofMillis(13));
		tools.mouseMove(211, 68, 0, 0, 0, Duration.ofMillis(15));
		tools.mouseMove(213, 68, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(214, 68, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(215, 68, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(216, 68, 0, 0, 0, Duration.ofMillis(8));
		tools.mouseMove(217, 68, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(218, 68, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(220, 68, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(221, 68, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(223, 68, 0, 0, 0, Duration.ofMillis(8));
		tools.mouseMove(224, 68, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(225, 68, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(226, 68, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(227, 68, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(228, 67, 0, 0, 0, Duration.ofMillis(13));
		tools.mouseMove(229, 67, 0, 0, 0, Duration.ofMillis(15));
		tools.mouseMove(230, 67, 0, 0, 0, Duration.ofMillis(13));
		tools.mouseMove(231, 66, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(232, 66, 0, 0, 0, Duration.ofMillis(16));
		tools.mouseMove(232, 65, 0, 0, 0, Duration.ofMillis(8));
		tools.mouseMove(233, 65, 0, 0, 0, Duration.ofMillis(11));
		tools.mouseMove(234, 65, 0, 0, 0, Duration.ofMillis(14));
		tools.mouseMove(235, 65, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(235, 64, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(236, 64, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(237, 64, 0, 0, 0, Duration.ofMillis(14));
		tools.mouseMove(237, 63, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(238, 63, 0, 0, 0, Duration.ofMillis(42));
		tools.mouseMove(239, 63, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(240, 62, 0, 0, 0, Duration.ofMillis(87));
		tools.mouseMove(242, 62, 0, 0, 0, Duration.ofMillis(21));
		tools.mouseMove(243, 62, 0, 0, 0, Duration.ofMillis(14));
		tools.mouseMove(244, 62, 0, 0, 0, Duration.ofMillis(13));
		tools.mouseMove(245, 62, 0, 0, 0, Duration.ofMillis(41));
		tools.mousePress(245, 62, 1, 1, 0, Duration.ofMillis(174));
		tools.mouseRelease(245, 62, 1, 0, 0, Duration.ofMillis(94));
		tools.mouseMove(240, 62, 0, 0, 0, Duration.ofMillis(323));
		tools.mouseMove(232, 63, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(224, 65, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(217, 66, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(212, 67, 0, 0, 0, Duration.ofMillis(8));
		tools.mouseMove(206, 68, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(200, 69, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(193, 70, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(187, 71, 0, 0, 0, Duration.ofMillis(9));
		tools.mouseMove(181, 73, 0, 0, 0, Duration.ofMillis(3));
		tools.mouseMove(176, 74, 0, 0, 0, Duration.ofMillis(9));
		tools.mouseMove(171, 75, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(166, 77, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(162, 78, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(158, 78, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(155, 79, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(152, 80, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(150, 80, 0, 0, 0, Duration.ofMillis(8));
		tools.mouseMove(149, 80, 0, 0, 0, Duration.ofMillis(5));
		tools.mouseMove(148, 80, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(146, 80, 0, 0, 0, Duration.ofMillis(11));
		tools.mouseMove(145, 80, 0, 0, 0, Duration.ofMillis(11));
		tools.mouseMove(144, 80, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(143, 80, 0, 0, 0, Duration.ofMillis(5));
		tools.mouseMove(142, 80, 0, 0, 0, Duration.ofMillis(8));
		tools.mouseMove(141, 80, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(140, 80, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(139, 80, 0, 0, 0, Duration.ofMillis(19));
		tools.mouseMove(138, 81, 0, 0, 0, Duration.ofMillis(69));
		tools.mouseMove(137, 81, 0, 0, 0, Duration.ofMillis(12));
		tools.mouseMove(136, 82, 0, 0, 0, Duration.ofMillis(29));
		tools.mouseMove(135, 82, 0, 0, 0, Duration.ofMillis(6));
		tools.mouseMove(134, 82, 0, 0, 0, Duration.ofMillis(15));
		tools.mouseMove(133, 82, 0, 0, 0, Duration.ofMillis(5));
		tools.mouseMove(133, 83, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(132, 83, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(132, 84, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(131, 84, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(130, 85, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(129, 86, 0, 0, 0, Duration.ofMillis(14));
		tools.mouseMove(128, 86, 0, 0, 0, Duration.ofMillis(20));
		tools.mouseMove(127, 86, 0, 0, 0, Duration.ofMillis(20));
		tools.mouseMove(127, 87, 0, 0, 0, Duration.ofMillis(7));
		tools.mouseMove(126, 89, 0, 0, 0, Duration.ofMillis(62));
		tools.mouseMove(126, 90, 0, 0, 0, Duration.ofMillis(34));
		tools.mouseMove(126, 91, 0, 0, 0, Duration.ofMillis(33));
		tools.mousePress(126, 91, 1, 1, 0, Duration.ofMillis(396));
		tools.mouseRelease(126, 91, 1, 0, 0, Duration.ofMillis(94));

		tools.endIntegrationTest();
	}

}
