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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class ResizableByteBuffer {

	private static final byte TRUE = 1;
	private static final byte FALSE = 0;

	private ByteBuffer b;

	public ResizableByteBuffer() {
		b = ByteBuffer.allocate(1024).order(ByteOrder.nativeOrder());
	}

	public ResizableByteBuffer(final int initialSize) {
		b = ByteBuffer.allocate(initialSize).order(ByteOrder.nativeOrder());
	}

	private void checkSize(final int increment) {
		// 0 + 4 > 4
		if ((b.position() + increment) > b.capacity()) {
			final ByteBuffer temp = ByteBuffer.allocate(2 * b.capacity()).order(ByteOrder.nativeOrder());
			System.arraycopy(b.array(), 0, temp.array(), 0, b.capacity());
			b = temp;
		}
	}

	public void putBoolean(final boolean v) {
		checkSize(Byte.BYTES);
		b.put(v ? TRUE : FALSE);
	}

	public void put(final byte v) {
		checkSize(Byte.BYTES);
		b.put(v);
	}

	public void putInt(final int i) {
		checkSize(Integer.BYTES);
		b.putInt(i);
	}

	public void putDouble(final double d) {
		checkSize(Double.BYTES);
		b.putDouble(d);
	}

	public void putString(final String s) {
		final byte[] array = s.getBytes(StandardCharsets.UTF_8);
		checkSize(array.length);
		b.put(array);
	}

	public byte[] toArray() {
		final byte[] dst = new byte[b.position()];
		b.get(dst);
		return dst;
	}
}
