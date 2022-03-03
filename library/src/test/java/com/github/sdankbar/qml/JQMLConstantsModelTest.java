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

import java.awt.geom.Point2D;

import org.junit.After;
import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.models.singleton.JQMLConstantsModel;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;

/**
 * Tests the JQMLConstantModel class.
 */
public class JQMLConstantsModelTest {

	/**
	 *
	 */
	public interface EventProcessor {
		// Empty Implementation
	}

	public static class Constants {
		public static final int a = 33;
		public static final double b = 1.5;
		public static final String c = "ABC";
		public static final Point2D d = new Point2D.Double(1, 2);
	}

	public enum TestEnum {
		MY_VALUE1, MY_VALUE2, ANOTHER;
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
	@Test
	public void staticClass() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLConstantsModel model = app.getModelFactory().createConstantModel(Constants.class);

		assertEquals("Constants", model.getModelName());
		final JQMLSingletonModel<Object> inner = app.getModelFactory().getSingletonModel("Constants").get();
		assertEquals(Constants.a, inner.get("a").asInteger());
		assertEquals(Constants.b, inner.get("b").asDouble(), 0.0001);
		assertEquals(Constants.c, inner.get("c").asString());
		assertEquals(Constants.d, inner.get("d").asPoint());
	}

	/**
	 *
	 */
	@Test
	public void enumClass() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLConstantsModel model = app.getModelFactory().createEnumModel(TestEnum.class);

		assertEquals("TestEnum", model.getModelName());
		final JQMLSingletonModel<Object> inner = app.getModelFactory().getSingletonModel("TestEnum").get();
		assertEquals(0, inner.get("MY_VALUE1").asInteger());
		assertEquals(1, inner.get("MY_VALUE2").asInteger());
		assertEquals(2, inner.get("ANOTHER").asInteger());
	}

}
