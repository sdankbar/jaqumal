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

import java.util.Objects;

public class JRect {

	public enum ContainsMode {
		INCLUDE_EDGE, EXCLUDE_EDGE;
	}

	public static final JRect NULL = new JRect(0, 0, 0, 0);

	public static JRect rect(final int x, final int y, final int w, final int h) {
		if (x == 0 && y == 0 && w == 0 && h == 0) {
			return NULL;
		} else {
			return new JRect(x, y, w, h);
		}
	}

	public static JRect rect(final JPoint topLeft, final JPoint bottomRight) {
		Objects.requireNonNull(topLeft, "topLeft is null");
		Objects.requireNonNull(bottomRight, "bottomRight is null");
		// bottom = y + h - 1
		// bottom - y + 1 = h
		return rect(topLeft.x(), topLeft.y(), bottomRight.x() - topLeft.x() + 1, bottomRight.y() - topLeft.y() + 1);
	}

	private final int x;
	private final int y;
	private final int w;
	private final int h;

	private JRect(final int x, final int y, final int w, final int h) {
		this.x = x;
		this.y = y;
		if (w > 0) {
			this.w = w;
		} else {
			this.w = 0;
		}

		if (h > 0) {
			this.h = h;
		} else {
			this.h = h;
		}
	}

	public boolean isValid() {
		return w != 0 && h != 0;
	}

	public boolean isEmpty() {
		return w == 0 || h == 0;
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	public int width() {
		return w;
	}

	public int height() {
		return h;
	}

	public JPoint center() {
		return JPoint.point(x + w / 2, y + h / 2);
	}

	public int left() {
		return x;
	}

	public int right() {
		return x + w - 1;
	}

	public int top() {
		return y;
	}

	public int bottom() {
		return y + h - 1;
	}

	public JPoint topLeft() {
		return JPoint.point(left(), top());
	}

	public JPoint bottomLeft() {
		return JPoint.point(left(), bottom());
	}

	public JPoint topRight() {
		return JPoint.point(right(), top());
	}

	public JPoint bottomRight() {
		return JPoint.point(right(), bottom());
	}

	public boolean contains(final JPoint p, final ContainsMode mode) {
		Objects.requireNonNull(p, "p is null");
		Objects.requireNonNull(mode, "mode is null");
		if (isEmpty()) {
			return false;
		} else if (mode == ContainsMode.INCLUDE_EDGE) {
			return x <= p.x() && p.x() <= (x + w) && y <= p.y() && p.y() <= (y + h);
		} else {
			return x < p.x() && p.x() < (x + w) && y < p.y() && p.y() < (y + h);
		}
	}

	public JRect adjust(final int dx1, final int dy1, final int dx2, final int dy2) {
		final int l = x + dx1;
		final int t = y + dy1;
		final int r = x + w + dx2;
		final int b = y + h + dy2;
		return new JRect(l, t, r - l, b - t);
	}

	public JRect moveCenter(final JPoint center) {
		Objects.requireNonNull(center, "center is null");

		// cx = x + w / 2
		// 2 * cx = 2 * x + w
		// (2 * cx - w) / 2 = x
		return new JRect((2 * center.x() - w) / 2, (2 * center.y() - h) / 2, w, h);
	}

	public JRect moveTo(final JPoint p) {
		Objects.requireNonNull(p, "p is null");
		return new JRect(p.x(), p.y(), w, h);
	}

	public JRect translate(final JPoint p) {
		Objects.requireNonNull(p, "p is null");
		return new JRect(x + p.x(), y + p.y(), w, h);
	}

	public JRect transpose() {
		return new JRect(x, y, h, w);
	}

	public JRect union(final JRect r) {
		Objects.requireNonNull(r, "r is null");

		final int x = Math.min(this.x, r.x);
		final int y = Math.min(this.y, r.y);
		final int right = Math.max(this.x + w, r.x + r.w);
		final int bottom = Math.max(this.y + h, r.y + r.h);
		return new JRect(x, y, right - x, bottom - y);
	}

	@Override
	public int hashCode() {
		return Objects.hash(h, w, x, y);
	}

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
		final JRect other = (JRect) obj;
		return h == other.h && w == other.w && x == other.x && y == other.y;
	}

	@Override
	public String toString() {
		return "JRect [x=" + x + ", y=" + y + ", w=" + w + ", h=" + h + "]";
	}

}
