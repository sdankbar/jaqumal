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

import java.util.Arrays;
import java.util.HashMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class IndexedMap<K, V> {

	private static class Entry<K, V> {
		private final int index;
		private final K key;
		private V value;

		public Entry(final int index, final K key, final V value) {
			this.index = index;
			this.key = key;
			this.value = value;
		}
	}

	private final Entry<K, V>[] table;
	private final HashMap<K, Entry<K, V>> keyMap;

	@SuppressWarnings("unchecked")
	public IndexedMap(final int totalIndices) {
		table = new Entry[totalIndices];
		keyMap = new HashMap<>(totalIndices);
	}

	public int size() {
		return keyMap.size();
	}

	public boolean isEmpty() {
		return keyMap.isEmpty();
	}

	public boolean containsKey(final K key) {
		return keyMap.containsKey(key);
	}

	public ImmutableSet<K> keySet() {
		return ImmutableSet.copyOf(keyMap.keySet());
	}

	public ImmutableList<V> values() {
		return keyMap.values().stream().map(e -> e.value).collect(ImmutableList.toImmutableList());
	}

	public V get(final K key) {
		final Entry<K, V> item = keyMap.get(key);
		if (item != null) {
			return item.value;
		} else {
			return null;
		}
	}

	public V atIndex(final int index) {
		final Entry<K, V> item = table[index];
		if (item != null) {
			return item.value;
		} else {
			return null;
		}
	}

	public V put(final int index, final K key, final V value) {
		final Entry<K, V> existingItem = table[index];
		if (existingItem != null) {
			Preconditions.checkArgument(existingItem.key.equals(key), "Mapping between index and key changed (%s, %s)",
					index, key);
			final V oldValue = existingItem.value;
			existingItem.value = value;
			return oldValue;
		} else {
			final Entry<K, V> newItem = new Entry<>(index, key, value);
			table[index] = newItem;
			keyMap.put(key, newItem);
			return null;
		}
	}

	public V remove(final K key) {
		final Entry<K, V> item = keyMap.remove(key);
		if (item != null) {
			final V oldValue = item.value;
			table[item.index] = null;
			return oldValue;
		} else {
			return null;
		}
	}

	public void clear() {
		keyMap.clear();
		Arrays.setAll(table, v -> null);
	}

}
