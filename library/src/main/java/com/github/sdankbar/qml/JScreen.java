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
package com.github.sdankbar.qml;

import java.awt.geom.Rectangle2D;
import java.util.Objects;

/**
 * Details about a screen (ex. dimensions, dpi, etc.)
 */
public class JScreen {

	private final Rectangle2D geometry;
	private final double dpi;

	/**
	 * Constructor;
	 *
	 * @param geometry Screen's geometry
	 * @param dpi      Screen's average dots per inch
	 */
	public JScreen(final Rectangle2D geometry, final double dpi) {
		this.geometry = Objects.requireNonNull(geometry, "geometry is null");
		this.dpi = dpi;
	}

	public JScreen(final int x, final int y, final int w, final int h, final double dpi) {
		geometry = new Rectangle2D.Double(x, y, w, h);
		this.dpi = dpi;
	}

	/**
	 * @return the screen dots per inch.
	 */
	public double getDpi() {
		return dpi;
	}

	/**
	 * @return the screen geometry.
	 */
	public Rectangle2D getGeometry() {
		return geometry;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JScreen [geometry=" + geometry + ", dpi=" + dpi + "]";
	}

}
