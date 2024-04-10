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
package com.github.sdankbar.qml.models.lazy;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import com.github.sdankbar.qml.JVariant;
import com.google.common.collect.ImmutableMap;

class LazyListModelData<Q> implements Comparable<LazyListModelData<Q>> {
	private static long NEXT_INDEX = 0;

	private final Map<Q, JVariant> data = new HashMap<>();
	private final long index = ++NEXT_INDEX;
	private JVariant sortValue;
	private boolean isExcluded = false;

	private Q sortingKey;
	private final Predicate<Map<Q, JVariant>> filterFunction = null;

	public LazyListModelData(final Q sortingKey) {
		this.sortingKey = sortingKey;
		if (sortingKey == null) {
			sortValue = new JVariant(index);
		} else {
			sortValue = data.get(sortingKey);
		}
	}

	public boolean applyFiltering(final Predicate<Map<Q, JVariant>> excludeFunction) {
		final boolean oldExclusion = isExcluded;
		if (filterFunction == null) {
			isExcluded = false;
		} else {
			isExcluded = excludeFunction.test(Collections.unmodifiableMap(data));
		}
		return oldExclusion != isExcluded;
	}

	/**
	 * @param sortingKey
	 * @return True if the sorting value changed.
	 */
	public boolean updateSortingValue(final Q sortingKey) {
		this.sortingKey = sortingKey;
		final JVariant oldSort = sortValue;
		if (sortingKey == null) {
			sortValue = new JVariant(index);
		} else {
			sortValue = data.get(sortingKey);
		}
		return !Objects.equals(oldSort, sortValue);
	}

	public EnumSet<Task> upsert(final ImmutableMap<Q, JVariant> map) {
		data.putAll(map);
		if (updateSortingValue(sortingKey)) {
			return EnumSet.of(Task.SORT);
		} else {
			return EnumSet.noneOf(Task.class);
		}
	}

	public EnumSet<Task> set(final ImmutableMap<Q, JVariant> map) {
		data.clear();
		data.putAll(map);
		if (updateSortingValue(sortingKey)) {
			return EnumSet.of(Task.SORT);
		} else {
			return EnumSet.noneOf(Task.class);
		}
	}

	@Override
	public int compareTo(final LazyListModelData<Q> arg) {
		return sortValue.compareTo(arg.sortValue);
	}
}
