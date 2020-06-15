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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.github.sdankbar.qml.JQMLExceptionHandling;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.ApiInstance;
import com.github.sdankbar.qml.cpp.jna.list.ListQMLAPIFast;
import com.github.sdankbar.qml.cpp.memory.SharedJavaCppMemory;
import com.github.sdankbar.qml.models.AbstractJQMLModel;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * A model that is available to QML. Represents a list of Maps from the key type
 * K, to a JVariant.
 *
 * @param <K> The type of the K in the Map.
 */
public class JQMLListModel<K> extends AbstractJQMLModel implements List<Map<K, JVariant>> {

	private final String modelName;
	private final Pointer modelPointer;

	private final Set<K> keySet;
	private final AtomicReference<Thread> eventLoopThread;
	private final ListAccessor accessor;

	private final Map<String, Integer> indexLookup = new HashMap<>();

	private final List<Map<K, JVariant>> mapRefs = new ArrayList<>();

	private final List<ListListener<K>> listeners = new ArrayList<>();

	/**
	 * Constructor.
	 *
	 * @param modelName       The name of the model. This is the name that the model
	 *                        is made available to QML as.
	 * @param keys            Set of keys this model can use.
	 * @param eventLoopThread Reference to the Qt Thread.
	 * @param accessor        Accessor this model will use to to access the C++
	 *                        portion of this model.
	 */
	public JQMLListModel(final String modelName, final Set<K> keys, final AtomicReference<Thread> eventLoopThread,
			final ListAccessor accessor) {
		super(eventLoopThread);
		this.eventLoopThread = eventLoopThread;
		this.modelName = Objects.requireNonNull(modelName, "modelName is null");
		Objects.requireNonNull(keys, "keys is null");

		this.accessor = accessor;

		final String[] roleArray = new String[keys.size()];
		final int[] indicesArray = new int[keys.size()];
		int roleIndex = AbstractJQMLModel.USER_ROLE_STARTING_INDEX;
		int i = 0;
		for (final K k : keys) {
			final String s = k.toString();
			final int temp = roleIndex++;
			roleArray[i] = s;
			indicesArray[i] = temp;

			indexLookup.put(s, Integer.valueOf(temp));
			++i;
		}

		verifyEventLoopThread();
		modelPointer = ApiInstance.LIST_LIB_INSTANCE.createGenericListModel(modelName, roleArray, indicesArray,
				roleArray.length);
		JQMLExceptionHandling.checkExceptions();

		accessor.setModelPointer(modelPointer);

		keySet = keys;
	}

	/**
	 * Adds the map's data to the end of this list model.
	 *
	 * @param map The data to append to the end of the model.
	 * @return A Map reference that can be used to modify the data stored at the new
	 *         Map in the list model.
	 */
	public Map<K, JVariant> add(final ImmutableMap<K, JVariant> map) {
		Objects.requireNonNull(map, "map is null");
		verifyEventLoopThread();
		final SharedJavaCppMemory m = accessor.getJavaToCppMemory();
		final List<JVariant> variantList = new ArrayList<>(map.size());
		final int[] roles = new int[map.size()];
		int i = 0;
		for (final Entry<K, JVariant> entry : map.entrySet()) {
			variantList.add(entry.getValue());
			roles[i] = indexLookup.get(entry.getKey().toString()).intValue();
			++i;
		}

		JVariant.serialize(variantList, m);
		final int newIndex = ListQMLAPIFast.appendGenericListModelDataMulti(modelPointer, m.getPointer(), roles,
				roles.length);
		JQMLExceptionHandling.checkExceptions();

		final JQMLListModelMap<K> temp = new JQMLListModelMap<>(modelName, keySet, eventLoopThread,
				accessor.copy(newIndex), indexLookup);
		mapRefs.add(temp);

		fireAddEvent(mapRefs.size() - 1, temp);

		return temp;
	}

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
	public Map<K, JVariant> add(final int index, final ImmutableMap<K, JVariant> map) {
		Objects.requireNonNull(map, "map is null");
		verifyEventLoopThread();

		final List<JVariant> valueList = new ArrayList<>(map.size());
		final int[] array = new int[map.size()];
		int i = 0;
		final SharedJavaCppMemory m = accessor.getJavaToCppMemory();
		for (final Entry<K, JVariant> entry : map.entrySet()) {
			valueList.add(entry.getValue());
			array[i] = indexLookup.get(entry.getKey().toString()).intValue();
			++i;
		}
		JVariant.serialize(valueList, m);

		ListQMLAPIFast.insertGenericListModelDataMulti(modelPointer, index, m.getPointer(), array, map.size());
		JQMLExceptionHandling.checkExceptions();

		final JQMLListModelMap<K> temp = new JQMLListModelMap<>(modelName, keySet, eventLoopThread,
				accessor.copy(index), indexLookup);
		mapRefs.add(index, temp);

		resetMapIndicies();

		fireAddEvent(index, temp);

		return temp;
	}

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
	public Map<K, JVariant> add(final int index, final JVariant data, final K role) {
		Objects.requireNonNull(data, "data is null");
		Objects.requireNonNull(role, "role is null");
		verifyEventLoopThread();

		data.serialize(accessor.getJavaToCppMemory());
		ListQMLAPIFast.insertGenericListModelData(modelPointer, index, accessor.getJavaToCppMemory().getPointer(),
				indexLookup.get(role.toString()).intValue());
		JQMLExceptionHandling.checkExceptions();

		final JQMLListModelMap<K> temp = new JQMLListModelMap<>(modelName, keySet, eventLoopThread,
				accessor.copy(index), indexLookup);
		mapRefs.add(index, temp);

		resetMapIndicies();

		fireAddEvent(index, temp);

		return temp;
	}

	@Deprecated
	@Override
	public void add(final int index, final Map<K, JVariant> element) {
		if (element instanceof ImmutableMap) {
			add(index, (ImmutableMap<K, JVariant>) element);
		} else {
			throw new UnsupportedOperationException("Adding mutable maps is not supported");
		}
	}

	/**
	 * Appends the data to the end of this list model.
	 *
	 * @param data The data to append to this model.
	 * @param role The role to associate with the data.
	 *
	 * @return A Map reference that can be used to modify the data stored at the new
	 *         Map in the list model.
	 */
	public Map<K, JVariant> add(final JVariant data, final K role) {
		Objects.requireNonNull(data, "data is null");
		Objects.requireNonNull(role, "role is null");

		verifyEventLoopThread();
		data.serialize(accessor.getJavaToCppMemory());
		final int newIndex = ListQMLAPIFast.appendGenericListModelData(modelPointer,
				accessor.getJavaToCppMemory().getPointer(), indexLookup.get(role.toString()).intValue());
		JQMLExceptionHandling.checkExceptions();

		final JQMLListModelMap<K> map = new JQMLListModelMap<>(modelName, keySet, eventLoopThread,
				accessor.copy(newIndex), indexLookup);
		mapRefs.add(map);

		fireAddEvent(mapRefs.size() - 1, map);

		return map;
	}

	@Override
	@Deprecated
	public boolean add(final Map<K, JVariant> e) {
		if (e instanceof ImmutableMap) {
			add((ImmutableMap<K, JVariant>) e);
			return true;
		} else {
			throw new UnsupportedOperationException("Adding mutable maps is not supported");
		}
	}

	@Override
	public boolean addAll(final Collection<? extends Map<K, JVariant>> c) {
		for (final Map<K, JVariant> m : c) {
			if (m instanceof ImmutableMap) {
				add((ImmutableMap<K, JVariant>) m);
			} else {
				throw new UnsupportedOperationException("Adding mutable maps is not supported");
			}
		}
		return true;
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends Map<K, JVariant>> c) {
		int tempIndex = index;
		for (final Map<K, JVariant> m : c) {
			if (m instanceof ImmutableMap) {
				add(tempIndex, (ImmutableMap<K, JVariant>) m);
				++tempIndex;
			} else {
				throw new UnsupportedOperationException("Adding mutable maps is not supported");
			}
		}
		return true;
	}

	@Override
	public void clear() {
		while (size() > 0) {
			remove(0);
		}
	}

	/**
	 * Clears all keys from the Map stored at index.
	 *
	 * @param index The index into this list model to clear the map's keys.
	 */
	public void clear(final int index) {
		verifyEventLoopThread();
		ListQMLAPIFast.clearAllGenericListModelData(modelPointer, index);
		JQMLExceptionHandling.checkExceptions();
	}

	@Override
	public boolean contains(final Object o) {
		return indexOf(o) != -1;
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		for (final Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	private void fireAddEvent(final int index, final Map<K, JVariant> map) {
		for (final ListListener<K> l : listeners) {
			l.added(index, map);
		}
	}

	@Override
	public Map<K, JVariant> get(final int index) {
		verifyEventLoopThread();
		return mapRefs.get(index);
	}

	/**
	 * Returns the data stored at the index in the list and with the key of role.
	 *
	 * @param index Index in the list.
	 * @param role  Key in the map.
	 * @return Data that is stored or Optional.empty() if data is not stored.
	 */
	public Optional<JVariant> getData(final int index, final K role) {
		return Optional.ofNullable(get(index).get(role));
	}

	/**
	 * @return The name of this model.
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * Returns a value from the root value map.
	 *
	 * @param key Key of the value to return.
	 * @return The key's value or Optional.empty().
	 */
	public Optional<JVariant> getRootValue(final String key) {
		final IntByReference length = new IntByReference();
		final Pointer data = ListQMLAPIFast.getRootValueFromListModel(modelPointer, key, length);
		JQMLExceptionHandling.checkExceptions();
		if (data == null || Pointer.nativeValue(data) == 0) {
			return Optional.empty();
		} else {
			return JVariant.deserialize(data.getByteBuffer(0, length.getValue()));
		}
	}

	@Override
	public int indexOf(final Object o) {
		if (o == null) {
			return -1;
		} else {
			for (int i = 0; i < mapRefs.size(); ++i) {
				final Map<K, JVariant> m = mapRefs.get(i);
				if (m.equals(o)) {
					return i;
				}
			}

			return -1;
		}
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Checks if the Map at index contains a value for role.
	 *
	 * @param index Index into the list.
	 * @param role  Key into the Map in the list at index.
	 * @return True if the Key, role, has a value associated with it.
	 */
	public boolean isPresent(final int index, final K role) {
		Objects.requireNonNull(role, "role is null");

		verifyEventLoopThread();
		final boolean a = ListQMLAPIFast.isGenericListModelRolePresent(modelPointer, index,
				indexLookup.get(role.toString()).intValue());
		JQMLExceptionHandling.checkExceptions();
		return a;
	}

	@Override
	public Iterator<Map<K, JVariant>> iterator() {
		return ImmutableList.copyOf(mapRefs).iterator();
	}

	@Override
	public int lastIndexOf(final Object o) {
		if (o == null) {
			return -1;
		} else {
			for (int i = mapRefs.size() - 1; i >= 0; --i) {
				final Map<K, JVariant> m = mapRefs.get(i);
				if (m.equals(o)) {
					return i;
				}
			}

			return -1;
		}
	}

	@Override
	public ListIterator<Map<K, JVariant>> listIterator() {
		return ImmutableList.copyOf(mapRefs).listIterator();
	}

	@Override
	public ListIterator<Map<K, JVariant>> listIterator(final int index) {
		return ImmutableList.copyOf(mapRefs).listIterator(index);
	}

	/**
	 * Puts a value in the root value map.
	 *
	 * @param key  The value's key
	 * @param data The new value.
	 */
	public void putRootValue(final String key, final JVariant data) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(data, "data is null");

		data.serialize(accessor.getJavaToCppMemory());
		ListQMLAPIFast.putRootValueIntoListModel(modelPointer, key, accessor.getJavaToCppMemory().getPointer());
		JQMLExceptionHandling.checkExceptions();
	}

	/**
	 * @param l ListListener to receive callbacks on this list changing.
	 */
	public void registerListener(final ListListener<K> l) {
		verifyEventLoopThread();
		listeners.add(Objects.requireNonNull(l, "l is null"));
	}

	@Override
	public Map<K, JVariant> remove(final int index) {
		final JQMLListModelMap<K> old = (JQMLListModelMap<K>) mapRefs.get(index);
		final Map<K, JVariant> copy = new HashMap<>(old);

		verifyEventLoopThread();
		ListQMLAPIFast.eraseGenericListModelData(modelPointer, index);
		JQMLExceptionHandling.checkExceptions();

		mapRefs.remove(index);
		old.setIndex(-1);
		resetMapIndicies();

		return copy;
	}

	/**
	 * Removes data from the Map stored at index for key role.
	 *
	 * @param index Index in the list for the map to modify.
	 * @param role  Key remove from the Map.
	 */
	public void remove(final int index, final K role) {
		Objects.requireNonNull(role, "role is null");

		verifyEventLoopThread();
		ListQMLAPIFast.clearGenericListModelData(modelPointer, index, indexLookup.get(role.toString()).intValue());
		JQMLExceptionHandling.checkExceptions();
	}

	@Override
	public boolean remove(final Object o) {
		final int index = indexOf(o);
		if (index != -1) {
			remove(index);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		boolean modified = false;
		for (final Object o : c) {
			final int index = indexOf(o);
			if (index != -1) {
				remove(index);
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * Removes a value from the root value map.
	 *
	 * @param key Key to remove.
	 */
	public void removeRootValue(final String key) {
		Objects.requireNonNull(key, "key is null");
		ListQMLAPIFast.removeRootValueFromListModel(modelPointer, key);
		JQMLExceptionHandling.checkExceptions();
	}

	private void resetMapIndicies() {
		for (int i = 0; i < mapRefs.size(); ++i) {
			((JQMLListModelMap<K>) mapRefs.get(i)).setIndex(i);
		}
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		boolean modified = false;
		for (final Map<K, JVariant> m : ImmutableList.copyOf(mapRefs)) {
			boolean found = false;
			for (final Object o : c) {
				if (m.equals(o)) {
					found = true;
					break;
				}
			}

			if (!found) {
				remove(m);
				modified = true;
			}
		}
		return modified;
	}

	@Override
	@Deprecated
	public Map<K, JVariant> set(final int index, final Map<K, JVariant> element) {
		throw new UnsupportedOperationException("Setting mutable maps is not supported");
	}

	/**
	 * Sets the data at the index and role.
	 *
	 * @param index The index in the list to place the data.
	 * @param data  The data to store.
	 * @param role  The Role to store the data under.
	 */
	public void setData(final int index, final K role, final JVariant data) {
		Objects.requireNonNull(data, "data is null");
		Objects.requireNonNull(role, "role is null");

		verifyEventLoopThread();

		accessor.setListIndex(index);
		accessor.set(data, indexLookup.get(role.toString()).intValue());

		boolean added = false;
		while (mapRefs.size() <= index) {
			final JQMLListModelMap<K> map = new JQMLListModelMap<>(modelName, keySet, eventLoopThread,
					accessor.copy(mapRefs.size()), indexLookup);
			mapRefs.add(map);

			fireAddEvent(mapRefs.size() - 1, map);

			added = true;
		}

		if (added) {
			resetMapIndicies();
		}
	}

	/**
	 * Copies the data from the Map to the index.
	 *
	 * @param index The index in the list to place the data.
	 * @param data  The data to store.
	 */
	public void setData(final int index, final Map<K, JVariant> data) {
		Objects.requireNonNull(data, "data is null");

		verifyEventLoopThread();
		for (final Entry<K, JVariant> entry : data.entrySet()) {
			setData(index, entry.getKey(), entry.getValue());
		}
	}

	@Override
	public int size() {
		verifyEventLoopThread();
		final int a = ListQMLAPIFast.getGenericListModelSize(modelPointer);
		JQMLExceptionHandling.checkExceptions();
		return a;
	}

	@Override
	public void sort(final Comparator<? super Map<K, JVariant>> c) {
		Objects.requireNonNull(c, "c is null");

		Collections.sort(mapRefs, c);

		final int[] ordering = new int[mapRefs.size()];
		for (int i = 0; i < mapRefs.size(); ++i) {
			final JQMLListModelMap<K> map = (JQMLListModelMap<K>) mapRefs.get(i);
			ordering[i] = map.getIndex();
		}

		ListQMLAPIFast.reorderGenericListModel(modelPointer, ordering, mapRefs.size());
		JQMLExceptionHandling.checkExceptions();

		resetMapIndicies();
	}

	@Override
	public List<Map<K, JVariant>> subList(final int fromIndex, final int toIndex) {
		return mapRefs.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return mapRefs.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return mapRefs.toArray(a);
	}

	/**
	 * @param l ListListener to unregister.
	 */
	public void unregisterListener(final ListListener<K> l) {
		verifyEventLoopThread();
		listeners.remove(l);
	}

}
