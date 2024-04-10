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
import java.util.function.Predicate;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.google.common.collect.ImmutableMap;

public class LazyListModel<K, Q> {

	private final Map<K, LazyListModelData<Q>> unsortedValues = new HashMap<>();
	private final List<LazyListModelData<Q>> sortedValues = new ArrayList<>();
	private final JQMLListModel<Q> qmlModel;

	private Q sortingKey = null;
	private Predicate<Map<Q, JVariant>> exclusionFunction = null;

	public LazyListModel(final JQMLListModel<Q> qmlModel) {
		this.qmlModel = Objects.requireNonNull(qmlModel, "qmlModel is null");
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
		}

		// TODO update qmlModel
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
		}
	}

	private LazyListModelData<Q> getData(final K key, final EnumSet<Task> tasks) {
		LazyListModelData<Q> d = unsortedValues.get(key);
		if (d == null) {
			tasks.add(Task.LAYOUT);
			tasks.add(Task.SORT);
			d = new LazyListModelData<>(sortingKey);
			unsortedValues.put(key, d);
			sortedValues.add(d);
		}
		return d;
	}

	private void sort(final EnumSet<Task> tasks) {
		if (tasks.contains(Task.SORT)) {
			sortedValues.sort(null);
		}
	}

	private void layout(final EnumSet<Task> tasks) {
		if (tasks.contains(Task.SORT)) {
			// TODO
		}
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
		// TODO update qmlModel
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
		// TODO update qmlModel
	}

	public void remove(final K key) {
		final LazyListModelData<Q> old = unsortedValues.remove(key);
		if (old != null) {
			// Not necessary to resort or refilter when removing an entry

			layout(EnumSet.of(Task.LAYOUT));
			// TODO update qmlModel
		}
	}

}
