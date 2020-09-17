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
package com.github.sdankbar.qml.invocation;

import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.github.sdankbar.qml.JInvokable;
import com.github.sdankbar.qml.utility.QMLRequestParser;

/**
 * Tests the RefectiveEventFactory.
 */
public class InvocationWrapperTest {

	/**
	 *
	 *
	 */
	public static class InvokeObject {
		private boolean call1Called = false;
		private boolean call2Called = false;
		private boolean call3Called = false;

		@JInvokable
		public void call1(final boolean b) {
			call1Called = true;
		}

		@JInvokable
		public void call2(final int b) {
			call2Called = true;
		}

		@JInvokable
		public void call3(final double a, final String b) {
			call3Called = true;
		}
	}

	/**
	 *
	 */
	@Test
	public void testInvokeBoolean() {
		final InvokeObject obj = new InvokeObject();
		final InvocableWrapper wrapper = new InvocableWrapper(obj);

		final ByteBuffer buffer = ByteBuffer.allocate(64);
		buffer.put((byte) 1);
		buffer.position(0);
		final QMLRequestParser parser = new QMLRequestParser(buffer);

		wrapper.invoke("call1", parser);
		assertTrue(obj.call1Called);
	}

	/**
	 *
	 */
	@Test
	public void testInvokeInteger() {
		final InvokeObject obj = new InvokeObject();
		final InvocableWrapper wrapper = new InvocableWrapper(obj);

		final ByteBuffer buffer = ByteBuffer.allocate(64);
		buffer.putInt(3);
		buffer.position(0);
		final QMLRequestParser parser = new QMLRequestParser(buffer);

		wrapper.invoke("call2", parser);
		assertTrue(obj.call2Called);
	}

	/**
	 *
	 */
	@Test
	public void testInvokeMultipleParameters() {
		final InvokeObject obj = new InvokeObject();
		final InvocableWrapper wrapper = new InvocableWrapper(obj);

		final ByteBuffer buffer = ByteBuffer.allocate(64);
		buffer.putDouble(4.0);
		buffer.put("ABCD".getBytes());
		buffer.put((byte) 0);
		buffer.position(0);
		final QMLRequestParser parser = new QMLRequestParser(buffer);

		wrapper.invoke("call3", parser);
		assertTrue(obj.call3Called);
	}

}
