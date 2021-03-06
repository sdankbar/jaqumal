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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Provides methods for fast access to JFonts.
 */
public final class JFontCache {

	private final HashMap<JFont.Builder, JFont> builderCache = new HashMap<>();
	private final HashMap<String, JFont> stringCache = new HashMap<>();
	private final List<JFont> indexLookup = new ArrayList<>();
	private int index = 0;

	/**
	 * Retrieve a JFont from the cache or create it if it is not in the cache.
	 *
	 * @param builder Builder that describes the JFont to return.
	 * @return The JFont for the provided builder.
	 */
	public synchronized JFont getFont(final JFont.Builder builder) {
		final JFont cachedFont = builderCache.get(builder);
		if (cachedFont != null) {
			return cachedFont;
		} else {
			storeAllSizes(new JFont.Builder(builder));
			return builderCache.get(builder);
		}
	}

	/**
	 * Retrieve a JFont from the cache or create it if it is not in the cache.
	 *
	 * @param fontStr String from QFont used to construct a JFont.
	 * @return The JFont for the provided builder.
	 */
	public synchronized JFont getFont(final String fontStr) {
		final JFont cachedFont = stringCache.get(fontStr);
		if (cachedFont != null) {
			return cachedFont;
		} else {
			storeAllSizes(JFont.builder(fontStr));
			return stringCache.get(fontStr);
		}
	}

	public synchronized JFont getFont(final int fontIndex) {
		return indexLookup.get(fontIndex);
	}

	private void storeAllSizes(final JFont.Builder start) {
		for (int size = JFont.MAX_FONT_SIZE; size > 0; --size) {
			start.setPointSize(size);
			storeFont(start);
		}
		for (int size = JFont.MAX_FONT_SIZE; size > 0; --size) {
			start.setPixelSize(size);
			storeFont(start);
		}

		{
			start.setDefaultSize();
			storeFont(start);
		}
	}

	private void storeFont(final JFont.Builder builder) {
		final int fontIndex = index++;
		final String fontStr = builder.getQFontString(fontIndex);
		final JFont f = new JFont(fontStr, fontIndex);

		indexLookup.add(f);
		builderCache.putIfAbsent(new JFont.Builder(builder), f);
		stringCache.putIfAbsent(fontStr, f);
	}

}
