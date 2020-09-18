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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.github.sdankbar.qml.utility.QMLRequestParser;

/**
 * Tests the EventParser.
 */
public class EventParserTest {

	/**
	 *
	 */
	@Test
	public void getters() {
		final ByteBuffer buffer = ByteBuffer.allocate(100);
		buffer.order(ByteOrder.nativeOrder());
		buffer.put((byte) 1);
		buffer.put((byte) 0);
		buffer.putInt(new Color(30, 31, 32).getRGB());
		buffer.putDouble(928.1);
		buffer.putFloat(928.2f);
		buffer.putInt(13);
		buffer.putLong(14);

		buffer.put("Hello".getBytes(StandardCharsets.UTF_8));

		buffer.position(0);

		final QMLRequestParser p = new QMLRequestParser(buffer);

		assertTrue(p.getBoolean());
		assertFalse(p.getBoolean());
		assertEquals(p.getColor(), new Color(30, 31, 32));
		assertEquals(p.getDouble(), 928.1, 0.0001);
		assertEquals(p.getFloat(), 928.2, 0.0001);
		assertEquals(p.getInteger(), 13);
		assertEquals(p.getLong(), 14);
		assertEquals(p.getString(), "Hello");
	}
}
