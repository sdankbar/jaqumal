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
package com.github.sdankbar.qml.fonts;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides methods for fast access to JFonts.
 */
public final class JFontCache {

	private final Map<JFont.Builder, JFont> cache = new HashMap<>();
	private int index = 0;

	/**
	 * Retrieve a JFont from the cache or create it if it is not in the cache.
	 *
	 * @param builder Builder that describes the JFont to return.
	 * @return The JFont for the provided builder.
	 */
	public synchronized JFont getFont(final JFont.Builder builder) {
		final JFont cachedFont = cache.get(builder);
		if (cachedFont != null) {
			return cachedFont;
		} else {
			final int fontIndex = index++;
			final JFont f = builder.privateBuild(fontIndex);
			cache.put(builder, f);
			return f;
		}
	}

}
