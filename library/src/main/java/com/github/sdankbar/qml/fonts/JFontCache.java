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
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * Provides methods for fast access to JFonts.
 */
public final class JFontCache {

	private ImmutableMap<JFont.Builder, JFont> builderCache = ImmutableMap.of();
	private ImmutableMap<String, JFont> stringCache = ImmutableMap.of();
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
		final Map<JFont.Builder, JFont> tempBuilderMap = new HashMap<>(builderCache);
		final Map<String, JFont> tempStringMap = new HashMap<>(stringCache);

		for (int size = JFont.MAX_FONT_SIZE; size > 0; --size) {
			final JFont.Builder b = start.setPointSize(size);
			final int fontIndex = index++;
			final String fontStr = b.getQFontString(fontIndex);
			final JFont f = new JFont(fontStr, fontIndex);
			storeFont(f, b, fontStr, tempBuilderMap, tempStringMap);
		}
		for (int size = JFont.MAX_FONT_SIZE; size > 0; --size) {
			final JFont.Builder b = start.setPixelSize(size);
			final int fontIndex = index++;
			final String fontStr = b.getQFontString(fontIndex);
			final JFont f = new JFont(fontStr, fontIndex);
			storeFont(f, b, fontStr, tempBuilderMap, tempStringMap);
		}

		final JFont.Builder b = start.setDefaultSize();
		final int fontIndex = index++;
		final String fontStr = b.getQFontString(fontIndex);
		final JFont f = new JFont(fontStr, fontIndex);
		storeFont(f, b, fontStr, tempBuilderMap, tempStringMap);

		builderCache = ImmutableMap.copyOf(tempBuilderMap);
		stringCache = ImmutableMap.copyOf(tempStringMap);
	}

	private void storeFont(final JFont f, final JFont.Builder builder, final String fontStr,
			final Map<JFont.Builder, JFont> tempBuilderMap, final Map<String, JFont> tempStringMap) {
		if (builder != null) {
			tempBuilderMap.put(new JFont.Builder(builder), f);
		} else {
			tempBuilderMap.put(f.toBuilder(), f);
		}
		indexLookup.add(f);

		tempStringMap.putIfAbsent(fontStr, f);
	}

}
