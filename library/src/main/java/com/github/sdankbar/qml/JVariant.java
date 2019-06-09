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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.cpp.memory.SharedJavaCppMemory;
import com.github.sdankbar.qml.fonts.JFont;
import com.google.common.base.Preconditions;

/**
 * Corresponding Java class for QVariant on the C++ side. Used to pass data of
 * various types between Java and C++ and vice versa. Data passing between the
 * two languages is done by serializing the variant into a byte array. This was
 * done so that it was not necessary to write a C++ function for every type that
 * needed to be passed between Java and C++ for every model type.
 *
 * The serialized data format is
 *
 * First byte is the Type of the variant. Maps to Type.ordinal().
 *
 * The remaining bytes are the actual data. Primitive data types like int and
 * float are stored in native endian order. Types that can be represented as
 * strings are stored in UTF-8. For more details and for how complex types line
 * Point2D are stored, see the documentation for Type.
 */
public class JVariant {

	/**
	 * Enumeration of the types supported by JVariant.
	 */
	public enum Type {
		/**
		 * Pattern
		 *
		 * UTF-8
		 */
		REGULAR_EXPRESSION,
		/**
		 * URL
		 *
		 * UTF-8
		 */
		URL,
		/**
		 * UUID
		 *
		 * UTF-8
		 */
		UUID,
		/**
		 * int
		 *
		 * 4 native endian order bytes
		 */
		INT,
		/**
		 * long
		 *
		 * 8 native endian order bytes
		 */
		LONG,
		/**
		 * boolean
		 *
		 * 1 byte, 0 or 1
		 */
		BOOL,
		/**
		 * double
		 *
		 * 8 native endian order bytes
		 */
		DOUBLE,
		/**
		 * float
		 *
		 * 4 native endian order bytes
		 */
		FLOAT,
		/**
		 * String
		 *
		 * UTF-8
		 */
		STRING,
		/**
		 * byte[]
		 *
		 * Stored as byte array.
		 */
		BYTE_ARRAY,
		/**
		 * Instant
		 *
		 * 8 native endian order bytes for seconds, 4 native endian order bytes for
		 * nanoseconds.
		 */
		DATE_TIME,
		/**
		 * Dimension
		 *
		 * 8 native endian order bytes for width, 8 native endian order bytes for height
		 */
		SIZE,
		/**
		 * Point2D
		 *
		 * 8 native endian order bytes for x, 8 native endian order bytes for y
		 */
		POINT,
		/**
		 * Line2D
		 *
		 * 8 native endian order bytes for x1, 8 native endian order bytes for y1, 8
		 * native endian order bytes for x2, 8 native endian order bytes for y2.
		 */
		LINE,
		/**
		 * Rectangle2D
		 *
		 * 8 native endian order bytes for x, 8 native endian order bytes for y, 8
		 * native endian order bytes for width, 8 native endian order bytes for height
		 */
		RECTANGLE,
		/**
		 * Color
		 *
		 * 4 bytes in ARGB order.
		 */
		COLOR,
		/**
		 * BufferedImage
		 *
		 * 4 native endian order bytes for image width, 4 native endian order bytes for
		 * image height, width * height * 4 bytes containing uncompressed ARGB pixel
		 * data.
		 */
		IMAGE,
		/**
		 * JFont
		 *
		 * UTF-8
		 */
		FONT
	}

	private static final Logger logger = LoggerFactory.getLogger(JVariant.class);
	private static final Type[] TYPE_ARRAY = Type.values();

	private static void checkSize(final SharedJavaCppMemory reuse, final int size) {
		if (size > reuse.getSize()) {
			throw new IllegalArgumentException("Serialized JVariant does not fit in shared memory");
		}
	}

	/**
	 * Given a ByteBuffer, attempts to deserialize the ByteBuffer into a JVariant.
	 *
	 * @param buffer Buffer to get the byte to deserialize.
	 * @return The deserialized JVariant in an Optional or Optional.empty().
	 */
	public static Optional<JVariant> deserialize(final ByteBuffer buffer) {
		Objects.requireNonNull(buffer, "buffer is null");
		final int index = buffer.get();

		if (index >= TYPE_ARRAY.length) {
			return Optional.empty();
		}

		final Type t = TYPE_ARRAY[index];
		switch (t) {
		case BOOL: {
			return Optional.of(new JVariant(buffer.get() != 0 ? Boolean.TRUE : Boolean.FALSE));
		}
		case BYTE_ARRAY: {
			final int length = buffer.getInt();
			final byte[] array = new byte[length];
			buffer.get(array);
			return Optional.of(new JVariant(array));
		}
		case COLOR: {
			return Optional.of(new JVariant(new Color(buffer.getInt(), true)));
		}
		case DATE_TIME: {
			return Optional.of(new JVariant(Instant.ofEpochSecond(buffer.getLong(), buffer.getInt())));
		}
		case DOUBLE: {
			return Optional.of(new JVariant(buffer.getDouble()));
		}
		case FLOAT: {
			return Optional.of(new JVariant(buffer.getFloat()));
		}
		case IMAGE: {
			final int width = buffer.getInt();
			final int height = buffer.getInt();
			final BufferedImage v = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			for (int r = 0; r < height; ++r) {
				for (int c = 0; c < width; ++c) {
					v.setRGB(c, r, buffer.getInt());
				}
			}
			return Optional.of(new JVariant(v));
		}
		case INT: {
			return Optional.of(new JVariant(buffer.getInt()));
		}
		case LINE: {
			return Optional.of(new JVariant(
					new Line2D.Double(buffer.getDouble(), buffer.getDouble(), buffer.getDouble(), buffer.getDouble())));
		}
		case LONG: {
			return Optional.of(new JVariant(buffer.getLong()));
		}
		case POINT: {
			return Optional.of(new JVariant(new Point2D.Double(buffer.getDouble(), buffer.getDouble())));
		}
		case RECTANGLE: {
			return Optional.of(new JVariant(new Rectangle2D.Double(buffer.getDouble(), buffer.getDouble(),
					buffer.getDouble(), buffer.getDouble())));
		}
		case REGULAR_EXPRESSION: {
			final byte[] array = new byte[buffer.getInt()];
			buffer.get(array);
			final String s = new String(array, StandardCharsets.UTF_8);
			return Optional.of(new JVariant(Pattern.compile(s)));
		}
		case SIZE: {
			final double w = buffer.getDouble();
			final double h = buffer.getDouble();
			final Dimension temp = new Dimension();
			temp.setSize(w, h);
			return Optional.of(new JVariant(temp));
		}
		case STRING: {
			final byte[] array = new byte[buffer.getInt()];
			buffer.get(array);
			final String s = new String(array, StandardCharsets.UTF_8);
			return Optional.of(new JVariant(s));
		}
		case URL: {
			final byte[] array = new byte[buffer.getInt()];
			buffer.get(array);
			final String s = new String(array, StandardCharsets.UTF_8);
			try {
				return Optional.of(new JVariant(new URL(s)));
			} catch (final MalformedURLException e) {
				logger.warn("Failed to deserialize JVariant into URL", e);
				return Optional.empty();
			}
		}
		case UUID: {
			final byte[] array = new byte[buffer.getInt()];
			buffer.get(array);
			final String s = new String(array, StandardCharsets.UTF_8);
			return Optional.of(new JVariant(UUID.fromString(s)));
		}
		case FONT: {
			final byte[] array = new byte[buffer.getInt()];
			buffer.get(array);
			final String s = new String(array, StandardCharsets.UTF_8);
			return Optional.of(new JVariant(JFont.fromString(s)));
		}
		default:
			return Optional.empty();
		}
	}

	/**
	 * Serializes a list of JVariants into the existing memory provided by
	 * SharedJavaCppMemory.
	 *
	 * @param data  List of JVariants to serialize.
	 * @param reuse Existing memory to place the serialized JVariants into, one
	 *              directly after another.
	 */
	public static void serialize(final List<JVariant> data, final SharedJavaCppMemory reuse) {
		int offset = 0;
		for (final JVariant d : data) {
			offset += d.serialize(reuse, offset);
		}
	}

	private final Type type;

	private final Object obj;

	/**
	 * Constructs a new JVariant from a boolean.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final boolean v) {
		type = Type.BOOL;
		obj = Boolean.valueOf(v);
	}

	/**
	 * Constructs a new JVariant from a Boolean.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final Boolean v) {
		type = Type.BOOL;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a BufferedImage.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final BufferedImage v) {
		type = Type.IMAGE;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a byte array.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final byte[] v) {
		type = Type.BYTE_ARRAY;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a Color.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final Color v) {
		type = Type.COLOR;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a Dimension.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final Dimension v) {
		type = Type.SIZE;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a double.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final double v) {
		type = Type.DOUBLE;
		obj = Double.valueOf(v);
	}

	/**
	 * Constructs a new JVariant from a Double.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final Double v) {
		type = Type.DOUBLE;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a float.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final float v) {
		type = Type.FLOAT;
		obj = Float.valueOf(v);
	}

	/**
	 * Constructs a new JVariant from a F.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final Float v) {
		type = Type.FLOAT;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from an Instant.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final Instant v) {
		type = Type.DATE_TIME;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from an int.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final int v) {
		type = Type.INT;
		obj = Integer.valueOf(v);
	}

	/**
	 * Constructs a new JVariant from an Integer.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final Integer v) {
		type = Type.INT;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a JFont.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final JFont v) {
		type = Type.FONT;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a Line2D.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final Line2D v) {
		type = Type.LINE;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a long.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final long v) {
		type = Type.LONG;
		obj = Long.valueOf(v);
	}

	/**
	 * Constructs a new JVariant from a Long.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final Long v) {
		type = Type.LONG;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a Pattern.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final Pattern v) {
		type = Type.REGULAR_EXPRESSION;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a Point2D.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final Point2D v) {
		type = Type.POINT;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a Rectangle2D.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final Rectangle2D v) {
		type = Type.RECTANGLE;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a String.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final String v) {
		type = Type.STRING;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a URL.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final URL v) {
		type = Type.URL;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a UUID.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final UUID v) {
		type = Type.UUID;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * @return The JVariant's value as a boolean.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not BOOL
	 */
	public boolean asBoolean() {
		Preconditions.checkArgument(type == Type.BOOL, "Wrong type, type is {}", type);
		return ((Boolean) obj).booleanValue();
	}

	/**
	 * @return The JVariant's value as a byte array.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not
	 *                                  BYTE_ARRAY
	 */
	public byte[] asByteArray() {
		Preconditions.checkArgument(type == Type.BYTE_ARRAY, "Wrong type, type is {}", type);
		return (byte[]) obj;
	}

	/**
	 * @return The JVariant's value as a Color.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not COLOR.
	 */
	public Color asColor() {
		Preconditions.checkArgument(type == Type.COLOR, "Wrong type, type is {}", type);
		return (Color) obj;
	}

	/**
	 * @return The JVariant's value as an Instant.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not
	 *                                  DATE_TIME
	 */
	public Instant asDateTime() {
		Preconditions.checkArgument(type == Type.DATE_TIME, "Wrong type, type is {}", type);
		return (Instant) obj;
	}

	/**
	 * @return The JVariant's value as a double.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not DOUBLE
	 */
	public double asDouble() {
		Preconditions.checkArgument(type == Type.DOUBLE, "Wrong type, type is {}", type);
		return ((Double) obj).doubleValue();
	}

	/**
	 * @return The JVariant's value as a float.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not FLOAT
	 */
	public float asFloat() {
		Preconditions.checkArgument(type == Type.FLOAT, "Wrong type, type is {}", type);
		return ((Float) obj).floatValue();
	}

	/**
	 * @return The JVariant's value as a JFont.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not Font
	 */
	public JFont asFont() {
		Preconditions.checkArgument(type == Type.FONT, "Wrong type, type is {}", type);
		return (JFont) obj;
	}

	/**
	 * @return The JVariant's value as a BufferedImage.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not IMAGE
	 */
	public BufferedImage asImage() {
		Preconditions.checkArgument(type == Type.IMAGE, "Wrong type, type is {}", type);
		return (BufferedImage) obj;
	}

	/**
	 * @return The JVariant's value as an int.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not INT
	 */
	public int asInteger() {
		Preconditions.checkArgument(type == Type.INT, "Wrong type, type is {}", type);
		return ((Integer) obj).intValue();
	}

	/**
	 * @return The JVariant's value as a Line2D.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not LINE
	 */
	public Line2D asLine() {
		Preconditions.checkArgument(type == Type.LINE, "Wrong type, type is {}", type);
		return (Line2D) obj;
	}

	/**
	 * @return The JVariant's value as a long.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not LONG
	 */
	public long asLong() {
		Preconditions.checkArgument(type == Type.LONG, "Wrong type, type is {}", type);
		return ((Long) obj).longValue();
	}

	/**
	 * @return The JVariant's value as a Point2D.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not POINT
	 */
	public Point2D asPoint() {
		Preconditions.checkArgument(type == Type.POINT, "Wrong type, type is {}", type);
		return (Point2D) obj;
	}

	/**
	 * @return The JVariant's value as a Rectangle2D.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not
	 *                                  RECTANGLE
	 */
	public Rectangle2D asRectangle() {
		Preconditions.checkArgument(type == Type.RECTANGLE, "Wrong type, type is {}", type);
		return (Rectangle2D) obj;
	}

	/**
	 * @return The JVariant's value as a Pattern.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not
	 *                                  REGULAR_EXPRESSION
	 */
	public Pattern asRegularExpression() {
		Preconditions.checkArgument(type == Type.REGULAR_EXPRESSION, "Wrong type, type is {}", type);
		return (Pattern) obj;
	}

	/**
	 * @return The JVariant's value as a Dimension.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not SIZE
	 */
	public Dimension asSize() {
		Preconditions.checkArgument(type == Type.SIZE, "Wrong type, type is {}", type);
		return (Dimension) obj;
	}

	/**
	 * @return The JVariant's value as a String.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not STRING
	 */
	public String asString() {
		Preconditions.checkArgument(type == Type.STRING, "Wrong type, type is {}", type);
		return (String) obj;
	}

	/**
	 * @param c The type to attempt to cast this JVariant's value to.
	 * @return This JVariant's value cast to type T wrapped in an Optional or
	 *         Optional.empty().
	 */
	public <T> Optional<T> asType(final Class<T> c) {
		if (c.isInstance(obj)) {
			return Optional.of(c.cast(obj));
		} else {
			return Optional.empty();
		}
	}

	/**
	 * @param c            The type to attempt to cast this JVariant's value to.
	 * @param defaultValue The value to return if the cast failed.
	 * @return This JVariant's value cast to type T or the defaultValue if unable to
	 *         cast.
	 */
	public <T> T asType(final Class<T> c, final T defaultValue) {
		if (c.isInstance(obj)) {
			return c.cast(obj);
		} else {
			return defaultValue;
		}
	}

	/**
	 * @return The JVariant's value as a URL.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not URL
	 */
	public URL asURL() {
		Preconditions.checkArgument(type == Type.URL, "Wrong type, type is {}", type);
		return (URL) obj;
	}

	/**
	 * @return The JVariant's value as a UUID.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not UUID
	 */
	public UUID asUUID() {
		Preconditions.checkArgument(type == Type.UUID, "Wrong type, type is {}", type);
		return (UUID) obj;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final JVariant other = (JVariant) obj;
		if (this.obj == null) {
			if (other.obj != null) {
				return false;
			}
		} else if (!this.obj.equals(other.obj)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((obj == null) ? 0 : obj.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/**
	 * Returns true if the type of the data stored in this JVariant equals that of
	 * the provided Class.
	 *
	 * @param c The class to compare the stored data's type with.
	 * @return True if the types match.
	 */
	public boolean isInstanceOf(final Class<?> c) {
		return c.isInstance(obj);
	}

	/**
	 * Returns true if the Type of this JVariant equals the provided Type.
	 *
	 * @param t The Type to compare to.
	 * @return True if equal.
	 */
	public boolean isInstanceOf(final Type t) {
		return t == type;
	}

	/**
	 * Serializes this JVariant, storing it at the beginning of the memory provided
	 * by resuse.
	 *
	 * @param reuse The memory to store the serialized JVariant in.
	 */
	public void serialize(final SharedJavaCppMemory reuse) {
		serialize(reuse, 0);
	}

	private int serialize(final SharedJavaCppMemory reuse, final int offset) {
		final ByteBuffer buffer = reuse.getBuffer(offset);
		switch (type) {
		case BOOL: {
			final Boolean v = (Boolean) obj;
			checkSize(reuse, 2);
			buffer.put((byte) type.ordinal());
			buffer.put((byte) (v.booleanValue() ? 1 : 0));
			break;
		}
		case BYTE_ARRAY: {
			final byte[] v = (byte[]) obj;
			checkSize(reuse, 1 + 4 + v.length);
			buffer.put((byte) type.ordinal());
			buffer.putInt(v.length);
			buffer.put(v);
			break;
		}
		case COLOR: {
			final Color v = (Color) obj;
			checkSize(reuse, 1 + 4);
			buffer.put((byte) type.ordinal());
			buffer.putInt(v.getRGB());
			break;
		}
		case DATE_TIME: {
			final Instant v = (Instant) obj;
			checkSize(reuse, 1 + 8 + 4);
			buffer.put((byte) type.ordinal());
			buffer.putLong(v.getEpochSecond());
			buffer.putInt(v.getNano());
			break;
		}
		case DOUBLE: {
			final Double v = (Double) obj;
			checkSize(reuse, 1 + 8);
			buffer.put((byte) type.ordinal());
			buffer.putDouble(v.doubleValue());
			break;
		}
		case FLOAT: {
			final Float v = (Float) obj;
			checkSize(reuse, 1 + 4);
			buffer.put((byte) type.ordinal());
			buffer.putFloat(v.floatValue());
			break;
		}
		case IMAGE: {
			final BufferedImage v = (BufferedImage) obj;
			final int[] pixels = v.getRGB(0, 0, v.getWidth(), v.getHeight(), null, 0, v.getWidth());
			checkSize(reuse, 1 + 4 + 4 + (4 * pixels.length));
			buffer.put((byte) type.ordinal());
			buffer.putInt(v.getWidth());
			buffer.putInt(v.getHeight());
			for (final int i : pixels) {
				buffer.putInt(i);
			}
			break;
		}
		case INT: {
			final Integer v = (Integer) obj;
			checkSize(reuse, 1 + 4);
			buffer.put((byte) type.ordinal());
			buffer.putInt(v.intValue());
			break;
		}
		case LINE: {
			final Line2D v = (Line2D) obj;
			checkSize(reuse, 1 + 8 + 8 + 8 + 8);
			buffer.put((byte) type.ordinal());
			buffer.putDouble(v.getX1());
			buffer.putDouble(v.getY1());
			buffer.putDouble(v.getX2());
			buffer.putDouble(v.getY2());
			break;
		}
		case LONG: {
			final Long v = (Long) obj;
			checkSize(reuse, 1 + 8);

			buffer.put((byte) type.ordinal());
			buffer.putLong(v.longValue());
			break;
		}
		case POINT: {
			final Point2D v = (Point2D) obj;
			checkSize(reuse, 1 + 8 + 8);

			buffer.put((byte) type.ordinal());
			buffer.putDouble(v.getX());
			buffer.putDouble(v.getY());
			break;
		}
		case RECTANGLE: {
			final Rectangle2D v = (Rectangle2D) obj;
			checkSize(reuse, 1 + 8 + 8 + 8 + 8);

			buffer.put((byte) type.ordinal());
			buffer.putDouble(v.getX());
			buffer.putDouble(v.getY());
			buffer.putDouble(v.getWidth());
			buffer.putDouble(v.getHeight());
			break;
		}
		case REGULAR_EXPRESSION: {
			final Pattern v = (Pattern) obj;
			final byte[] utf8 = v.pattern().getBytes(StandardCharsets.UTF_8);
			checkSize(reuse, 1 + 4 + utf8.length);
			buffer.put((byte) type.ordinal());
			buffer.putInt(utf8.length);
			buffer.put(utf8);
			break;
		}
		case SIZE: {
			final Dimension v = (Dimension) obj;
			checkSize(reuse, 1 + 8 + 8);
			buffer.put((byte) type.ordinal());
			buffer.putDouble(v.getWidth());
			buffer.putDouble(v.getHeight());
			break;
		}
		case STRING: {
			final String v = (String) obj;
			final byte[] utf8 = v.getBytes(StandardCharsets.UTF_8);
			checkSize(reuse, 1 + 4 + utf8.length);
			buffer.put((byte) type.ordinal());
			buffer.putInt(utf8.length);
			buffer.put(utf8);
			break;
		}
		case URL: {
			final URL v = (URL) obj;
			final byte[] utf8 = v.toExternalForm().getBytes(StandardCharsets.UTF_8);
			checkSize(reuse, 1 + 4 + utf8.length);
			buffer.put((byte) type.ordinal());
			buffer.putInt(utf8.length);
			buffer.put(utf8);
			break;
		}
		case UUID: {
			final UUID v = (UUID) obj;
			final byte[] utf8 = v.toString().getBytes(StandardCharsets.UTF_8);
			checkSize(reuse, 1 + 4 + utf8.length);
			buffer.put((byte) type.ordinal());
			buffer.putInt(utf8.length);
			buffer.put(utf8);
			break;
		}
		case FONT: {
			final JFont v = (JFont) obj;
			final byte[] utf8 = v.toString().getBytes(StandardCharsets.UTF_8);
			checkSize(reuse, 1 + 4 + utf8.length);
			buffer.put((byte) type.ordinal());
			buffer.putInt(utf8.length);
			buffer.put(utf8);
			break;
		}
		default: {
			logger.error("Unkonwn type {}", type);
			throw new IllegalStateException("Unkonwn type " + type);
		}
		}// end switch

		return buffer.position() - offset;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JVariant [type=" + type + ", obj=" + obj + "]";
	}

}
