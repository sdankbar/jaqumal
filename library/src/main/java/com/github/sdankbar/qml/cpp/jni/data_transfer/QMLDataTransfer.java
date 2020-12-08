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
package com.github.sdankbar.qml.cpp.jni.data_transfer;

public class QMLDataTransfer {
	private QMLDataTransfer() {
		// Empty Implementation
	}

	public static native void setInteger(int i, int roleIndex);

	public static native void setLong(long v, int roleIndex);

	public static native void setBoolean(boolean v, int roleIndex);

	public static native void setFloat(float v, int roleIndex);

	public static native void setDouble(double v, int roleIndex);

	public static native void setSize(int w, int h, int roleIndex);

	public static native void setPoint(int x, int y, int roleIndex);

	public static native void setLine(int x1, int y1, int x2, int y2, int roleIndex);

	public static native void setRectangle(int x, int y, int w, int h, int roleIndex);

	public static native void setString(String v, int roleIndex);

	public static native void setRegularExpression(String v, int roleIndex);

	public static native void setURL(String v, int roleIndex);

	public static native void setUUID(String v, int roleIndex);

	public static native void setByteArray(byte[] v, int roleIndex);

	public static native void setColor(int v, int roleIndex);

	public static native void setDateTime(long seconds, int nanos, int roleIndex);

	public static native void setImage(int w, int h, byte[] data, int roleIndex);

	public static native void setFont(String v, int roleIndex);

	public static native void setFont(int fontIndex, int roleIndex);

	public static native void setPolyline(int length, double[] data, int roleIndex);
}
