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

public class JRectReal {

	public enum ContainsMode {
		INCLUDE_EDGE, EXCLUDE_EDGE;
	}

	private static final long DOUBLE_ZERO = Double.doubleToLongBits(0);
	public static final JRectReal NULL = new JRectReal(0, 0, 0, 0);

	private static boolean isZero(final double v) {
		return Double.doubleToLongBits(v) == DOUBLE_ZERO;
	}

	public static JRectReal rect(final double x, final double y, final double w, final double h) {
		if (isZero(x) && isZero(y) && isZero(w) && isZero(h)) {
			return NULL;
		} else {
			return new JRectReal(x, y, w, h);
		}
	}

	public static JRectReal rect(final JPointReal topLeft, final JPointReal bottomRight) {
		Objects.requireNonNull(topLeft, "topLeft is null");
		Objects.requireNonNull(bottomRight, "bottomRight is null");
		// bottom = y + h - 1
		// bottom - y + 1 = h
		return rect(topLeft.x(), topLeft.y(), bottomRight.x() - topLeft.x() + 1, bottomRight.y() - topLeft.y() + 1);
	}

	private final double x;
	private final double y;
	private final double w;
	private final double h;

	private JRectReal(final double x, final double y, final double w, final double h) {
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
		return !isZero(w) && !isZero(h);
	}

	public boolean isEmpty() {
		return isZero(w) || isZero(h);
	}

	public double x() {
		return x;
	}

	public double y() {
		return y;
	}

	public double width() {
		return w;
	}

	public double height() {
		return h;
	}

	public JPointReal center() {
		return JPointReal.point(x + w / 2, y + h / 2);
	}

	public double left() {
		return x;
	}

	public double right() {
		return x + w - 1;
	}

	public double top() {
		return y;
	}

	public double bottom() {
		return y + h - 1;
	}

	public JPointReal topLeft() {
		return JPointReal.point(left(), top());
	}

	public JPointReal bottomLeft() {
		return JPointReal.point(left(), bottom());
	}

	public JPointReal topRight() {
		return JPointReal.point(right(), top());
	}

	public JPointReal bottomRight() {
		return JPointReal.point(right(), bottom());
	}

	public boolean contains(final JPointReal p, final ContainsMode mode) {
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

	public JRectReal adjust(final double dx1, final double dy1, final double dx2, final double dy2) {
		final double l = x + dx1;
		final double t = y + dy1;
		final double r = x + w + dx2;
		final double b = y + h + dy2;
		return new JRectReal(l, t, r - l, b - t);
	}

	public JRectReal moveCenter(final JPointReal center) {
		Objects.requireNonNull(center, "center is null");

		// cx = x + w / 2
		// 2 * cx = 2 * x + w
		// (2 * cx - w) / 2 = x
		return new JRectReal((2 * center.x() - w) / 2, (2 * center.y() - h) / 2, w, h);
	}

	public JRectReal moveTo(final JPointReal p) {
		Objects.requireNonNull(p, "p is null");
		return new JRectReal(p.x(), p.y(), w, h);
	}

	public JRectReal translate(final JPointReal p) {
		Objects.requireNonNull(p, "p is null");
		return new JRectReal(x + p.x(), y + p.y(), w, h);
	}

	public JRectReal transpose() {
		return new JRectReal(x, y, h, w);
	}

	public JRectReal union(final JRectReal r) {
		Objects.requireNonNull(r, "r is null");

		final double x = Math.min(this.x, r.x);
		final double y = Math.min(this.y, r.y);
		final double right = Math.max(this.x + w, r.x + r.w);
		final double bottom = Math.max(this.y + h, r.y + r.h);
		return new JRectReal(x, y, right - x, bottom - y);
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
		final JRectReal other = (JRectReal) obj;
		return Double.doubleToLongBits(h) == Double.doubleToLongBits(other.h)
				&& Double.doubleToLongBits(w) == Double.doubleToLongBits(other.w)
				&& Double.doubleToLongBits(x) == Double.doubleToLongBits(other.x)
				&& Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y);
	}

	@Override
	public String toString() {
		return "JRectReal [x=" + x + ", y=" + y + ", w=" + w + ", h=" + h + "]";
	}

}
