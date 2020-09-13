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
package com.github.sdankbar.qml.eventing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;

/**
 * The EventParser class allows for the extraction of an Event's data from its
 * serialized format.
 */
public class EventParser {

	private final ByteBuffer buffer;

	/**
	 * @param buffer ByteBuffer containing the Event's data in serialized format.
	 */
	public EventParser(final ByteBuffer buffer) {
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
	 * @return The next 16 bytes as a Rectangle2D.
	 */
	public Rectangle2D getRectangle() {
		return new Rectangle(buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt());
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

}
