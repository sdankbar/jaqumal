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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.cpp.jni.data_transfer.QMLDataTransfer;
import com.github.sdankbar.qml.fonts.JFont;
import com.github.sdankbar.qml.painting.JPoint;
import com.github.sdankbar.qml.painting.JPointReal;
import com.github.sdankbar.qml.painting.JRect;
import com.github.sdankbar.qml.painting.JRectReal;
import com.github.sdankbar.qml.painting.PainterInstructions;
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
	 * Interface that custom types must implement to be stored inside JVariant.
	 */
	public interface Storable {
		/**
		 * Used to store this Java object in C++. See TestStorable.java for an example
		 * of the Java side of an implementation of this function and see
		 * registerNewType.cpp:setTestStorable for a C++ side example.
		 *
		 * @param role Model user role to store under.
		 */
		void store(int role);
	}

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
		 * JPoint
		 *
		 * 4 native endian order bytes for x, 4 native endian order bytes for y
		 */
		POINT,
		/**
		 * JPointReal
		 *
		 * 8 native endian order bytes for x, 8 native endian order bytes for y
		 */
		POINT_REAL,
		/**
		 * Line2D
		 *
		 * 8 native endian order bytes for x1, 8 native endian order bytes for y1, 8
		 * native endian order bytes for x2, 8 native endian order bytes for y2.
		 */
		LINE,
		/**
		 * JRect
		 *
		 * 4 native endian order bytes for x, 4 native endian order bytes for y, 4
		 * native endian order bytes for width, 4 native endian order bytes for height
		 */
		RECTANGLE,
		/**
		 * JRectReal
		 *
		 * 8 native endian order bytes for x, 8 native endian order bytes for y, 8
		 * native endian order bytes for width, 8 native endian order bytes for height
		 */
		RECTANGLE_REAL,
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
		 * ImmutableList&lt;Point2D&gt;
		 *
		 * 4 native endian order bytes for count and for each point 8 native endian
		 * order bytes for x, 8 native endian order bytes for y
		 */
		POLYLINE,
		/**
		 * PainterInstructions
		 *
		 * 4 native endian bytes for array length and byte array of instructions
		 */
		PAINTER_INSTRUCTIONS,
		/**
		 * User defined type.
		 */
		CUSTOM
	}

	private static final Logger logger = LoggerFactory.getLogger(JVariant.class);

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

	// Used by JNI
	@SuppressWarnings("unused")
	private static JVariant fromBufferedImage(final int w, final int h, final int[] array) {
		final BufferedImage v = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int i = 0;
		for (int r = 0; r < h; ++r) {
			for (int c = 0; c < w; ++c) {
				v.setRGB(c, r, array[i]);
				++i;
			}
		}
		return null;
	}

	// Used by JNI
	@SuppressWarnings("unused")
	private static JVariant fromColor(final int rgba) {
		return new JVariant(new Color(rgba, true));
	}

	// Used by JNI
	@SuppressWarnings("unused")
	private static JVariant fromDimension(final int w, final int h) {
		return new JVariant(new Dimension(w, h));
	}

	// Used by JNI
	@SuppressWarnings("unused")
	private static JVariant fromPolygon(final double[] x, final double[] y) {
		Preconditions.checkArgument(x.length == y.length, "Lengths not equal");
		final ImmutableList.Builder<Point2D> polygon = ImmutableList.builder();
		for (int i = 0; i < x.length; ++i) {
			polygon.add(new Point2D.Double(x[i], y[i]));
		}
		return new JVariant(polygon.build());
	}

	// Used by JNI
	@SuppressWarnings("unused")
	private static JVariant fromInstant(final long epoch, final int nano) {
		return new JVariant(Instant.ofEpochSecond(epoch, nano));
	}

	// Used by JNI
	@SuppressWarnings("unused")
	private static JVariant fromJFont(final String str) {
		return new JVariant(JFont.fromString(str));
	}

	// Used by JNI
	@SuppressWarnings("unused")
	private static JVariant fromLine(final int x1, final int y1, final int x2, final int y2) {
		return new JVariant(new Line2D.Double(x1, y1, x2, y2));
	}

	// Used by JNI
	@SuppressWarnings("unused")
	private static JVariant fromPattern(final String patternStr) {
		return new JVariant(Pattern.compile(patternStr));
	}

	// Used by JNI
	@SuppressWarnings("unused")
	private static JVariant fromPoint(final int x, final int y) {
		return new JVariant(JPoint.point(x, y));
	}

	// Used by JNI
	@SuppressWarnings("unused")
	private static JVariant fromRectangle(final int x, final int y, final int w, final int h) {
		return new JVariant(JRect.rect(x, y, w, h));
	}

	// Used by JNI
	@SuppressWarnings("unused")
	private static JVariant fromURL(final String str) {
		try {
			return new JVariant(new URL(str));
		} catch (final MalformedURLException e) {
			return null;
		}
	}

	// Used by JNI
	@SuppressWarnings("unused")
	private static JVariant fromUUID(final String str) {
		return new JVariant(UUID.fromString(str));
	}

	// Used by JNI
	@SuppressWarnings("unused")
	private static JVariant fromPainterInstructions(final byte[] data) {
		return new JVariant(new PainterInstructions(data));
	}

	// Used by JNI
	@SuppressWarnings("unused")
	private static JVariant fromStorable(final Storable obj) {
		return new JVariant(obj);
	}

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

	/**
	 * @param json JSONObject to parse.
	 * @return The JVariant converted from the JSONObject or empty if parsing fails.
	 */
	public static Optional<JVariant> fromJSON(final JSONObject json) {
		Objects.requireNonNull(json, "json is null");

		final Type t = Type.valueOf(json.getString("type"));
		switch (t) {
		case BOOL: {
			return Optional.of(JVariant.valueOf(Boolean.valueOf(json.getBoolean("value"))));
		}
		case BYTE_ARRAY: {
			final JSONArray array = json.getJSONArray("value");
			final byte[] byteArray = new byte[array.length()];
			for (int i = 0; i < byteArray.length; ++i) {
				byteArray[i] = (byte) array.getInt(i);
			}
			return Optional.of(new JVariant(byteArray));
		}
		case COLOR: {
			final int rgb = json.getInt("value");
			return Optional.of(new JVariant(new Color(rgb, true)));
		}
		case DATE_TIME: {
			final JSONObject sub = json.getJSONObject("value");
			final long seconds = sub.getBigInteger("seconds").longValue();
			final int nano = sub.getInt("nano");
			return Optional.of(new JVariant(Instant.ofEpochSecond(seconds, nano)));
		}
		case DOUBLE: {
			final double v = json.getDouble("value");
			return Optional.of(new JVariant(v));
		}
		case FLOAT: {
			final float v = json.getFloat("value");
			return Optional.of(new JVariant(v));
		}
		case IMAGE: {
			final JSONArray array = json.getJSONArray("value");
			final byte[] byteArray = new byte[array.length()];
			for (int i = 0; i < byteArray.length; ++i) {
				byteArray[i] = (byte) array.getInt(i);
			}
			final ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);
			try {
				return Optional.of(new JVariant(ImageIO.read(stream)));
			} catch (final IOException e) {
				return Optional.empty();
			}
		}
		case INT: {
			final int v = json.getInt("value");
			return Optional.of(new JVariant(v));
		}
		case LINE: {
			final JSONObject sub = json.getJSONObject("value");
			final int x1 = sub.getInt("x1");
			final int y1 = sub.getInt("y1");
			final int x2 = sub.getInt("x2");
			final int y2 = sub.getInt("y2");
			return Optional.of(new JVariant(new Line2D.Double(x1, y1, x2, y2)));
		}
		case LONG: {
			final long v = json.getLong("value");
			return Optional.of(new JVariant(v));
		}
		case POINT: {
			final JSONObject sub = json.getJSONObject("value");
			final int x = sub.getInt("x");
			final int y = sub.getInt("y");
			return Optional.of(new JVariant(JPoint.point(x, y)));
		}
		case POINT_REAL: {
			final JSONObject sub = json.getJSONObject("value");
			final double x = sub.getDouble("x");
			final double y = sub.getDouble("y");
			return Optional.of(new JVariant(JPointReal.point(x, y)));
		}
		case RECTANGLE: {
			final JSONObject sub = json.getJSONObject("value");
			final int x = sub.getInt("x");
			final int y = sub.getInt("y");
			final int w = sub.getInt("w");
			final int h = sub.getInt("h");
			return Optional.of(new JVariant(JRect.rect(x, y, w, h)));
		}
		case RECTANGLE_REAL: {
			final JSONObject sub = json.getJSONObject("value");
			final double x = sub.getDouble("x");
			final double y = sub.getDouble("y");
			final double w = sub.getDouble("w");
			final double h = sub.getDouble("h");
			return Optional.of(new JVariant(JRectReal.rect(x, y, w, h)));
		}
		case REGULAR_EXPRESSION: {
			final String v = json.getString("value");
			return Optional.of(new JVariant(Pattern.compile(v)));
		}
		case SIZE: {
			final JSONObject sub = json.getJSONObject("value");
			final int w = sub.getInt("w");
			final int h = sub.getInt("h");
			return Optional.of(new JVariant(new Dimension(w, h)));
		}
		case STRING: {
			final String v = json.getString("value");
			return Optional.of(new JVariant(v));
		}
		case URL: {
			final String v = json.getString("value");
			try {
				return Optional.of(new JVariant(new URL(v)));
			} catch (final MalformedURLException e) {
				return Optional.empty();
			}
		}
		case UUID: {
			final String v = json.getString("value");
			return Optional.of(new JVariant(UUID.fromString(v)));
		}
		case FONT: {
			final String v = json.getString("value");
			return Optional.of(new JVariant(JFont.fromString(v)));
		}
		case POLYLINE: {
			final ImmutableList.Builder<Point2D> b = ImmutableList.builder();
			final JSONArray array = json.getJSONArray("value");
			for (int i = 0; i < array.length(); ++i) {
				final JSONObject sub = array.getJSONObject(i);
				b.add(new Point2D.Double(sub.getDouble("x"), sub.getDouble("y")));
			}
			return Optional.of(new JVariant(b.build()));
		}
		case PAINTER_INSTRUCTIONS: {
			final JSONArray array = json.getJSONArray("value");
			final byte[] byteArray = new byte[array.length()];
			for (int i = 0; i < byteArray.length; ++i) {
				byteArray[i] = (byte) array.getInt(i);
			}
			return Optional.of(new JVariant(new PainterInstructions(byteArray)));
		}
		case CUSTOM:
		default:
			return Optional.empty();
		}// end switch
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
		Objects.requireNonNull(v, "v is null");
		obj = JPoint.point((int) v.getX(), (int) v.getY());
	}

	/**
	 * Constructs a new JVariant from a JPoint.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final JPoint v) {
		type = Type.POINT;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a JPointReal.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final JPointReal v) {
		type = Type.POINT_REAL;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a Rectangle2D.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final Rectangle2D v) {
		type = Type.RECTANGLE;
		Objects.requireNonNull(v, "v is null");
		obj = JRect.rect((int) v.getX(), (int) v.getY(), (int) v.getWidth(), (int) v.getHeight());
	}

	/**
	 * Constructs a new JVariant from a JRect.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final JRect v) {
		type = Type.RECTANGLE;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a JRectReal.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final JRectReal v) {
		type = Type.RECTANGLE_REAL;
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
	 * Constructs a new JVariant from a PainterInstructions.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final PainterInstructions v) {
		type = Type.PAINTER_INSTRUCTIONS;
		obj = Objects.requireNonNull(v, "v is null");
	}

	/**
	 * Constructs a new JVariant from a user defined type that extends Storable.
	 *
	 * @param v The variant's value.
	 */
	public JVariant(final Storable v) {
		type = Type.CUSTOM;
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
		final JPoint p = (JPoint) obj;
		return new Point2D.Double(p.x(), p.y());
	}

	/**
	 * @param defaultValue Value to return if JVariant is not a POINT
	 * @return The JVariant's value as a Point2D or the defaultValue if not the
	 *         correct type.
	 */
	public Point2D asPoint(final Point2D defaultValue) {
		if (type == Type.POINT) {
			final JPoint p = (JPoint) obj;
			return new Point2D.Double(p.x(), p.y());
		} else {
			return defaultValue;
		}
	}

	/**
	 * @return The JVariant's value as a JPoint.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not POINT
	 */
	public JPoint asJPoint() {
		Preconditions.checkArgument(type == Type.POINT, "Wrong type, type is {}", type);
		return (JPoint) obj;
	}

	/**
	 * @param defaultValue Value to return if JVariant is not a POINT
	 * @return The JVariant's value as a JPoint or the defaultValue if not the
	 *         correct type.
	 */
	public JPoint asJPoint(final JPoint defaultValue) {
		if (type == Type.POINT) {
			return (JPoint) obj;
		} else {
			return defaultValue;
		}
	}

	/**
	 * @return The JVariant's value as a JPointReal.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not POINT
	 */
	public JPointReal asJPointReal() {
		Preconditions.checkArgument(type == Type.POINT_REAL, "Wrong type, type is {}", type);
		return (JPointReal) obj;
	}

	/**
	 * @param defaultValue Value to return if JVariant is not a POINT
	 * @return The JVariant's value as a JPoint or the defaultValue if not the
	 *         correct type.
	 */
	public JPointReal asJPointReal(final JPointReal defaultValue) {
		if (type == Type.POINT_REAL) {
			return (JPointReal) obj;
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
	 * @return The JVariant's value as an ImmutableList&lt;Point2D&gt; or the
	 *         defaultValue if not the correct type.
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
		final JRect r = (JRect) obj;
		return new Rectangle2D.Double(r.x(), r.y(), r.width(), r.height());
	}

	/**
	 * @param defaultValue Value to return if JVariant is not a RECTANGLE
	 * @return The JVariant's value as a Rectangle2D or the defaultValue if not the
	 *         correct type.
	 */
	public Rectangle2D asRectangle(final Rectangle2D defaultValue) {
		if (type == Type.RECTANGLE) {
			final JRect r = (JRect) obj;
			return new Rectangle2D.Double(r.x(), r.y(), r.width(), r.height());
		} else {
			return defaultValue;
		}
	}

	/**
	 * @return The JVariant's value as a Rectangle2D.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not
	 *                                  RECTANGLE
	 */
	public JRect asJRect() {
		Preconditions.checkArgument(type == Type.RECTANGLE, "Wrong type, type is {}", type);
		return (JRect) obj;
	}

	/**
	 * @param defaultValue Value to return if JVariant is not a RECTANGLE
	 * @return The JVariant's value as a Rectangle2D or the defaultValue if not the
	 *         correct type.
	 */
	public JRect asJRect(final JRect defaultValue) {
		if (type == Type.RECTANGLE) {
			return (JRect) obj;
		} else {
			return defaultValue;
		}
	}

	/**
	 * @return The JVariant's value as a Rectangle2D.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not
	 *                                  RECTANGLE
	 */
	public JRectReal asJRectReal() {
		Preconditions.checkArgument(type == Type.RECTANGLE_REAL, "Wrong type, type is {}", type);
		return (JRectReal) obj;
	}

	/**
	 * @param defaultValue Value to return if JVariant is not a RECTANGLE
	 * @return The JVariant's value as a Rectangle2D or the defaultValue if not the
	 *         correct type.
	 */
	public JRectReal asJRectReal(final JRectReal defaultValue) {
		if (type == Type.RECTANGLE_REAL) {
			return (JRectReal) obj;
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
	 * @return The JVariant's value as a PainterInstructions.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not
	 *                                  PAINTER_INSTRUCTIONS
	 */
	public PainterInstructions asPainterInstructions() {
		Preconditions.checkArgument(type == Type.PAINTER_INSTRUCTIONS, "Wrong type, type is {}", type);
		return (PainterInstructions) obj;
	}

	/**
	 * @param defaultValue Value to return if JVariant is not a STRING
	 * @return The JVariant's value as a String or the defaultValue if not the
	 *         correct type.
	 */
	public PainterInstructions asPainterInstructions(final PainterInstructions defaultValue) {
		if (type == Type.PAINTER_INSTRUCTIONS) {
			return (PainterInstructions) obj;
		} else {
			return defaultValue;
		}
	}

	/**
	 * @return The JVariant's value as a Storable.
	 * @throws IllegalArgumentException Thrown if the JVariant's Type is not CUSTOM
	 */
	public Storable asStorable() {
		Preconditions.checkArgument(type == Type.CUSTOM, "Wrong type, type is {}", type);
		return (Storable) obj;
	}

	/**
	 * @param defaultValue Value to return if JVariant is not a CUSTOM
	 * @return The JVariant's value as a Storable or the defaultValue if not the
	 *         correct type.
	 */
	public Storable asStorable(final Storable defaultValue) {
		if (type == Type.CUSTOM) {
			return (Storable) obj;
		} else {
			return defaultValue;
		}
	}

	/**
	 * @param c The type to attempt to cast this JVariant's value to.
	 * @return This JVariant's value cast to type T wrapped in an Optional or
	 *         Optional.empty().
	 */
	@SuppressWarnings("unchecked")
	public <T> Optional<T> asType(final Class<T> c) {
		if (Point2D.class.equals(c) && type == Type.POINT) {
			final JPoint p = (JPoint) obj;
			return (Optional<T>) Optional.of(new Point2D.Double(p.x(), p.y()));
		} else if (Rectangle2D.class.equals(c) && type == Type.RECTANGLE) {
			final JRect r = (JRect) obj;
			return (Optional<T>) Optional.of(new Rectangle2D.Double(r.x(), r.y(), r.width(), r.height()));
		} else if (c.isInstance(obj)) {
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
	@SuppressWarnings("unchecked")
	public <T> T asType(final Class<T> c, final T defaultValue) {
		if (Point2D.class.equals(c) && type == Type.POINT) {
			final JPoint p = (JPoint) obj;
			return (T) new Point2D.Double(p.x(), p.y());
		} else if (Rectangle2D.class.equals(c) && type == Type.RECTANGLE) {
			final JRect r = (JRect) obj;
			return (T) new Rectangle2D.Double(r.x(), r.y(), r.width(), r.height());
		} else if (c.isInstance(obj)) {
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

	/**
	 * Internal method for use by Java models to send data to C++ and ultimately
	 * QML. NOTE FOR USE OUTSIDE OF THIS LIBRARY.
	 *
	 * @param role Model role index to store at.
	 */
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
			final Instant i = (Instant) obj;
			QMLDataTransfer.setDateTime(i.getEpochSecond(), i.getNano(), role);
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
			final BufferedImage image = (BufferedImage) obj;
			final byte[] array = bufferedImageToArray(image);
			QMLDataTransfer.setImage(image.getWidth(), image.getHeight(), array, role);
			break;
		}
		case INT: {
			QMLDataTransfer.setInteger(((Integer) obj).intValue(), role);
			break;
		}
		case LINE: {
			final Line2D l = (Line2D) obj;
			QMLDataTransfer.setLine((int) l.getX1(), (int) l.getY1(), (int) l.getX2(), (int) l.getY2(), role);
			break;
		}
		case LONG: {
			QMLDataTransfer.setLong(((Long) obj).longValue(), role);
			break;
		}
		case POINT: {
			final JPoint p = (JPoint) obj;
			QMLDataTransfer.setPoint(p.x(), p.y(), role);
			break;
		}
		case POINT_REAL: {
			final JPointReal p = (JPointReal) obj;
			QMLDataTransfer.setPointReal(p.x(), p.y(), role);
			break;
		}
		case RECTANGLE: {
			final JRect r = (JRect) obj;
			QMLDataTransfer.setRectangle(r.x(), r.y(), r.width(), r.height(), role);
			break;
		}
		case RECTANGLE_REAL: {
			final JRectReal r = (JRectReal) obj;
			QMLDataTransfer.setRectangleReal(r.x(), r.y(), r.width(), r.height(), role);
			break;
		}
		case REGULAR_EXPRESSION: {
			final Pattern s = (Pattern) obj;
			QMLDataTransfer.setRegularExpression(s.pattern(), role);
			break;
		}
		case SIZE: {
			final Dimension s = (Dimension) obj;
			QMLDataTransfer.setSize(s.width, s.height, role);
			break;
		}
		case STRING: {
			QMLDataTransfer.setString(((String) obj), role);
			break;
		}
		case URL: {
			QMLDataTransfer.setURL(((URL) obj).toExternalForm(), role);
			break;
		}
		case UUID: {
			QMLDataTransfer.setUUID(((UUID) obj).toString(), role);
			break;
		}
		case FONT: {
			final JFont f = (JFont) obj;
			QMLDataTransfer.setFont(f.getFontIndex(), role);
			break;
		}
		case POLYLINE: {
			@SuppressWarnings("unchecked")
			final ImmutableList<Point2D> list = (ImmutableList<Point2D>) obj;
			final double[] array = new double[2 * list.size()];
			int i = 0;
			for (final Point2D p : list) {
				array[i++] = p.getX();
				array[i++] = p.getY();
			}
			QMLDataTransfer.setPolyline(list.size(), array, role);
			break;
		}
		case PAINTER_INSTRUCTIONS: {
			final byte[] array = ((PainterInstructions) obj).getArray();
			QMLDataTransfer.setPainterInstructions(array.length, array, role);
			break;
		}
		case CUSTOM: {
			((Storable) obj).store(role);
			break;
		}
		default: {
			logger.error("Unkonwn type {}", type);
			throw new IllegalStateException("Unkonwn type " + type);
		}
		}// end switch
	}

	private byte[] bufferedImageToArray(final BufferedImage image) {
		final int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		final ByteBuffer b = ByteBuffer.allocate(4 * pixels.length);
		b.order(ByteOrder.nativeOrder());
		for (final int p : pixels) {
			b.putInt(p);
		}
		final byte[] array = b.array();
		return array;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JVariant [type=" + type + ", obj=" + obj + "]";
	}

	/**
	 * @return The JSONObject representation of this JVariant.
	 */
	public JSONObject toJSON() {
		final JSONObject json = new JSONObject();
		json.put("type", type.name());
		switch (type) {
		case BOOL: {
			json.put("value", obj);
			break;
		}
		case BYTE_ARRAY: {
			final byte[] byteArray = (byte[]) obj;
			final JSONArray array = byteArrayToJSONArray(byteArray);
			json.put("value", array);
			break;
		}
		case COLOR: {
			json.put("value", ((Color) obj).getRGB());
			break;
		}
		case DATE_TIME: {
			final Instant i = (Instant) obj;
			final JSONObject sub = new JSONObject();
			sub.put("seconds", i.getEpochSecond());
			sub.put("nano", i.getNano());
			json.put("value", sub);
			break;
		}
		case DOUBLE: {
			json.put("value", obj);
			break;
		}
		case FLOAT: {
			json.put("value", ((Float) obj).doubleValue());
			break;
		}
		case IMAGE: {
			final BufferedImage image = (BufferedImage) obj;
			final ByteArrayOutputStream stream = new ByteArrayOutputStream(image.getHeight() * image.getWidth() * 4);
			try {
				ImageIO.write(image, "PNG", stream);
				final JSONArray array = byteArrayToJSONArray(stream.toByteArray());
				json.put("value", array);
			} catch (final IOException e) {
				json.put("value", JSONObject.NULL);
			}
			break;
		}
		case INT: {
			json.put("value", obj);
			break;
		}
		case LINE: {
			final Line2D l = (Line2D) obj;
			final JSONObject sub = new JSONObject();
			sub.put("x1", Integer.valueOf((int) l.getX1()));
			sub.put("y1", Integer.valueOf((int) l.getY1()));
			sub.put("x2", Integer.valueOf((int) l.getX2()));
			sub.put("y2", Integer.valueOf((int) l.getY2()));
			json.put("value", sub);
			break;
		}
		case LONG: {
			json.put("value", obj);
			break;
		}
		case POINT: {
			final JPoint p = (JPoint) obj;
			final JSONObject sub = new JSONObject();
			sub.put("x", Integer.valueOf(p.x()));
			sub.put("y", Integer.valueOf(p.y()));
			json.put("value", sub);
			break;
		}
		case POINT_REAL: {
			final JPointReal p = (JPointReal) obj;
			final JSONObject sub = new JSONObject();
			sub.put("x", Double.valueOf(p.x()));
			sub.put("y", Double.valueOf(p.y()));
			json.put("value", sub);
			break;
		}
		case RECTANGLE: {
			final JRect r = (JRect) obj;
			final JSONObject sub = new JSONObject();
			sub.put("x", Integer.valueOf(r.x()));
			sub.put("y", Integer.valueOf(r.y()));
			sub.put("w", Integer.valueOf(r.width()));
			sub.put("h", Integer.valueOf(r.height()));
			json.put("value", sub);
			break;
		}
		case RECTANGLE_REAL: {
			final JRectReal r = (JRectReal) obj;
			final JSONObject sub = new JSONObject();
			sub.put("x", Double.valueOf(r.x()));
			sub.put("y", Double.valueOf(r.y()));
			sub.put("w", Double.valueOf(r.width()));
			sub.put("h", Double.valueOf(r.height()));
			json.put("value", sub);
			break;
		}
		case REGULAR_EXPRESSION: {
			final Pattern s = (Pattern) obj;
			json.put("value", s.pattern());
			break;
		}
		case SIZE: {
			final Dimension s = (Dimension) obj;
			final JSONObject sub = new JSONObject();
			sub.put("w", Integer.valueOf(s.width));
			sub.put("h", Integer.valueOf(s.height));
			json.put("value", sub);
			break;
		}
		case STRING: {
			json.put("value", obj);
			break;
		}
		case URL: {
			json.put("value", ((URL) obj).toExternalForm());
			break;
		}
		case UUID: {
			json.put("value", ((UUID) obj).toString());
			break;
		}
		case FONT: {
			final JFont f = (JFont) obj;
			json.put("value", f.toString());
			break;
		}
		case POLYLINE: {
			@SuppressWarnings("unchecked")
			final ImmutableList<Point2D> list = (ImmutableList<Point2D>) obj;
			final JSONArray array = new JSONArray();
			for (final Point2D p : list) {
				final JSONObject sub = new JSONObject();
				sub.put("x", p.getX());
				sub.put("y", p.getY());
				array.put(sub);
			}
			json.put("value", array);
			break;
		}
		case PAINTER_INSTRUCTIONS: {
			final PainterInstructions p = (PainterInstructions) obj;
			final JSONArray array = byteArrayToJSONArray(p.getArray());
			json.put("value", array);
			break;
		}
		case CUSTOM: {
			// Persisting custom types is currently not supported
			json.put("value", JSONObject.NULL);
			break;
		}
		default: {
			logger.error("Unkonwn type {}", type);
			throw new IllegalStateException("Unkonwn type " + type);
		}
		}// end switch
		return json;
	}

	private JSONArray byteArrayToJSONArray(final byte[] byteArray) {
		final JSONArray array = new JSONArray();
		for (final byte b : byteArray) {
			array.put(b);
		}
		return array;
	}

}
