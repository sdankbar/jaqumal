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
package com.github.sdankbar.qml.utility;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;

import com.github.sdankbar.qml.exceptions.QMLException;
import com.github.sdankbar.qml.painting.JPoint;
import com.github.sdankbar.qml.painting.JPointReal;
import com.github.sdankbar.qml.painting.JRect;
import com.github.sdankbar.qml.painting.JRectReal;

/**
 * The QMLRequestParser class allows for the extraction of a QML request's
 * (Event or Invocation) data from its serialized format.
 */
public class QMLRequestParser {

	private final ByteBuffer buffer;

	/**
	 * @param buffer ByteBuffer containing the requests's data in serialized format.
	 */
	public QMLRequestParser(final ByteBuffer buffer) {
		buffer.order(ByteOrder.nativeOrder());
		this.buffer = Objects.requireNonNull(buffer, "buffer is null");
	}

	/**
	 * @return The next byte as a boolean
	 */
	public boolean getBoolean() {
		return buffer.get() != 0;
	}

	/**
	 * @return The next 4 bytes as a Color.
	 */
	public Color getColor() {
		return new Color(buffer.getInt(), true);
	}

	/**
	 * @return The next 8 bytes as a Dimension.
	 */
	public Dimension getDimension() {
		return new Dimension(buffer.getInt(), buffer.getInt());
	}

	/**
	 * @return The next 8 bytes as a double.
	 */
	public double getDouble() {
		return buffer.getDouble();
	}

	/**
	 * @return The next 4 bytes as a float.
	 */
	public float getFloat() {
		return buffer.getFloat();
	}

	/**
	 * @return The next 8 bytes as an Instant.
	 */
	public Instant getInstant() {
		return Instant.ofEpochMilli(buffer.getLong());
	}

	/**
	 * @return The next 4 bytes as an integer.
	 */
	public int getInteger() {
		return buffer.getInt();
	}

	/**
	 * @return The next 8 bytes as a long.
	 */
	public long getLong() {
		return buffer.getLong();
	}

	/**
	 * @return The next 8 bytes as a Point2D.
	 */
	public Point2D getPoint() {
		return new Point(buffer.getInt(), buffer.getInt());
	}

	/**
	 * @return The next 8 bytes as a JPoint.
	 */
	public JPoint getJPoint() {
		return JPoint.point(buffer.getInt(), buffer.getInt());
	}


	/**
	 * @return The next 16 bytes as a JPointReal.
	 */
	public JPointReal getJPointReal() {
		return JPointReal.point(buffer.getDouble(), buffer.getDouble());
	}

	/**
	 * @return The next 16 bytes as a Rectangle2D.
	 */
	public Rectangle2D getRectangle() {
		return new Rectangle(buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt());
	}

	/**
	 * @return The next 16 bytes as a JRect.
	 */
	public JRect getJRectangle() {
		return JRect.rect(buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt());
	}

	/**
	 * @return The next 32 bytes as a JRectReal.
	 */
	public JRectReal getJRectangleReal() {
		return JRectReal.rect(buffer.getDouble(), buffer.getDouble(), buffer.getDouble(), buffer.getDouble());
	}

	/**
	 * Reads until a null character is found and interprets the bytes as UTF-8.
	 *
	 * @return The deserialized String.
	 */
	public String getString() {
		int stringSize = 0;
		buffer.mark();
		while (buffer.get() != 0) {
			++stringSize;
		}
		buffer.reset();
		final byte[] array = new byte[stringSize];
		buffer.get(array);
		// One more get() to get the null terminating character
		buffer.get();

		return new String(array, StandardCharsets.UTF_8);
	}

	public Object getDataBasedOnClass(final Class<?> c) {
		if (c.equals(boolean.class) || c.equals(Boolean.class)) {
			return Boolean.valueOf(getBoolean());
		} else if (c.equals(Color.class)) {
			return getColor();
		} else if (c.equals(Dimension.class)) {
			return getDimension();
		} else if (c.equals(double.class) || c.equals(Double.class)) {
			return Double.valueOf(getDouble());
		} else if (c.equals(float.class) || c.equals(Float.class)) {
			return Float.valueOf(getFloat());
		} else if (c.equals(Instant.class)) {
			return getInstant();
		} else if (c.equals(int.class) || c.equals(Integer.class)) {
			return Integer.valueOf(getInteger());
		} else if (c.equals(long.class) || c.equals(Long.class)) {
			return Long.valueOf(getLong());
		} else if (c.equals(Point2D.class)) {
			return getPoint();
		} else if (c.equals(JPoint.class)) {
			return getJPoint();
		} else if (c.equals(JPointReal.class)) {
			return getJPointReal();
		} else if (c.equals(Rectangle2D.class)) {
			return getRectangle();
		} else if (c.equals(JRect.class)) {
			return getJRectangle();
		} else if (c.equals(JRectReal.class)) {
			return getJRectangleReal();
		} else if (c.equals(String.class)) {
			return getString();
		} else if (c.equals(QMLRequestParser.class)) {
			return this;
		} else {
			throw new QMLException("Unknown parameter type in constructor");
		}
	}

}
