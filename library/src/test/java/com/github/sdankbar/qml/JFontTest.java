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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.fonts.JFont;
import com.github.sdankbar.qml.fonts.JFont.Weight;

/**
 * Tests the JVariant class.
 */
public class JFontTest {

	/**
	 *
	 */
	public static interface EventProcessor {
		// Empty Implementation
	}

	/**
	 *
	 */
	@Test
	public void builder() {
		final String[] args = new String[0];
		@SuppressWarnings("unused")
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		{
			final JFont f = JFont.builder().build();
			assertEquals(",12,-1,5,50,0,0,0,0,0", f.toString());
			assertEquals(f.getFamily(), "");
			assertEquals(f.getPointSize(), 12);
			assertEquals(f.getPixelSize(), -1);
			assertEquals(f.getJFontInfo().getPointSize(), 12);
			assertEquals(f.getJFontInfo().getPixelSize(), 16);
		}
		{
			final JFont f = JFont.builder().setFamily("Arial").setPointSize(20).setWeight(Weight.ExtraBold).build();
			assertEquals("Arial,20,-1,5,81,0,0,0,0,0", f.toString());
			assertEquals(f.getFamily(), "Arial");
			assertEquals(f.getPointSize(), 20);
			assertEquals(f.getPixelSize(), -1);
			assertTrue(f.getJFontMetrics().getAverageCharWidth() > 10);
		}
		{
			final JFont f = JFont.builder().setFamily("Arial").setPixelSize(19).setWeight(Weight.ExtraBold).build();
			assertEquals("Arial,-1,19,5,81,0,0,0,0,0", f.toString());
			assertEquals(f.getFamily(), "Arial");
			assertEquals(f.getPointSize(), -1);
			assertEquals(f.getPixelSize(), 19);
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
	@Test(expected = IllegalArgumentException.class)
	public void fromStringFailure() {
		final String[] args = new String[0];
		@SuppressWarnings("unused")
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		JFont.fromString("Arial,20,-1,5,81,0,0,0,0,");
	}

	/**
	 *
	 */
	@Test
	public void fromStringSuccess() {
		final String[] args = new String[0];
		@SuppressWarnings("unused")
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		assertNotNull(JFont.fromString("Arial,20,-1,5,81,0,0,0,0,0"));
		assertNotNull(JFont.fromString(",20,-1,5,81,0,0,0,0,0"));
	}

}
