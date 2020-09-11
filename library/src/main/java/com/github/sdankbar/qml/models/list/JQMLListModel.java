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
package com.github.sdankbar.qml.models.list;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.sdankbar.qml.JVariant;
import com.google.common.collect.ImmutableMap;

/**
 * A model that is available to QML. Represents a list of Maps from the key type
 * K, to a JVariant.
 *
 * @param <K> The type of the K in the Map.
 */
public interface JQMLListModel<K> extends List<Map<K, JVariant>> {

	/**
	 * Adds the map's data to the end of this list model.
	 *
	 * @param map The data to append to the end of the model.
	 * @return A Map reference that can be used to modify the data stored at the new
	 *         Map in the list model.
	 */
	Map<K, JVariant> add(final ImmutableMap<K, JVariant> map);

	/**
	 * Inserts the map's data at index into this list model.
	 *
	 * @param index The index in the list model to insert the data at. If index is >
	 *              than the length of the list, empty data is automatically
	 *              appended to the model.
	 * @param map   The data to insert into the model.
	 *
	 * @return A Map reference that can be used to modify the data stored at the new
	 *         Map in the list model.
	 */
	Map<K, JVariant> add(final int index, final ImmutableMap<K, JVariant> map);

	/**
	 * Inserts the data at index into this list model.
	 *
	 * @param index The index in the list model to insert the data at. If index is >
	 *              than the length of the list, empty data is automatically
	 *              appended to the model.
	 * @param data  The data to insert into the model.
	 * @param role  The role to associate with the data.
	 *
	 * @return A Map reference that can be used to modify the data stored at the new
	 *         Map in the list model.
	 */
	Map<K, JVariant> add(final int index, final JVariant data, final K role);

	@Deprecated
	@Override
	void add(final int index, final Map<K, JVariant> element);

	/**
	 * Appends the data to the end of this list model.
	 *
	 * @param data The data to append to this model.
	 * @param role The role to associate with the data.
	 *
	 * @return A Map reference that can be used to modify the data stored at the new
	 *         Map in the list model.
	 */
	Map<K, JVariant> add(final JVariant data, final K role);

	@Override
	@Deprecated
	boolean add(final Map<K, JVariant> e);

	/**
	 * Clears all keys from the Map stored at index.
	 *
	 * @param index The index into this list model to clear the map's keys.
	 */
	void clear(final int index);

	/**
	 * Returns the data stored at the index in the list and with the key of role.
	 *
	 * @param index Index in the list.
	 * @param role  Key in the map.
	 * @return Data that is stored or Optional.empty() if data is not stored.
	 */
	Optional<JVariant> getData(final int index, final K role);

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
	 * Checks if the Map at index contains a value for role.
	 *
	 * @param index Index into the list.
	 * @param role  Key into the Map in the list at index.
	 * @return True if the Key, role, has a value associated with it.
	 */
	boolean isPresent(final int index, final K role);

	/**
	 * Puts a value in the root value map.
	 *
	 * @param key  The value's key
	 * @param data The new value.
	 */
	void putRootValue(final String key, final JVariant data);

	/**
	 * @param l ListListener to receive callbacks on this list changing.
	 */
	void registerListener(final ListListener<K> l);

	/**
	 * Removes data from the Map stored at index for key role.
	 *
	 * @param index Index in the list for the map to modify.
	 * @param role  Key remove from the Map.
	 */
	void remove(final int index, final K role);

	/**
	 * Removes a value from the root value map.
	 *
	 * @param key Key to remove.
	 */
	void removeRootValue(final String key);

	@Override
	@Deprecated
	Map<K, JVariant> set(final int index, final Map<K, JVariant> element);

	/**
	 * Sets the data at the index and role.
	 *
	 * @param index The index in the list to place the data.
	 * @param data  The data to store.
	 * @param role  The Role to store the data under.
	 */
	void setData(final int index, final K role, final JVariant data);

	/**
	 * Copies the data from the Map to the index.
	 *
	 * @param index The index in the list to place the data.
	 * @param data  The data to store.
	 */
	void setData(final int index, final Map<K, JVariant> data);

	/**
	 * @param l ListListener to unregister.
	 */
	void unregisterListener(final ListListener<K> l);

	/**
	 * Assigns the list to this list. Equivalent to clear and addAll.
	 *
	 * @param list List to assign to this map.
	 */
	void assign(List<Map<K, JVariant>> list);

	/**
	 * Assigns the map to the map at index.
	 *
	 * @param index Index of the Map to assign to.
	 * @param map   Map to assign to the target Map.
	 */
	void assign(int index, Map<K, JVariant> map);
}
