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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

/**
 * Handles allocating and freeing items in a JQMLListModel. Useful when the
 * order of the items in the list is not important and items are added and
 * removed often.
 *
 * @param <K> Key type for the JQMLListModel.
 */
public class JQMLMapPool<K> {

	private static final Logger logger = LoggerFactory.getLogger(JQMLMapPool.class);

	private final JQMLListModel<K> model;
	private final ImmutableMap<K, JVariant> initialValue;
	private IntFunction<Integer> growthFunction = oldSize -> Integer.valueOf(oldSize + 16);

	private final List<Boolean> inUse = new ArrayList<>();

	/**
	 * Constructs a new pool.
	 *
	 * @param model        The list model to manage.
	 * @param initialValue A map of the values to put into each item in the list
	 *                     when that item is released.
	 */
	public JQMLMapPool(final JQMLListModel<K> model, final ImmutableMap<K, JVariant> initialValue) {
		this.model = Objects.requireNonNull(model, "model is null");
		this.initialValue = Objects.requireNonNull(initialValue, "initialValue is null");
	}

	private void allocate(final int newSize) {
		final int oldSize = model.size();
		for (int i = oldSize; i < newSize; ++i) {
			model.add(initialValue);
			inUse.add(Boolean.FALSE);
		}
	}

	/**
	 * @param map The item to release back to the pool so it can be reused.
	 */
	public void release(final Map<K, JVariant> map) {
		Objects.requireNonNull(map, "map is null");
		for (int i = 0; i < model.size(); ++i) {
			// Identity equality check
			if (model.get(i) == map) {
				inUse.set(i, Boolean.FALSE);
				map.putAll(initialValue);
				return;
			}
		}

		logger.warn("Map {} not found in model to release", map);
	}

	/**
	 * Releases all in use items.
	 */
	public void releaseAll() {
		for (int i = 0; i < model.size(); ++i) {
			if (inUse.get(i).booleanValue()) {
				inUse.set(i, Boolean.FALSE);
				model.get(i).putAll(initialValue);
			}
		}
	}

	/**
	 * Returns a new item from the list model, allocating new items as necessary.
	 *
	 * @return A new item from the list model
	 */
	public Map<K, JVariant> request() {
		return request(0);
	}

	private Map<K, JVariant> request(final int hintIndex) {
		final int currentSize = inUse.size();
		for (int i = hintIndex; i < currentSize; ++i) {
			if (!inUse.get(i).booleanValue()) {
				inUse.set(i, Boolean.TRUE);
				return model.get(i);
			}
		}

		final int newSize = growthFunction.apply(currentSize).intValue();
		Preconditions.checkArgument((currentSize + 1) <= newSize, "growthFunction returned a smaller length ", newSize);
		allocate(newSize);
		return request(currentSize);
	}

	/**
	 * Asks the pool to reserve items in the list model such that allocates won't be
	 * necessary up to the newSize. Does not guarantee that allocates won't be
	 * necessary if all items are already in use. Just guarantees that the list
	 * model will be at least of size newSize.
	 *
	 * @param newSize The new target size for the list model.
	 */
	public void reserve(final int newSize) {
		Preconditions.checkArgument(newSize > 0, "newSize is less than or equal to 0 ", newSize);
		allocate(newSize);
	}

	/**
	 * Sets the strategy used to resize the list model when additional items are
	 * required. Function must return a new list size that is at least 1 larger than
	 * the current size passed to it.
	 *
	 * @param growthFunction Takes the current list size and returns the new size of
	 *                       the list. Must return an integer at least 1 larger than
	 *                       the current list size.
	 */
	public void setAllocationStrategy(final IntFunction<Integer> growthFunction) {
		this.growthFunction = Objects.requireNonNull(growthFunction, "growthFunction is null");
	}
}
