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
package com.github.sdankbar.qml.utility;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Tests the IndexedMap class.
 */
public class IndexedMapTest {

	/**
	 *
	 */
	@Test
	public void test_gettersAndSetters() {
		final IndexedMap<String, String> map = new IndexedMap<>(14000);

		map.put(0, "0", "V=0");

		assertEquals(map.get("0"), "V=0");
		assertEquals(map.atIndex(0), "V=0");

		map.put(1, "1", "V=1");

		assertEquals(map.get("1"), "V=1");
		assertEquals(map.getIndexForKey("1"), 1);
		assertEquals(map.atIndex(1), "V=1");

		assertEquals(map.size(), 2);
		assertEquals(map.isEmpty(), false);

		assertEquals(map.containsKey("1"), true);
		assertEquals(map.containsKey("2"), false);
		assertEquals(map.containsIndex(1), true);
		assertEquals(map.containsIndex(2), false);

		assertEquals(map.get("42"), null);
		assertEquals(map.atIndex(42), null);
		assertEquals(map.getIndexForKey("42"), -1);

		map.put(1, "1", "NEW");
		assertEquals(map.get("1"), "NEW");
		assertEquals(map.atIndex(1), "NEW");

		assertEquals(map.remove("2"), null);
		assertEquals(map.size(), 2);
		assertEquals(map.remove("1"), "NEW");
		assertEquals(map.size(), 1);

		map.put(1, "1", "V=1");

		assertEquals(map.removeIndex(1), "V=1");
		assertEquals(map.removeIndex(42), null);

		map.clear();
		assertEquals(map.isEmpty(), true);
	}

	/**
	 *
	 */
	@Test
	public void test_getKeysAndValues() {
		final IndexedMap<String, String> map = new IndexedMap<>(14000);

		map.put(0, "0", "V=0");
		map.put(1, "1", "V=1");

		assertEquals(map.keySet(), ImmutableSet.of("0", "1"));
		assertEquals(map.values(), ImmutableList.of("V=0", "V=1"));
		assertArrayEquals(map.indexSet(), new int[] { 0, 1 });
	}

}
