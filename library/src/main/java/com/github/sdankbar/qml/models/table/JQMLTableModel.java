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
import java.util.Map;
import java.util.Optional;

import com.github.sdankbar.qml.JVariant;

public interface JQMLTableModel<K> {

	void addColumn();

	void addColumn(int column);

	void removeColumn();

	void removeColumn(int column);

	void addRow();

	void addRow(final int row);

	void removeRow();

	void removeRow(int row);

	Map<K, JVariant> get(final int row, int column);

	/**
	 * @return The name of this model.
	 */
	String getModelName();

	/**
	 * Returns a value from the root value map.
	 *
	 * @param key Key of the value to return.
	 * @return The key's value or Optional.empty().
	 */
	Optional<JVariant> getRootValue(final String key);

	/**
	 * Puts a value in the root value map.
	 *
	 * @param key  The value's key
	 * @param data The new value.
	 */
	void putRootValue(final String key, final JVariant data);

	/**
	 * Removes a value from the root value map.
	 *
	 * @param key Key to remove.
	 */
	void removeRootValue(final String key);

	void setData(final int row, int column, final Map<K, JVariant> data);

	void serialize(OutputStream stream) throws IOException;

	void deserialize(InputStream stream) throws IOException;

	/**
	 * Register to receive callbacks when any part of the model changes, ex. add,
	 * remove, update, reorder, root data, etc.
	 */
	void registerModelChangedListener(Runnable r);

	/**
	 * Unregister to receive callbacks when any part of the model changes.
	 */
	void unregisterModelChangedListener(Runnable r);

	int getRowCount();

	int getColumnCount();
}
