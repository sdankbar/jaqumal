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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.fonts.JFont;
import com.github.sdankbar.qml.fonts.JFont.Weight;

/**
 * Tests the JFont class.
 */
public class JFontTest {

	/**
	 *
	 */
	@Test
	public void builder() {
		{
			final JFont f = JFont.builder().build();
			assertEquals(",12,-1,5,400,0,0,0,0,0,0,1,0,0,100,0", f.toString());
			assertEquals(f.getFamily(), "");
			assertEquals(f.getPointSize(), 12);
			assertEquals(f.getPixelSize(), -1);
			assertNotNull(f.getJFontInfo().getPointSize());
		}
		{
			final JFont f = JFont.builder().setFamily("Arial").setPointSize(20).setWeight(Weight.ExtraBold).build();
			assertEquals("Arial,20,-1,5,800,0,0,0,0,0,0,1,0,0,100,0", f.toString());
			assertEquals(f.getFamily(), "Arial");
			assertEquals(f.getPointSize(), 20);
			assertEquals(f.getPixelSize(), -1);
			assertTrue(f.getJFontMetrics().getAverageCharWidth() > 10);
		}
		{
			final JFont f = JFont.builder().setFamily("Arial").setPixelSize(19).setWeight(Weight.ExtraBold).build();
			assertEquals("Arial,-1,19,5,800,0,0,0,0,0,0,1,0,0,100,0", f.toString());
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
		JFont.fromString("Arial,20,-1,5,81,0,0,0,0,");
	}

	/**
	 *
	 */
	@Test
	public void fromStringSuccess() {
		assertNotNull(JFont.fromString("Arial,20,-1,5,81,0,0,0,0,0,0,1,0,0,100,0"));
		assertNotNull(JFont.fromString(",20,-1,5,81,0,0,0,0,0,0,1,0,0,100,0"));
		assertNotNull(JFont.fromString("Roboto Mono,20,-1,5,81,0,0,0,0,0,0,1,0,0,100,0"));
	}

	/**
	 *
	 */
	@Before
	public void setup() {
		JQMLApplication.create(new String[0], new NullEventFactory<>());
	}

	/**
	 *
	 */
	@Test
	public void test_toBuilder() {
		final JFont f = JFont.builder().setFamily("Arial").setPointSize(20).setWeight(Weight.ExtraBold).build();
		assertEquals(f.toString(), f.toBuilder().build().toString());
	}

	/**
	 *
	 */
	@Test
	public void test_scaleTextToFit() {
		final JFont f = JFont.builder().setFamily("Arial").setPointSize(20).build();
		assertEquals(20, f.scaleTextToFit(200, 50, "Hello World", 10).getPointSize());
		assertEquals(20, f.scaleTextToFit(140, 50, "Hello World", 10).getPointSize());
		assertEquals(15, f.scaleTextToFit(100, 50, "Hello World", 10).getPointSize());
		assertEquals(8, f.scaleTextToFit(50, 50, "Hello World", 5).getPointSize());
		assertEquals(5, f.scaleTextToFit(5, 50, "Hello World", 5).getPointSize());
	}

}
