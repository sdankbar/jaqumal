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
import java.net.URL;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.cpp.jni.data_transfer.QMLDataTransfer;
import com.github.sdankbar.qml.fonts.JFont;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

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
		FONT,
		/**
		 * ImmutableList<Point2D>
		 *
		 * 4 native endian order bytes for count and for each point 8 native endian
		 * order bytes for x, 8 native endian order bytes for y
		 */
		POLYLINE
	}

	private static final Logger logger = LoggerFactory.getLogger(JVariant.class);
	private static final Type[] TYPE_ARRAY = Type.values();

	/**
	 * Immutable JVariant containing the empty string.
	 */
	public static final JVariant NULL_STR = new JVariant("");

	/**
	 * Immutable JVariant containing 0 as an integer.
	 */
	public static final JVariant NULL_INT = new JVariant(0);

	/**
	 * Immutable JVariant containing 0 as a double.
	 */
	public static final JVariant NULL_DOUBLE = new JVariant(0.0);

	/**
	 * Immutable JVariant containing true.
	 */
	public static final JVariant TRUE = new JVariant(true);

	/**
	 * Immutable JVariant containing false.
	 */
	public static final JVariant FALSE = new JVariant(false);

	/**
	 * Returns a TRUE or FALSE JVariant. Does not allocate memory and thus has
	 * better time and space performance compared to calling new JVariant(boolean).
	 *
	 * @param v The value of the JVariant to return.
	 * @return a JVariant containing TRUE or FALSE to match the value of v.
	 */
	public static JVariant valueOf(final boolean v) {
		if (v) {
			return TRUE;
		} else {
			return FALSE;
		}
	}

	private final Type type;

	private final Object obj;

	/**
	 * Constructs a new JVariant from a boolean. Should prefer using valueOf to
	 * avoid memory allocation.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final boolean v) {
		type = Type.BOOL;
		obj = Boolean.valueOf(v);
	}

	/**
	 * Constructs a new JVariant from a Boolean. Should prefer using valueOf to
	 * avoid memory allocation.
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
	 * Constructs a new JVariant from a list of Point2D objects.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final ImmutableList<Point2D> v) {
		type = Type.POLYLINE;
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
	 * @param defaultValue Value to return if JVariant is not a BOOL
	 * @return The JVariant's value as a boolean or the defaultValue if not the
	 *         correct type.
	 */
	public boolean asBoolean(final boolean defaultValue) {
		if (type == Type.BOOL) {
			return ((Boolean) obj).booleanValue();
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a BYTE_ARRAY
	 * @return The JVariant's value as a byte array or the defaultValue if not the
	 *         correct type.
	 */
	public byte[] asByteArray(final byte[] defaultValue) {
		if (type == Type.BYTE_ARRAY) {
			return (byte[]) obj;
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a COLOR
	 * @return The JVariant's value as a Color or the defaultValue if not the
	 *         correct type.
	 */
	public Color asColor(final Color defaultValue) {
		if (type == Type.COLOR) {
			return (Color) obj;
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a DATE_TIME
	 * @return The JVariant's value as an Instant or the defaultValue if not the
	 *         correct type.
	 */
	public Instant asDateTime(final Instant defaultValue) {
		if (type == Type.DATE_TIME) {
			return (Instant) obj;
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a DOUBLE
	 * @return The JVariant's value as a double or the defaultValue if not the
	 *         correct type.
	 */
	public double asDouble(final double defaultValue) {
		if (type == Type.DOUBLE) {
			return ((Double) obj).doubleValue();
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a FLOAT
	 * @return The JVariant's value as a float or the defaultValue if not the
	 *         correct type.
	 */
	public float asFloat(final float defaultValue) {
		if (type == Type.FLOAT) {
			return ((Float) obj).floatValue();
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a FONT
	 * @return The JVariant's value as a Font or the defaultValue if not the correct
	 *         type.
	 */
	public JFont asFont(final JFont defaultValue) {
		if (type == Type.FONT) {
			return (JFont) obj;
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a Image
	 * @return The JVariant's value as a BufferedImage or the defaultValue if not
	 *         the correct type.
	 */
	public BufferedImage asImage(final BufferedImage defaultValue) {
		if (type == Type.IMAGE) {
			return (BufferedImage) obj;
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a INT
	 * @return The JVariant's value as a int or the defaultValue if not the correct
	 *         type.
	 */
	public int asInteger(final int defaultValue) {
		if (type == Type.INT) {
			return ((Integer) obj).intValue();
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a LINE
	 * @return The JVariant's value as a Line2D or the defaultValue if not the
	 *         correct type.
	 */
	public Line2D asLine(final Line2D defaultValue) {
		if (type == Type.LINE) {
			return (Line2D) obj;
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a LONG
	 * @return The JVariant's value as a long or the defaultValue if not the correct
	 *         type.
	 */
	public long asLong(final long defaultValue) {
		if (type == Type.LONG) {
			return ((Long) obj).longValue();
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a POINT
	 * @return The JVariant's value as a Point2D or the defaultValue if not the
	 *         correct type.
	 */
	public Point2D asPoint(final Point2D defaultValue) {
		if (type == Type.POINT) {
			return (Point2D) obj;
		} else {
			return defaultValue;
		}
	}

	/**
	 * @return The JVariant's value as a list of Point2D objects.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not
	 *                                  POLYLINE
	 */
	@SuppressWarnings("unchecked")
	public ImmutableList<Point2D> asPolyline() {
		Preconditions.checkArgument(type == Type.POLYLINE, "Wrong type, type is {}", type);
		return (ImmutableList<Point2D>) obj;
	}

	/**
	 * @param defaultValue Value to return if JVariant is not a POLYLINE
	 * @return The JVariant's value as an ImmutableList<Point2D> or the defaultValue
	 *         if not the correct type.
	 */
	@SuppressWarnings("unchecked")
	public ImmutableList<Point2D> asPolyline(final ImmutableList<Point2D> defaultValue) {
		if (type == Type.POLYLINE) {
			return (ImmutableList<Point2D>) obj;
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a RECTANGLE
	 * @return The JVariant's value as a Rectangle2D or the defaultValue if not the
	 *         correct type.
	 */
	public Rectangle2D asRectangle(final Rectangle2D defaultValue) {
		if (type == Type.RECTANGLE) {
			return (Rectangle2D) obj;
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a REGULAR_EXPRESSION
	 * @return The JVariant's value as a Pattern or the defaultValue if not the
	 *         correct type.
	 */
	public Pattern asRegularExpression(final Pattern defaultValue) {
		if (type == Type.REGULAR_EXPRESSION) {
			return (Pattern) obj;
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a SIZE
	 * @return The JVariant's value as a Dimension or the defaultValue if not the
	 *         correct type.
	 */
	public Dimension asSize(final Dimension defaultValue) {
		if (type == Type.SIZE) {
			return (Dimension) obj;
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a STRING
	 * @return The JVariant's value as a String or the defaultValue if not the
	 *         correct type.
	 */
	public String asString(final String defaultValue) {
		if (type == Type.STRING) {
			return (String) obj;
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a URL
	 * @return The JVariant's value as a URL or the defaultValue if not the correct
	 *         type.
	 */
	public URL asURL(final URL defaultValue) {
		if (type == Type.URL) {
			return (URL) obj;
		} else {
			return defaultValue;
		}
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
	 * @param defaultValue Value to return if JVariant is not a UUID
	 * @return The JVariant's value as a UUID or the defaultValue if not the correct
	 *         type.
	 */
	public UUID asUUID(final UUID defaultValue) {
		if (type == Type.UUID) {
			return (UUID) obj;
		} else {
			return defaultValue;
		}
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

	public void sendToQML(final int role) {
		switch (type) {
			case BOOL: {
				QMLDataTransfer.setBoolean(((Boolean) obj).booleanValue(), role);
				break;
			}
			case BYTE_ARRAY: {
				QMLDataTransfer.setByteArray(((byte[]) obj), role);
				break;
			}
			case COLOR: {
				QMLDataTransfer.setColor(((Color) obj).getRGB(), role);
				break;
			}
			case DATE_TIME: {
				// TODO
				break;
			}
			case DOUBLE: {
				QMLDataTransfer.setDouble(((Double) obj).doubleValue(), role);
				break;
			}
			case FLOAT: {
				QMLDataTransfer.setFloat(((Float) obj).floatValue(), role);
				break;
			}
			case IMAGE: {
				// TODO
				break;
			}
			case INT: {
				QMLDataTransfer.setInteger(((Integer) obj).intValue(), role);
				break;
			}
			case LINE: {
				// TODO
				break;
			}
			case LONG: {
				QMLDataTransfer.setLong(((Long) obj).longValue(), role);
				break;
			}
			case POINT: {
				// TODO
				break;
			}
			case RECTANGLE: {
				// TODO
				break;
			}
			case REGULAR_EXPRESSION: {
				// TODO
				break;
			}
			case SIZE: {
				// TODO
				break;
			}
			case STRING: {
				QMLDataTransfer.setString(((String) obj), role);
				break;
			}
			case URL: {
				QMLDataTransfer.setString(((URL) obj).toExternalForm(), role);
				break;
			}
			case UUID: {
				QMLDataTransfer.setUUID(((UUID) obj).toString(), role);
				break;
			}
			case FONT: {
				QMLDataTransfer.setFont(((JFont) obj).toString(), role);
				break;
			}
			case POLYLINE: {
				// TODO
				break;
			}
			default: {
				logger.error("Unkonwn type {}", type);
				throw new IllegalStateException("Unkonwn type " + type);
			}
		}// end switch
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JVariant [type=" + type + ", obj=" + obj + "]";
	}

}
