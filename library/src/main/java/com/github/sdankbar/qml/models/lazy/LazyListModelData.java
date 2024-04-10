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
import java.util.Optional;
import java.util.function.Predicate;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.models.JQMLMapPool;
import com.google.common.collect.ImmutableMap;

class LazyListModelData<Q> implements Comparable<LazyListModelData<Q>> {
	private static long NEXT_INDEX = 0;

	private final Map<Q, JVariant> localData = new HashMap<>();
	private Map<Q, JVariant> qmlData = null;
	private final long index = ++NEXT_INDEX;
	private JVariant sortValue;
	private boolean isExcluded = false;
	private final int itemHeight;
	private boolean needsFlush = false;

	private Q sortingKey;
	private final Predicate<Map<Q, JVariant>> filterFunction = null;

	public LazyListModelData(final Q sortingKey, final int itemHeight) {
		this.sortingKey = sortingKey;
		if (sortingKey == null) {
			sortValue = new JVariant(index);
		} else {
			sortValue = localData.get(sortingKey);
		}
		this.itemHeight = itemHeight;
	}

	public int getItemHeight() {
		return itemHeight;
	}

	public Map<Q, JVariant> getData() {
		return localData;
	}

	public boolean applyFiltering(final Predicate<Map<Q, JVariant>> excludeFunction) {
		final boolean oldExclusion = isExcluded;
		if (filterFunction == null) {
			isExcluded = false;
		} else {
			isExcluded = excludeFunction.test(Collections.unmodifiableMap(localData));
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
			sortValue = localData.get(sortingKey);
		}
		return !Objects.equals(oldSort, sortValue);
	}

	public EnumSet<Task> upsert(final ImmutableMap<Q, JVariant> map) {
		needsFlush = true;
		localData.putAll(map);
		if (updateSortingValue(sortingKey)) {
			return EnumSet.of(Task.SORT);
		} else {
			return EnumSet.noneOf(Task.class);
		}
	}

	public EnumSet<Task> set(final ImmutableMap<Q, JVariant> map) {
		needsFlush = true;
		localData.clear();
		localData.putAll(map);
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

	public void hide(final JQMLMapPool<Q> qmlModel) {
		if (qmlData != null) {
			qmlModel.release(qmlData);
		}
	}

	public void show(final JQMLMapPool<Q> qmlModel, final int position, final Q positionKey) {
		if (qmlData == null) {
			qmlData = qmlModel.request();
		}
		final int oldPosition = Optional.ofNullable(localData.get(positionKey)).orElse(new JVariant(-1)).asInteger();

		if (oldPosition != position) {
			localData.put(positionKey, new JVariant(position));
			needsFlush = true;
		}
	}

	public void flush() {
		if (needsFlush && qmlData != null) {
			qmlData.clear();
			qmlData.putAll(localData);
			needsFlush = false;
		}
	}
}
