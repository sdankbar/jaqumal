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
package com.github.sdankbar.qml.cpp.memory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.sun.jna.Memory;

/**
 * Creates and manages a block of memory that can be shared between Java and
 * C++.
 */
public class SharedJavaCppMemory {

	private final Memory allocatedMemory;
	private final ByteBuffer buffer;

	/**
	 * Creates a new SharedJavaCppMemory object, with the requested number of bytes
	 * allocated.
	 *
	 * @param size The number of bytes to allocate.
	 */
	public SharedJavaCppMemory(final int size) {
		allocatedMemory = new Memory(size);
		buffer = allocatedMemory.getByteBuffer(0, size);
		buffer.order(ByteOrder.nativeOrder());
	}

	/**
	 * @param position The offset into memory to start the buffer.
	 * @return A ByteBuffer view of this SharedJavaCppMemory object's memory.
	 */
	public ByteBuffer getBuffer(final int position) {
		return buffer.position(position);
	}

	/**
	 * @return The raw pointer.
	 */
	public Memory getPointer() {
		return allocatedMemory;
	}

	/**
	 * @return The number of bytes allocated.
	 */
	public int getSize() {
		return buffer.capacity();
	}
}
