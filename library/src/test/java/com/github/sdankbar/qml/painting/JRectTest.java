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
package com.github.sdankbar.qml.painting;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests the JRect class.
 */
public class JRectTest {

	/**
	 *
	 */
	@Test
	public void test_center() {
		{
			final JRect r = JRect.rect(0, 0, 10, 10);
			assertEquals(JPoint.point(5, 5), r.center());
			assertEquals(r, r.moveCenter(JPoint.point(5, 5)));
			for (int x = 0; x < 100; ++x) {
				for (int y = 0; y < 100; ++y) {
					assertEquals(JPoint.point(x, y), r.moveCenter(JPoint.point(x, y)).center());
				}
			}
		}
	}

}
