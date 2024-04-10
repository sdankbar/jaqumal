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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import com.github.sdankbar.qml.JQMLModelFactory;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.JQMLMapPool;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

public class LazyListModel<K, Q extends Enum<Q>> {

	private static <K> K getKey(final Set<K> keys, final String keyName) {
		for (final K v : keys) {
			if (v.toString().equals(keyName)) {
				return v;
			}
		}
		throw new IllegalArgumentException("Failed to find key: " + keyName);
	}

	private static boolean isBetween(final int v, final int l, final int h) {
		return l <= v && v <= h;
	}

	private static final String SIZE_KEY = "totalSize";

	private final Map<K, LazyListModelData<Q>> unsortedValues = new HashMap<>();
	private final List<LazyListModelData<Q>> sortedValues = new ArrayList<>();
	private final JQMLMapPool<Q> qmlModel;

	private final Q positionKey;
	private Q sortingKey = null;
	private Predicate<Map<Q, JVariant>> exclusionFunction = null;
	private final int defaultItemHeight;
	private final int windowSizePixels;
	private final int scrollPosition = 0;

	public LazyListModel(final JQMLModelFactory factory, final String modelName, final Class<Q> enumKeyClass,
			final int defaultItemHeight, final int windowSizePixels) {
		Objects.requireNonNull(factory, "factory is null");
		Objects.requireNonNull(enumKeyClass, "enumKeyClass is null");
		positionKey = getKey(EnumSet.allOf(enumKeyClass), "pos");
		this.qmlModel = new JQMLMapPool(factory.createListModel(modelName, enumKeyClass, PutMode.RETURN_NULL),
				ImmutableMap.of(positionKey, new JVariant(-1)));
		Preconditions.checkArgument(defaultItemHeight > 0, "defaultItemHeight is <= 0");
		this.defaultItemHeight = defaultItemHeight;
		qmlModel.putRootValue(SIZE_KEY, JVariant.NULL_INT);

		Preconditions.checkArgument(windowSizePixels > 0, "windowSizePixels is <= 0");
		this.windowSizePixels = windowSizePixels;
	}

	public void setExclusionFunction(final Predicate<Map<Q, JVariant>> exclusionFunction) {
		this.exclusionFunction = exclusionFunction;

		boolean needsLayout = false;
		for (final Map.Entry<K, LazyListModelData<Q>> entry : unsortedValues.entrySet()) {
			if (entry.getValue().applyFiltering(exclusionFunction)) {
				needsLayout = true;
			}
		}

		if (needsLayout) {
			layout(EnumSet.of(Task.LAYOUT));
			flush();
		}
	}

	public void setSortingKey(final Q sortingKey) {
		this.sortingKey = sortingKey;

		boolean needsSort = false;
		for (final Map.Entry<K, LazyListModelData<Q>> entry : unsortedValues.entrySet()) {
			if (entry.getValue().updateSortingValue(sortingKey)) {
				needsSort = true;
			}
		}

		if (needsSort) {
			sort(EnumSet.of(Task.SORT));
			layout(EnumSet.of(Task.LAYOUT));
			flush();
		}
	}

	private LazyListModelData<Q> getData(final K key, final EnumSet<Task> tasks) {
		LazyListModelData<Q> d = unsortedValues.get(key);
		if (d == null) {
			tasks.add(Task.LAYOUT);
			tasks.add(Task.SORT);
			d = new LazyListModelData<>(sortingKey, defaultItemHeight);
			unsortedValues.put(key, d);
			sortedValues.add(d);
		}
		return d;
	}

	public ImmutableMap<Q, JVariant> get(final K key) {
		final LazyListModelData<Q> d = unsortedValues.get(key);
		if (d != null) {
			return ImmutableMap.copyOf(d.getData());
		} else {
			return ImmutableMap.of();
		}
	}

	private void sort(final EnumSet<Task> tasks) {
		if (tasks.contains(Task.SORT)) {
			sortedValues.sort(null);
		}
	}

	private void layout(final EnumSet<Task> tasks) {
		if (tasks.contains(Task.LAYOUT)) {
			final int oldSize = qmlModel.getRootValue(SIZE_KEY).orElse(JVariant.NULL_INT).asInteger();
			int totalSize = 0;
			for (final Map.Entry<K, LazyListModelData<Q>> entry : unsortedValues.entrySet()) {
				totalSize += entry.getValue().getItemHeight();
			}

			if (oldSize != totalSize) {
				qmlModel.putRootValue(SIZE_KEY, new JVariant(totalSize));
			}

			// Perform hide and visible in 2 passes to minimize memory usage
			int currentPosition = 0;
			for (final LazyListModelData<Q> entry : sortedValues) {
				final int itemEnd = currentPosition + entry.getItemHeight();
				final boolean isVisible = isItemVisible(currentPosition, itemEnd);

				if (!isVisible) {
					entry.hide(qmlModel);
				}

				currentPosition += entry.getItemHeight();
			}

			currentPosition = 0;
			for (final LazyListModelData<Q> entry : sortedValues) {
				final int itemEnd = currentPosition + entry.getItemHeight();
				final boolean isVisible = isItemVisible(currentPosition, itemEnd);

				if (isVisible) {
					entry.show(qmlModel, currentPosition, positionKey);
				}

				currentPosition += entry.getItemHeight();
			}
		}
	}

	private void flush() {
		for (final Map.Entry<K, LazyListModelData<Q>> entry : unsortedValues.entrySet()) {
			entry.getValue().flush();
		}
	}

	private boolean isItemVisible(final int currentPosition, final int itemEnd) {
		final int windowEnd = scrollPosition + windowSizePixels;
		return isBetween(currentPosition, scrollPosition, windowEnd) || isBetween(itemEnd, scrollPosition, windowEnd)
				|| isBetween(scrollPosition, currentPosition, itemEnd);
	}

	public void upsert(final K key, final ImmutableMap<Q, JVariant> values) {
		final EnumSet<Task> tasks = EnumSet.noneOf(Task.class);
		final LazyListModelData<Q> d = getData(key, tasks);
		tasks.addAll(d.upsert(values));

		sort(tasks);

		// Apply any filtering
		if (d.applyFiltering(exclusionFunction)) {
			tasks.add(Task.LAYOUT);
		}

		layout(tasks);
		flush();
	}

	public void set(final K key, final ImmutableMap<Q, JVariant> values) {
		final EnumSet<Task> tasks = EnumSet.noneOf(Task.class);
		final LazyListModelData<Q> d = getData(key, tasks);
		tasks.addAll(d.set(values));

		sort(tasks);

		// Apply any filtering
		if (d.applyFiltering(exclusionFunction)) {
			tasks.add(Task.LAYOUT);
		}

		layout(tasks);
		flush();
	}

	public void remove(final K key) {
		final LazyListModelData<Q> old = unsortedValues.remove(key);
		if (old != null) {
			// Not necessary to resort or filter when removing an entry
			old.hide(qmlModel);
			layout(EnumSet.of(Task.LAYOUT));
			flush();
		}
	}

}
