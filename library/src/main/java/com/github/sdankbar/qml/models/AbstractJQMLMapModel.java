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
package com.github.sdankbar.qml.models;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.exceptions.IllegalKeyException;

/**
 * Abstract base class that provides a Map interface to QML models.
 *
 * @param <K> The Key type for the map.
 */
public abstract class AbstractJQMLMapModel<K> extends AbstractJQMLModel implements Map<K, JVariant> {

	private static class EntrySet<K> extends AbstractSet<Map.Entry<K, JVariant>> {

		public static class EntryIterator<K> implements Iterator<Map.Entry<K, JVariant>> {

			private final AbstractJQMLMapModel<K> parentModel;
			private final Iterator<K> keyIter;

			public EntryIterator(final AbstractJQMLMapModel<K> parentModel) {
				this.parentModel = parentModel;
				this.keyIter = parentModel.keySet.iterator();
			}

			@Override
			public boolean hasNext() {
				return keyIter.hasNext();
			}

			@Override
			public Entry<K, JVariant> next() {
				final K key = keyIter.next();
				final JVariant value = parentModel.get(key);
				return new AbstractMap.SimpleImmutableEntry<>(key, value);
			}

		}

		private final AbstractJQMLMapModel<K> parentModel;

		public EntrySet(final AbstractJQMLMapModel<K> parentModel) {
			this.parentModel = parentModel;
		}

		@Override
		public Iterator<Map.Entry<K, JVariant>> iterator() {
			return new EntryIterator<>(parentModel);
		}

		@Override
		public int size() {
			return parentModel.size();
		}

	}

	private static class KeySet<K> extends AbstractSet<K> {

		private static class KeyIterator<K> implements Iterator<K> {

			private final AbstractJQMLMapModel<K> parentModel;
			private final Iterator<K> fullKeyIter;
			private K nextKey;

			public KeyIterator(final AbstractJQMLMapModel<K> parentModel) {
				this.parentModel = parentModel;
				fullKeyIter = parentModel.keys.iterator();

				findNextPresentKey();
			}

			private void findNextPresentKey() {
				while (fullKeyIter.hasNext()) {
					nextKey = fullKeyIter.next();
					if (parentModel.containsKey(nextKey)) {
						return;
					}
				}
				nextKey = null;
			}

			@Override
			public boolean hasNext() {
				return nextKey != null;
			}

			@Override
			public K next() {
				if (nextKey != null) {
					final K retValue = nextKey;
					findNextPresentKey();
					return retValue;
				} else {
					throw new NoSuchElementException();
				}
			}
		}

		private final AbstractJQMLMapModel<K> parentModel;

		public KeySet(final AbstractJQMLMapModel<K> parentModel) {
			this.parentModel = parentModel;
		}

		@Override
		public Iterator<K> iterator() {
			return new KeyIterator<>(parentModel);
		}

		@Override
		public int size() {
			return parentModel.size();
		}

	}

	private static class ValueCollection<K> extends AbstractCollection<JVariant> {

		private static class ValueIterator<K> implements Iterator<JVariant> {

			private final AbstractJQMLMapModel<K> parentModel;
			private final Iterator<K> keyIter;

			public ValueIterator(final AbstractJQMLMapModel<K> parentModel) {
				this.parentModel = parentModel;
				this.keyIter = parentModel.keySet.iterator();
			}

			@Override
			public boolean hasNext() {
				return keyIter.hasNext();
			}

			@Override
			public JVariant next() {
				final K key = keyIter.next();
				return parentModel.get(key);
			}

		}

		private final AbstractJQMLMapModel<K> parentModel;

		public ValueCollection(final AbstractJQMLMapModel<K> parentModel) {
			this.parentModel = parentModel;
		}

		@Override
		public Iterator<JVariant> iterator() {
			return new ValueIterator<>(parentModel);
		}

		@Override
		public int size() {
			return parentModel.size();
		}

	}

	/**
	 * Enumeration of the various modes for put() operations.
	 */
	public enum PutMode {
		/**
		 * Put functions return the previous value for the key.
		 */
		RETURN_PREVIOUS_VALUE,
		/**
		 * Put functions always return null. Increases throughput.
		 */
		RETURN_NULL
	}

	protected final String modelName;
	private final PutMode putMode;
	protected final Set<K> keys;
	protected final Map<String, Integer> indexLookup = new HashMap<>();

	private final KeySet<K> keySet = new KeySet<>(this);
	private final ValueCollection<K> valueCollection = new ValueCollection<>(this);
	private final EntrySet<K> entrySet = new EntrySet<>(this);

	private final MapAccessor accessor;

	protected AbstractJQMLMapModel(final String modelName, final Set<K> keys,
			final AtomicReference<Thread> eventLoopThread, final MapAccessor accessor, final PutMode putMode) {
		super(eventLoopThread);
		this.modelName = Objects.requireNonNull(modelName, "modelName is null");
		this.putMode = Objects.requireNonNull(putMode, "putMode is null");
		this.keys = Objects.requireNonNull(keys, "keys is null");
		this.accessor = accessor;
	}

	/**
	 * Assigns the passed in map to this map. Equivalent to clear() followed by
	 * putAll().
	 *
	 * @param map Map to assign to this one.
	 */
	public void assign(final Map<K, JVariant> map) {
		Objects.requireNonNull(map, "map is null");
		verifyEventLoopThread();
		final int size = map.size();
		final int[] roles = new int[size];
		final JVariant[] data = new JVariant[size];
		convert(map, roles, data);
		accessor.assign(roles, data);
	}

	@Override
	public void clear() {
		verifyEventLoopThread();
		accessor.clear();
	}

	@Override
	public boolean containsKey(final Object key) {
		if (key == null) {
			return false;
		} else {
			final Integer index = indexLookup.get(key.toString());
			if (index == null) {
				return false;
			} else {
				return accessor.get(index.intValue()).isPresent();
			}
		}
	}

	@Override
	public boolean containsValue(final Object value) {
		verifyEventLoopThread();
		for (int i = 0; i < keys.size(); ++i) {
			final Optional<JVariant> opt = accessor.get(i);
			if (opt.isPresent() && opt.get().equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<Entry<K, JVariant>> entrySet() {
		verifyEventLoopThread();
		return entrySet;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof Map)) {
			return false;
		} else {
			@SuppressWarnings("unchecked")
			final Map<K, JVariant> otherMap = (Map<K, JVariant>) o;

			if (size() != otherMap.size()) {
				return false;
			}

			for (final K k : keySet) {
				final JVariant v1 = get(k);
				final JVariant v2 = otherMap.get(k);
				if (!v1.equals(v2)) {
					return false;
				}
			}
			return true;
		}
	}

	@Override
	public JVariant get(final Object key) {
		verifyEventLoopThread();
		final int index = verifyKey(key);

		final Optional<JVariant> opt = accessor.get(index);
		return opt.orElse(null);
	}

	@Override
	public int hashCode() {
		final HashCodeBuilder b = new HashCodeBuilder();
		for (final K k : keySet) {
			final JVariant v1 = get(k);
			b.append(k);
			b.append(v1);
		}
		return b.toHashCode();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Set<K> keySet() {
		verifyEventLoopThread();
		return keySet;
	}

	@Override
	public JVariant put(final K key, final JVariant value) {
		verifyEventLoopThread();

		if (value != null) {
			final int index = verifyKey(key);

			final JVariant existingValue;
			if (putMode == PutMode.RETURN_PREVIOUS_VALUE) {
				existingValue = accessor.get(index).orElse(null);
			} else {
				existingValue = null;
			}

			accessor.set(value, index);

			return existingValue;
		} else {
			return remove(key);
		}
	}

	private void convert(final Map<? extends K, ? extends JVariant> map, final int[] roles, final JVariant[] data) {
		int i = 0;
		for (final Entry<? extends K, ? extends JVariant> e : map.entrySet()) {
			roles[i] = verifyKey(e.getKey());
			data[i] = e.getValue();
			++i;
		}
	}

	@Override
	public void putAll(final Map<? extends K, ? extends JVariant> map) {
		Objects.requireNonNull(map, "map is null");
		verifyEventLoopThread();

		final int size = map.size();
		final int[] roles = new int[size];
		final JVariant[] data = new JVariant[size];
		convert(map, roles, data);
		accessor.set(roles, data);
	}

	@Override
	public JVariant remove(final Object key) {
		verifyEventLoopThread();
		final int index = verifyKey(key);
		return accessor.remove(index).orElse(null);
	}

	@Override
	public int size() {
		verifyEventLoopThread();

		int count = 0;
		for (final K key : keys) {
			final JVariant v = get(key);
			if (v != null) {
				++count;
			}
		}
		return count;
	}

	@Override
	public String toString() {
		// ex. {R1=JVariant [type=STRING, obj=A], R3=JVariant [type=INT, obj=3]}
		return entrySet().stream().map(e -> e.getKey().toString() + "=" + e.getValue().toString())
				.collect(Collectors.joining(", "));
	}

	@Override
	public Collection<JVariant> values() {
		verifyEventLoopThread();
		return valueCollection;
	}

	private int verifyKey(final Object k) {
		if (k == null) {
			throw new IllegalKeyException(k + " is not a valid key");
		} else {
			final Integer index = indexLookup.get(k.toString());
			if (index == null) {
				throw new IllegalKeyException(k + " is not a valid key");
			} else {
				return index.intValue();
			}
		}
	}

}
