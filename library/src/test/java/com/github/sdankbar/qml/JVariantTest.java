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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Test;

import com.github.sdankbar.qml.JVariant.Type;
import com.github.sdankbar.qml.cpp.memory.SharedJavaCppMemory;
import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.fonts.JFont;
import com.google.common.collect.ImmutableList;

/**
 * Tests the JVariant class.
 */
public class JVariantTest {

	/**
	 *
	 */
	@Test
	public void bool() {
		{
			final JVariant v1 = new JVariant(true);
			assertTrue(v1.asBoolean());
			assertTrue(new JVariant(3).asBoolean(true));
			assertTrue(v1.asType(Boolean.class).get().booleanValue());
			assertTrue(v1.asType(Boolean.class, Boolean.FALSE).booleanValue());
			assertFalse(v1.asType(Double.class).isPresent());
			assertEquals(55, v1.asType(Integer.class, Integer.valueOf(55)).intValue());
			assertTrue(v1.isInstanceOf(Boolean.class));
			assertFalse(v1.isInstanceOf(Color.class));
			assertTrue(v1.isInstanceOf(JVariant.Type.BOOL));

			final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
			v1.serialize(memory);
			final ByteBuffer b = memory.getBuffer(0);
			assertEquals(Type.BOOL.ordinal(), b.get());
			assertEquals(1, b.get());

			b.position(0);
			final Optional<JVariant> v2 = JVariant.deserialize(b);
			assertTrue(v2.isPresent());
			assertTrue(v2.get().asBoolean());
		}

		final JVariant v = new JVariant(Boolean.FALSE);
		assertFalse(v.asBoolean());

		try {
			v.asColor();
			assertTrue(false);
		} catch (final IllegalArgumentException e) {
			// Expected
		}
	}

	/**
	 *
	 */
	@Test
	public void byteArray() {
		final byte[] array = new byte[128];
		new Random().nextBytes(array);

		final JVariant v1 = new JVariant(array);
		assertArrayEquals(array, v1.asByteArray());
		assertArrayEquals(new JVariant(3).asByteArray(new byte[2]), new byte[2]);
		assertArrayEquals(array, v1.asType(byte[].class).get());
		assertArrayEquals(array, v1.asType(byte[].class, new byte[2]));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.BYTE_ARRAY.ordinal(), b.get());
		assertEquals(128, b.getInt());
		final byte[] dst = new byte[128];
		b.get(dst);
		assertArrayEquals(array, dst);

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertArrayEquals(array, v2.get().asByteArray());
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
	public void color() {
		final Color v = new Color(45, 46, 47);

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asColor());
		assertEquals(new JVariant(3).asColor(Color.blue), Color.blue);
		assertEquals(v, v1.asType(Color.class).get());
		assertEquals(v, v1.asType(Color.class, Color.yellow));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.COLOR.ordinal(), b.get());
		assertEquals(v.getRGB(), b.getInt());

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(v, v2.get().asColor());
	}

	/**
	 *
	 */
	@Test
	public void dateTime() {
		final Instant v = Instant.now();

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asDateTime());
		assertEquals(new JVariant(3).asDateTime(v), v);
		assertEquals(v, v1.asType(Instant.class).get());
		assertEquals(v, v1.asType(Instant.class, Instant.EPOCH));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.DATE_TIME.ordinal(), b.get());
		assertEquals(v.getEpochSecond(), b.getLong());
		assertEquals(v.getNano(), b.getInt());

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(v, v2.get().asDateTime());
	}

	/**
	 * @throws MalformedURLException
	 *
	 */
	@Test
	public void deserialize_error() throws MalformedURLException {
		{
			final ByteBuffer b = ByteBuffer.allocate(256);
			b.put((byte) 99);

			b.position(0);
			final Optional<JVariant> v2 = JVariant.deserialize(b);
			assertFalse(v2.isPresent());
		}

		{
			final ByteBuffer b = ByteBuffer.allocate(256);
			b.put((byte) Type.URL.ordinal());
			b.putInt(9);
			b.put("NOT_A_URL".getBytes());

			b.position(0);
			final Optional<JVariant> v2 = JVariant.deserialize(b);
			assertFalse(v2.isPresent());
		}
	}

	/**
	 *
	 */
	@Test
	public void font() {
		final String[] args = new String[0];
		@SuppressWarnings("unused")
		final JQMLApplication<?> app = JQMLApplication.create(args, new NullEventFactory<>());

		final JFont v = JFont.builder().setFamily("Arial").setBold(true).setPixelSize(20).build();

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asFont());
		assertEquals(new JVariant(3).asFont(v), v);
		assertEquals(v, v1.asType(JFont.class).get());
		assertEquals(v, v1.asType(JFont.class, JFont.builder().build()));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.FONT.ordinal(), b.get());
		assertEquals(26, b.getInt());
		final byte[] dst = new byte[26];
		b.get(dst);
		assertEquals(v, JFont.fromString(new String(dst)));

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(v, v2.get().asFont());
	}

	/**
	 *
	 */
	@Test
	public void image() {
		final BufferedImage v = new BufferedImage(30, 31, BufferedImage.TYPE_INT_ARGB);

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asImage());
		assertEquals(new JVariant(3).asImage(v), v);
		assertEquals(v, v1.asType(BufferedImage.class).get());
		assertEquals(v, v1.asType(BufferedImage.class, new BufferedImage(30, 29, BufferedImage.TYPE_INT_ARGB)));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(16 * 1024);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.IMAGE.ordinal(), b.get());
		assertEquals(30, b.getInt());
		assertEquals(31, b.getInt());

		final int[] dst = new int[30 * 31];
		for (int i = 0; i < dst.length; ++i) {
			dst[i] = b.getInt();
		}
		assertArrayEquals(v.getRGB(0, 0, v.getWidth(), v.getHeight(), null, 0, v.getWidth()), dst);

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertArrayEquals(v.getRGB(0, 0, v.getWidth(), v.getHeight(), null, 0, v.getWidth()),
				v2.get().asImage().getRGB(0, 0, v.getWidth(), v.getHeight(), null, 0, v.getWidth()));
	}

	/**
	 *
	 */
	@Test
	public void line() {
		final Line2D v = new Line2D.Double(33, 29, 34, 30);

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asLine());
		assertEquals(new JVariant(3).asLine(v), v);
		assertEquals(v, v1.asType(Line2D.class).get());
		assertEquals(v, v1.asType(Line2D.class, new Line2D.Double()));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.LINE.ordinal(), b.get());
		assertEquals(33, b.getDouble(), 0.0001);
		assertEquals(29, b.getDouble(), 0.0001);
		assertEquals(34, b.getDouble(), 0.0001);
		assertEquals(30, b.getDouble(), 0.0001);

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		final Line2D temp = v2.get().asLine();
		assertEquals(33, temp.getX1(), 0);
		assertEquals(29, temp.getY1(), 0);
		assertEquals(34, temp.getX2(), 0);
		assertEquals(30, temp.getY2(), 0);
	}

	/**
	 *
	 */
	@Test
	public void point() {
		final Point2D v = new Point2D.Double(33, 29);

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asPoint());
		assertEquals(new JVariant(3).asPoint(v), v);
		assertEquals(v, v1.asType(Point2D.class).get());
		assertEquals(v, v1.asType(Point2D.class, new Point2D.Double(0, 0)));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.POINT.ordinal(), b.get());
		assertEquals(33, b.getDouble(), 0.0001);
		assertEquals(29, b.getDouble(), 0.0001);

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(v, v2.get().asPoint());
	}

	/**
	 *
	 */
	@Test
	public void polygon_0() {
		final JVariant v1 = new JVariant(ImmutableList.of());
		assertEquals(ImmutableList.of(), v1.asPolyline());
		assertEquals(new JVariant(3).asPolyline(ImmutableList.of()), ImmutableList.of());
		assertEquals(ImmutableList.of(), v1.asType(ImmutableList.class).get());
		assertEquals(ImmutableList.of(), v1.asType(ImmutableList.class, ImmutableList.of()));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.POLYLINE.ordinal(), b.get());
		assertEquals(0, b.getInt());

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(ImmutableList.of(), v2.get().asPolyline());
	}

	/**
	 *
	 */
	@Test
	public void polygon_1() {
		final Point2D p1 = new Point2D.Double(33, 29);

		final JVariant v1 = new JVariant(ImmutableList.of(p1));
		assertEquals(ImmutableList.of(p1), v1.asPolyline());
		assertEquals(ImmutableList.of(p1), v1.asType(ImmutableList.class).get());
		assertEquals(ImmutableList.of(p1), v1.asType(ImmutableList.class, ImmutableList.of()));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.POLYLINE.ordinal(), b.get());
		assertEquals(1, b.getInt());
		assertEquals(33, b.getDouble(), 0.0001);
		assertEquals(29, b.getDouble(), 0.0001);

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(ImmutableList.of(p1), v2.get().asPolyline());
	}

	/**
	 *
	 */
	@Test
	public void polygon_2() {
		final Point2D p1 = new Point2D.Double(33, 29);
		final Point2D p2 = new Point2D.Double(34, 30);

		final JVariant v1 = new JVariant(ImmutableList.of(p1, p2));
		assertEquals(ImmutableList.of(p1, p2), v1.asPolyline());
		assertEquals(ImmutableList.of(p1, p2), v1.asType(ImmutableList.class).get());
		assertEquals(ImmutableList.of(p1, p2), v1.asType(ImmutableList.class, ImmutableList.of()));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.POLYLINE.ordinal(), b.get());
		assertEquals(2, b.getInt());
		assertEquals(33, b.getDouble(), 0.0001);
		assertEquals(29, b.getDouble(), 0.0001);
		assertEquals(34, b.getDouble(), 0.0001);
		assertEquals(30, b.getDouble(), 0.0001);

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(ImmutableList.of(p1, p2), v2.get().asPolyline());
	}

	/**
	 *
	 */
	@Test
	public void rectangle() {
		final Rectangle2D v = new Rectangle2D.Double(33, 29, 34, 30);

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asRectangle());
		assertEquals(new JVariant(3).asRectangle(v), v);
		assertEquals(v, v1.asType(Rectangle2D.class).get());
		assertEquals(v, v1.asType(Rectangle2D.class, new Rectangle2D.Double()));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.RECTANGLE.ordinal(), b.get());
		assertEquals(33, b.getDouble(), 0.0001);
		assertEquals(29, b.getDouble(), 0.0001);
		assertEquals(34, b.getDouble(), 0.0001);
		assertEquals(30, b.getDouble(), 0.0001);

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(v, v2.get().asRectangle());
	}

	/**
	 *
	 */
	@Test
	public void regular_expresion() {
		final Pattern v = Pattern.compile(".*");

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asRegularExpression());
		assertEquals(new JVariant(3).asRegularExpression(v), v);
		assertEquals(v, v1.asType(Pattern.class).get());
		assertEquals(v, v1.asType(Pattern.class, Pattern.compile(".+")));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.REGULAR_EXPRESSION.ordinal(), b.get());
		assertEquals(2, b.getInt());
		final byte[] dst = new byte[2];
		b.get(dst);
		assertEquals(v.toString(), Pattern.compile(new String(dst)).toString());

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(v.toString(), v2.get().asRegularExpression().toString());
	}

	/**
	 *
	 */
	@Test
	public void size() {
		final Dimension v = new Dimension(33, 29);

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asSize());
		assertEquals(new JVariant(3).asSize(v), v);
		assertEquals(v, v1.asType(Dimension.class).get());
		assertEquals(v, v1.asType(Dimension.class, new Dimension(0, 0)));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.SIZE.ordinal(), b.get());
		assertEquals(33, b.getDouble(), 0.0001);
		assertEquals(29, b.getDouble(), 0.0001);

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(v, v2.get().asSize());
	}

	/**
	 *
	 */
	@Test(expected = IllegalArgumentException.class)
	public void smallSharedMemory() {
		final BufferedImage v = new BufferedImage(30, 31, BufferedImage.TYPE_INT_ARGB);

		final JVariant v1 = new JVariant(v);

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(1024);
		v1.serialize(memory);
	}

	/**
	 *
	 */
	@Test
	public void string() {
		final String v = "Hello World!!!";

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asString());
		assertEquals(new JVariant(3).asString(v), v);
		assertEquals(v, v1.asType(String.class).get());
		assertEquals(v, v1.asType(String.class, ""));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.STRING.ordinal(), b.get());
		assertEquals(14, b.getInt());
		final byte[] dst = new byte[14];
		b.get(dst);
		assertEquals(v, new String(dst));

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(v, v2.get().asString());
	}

	/**
	 *
	 */
	@Test
	public void test_double() {
		final double v = 33.2;

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asDouble(), 0.00001);
		assertEquals(new JVariant(4).asDouble(3.0), 3.0, 0.00001);
		assertEquals(v, v1.asType(Double.class).get().doubleValue(), 0.00001);
		assertEquals(v, v1.asType(Double.class, Double.valueOf(992.9)).doubleValue(), 0.00001);

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.DOUBLE.ordinal(), b.get());
		assertEquals(v, b.getDouble(), 0.00001);

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(v, v2.get().asDouble(), 0.00001);

		{
			final JVariant v3 = new JVariant(Double.valueOf(v));
			assertEquals(v, v3.asDouble(), 0.00001);
		}
	}

	/**
	 *
	 */
	@Test
	public void test_float() {
		final float v = 33.2f;

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asFloat(), 0.00001);
		assertEquals(new JVariant(4).asFloat(3.0f), 3.0, 0.00001);
		assertEquals(v, v1.asType(Float.class).get().floatValue(), 0.00001);
		assertEquals(v, v1.asType(Float.class, Float.valueOf(992.9f)).floatValue(), 0.00001);

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.FLOAT.ordinal(), b.get());
		assertEquals(v, b.getFloat(), 0.00001);

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(v, v2.get().asFloat(), 0.00001);

		{
			final JVariant v3 = new JVariant(Float.valueOf(v));
			assertEquals(v, v3.asFloat(), 0.00001);
		}
	}

	/**
	 *
	 */
	@Test
	public void test_int() {
		final int v = 33;

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asInteger());
		assertEquals(new JVariant(4.5).asInteger(3), 3);
		assertEquals(v, v1.asType(Integer.class).get().intValue());
		assertEquals(v, v1.asType(Integer.class, Integer.valueOf(44)).intValue());

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.INT.ordinal(), b.get());
		assertEquals(v, b.getInt());

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(v, v2.get().asInteger());

		{
			final JVariant v3 = new JVariant(Integer.valueOf(v));
			assertEquals(v, v3.asInteger());
		}
	}

	/**
	 *
	 */
	@Test
	public void test_long() {
		final long v = 33;

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asLong());
		assertEquals(new JVariant(4.5).asLong(3), 3);
		assertEquals(v, v1.asType(Long.class).get().longValue());
		assertEquals(v, v1.asType(Long.class, Long.valueOf(44)).longValue());

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.LONG.ordinal(), b.get());
		assertEquals(v, b.getLong());

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(v, v2.get().asLong());

		{
			final JVariant v3 = new JVariant(Long.valueOf(v));
			assertEquals(v, v3.asLong());
		}
	}

	/**
	 * @throws MalformedURLException
	 *
	 */
	@Test
	public void url() throws MalformedURLException {
		final URL v = new URL("http://www.a.com");

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asURL());
		assertEquals(new JVariant(4.5).asURL(v), v);
		assertEquals(v, v1.asType(URL.class).get());
		assertEquals(v, v1.asType(URL.class, new URL("http://www.b.com")));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.URL.ordinal(), b.get());
		assertEquals(16, b.getInt());
		final byte[] dst = new byte[16];
		b.get(dst);
		assertEquals(v, new URL(new String(dst)));

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(v, v2.get().asURL());
	}

	/**
	 *
	 */
	@Test
	public void uuid() {
		final UUID v = UUID.randomUUID();

		final JVariant v1 = new JVariant(v);
		assertEquals(v, v1.asUUID());
		assertEquals(new JVariant(4.5).asUUID(v), v);
		assertEquals(v, v1.asType(UUID.class).get());
		assertEquals(v, v1.asType(UUID.class, UUID.randomUUID()));

		final SharedJavaCppMemory memory = new SharedJavaCppMemory(256);
		v1.serialize(memory);
		final ByteBuffer b = memory.getBuffer(0);
		assertEquals(Type.UUID.ordinal(), b.get());
		assertEquals(36, b.getInt());
		final byte[] dst = new byte[36];
		b.get(dst);
		assertEquals(v, UUID.fromString(new String(dst)));

		b.position(0);
		final Optional<JVariant> v2 = JVariant.deserialize(b);
		assertTrue(v2.isPresent());
		assertEquals(v, v2.get().asUUID());
	}
}
