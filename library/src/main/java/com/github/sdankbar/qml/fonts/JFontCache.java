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

import com.google.common.collect.ImmutableMap;

/**
 * Provides methods for fast access to JFonts.
 */
public final class JFontCache {

	private volatile ImmutableMap<JFont.Builder, JFont> builderCache = ImmutableMap.of();
	private volatile ImmutableMap<String, JFont> stringCache = ImmutableMap.of();
	private int index = 0;

	/**
	 * Retrieve a JFont from the cache or create it if it is not in the cache.
	 *
	 * @param builder Builder that describes the JFont to return.
	 * @return The JFont for the provided builder.
	 */
	public JFont getFont(final JFont.Builder builder) {
		final JFont cachedFont = builderCache.get(builder);
		if (cachedFont != null) {
			return cachedFont;
		} else {
			synchronized (this) {
				final int fontIndex = index++;
				final JFont f = new JFont(builder.getQFontString(fontIndex), fontIndex);
				final ImmutableMap.Builder<JFont.Builder, JFont> mapBuilder = ImmutableMap.builder();
				mapBuilder.putAll(builderCache);
				mapBuilder.put(builder, f);
				builderCache = mapBuilder.build();
				return f;
			}

		}
	}

	/**
	 * Retrieve a JFont from the cache or create it if it is not in the cache.
	 *
	 * @param fontStr String from QFont used to construct a JFont.
	 * @return The JFont for the provided builder.
	 */
	public JFont getFont(final String fontStr) {
		final JFont cachedFont = stringCache.get(fontStr);
		if (cachedFont != null) {
			return cachedFont;
		} else {
			synchronized (this) {
				final int fontIndex = index++;
				final JFont f = new JFont(fontStr, fontIndex);
				final ImmutableMap.Builder<String, JFont> mapBuilder = ImmutableMap.builder();
				mapBuilder.putAll(stringCache);
				mapBuilder.put(fontStr, f);
				stringCache = mapBuilder.build();
				return f;
			}
		}
	}

}
