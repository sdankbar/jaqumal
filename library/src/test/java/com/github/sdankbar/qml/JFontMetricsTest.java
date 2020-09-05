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

import static org.junit.Assert.assertEquals;

import java.awt.Rectangle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.fonts.JFont;
import com.github.sdankbar.qml.fonts.JFont.Weight;
import com.github.sdankbar.qml.fonts.JFontMetrics;
import com.google.common.collect.ImmutableSet;

/**
 * Tests the JFontMetrics class.
 */
public class JFontMetricsTest {

	/**
	 *
	 */
	@Test
	public void builder() {
		{
			final JFont f = JFont.builder().setFamily("Arial").setPointSize(20).setWeight(Weight.ExtraBold).build();
			final JFontMetrics metrics = f.getJFontMetrics();
			assertEquals(new Rectangle(0, 0, 19, 32), metrics.getBoundingRect(new Rectangle(0, 0, 1000, 1000),
					ImmutableSet.of(), ImmutableSet.of(), "A"));
			assertEquals(new Rectangle(0, 0, 20, 65), metrics.getBoundingRect(new Rectangle(0, 0, 1000, 1000),
					ImmutableSet.of(), ImmutableSet.of(), "A\nB"));
			assertEquals(new Rectangle(0, 0, 39, 98), metrics.getBoundingRect(new Rectangle(0, 0, 1000, 1000),
					ImmutableSet.of(), ImmutableSet.of(), "AB\nC\nD"));
		}
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
	@Before
	public void setup() {
		JQMLApplication.create(new String[0], new NullEventFactory<>());
	}
}
