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
package com.github.sdankbar.qml.eventing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Tests the RefectiveEventFactory.
 */
public class ReflectiveEventFactoryTest {

	/**
	 *
	 *
	 */
	public static interface AbstractEventProcessor {
		/**
		 * @param e
		 */
		default void handle(final ComplexClass e) {
			// Empty Implementation
		}

		/**
		 * @param e
		 */
		default void handle(final Event1 e) {
			// Empty Implementation
		}

		/**
		 * @param e
		 */
		default void handle(final Event2 e) {
			// Empty Implementation
		}

		/**
		 * @param e
		 */
		default void handle(final Event3 e) {
			// Empty Implementation
		}
	}

	public static class ComplexClass extends Event<AbstractEventProcessor> {
		public ComplexClass(final List<String> args) {
			// Empty Implementation
		}

		@Override
		public void handle(final AbstractEventProcessor processor) {
			processor.handle(this);
		}
	}

	/**
	 *
	 *
	 */
	public static class Event1 extends Event<AbstractEventProcessor> {

		private final int a;
		private final double b;
		private final String c;

		/**
		 * @param a
		 * @param b
		 * @param c
		 */
		public Event1(final int a, final double b, final String c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}

		/**
		 * @return the a
		 */
		public int getA() {
			return a;
		}

		/**
		 * @return the b
		 */
		public double getB() {
			return b;
		}

		/**
		 * @return the c
		 */
		public String getC() {
			return c;
		}

		@Override
		public void handle(final AbstractEventProcessor processor) {
			processor.handle(this);
		}

	}

	/**
	 *
	 *
	 */
	public static class Event2 extends Event<AbstractEventProcessor> {

		private final boolean a;
		private final float b;
		private final long c;
		private final Color d;
		private final Dimension e;
		private final Instant f;
		private final Point2D g;
		private final Rectangle2D h;

		/**
		 * @param a
		 * @param b
		 * @param c
		 * @param d
		 * @param e
		 * @param f
		 * @param g
		 * @param h
		 */
		public Event2(final boolean a, final float b, final long c, final Color d, final Dimension e, final Instant f,
				final Point2D g, final Rectangle2D h) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
			this.e = e;
			this.f = f;
			this.g = g;
			this.h = h;
		}

		/**
		 * @return the b
		 */
		public float getB() {
			return b;
		}

		/**
		 * @return the c
		 */
		public long getC() {
			return c;
		}

		/**
		 * @return the d
		 */
		public Color getD() {
			return d;
		}

		/**
		 * @return the e
		 */
		public Dimension getE() {
			return e;
		}

		/**
		 * @return the f
		 */
		public Instant getF() {
			return f;
		}

		/**
		 * @return the g
		 */
		public Point2D getG() {
			return g;
		}

		/**
		 * @return the h
		 */
		public Rectangle2D getH() {
			return h;
		}

		@Override
		public void handle(final AbstractEventProcessor processor) {
			processor.handle(this);
		}

		/**
		 * @return the a
		 */
		public boolean isA() {
			return a;
		}

	}

	/**
	 *
	 *
	 */
	public static class Event3 extends Event<AbstractEventProcessor> {

		private final int a;
		private final double b;
		private final String c;

		/**
		 * @param p
		 */
		public Event3(final EventParser p) {
			a = p.getInteger();
			b = p.getDouble();
			c = p.getString();
		}

		/**
		 * @return the a
		 */
		public int getA() {
			return a;
		}

		/**
		 * @return the b
		 */
		public double getB() {
			return b;
		}

		/**
		 * @return the c
		 */
		public String getC() {
			return c;
		}

		@Override
		public void handle(final AbstractEventProcessor processor) {
			processor.handle(this);
		}

	}

	/**
	 *
	 */
	@Test
	public void testCreate_1() {
		final ReflectiveEventFactory<AbstractEventProcessor> f = new ReflectiveEventFactory<>(Event1.class);

		final ByteBuffer buffer = ByteBuffer.allocate(64);
		buffer.putInt(73);
		buffer.putDouble(1.5);
		buffer.put("ABCD".getBytes());
		buffer.put((byte) 0);
		buffer.position(0);
		final EventParser parser = new EventParser(buffer);
		final Event1 e = (Event1) f.create("Event1", parser);

		assertEquals(73, e.getA());
		assertEquals(1.5, e.getB(), 0.001);
		assertEquals("ABCD", e.getC());

		assertNull(f.create("?", parser));
	}

	/**
	 *
	 */
	@Test
	public void testCreate_2() {
		final ReflectiveEventFactory<AbstractEventProcessor> f = new ReflectiveEventFactory<>(Event2.class);

		final ByteBuffer buffer = ByteBuffer.allocate(64);
		buffer.put((byte) 1);
		buffer.putFloat(1.5f);
		buffer.putLong(92);
		buffer.putInt(Color.cyan.getRGB());

		buffer.putInt(4);
		buffer.putInt(102);

		buffer.putLong(212379);

		buffer.putInt(210);
		buffer.putInt(1004);

		buffer.putInt(5);
		buffer.putInt(6);
		buffer.putInt(7);
		buffer.putInt(8);

		buffer.position(0);
		final EventParser parser = new EventParser(buffer);
		final Event2 e = (Event2) f.create("Event2", parser);

		assertTrue(e.isA());
		assertEquals(1.5, e.getB(), 0.001);
		assertEquals(92, e.getC());
		assertEquals(Color.CYAN, e.getD());
		assertEquals(new Dimension(4, 102), e.getE());
		assertEquals(Instant.ofEpochMilli(212379), e.getF());
		assertEquals(new Point(210, 1004), e.getG());
		assertEquals(new Rectangle(5, 6, 7, 8), e.getH());
	}

	/**
	 *
	 */
	@Test
	public void testCreate_3() {
		final ReflectiveEventFactory<AbstractEventProcessor> f = new ReflectiveEventFactory<>(
				Arrays.asList(Event1.class, Event2.class));

		{
			final ByteBuffer buffer = ByteBuffer.allocate(64);
			buffer.putInt(73);
			buffer.putDouble(1.5);
			buffer.put("ABCD".getBytes());
			buffer.put((byte) 0);
			buffer.position(0);
			final EventParser parser = new EventParser(buffer);
			final Event1 e = (Event1) f.create("Event1", parser);

			assertEquals(73, e.getA());
			assertEquals(1.5, e.getB(), 0.001);
			assertEquals("ABCD", e.getC());
		}
		{
			final ByteBuffer buffer = ByteBuffer.allocate(64);
			buffer.put((byte) 1);
			buffer.putFloat(1.5f);
			buffer.putLong(92);
			buffer.putInt(Color.cyan.getRGB());

			buffer.putInt(4);
			buffer.putInt(102);

			buffer.putLong(212379);

			buffer.putInt(210);
			buffer.putInt(1004);

			buffer.putInt(5);
			buffer.putInt(6);
			buffer.putInt(7);
			buffer.putInt(8);

			buffer.position(0);
			final EventParser parser = new EventParser(buffer);
			final Event2 e = (Event2) f.create("Event2", parser);

			assertTrue(e.isA());
			assertEquals(1.5, e.getB(), 0.001);
			assertEquals(92, e.getC());
			assertEquals(Color.CYAN, e.getD());
			assertEquals(new Dimension(4, 102), e.getE());
			assertEquals(Instant.ofEpochMilli(212379), e.getF());
			assertEquals(new Point(210, 1004), e.getG());
			assertEquals(new Rectangle(5, 6, 7, 8), e.getH());
		}
	}

	/**
	 *
	 */
	@Test
	public void testCreate_4() {
		final ReflectiveEventFactory<AbstractEventProcessor> f = new ReflectiveEventFactory<>(Event3.class);

		final ByteBuffer buffer = ByteBuffer.allocate(64);
		buffer.putInt(73);
		buffer.putDouble(1.5);
		buffer.put("ABCD".getBytes());
		buffer.put((byte) 0);
		buffer.position(0);
		final EventParser parser = new EventParser(buffer);
		final Event3 e = (Event3) f.create("Event3", parser);

		assertEquals(73, e.getA());
		assertEquals(1.5, e.getB(), 0.001);
		assertEquals("ABCD", e.getC());
	}

	/**
	 *
	 */
	@Test
	public void testCreate_5() {
		final ReflectiveEventFactory<AbstractEventProcessor> f = ReflectiveEventFactory
				.createFromInterface(AbstractEventProcessor.class);

		final ByteBuffer buffer = ByteBuffer.allocate(64);
		buffer.putInt(73);
		buffer.putDouble(1.5);
		buffer.put("ABCD".getBytes());
		buffer.put((byte) 0);
		buffer.position(0);
		final EventParser parser = new EventParser(buffer);
		final Event3 e = (Event3) f.create("Event3", parser);

		assertEquals(73, e.getA());
		assertEquals(1.5, e.getB(), 0.001);
		assertEquals("ABCD", e.getC());
	}

	/**
	 *
	 */
	@Test
	public void testCreate_6() {
		final ReflectiveEventFactory<AbstractEventProcessor> f = ReflectiveEventFactory
				.createFromInterface(AbstractEventProcessor.class);

		final ByteBuffer buffer = ByteBuffer.allocate(64);
		final EventParser parser = new EventParser(buffer);
		assertNull(f.create("ComplexClass", parser));
	}

	/**
	 *
	 */
	@Test
	public void testCreate_perf() {
		final ReflectiveEventFactory<AbstractEventProcessor> f = new ReflectiveEventFactory<>(Event1.class);

		final ByteBuffer buffer = ByteBuffer.allocate(64);
		buffer.putInt(73);
		buffer.putDouble(1.5);
		buffer.put("ABCD".getBytes());
		buffer.put((byte) 0);

		final long start = System.currentTimeMillis();
		final int iterations = 100000;
		for (int i = 0; i < iterations; ++i) {
			buffer.position(0);
			final EventParser parser = new EventParser(buffer);
			assertTrue(f.create("Event1", parser) != null);
		}
		final long end = System.currentTimeMillis();

		final double seconds = (end - start) / 1000.0;
		System.out.println("Created " + (iterations / seconds) + " events per second");

	}
}
