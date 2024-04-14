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
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;

import com.github.sdankbar.qml.JInvokable;
import com.github.sdankbar.qml.JQMLModelFactory;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.invocation.InvokableDispatcher;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.JQMLMapPool;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

public class LazyListModel<K, Q extends Enum<Q>> {

	public enum SortDirection {
		ASCENDING, DESCENDING;
	}

	private static <K> K getKey(final ImmutableMap<String, K> keys, final String keyName,
			final boolean throwException) {
		final K k = keys.get(keyName);
		if (k == null && throwException) {
			throw new IllegalArgumentException("Failed to find key: " + keyName);
		}
		return k;
	}

	private static boolean isBetween(final int v, final int l, final int h) {
		return l <= v && v < h;
	}

	private static final String SIZE_KEY = "totalSize";

	private final Map<K, LazyListModelData<Q>> unsortedValues = new HashMap<>();
	private final List<LazyListModelData<Q>> sortedValues = new ArrayList<>();
	private final JQMLMapPool<Q> qmlModel;
	private final ImmutableMap<String, Q> allKeys;

	private final Q positionKey;
	private Q sortingKey = null;
	private SortDirection sortDirection = SortDirection.ASCENDING;
	private Predicate<Map<Q, JVariant>> exclusionFunction = null;
	private final int defaultItemHeight;
	private int windowSizePixels;
	private int scrollPosition = 0;
	private int pixelBuffer = 0;

	public LazyListModel(final JQMLModelFactory factory, final InvokableDispatcher dispatch, final String modelName,
			final Class<Q> enumKeyClass, final int defaultItemHeight, final int windowSizePixels,
			final ImmutableMap<Q, JVariant> defaultValues) {
		Objects.requireNonNull(factory, "factory is null");
		Objects.requireNonNull(enumKeyClass, "enumKeyClass is null");
		allKeys = EnumSet.allOf(enumKeyClass).stream().collect(ImmutableMap.toImmutableMap(Enum::name, k -> k));
		positionKey = getKey(allKeys, "pos", true);
		this.qmlModel = new JQMLMapPool<>(factory.createListModel(modelName, enumKeyClass, PutMode.RETURN_NULL),
				defaultValues);
		Preconditions.checkArgument(defaultItemHeight > 0, "defaultItemHeight is <= 0");
		this.defaultItemHeight = defaultItemHeight;
		qmlModel.putRootValue(SIZE_KEY, JVariant.NULL_INT);

		Preconditions.checkArgument(windowSizePixels > 0, "windowSizePixels is <= 0");
		this.windowSizePixels = windowSizePixels;

		dispatch.registerInvokable(modelName + "_invoke", this);
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

	public void setSortingKey(final Q sortingKey, final SortDirection direction) {
		this.sortingKey = sortingKey;
		this.sortDirection = Objects.requireNonNull(direction, "direction is null");

		boolean needsSort = false;
		for (final Map.Entry<K, LazyListModelData<Q>> entry : unsortedValues.entrySet()) {
			if (entry.getValue().updateSortingValue(sortingKey, sortDirection)) {
				needsSort = true;
			}
		}

		if (needsSort) {
			sort(EnumSet.of(Task.SORT));
			layout(EnumSet.of(Task.LAYOUT));
			flush();
		}
	}

	@JInvokable
	public void setSortingKey(final String sortingKey, final boolean ascending) {
		final Q key = getKey(allKeys, sortingKey, false);
		if (ascending) {
			setSortingKey(key, SortDirection.ASCENDING);
		} else {
			setSortingKey(key, SortDirection.DESCENDING);
		}
	}

	private LazyListModelData<Q> getData(final K key, final EnumSet<Task> tasks) {
		LazyListModelData<Q> d = unsortedValues.get(key);
		if (d == null) {
			tasks.add(Task.LAYOUT);
			tasks.add(Task.SORT);
			d = new LazyListModelData<>(sortingKey, sortDirection, defaultItemHeight);
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

	private int getTotalSize() {
		int totalSize = 0;
		for (final LazyListModelData<Q> entry : sortedValues) {
			if (!entry.isExcluded()) {
				totalSize += entry.getItemSize();
			}
		}
		return totalSize;
	}

	private void updateTotalSize() {
		final int oldSize = qmlModel.getRootValue(SIZE_KEY).orElse(JVariant.NULL_INT).asInteger();
		final int totalSize = getTotalSize();

		if (oldSize != totalSize) {
			qmlModel.putRootValue(SIZE_KEY, new JVariant(totalSize));
		}
	}

	private void layout(final EnumSet<Task> tasks) {
		if (tasks.contains(Task.LAYOUT)) {
			updateTotalSize();

			// Perform hide and visible in 2 passes to minimize memory usage
			int currentPosition = 0;
			for (final LazyListModelData<Q> entry : sortedValues) {
				if (!entry.isExcluded()) {
					final int itemEnd = currentPosition + entry.getItemSize();
					final boolean isVisible = isItemVisible(currentPosition, itemEnd);

					if (!isVisible) {
						entry.hide(qmlModel);
					}

					currentPosition += entry.getItemSize();
				}
			}

			currentPosition = 0;
			for (final LazyListModelData<Q> entry : sortedValues) {
				if (!entry.isExcluded()) {
					final int itemEnd = currentPosition + entry.getItemSize();
					final boolean isVisible = isItemVisible(currentPosition, itemEnd);

					if (isVisible) {
						entry.show(qmlModel, currentPosition, positionKey);
					}

					currentPosition += entry.getItemSize();
				}
			}
		}
	}

	private void flush() {
		for (final LazyListModelData<Q> entry : sortedValues) {
			entry.flush();
		}
	}

	private boolean isItemVisible(final int currentPosition, final int itemEnd) {
		final int windowEnd = scrollPosition + windowSizePixels;
		return isBetween(currentPosition, scrollPosition - pixelBuffer, windowEnd + pixelBuffer)
				|| isBetween(itemEnd, scrollPosition - pixelBuffer, windowEnd + pixelBuffer)
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

	public void upsertAll(final Map<K, ImmutableMap<Q, JVariant>> values) {
		final EnumSet<Task> tasks = EnumSet.noneOf(Task.class);
		final List<LazyListModelData<Q>> modifiedData = new ArrayList<>(values.size());
		for (final Entry<K, ImmutableMap<Q, JVariant>> entry : values.entrySet()) {
			final LazyListModelData<Q> d = getData(entry.getKey(), tasks);
			tasks.addAll(d.upsert(entry.getValue()));
			modifiedData.add(d);
		}

		sort(tasks);

		// Apply any filtering
		boolean filterApplied = false;
		for (final LazyListModelData<Q> d : modifiedData) {
			if (d.applyFiltering(exclusionFunction)) {
				filterApplied = true;
			}
		}

		if (filterApplied) {
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
			sortedValues.remove(old);
			old.hide(qmlModel);
			layout(EnumSet.of(Task.LAYOUT));
			flush();
		}
	}

	public void clear() {
		for (final LazyListModelData<Q> d : sortedValues) {
			d.hide(qmlModel);
		}
		unsortedValues.clear();
		sortedValues.clear();
		updateTotalSize();
	}

	public void setPixelBuffer(final int bufferPixels) {
		if (this.pixelBuffer != bufferPixels) {
			this.pixelBuffer = bufferPixels;
			layout(EnumSet.of(Task.LAYOUT));
			flush();
		}
	}

	@JInvokable
	public void setScrollPosition(final int scrollPosition) {
		if (this.scrollPosition != scrollPosition) {
			this.scrollPosition = scrollPosition;
			layout(EnumSet.of(Task.LAYOUT));
			flush();
		}
	}

	@JInvokable
	public void setWindowSize(final int pixels) {
		if (this.windowSizePixels != pixels) {
			this.windowSizePixels = pixels;
			layout(EnumSet.of(Task.LAYOUT));
			flush();
		}
	}

}
