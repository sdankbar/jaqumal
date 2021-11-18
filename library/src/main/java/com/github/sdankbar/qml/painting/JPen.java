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
package com.github.sdankbar.qml.painting;

import java.awt.Color;
import java.util.Objects;

import com.github.sdankbar.qml.utility.ResizableByteBuffer;

public class JPen {
	public enum PenStyle {
		NoPen, // 0
		SolidLine, // 1
		DashLine, // 2
		DotLine, // 3
		DashDotLine, // 4
		DashDotDotLine, // 5
		CustomDashLine// 6
	}

	public enum PenCapStyle {
		FlatCap(0x00), SquareCap(0x10), RoundCap(0x20);

		private final int mask;

		private PenCapStyle(final int mask) {
			this.mask = mask;
		}

		public int getMask() {
			return mask;
		}
	}

	public enum PenJoinStyle {
		MiterJoin(0x00), BevelJoin(0x40), RoundJoin(0x80), SvgMiterJoin(0x100);

		private final int mask;

		private PenJoinStyle(final int mask) {
			this.mask = mask;
		}

		public int getMask() {
			return mask;
		}
	}

	private final Color c;
	private final double width;
	private final PenStyle style;
	private final PenCapStyle capStyle;
	private final PenJoinStyle joinStyle;

	public JPen(final Color c) {
		this.c = Objects.requireNonNull(c, "c is null");
		width = 1;
		style = PenStyle.SolidLine;
		capStyle = PenCapStyle.SquareCap;
		joinStyle = PenJoinStyle.BevelJoin;
	}

	public JPen(final Color c, final double width, final PenStyle style, final PenCapStyle capStyle,
			final PenJoinStyle joinStyle) {
		this.c = Objects.requireNonNull(c, "c is null");
		this.width = width;
		this.style = Objects.requireNonNull(style, "style is null");
		this.capStyle = Objects.requireNonNull(capStyle, "capStyle is null");
		this.joinStyle = Objects.requireNonNull(joinStyle, "joinStyle is null");
	}

	public void serialize(final ResizableByteBuffer buffer) {
		buffer.putInt(c.getRGB());
		buffer.putDouble(width);
		buffer.putInt(style.ordinal());
		buffer.putInt(capStyle.getMask());
		buffer.putInt(joinStyle.getMask());
	}
}
