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
package com.github.sdankbar.qml.models.table;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.json.JSONObject;

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class JQMLTableModelImpl<K> implements JQMLTableModel<K> {

	private static <K> K getKey(final ImmutableSet<K> keys, final String keyName) {
		for (final K v : keys) {
			if (v.toString().equals(keyName)) {
				return v;
			}
		}
		throw new IllegalArgumentException("Failed to find key: " + keyName);
	}

	private final JQMLListModel<K> listModel;
	private int rowCount = 0;
	private int columnCount = 1;

	private final K rowKey;
	private final K columnKey;

	public JQMLTableModelImpl(final String modelName, final ImmutableSet<K> keys, final JQMLApplication<?> app,
			final PutMode putMode) {
		rowKey = getKey(keys, "row");
		columnKey = getKey(keys, "column");
		listModel = app.getModelFactory().createListModel(modelName, keys, putMode);
	}

	private int index(final int row, final int column) {
		Preconditions.checkArgument(row < rowCount, "Invalid row index: %s", row);
		Preconditions.checkArgument(column < columnCount, "Invalid column index: %s", column);
		return indexNoCheck(row, column);
	}

	private int indexNoCheck(final int row, final int column) {
		return row * columnCount + column;
	}

	@Override
	public void addColumn() {
		addColumn(columnCount);
	}

	@Override
	public void addColumn(final int column) {
		Preconditions.checkArgument(column <= columnCount, "Invalid column index: %s", column);
		for (int i = rowCount - 1; i >= 0; --i) {
			final int index = indexNoCheck(i, column);
			listModel.add(index, ImmutableMap.of());
		}
		++columnCount;
		updateIndices();
	}

	@Override
	public void removeColumn() {
		removeColumn(columnCount - 1);
	}

	@Override
	public void removeColumn(final int column) {
		Preconditions.checkArgument(column < columnCount, "Invalid column index: %s", column);
		for (int i = rowCount - 1; i >= 0; --i) {
			final int index = indexNoCheck(i, column);
			listModel.remove(index);
		}
		--columnCount;
		updateIndices();
	}

	@Override
	public void addRow() {
		addRow(rowCount);
	}

	@Override
	public void addRow(final int row) {
		Preconditions.checkArgument(row <= rowCount, "Invalid row index: %s", row);
		final int index = indexNoCheck(row, 0);
		for (int i = 0; i < columnCount; ++i) {
			listModel.add(index, ImmutableMap.of());
		}
		++rowCount;
		updateIndices();
	}

	@Override
	public void removeRow() {
		removeRow(rowCount - 1);
	}

	@Override
	public void removeRow(final int row) {
		Preconditions.checkArgument(row < rowCount, "Invalid row index: %s", row);
		final int index = indexNoCheck(row, 0);
		for (int i = 0; i < columnCount; ++i) {
			listModel.remove(index);
		}
		--rowCount;
		updateIndices();
	}

	@Override
	public Map<K, JVariant> get(final int row, final int column) {
		return listModel.get(index(row, column));
	}

	/**
	 * @return The name of this model.
	 */
	@Override
	public String getModelName() {
		return listModel.getModelName();
	}

	/**
	 * Returns a value from the root value map.
	 *
	 * @param key Key of the value to return.
	 * @return The key's value or Optional.empty().
	 */
	@Override
	public Optional<JVariant> getRootValue(final String key) {
		return listModel.getRootValue(key);
	}

	/**
	 * Puts a value in the root value map.
	 *
	 * @param key  The value's key
	 * @param data The new value.
	 */
	@Override
	public void putRootValue(final String key, final JVariant data) {
		listModel.putRootValue(key, data);
	}

	/**
	 * Removes a value from the root value map.
	 *
	 * @param key Key to remove.
	 */
	@Override
	public void removeRootValue(final String key) {
		listModel.removeRootValue(key);
	}

	@Override
	public void setData(final int row, final int column, final Map<K, JVariant> data) {
		listModel.setData(index(row, column), data);
		final Map<K, JVariant> map = get(row, column);
		map.putAll(ImmutableMap.of(rowKey, new JVariant(row), columnKey, new JVariant(column)));
	}

	@Override
	public void serialize(final OutputStream stream, final ImmutableSet<String> rootKeysToPersist) throws IOException {
		final JSONObject additional = new JSONObject();
		additional.put("rows", rowCount);
		additional.put("columns", columnCount);
		listModel.serialize(stream, additional, rootKeysToPersist);
	}

	@Override
	public void deserialize(final InputStream stream, final ImmutableSet<String> rootKeysToPersist) throws IOException {
		final JSONObject obj = listModel.deserialize(stream, rootKeysToPersist);
		Objects.requireNonNull(obj, "Invalid data");
		rowCount = obj.getInt("rows");
		columnCount = obj.getInt("columns");

		updateIndices();
	}

	/**
	 * Register to receive callbacks when any part of the model changes, ex. add,
	 * remove, update, reorder, root data, etc.
	 */
	@Override
	public void registerModelChangedListener(final Runnable r) {
		listModel.registerModelChangedListener(r);
	}

	/**
	 * Unregister to receive callbacks when any part of the model changes.
	 */
	@Override
	public void unregisterModelChangedListener(final Runnable r) {
		listModel.unregisterModelChangedListener(r);
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public int getColumnCount() {
		return columnCount;
	}

	private void updateIndices() {
		for (int row = 0; row < rowCount; ++row) {
			for (int column = 0; column < columnCount; ++column) {
				final Map<K, JVariant> map = get(row, column);
				map.putAll(ImmutableMap.of(rowKey, new JVariant(row), columnKey, new JVariant(column)));
			}
		}

		putRootValue("row_count", new JVariant(rowCount));
		putRootValue("column_count", new JVariant(columnCount));
	}

	@Override
	public void assign(final int rows, final int columns, final List<Map<K, JVariant>> list) {
		Objects.requireNonNull(list, "list is null");
		Preconditions.checkArgument(rows * columns == list.size(), "list length does not equal rows * columns");

		listModel.assign(list);
		rowCount = rows;
		columnCount = columns;
		updateIndices();
	}
}
