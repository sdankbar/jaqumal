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
package com.github.sdankbar.qml.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A DelayedMap wraps another map and allows for writes to the DelayedMap to be
 * forwarded to the wrapped map in batches. If writes are delayed, getters do
 * not return the delayed values. Any writes performed on the collections
 * returned by keySet(), entrySet(), or values() bypass this classes delay logic
 * so are immediate and thus should be avoided. Direct writes to the wrapped Map
 * should also be avoided due to operations like clear() not working properly in
 * that case.
 *
 * When in DELAYED WriteMode, clear, put, and remove operations respect the
 * order that they are called. So if remove("A") is called then put("A", value)
 * is called, after the Map is flushed, the wrapped Map will contain "A"/value.
 * Same with clear() so any put() calls after the clear() will be written to the
 * wrapped Map.
 *
 * Class is not thread safe.
 *
 * @param <K> The type for the Map's key
 * @param <V> The type for the Map's value
 */
public class DelayedMap<K, V> implements Map<K, V> {

	/**
	 * Enumeration of the various write modes for DelayedMap
	 */
	public enum WriteMode {
		/**
		 * Writes are immediately sent to the wrapped Map
		 */
		IMMEDIATE,
		/**
		 * Writes are held until the DelayedMap is flushed
		 */
		DELAYED
	}

	private final Map<K, V> wrapped;

	private final Map<K, V> newValues = new HashMap<>();
	private final Set<Object> removedKeys = new HashSet<>();

	private boolean immediateWrites = true;

	/**
	 * Constructs a new DelayedMap that wraps the provided Map. Defaults to
	 * WriteMode.IMMEDIATE.
	 *
	 * @param wrapped The Map to wrap.
	 * @throws NullPointerException Thrown if wrapped is null.
	 */
	public DelayedMap(final Map<K, V> wrapped) {
		this.wrapped = Objects.requireNonNull(wrapped, "wrapped is null");
	}

	/**
	 * Constructs a new DelayedMap that wraps the provided Map. Defaults to
	 * WriteMode.IMMEDIATE.
	 *
	 * @param wrapped          The Map to wrap.
	 * @param initialWriteMode The DelayedMap's initial WriteMode
	 * @throws NullPointerException Thrown if wrapped or initialWriteMode is null.
	 */
	public DelayedMap(final Map<K, V> wrapped, final WriteMode initialWriteMode) {
		this.wrapped = Objects.requireNonNull(wrapped, "wrapped is null");
		immediateWrites = WriteMode.IMMEDIATE == Objects.requireNonNull(initialWriteMode, "initialWriteMode is null");
	}

	@Override
	public void clear() {
		if (immediateWrites) {
			wrapped.clear();
		} else {
			newValues.clear();
			removedKeys.addAll(wrapped.keySet());
		}
	}

	@Override
	public boolean containsKey(final Object key) {
		return wrapped.containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value) {
		return wrapped.containsValue(value);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return wrapped.entrySet();
	}

	/**
	 * @return True if obj equals the wrapped Map.
	 */
	@Override
	public boolean equals(final Object obj) {
		return wrapped.equals(obj);
	}

	/**
	 * Flushes any delayed writes to the wrapped Map.
	 */
	public void flush() {
		for (final Object key : removedKeys) {
			wrapped.remove(key);
		}
		removedKeys.clear();

		wrapped.putAll(newValues);
		newValues.clear();
	}

	@Override
	public V get(final Object key) {
		return wrapped.get(key);
	}

	/**
	 * @return The wrapped Map's hashCode.
	 */
	@Override
	public int hashCode() {
		return wrapped.hashCode();
	}

	@Override
	public boolean isEmpty() {
		return wrapped.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return wrapped.keySet();
	}

	/**
	 * @return The current value in the wrapped Map, regardless of WriteMode. So if
	 *         there was a previous call to put() with the same key that has not
	 *         been flushed, that value will not be returned.
	 */
	@Override
	public V put(final K key, final V value) {
		if (immediateWrites) {
			return wrapped.put(key, value);
		} else {
			newValues.put(key, value);
			removedKeys.remove(key);
			return wrapped.get(key);
		}
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> values) {
		if (immediateWrites) {
			wrapped.putAll(values);
		} else {
			newValues.putAll(values);
			removedKeys.removeAll(values.keySet());
		}
	}

	@Override
	public V remove(final Object key) {
		if (immediateWrites) {
			return wrapped.remove(key);
		} else {
			newValues.remove(key);
			removedKeys.add(key);
			return wrapped.get(key);
		}
	}

	/**
	 * Sets the write mode for the DelayMap to use. If going from DELAYED to
	 * IMMEDIATE, automatically flushes the DELAYED data.
	 *
	 * @param mode The new write mode.
	 * @throws NullPointerException Thrown if mode is null.
	 */
	public void setWriteMode(final WriteMode mode) {
		final boolean oldImmediateWrites = immediateWrites;
		immediateWrites = WriteMode.IMMEDIATE == Objects.requireNonNull(mode, "mode is null");
		if (!oldImmediateWrites && immediateWrites) {
			flush();
		}
	}

	@Override
	public int size() {
		return wrapped.size();
	}

	@Override
	public String toString() {
		return "DelayedMap [wrapped=" + wrapped + ", newValues=" + newValues + ", removedKeys=" + removedKeys
				+ ", immediateWrites=" + immediateWrites + "]";
	}

	@Override
	public Collection<V> values() {
		return wrapped.values();
	}

}
