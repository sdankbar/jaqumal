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
package com.github.sdankbar.qml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.sdankbar.qml.models.DelayedMap;
import com.github.sdankbar.qml.models.DelayedMap.WriteMode;
import com.google.common.collect.ImmutableMap;

/**
 * Tests the DelayedMap class.
 */
public class DelayedMapTest {

	/**
	 *
	 */
	@Test
	public void test_delayedWrites() {
		final Map<String, String> wrapped = new HashMap<>();
		final DelayedMap<String, String> delayed = new DelayedMap<>(wrapped, WriteMode.DELAYED);

		assertEquals(null, delayed.put("A", "1"));
		assertEquals(null, delayed.get("A"));
		assertEquals(null, delayed.put("A", "2"));

		delayed.putAll(ImmutableMap.of("B", "3", "C", "4"));
		assertEquals(null, delayed.get("B"));
		assertEquals(0, delayed.size());

		assertEquals(null, delayed.remove("C"));
		assertEquals(0, delayed.size());

		delayed.flush();
		assertEquals("2", delayed.get("A"));
		assertEquals("3", delayed.get("B"));
		assertEquals(2, delayed.size());

		delayed.clear();
		assertEquals(2, delayed.size());

		assertEquals(null, delayed.put("Z", "26"));
		delayed.flush();

		assertEquals(1, delayed.size());
	}

	/**
	 *
	 */
	@Test
	public void test_delayedWrites_thenSwitchToImmediate() {
		final Map<String, String> wrapped = new HashMap<>();
		final DelayedMap<String, String> delayed = new DelayedMap<>(wrapped, WriteMode.DELAYED);

		assertEquals(null, delayed.put("A", "1"));
		assertEquals(null, delayed.get("A"));
		assertEquals(null, delayed.put("A", "2"));

		delayed.putAll(ImmutableMap.of("B", "3", "C", "4"));
		assertEquals(null, delayed.get("B"));
		assertEquals(0, delayed.size());

		assertEquals(null, delayed.remove("C"));
		assertEquals(0, delayed.size());

		delayed.setWriteMode(WriteMode.IMMEDIATE);
		assertEquals("2", delayed.get("A"));
		assertEquals("3", delayed.get("B"));
		assertEquals(2, delayed.size());
	}

	/**
	 *
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void test_getters() {
		final Map<String, String> wrapped = new HashMap<>();
		wrapped.put("A", "1");
		wrapped.put("B", "2");
		wrapped.put("C", "3");

		final DelayedMap<String, String> delayed = new DelayedMap<>(wrapped, WriteMode.IMMEDIATE);

		assertTrue(delayed.containsKey("A"));
		assertTrue(delayed.containsKey("B"));
		assertTrue(delayed.containsKey("C"));
		assertFalse(delayed.containsKey("D"));

		assertTrue(delayed.containsValue("1"));
		assertFalse(delayed.containsValue("4"));

		assertEquals(3, delayed.entrySet().size());

		assertEquals("1", delayed.get("A"));
		assertEquals(null, delayed.get("D"));

		assertFalse(delayed.isEmpty());

		assertEquals(3, delayed.keySet().size());

		assertEquals(3, delayed.size());

		assertEquals(3, delayed.values().size());

		assertNotEquals(null, delayed.toString());

		assertTrue(delayed.equals(wrapped));
		assertTrue(delayed.equals(ImmutableMap.of("A", "1", "B", "2", "C", "3")));
		assertTrue(!delayed.equals(ImmutableMap.of("A", "10", "B", "2", "C", "3")));

		assertEquals(wrapped.hashCode(), delayed.hashCode());
	}

	/**
	 *
	 */
	@Test
	public void test_immediateWrites() {
		final Map<String, String> wrapped = new HashMap<>();
		final DelayedMap<String, String> delayed = new DelayedMap<>(wrapped);

		assertEquals(null, delayed.put("A", "1"));
		assertEquals("1", delayed.get("A"));
		assertEquals("1", delayed.put("A", "2"));

		delayed.putAll(ImmutableMap.of("B", "3", "C", "4"));
		assertEquals("3", delayed.get("B"));
		assertEquals(3, delayed.size());

		assertEquals("4", delayed.remove("C"));
		assertEquals(2, delayed.size());

		delayed.clear();
		assertTrue(delayed.isEmpty());

		delayed.setWriteMode(WriteMode.DELAYED);
	}

}
