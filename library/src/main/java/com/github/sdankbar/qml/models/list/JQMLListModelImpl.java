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
package com.github.sdankbar.qml.models.list;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.jni.interfaces.InvokeCallback;
import com.github.sdankbar.qml.cpp.jni.list.ListModelFunctions;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.AbstractJQMLModel;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * A model that is available to QML. Represents a list of Maps from the key type
 * K, to a JVariant.
 *
 * @param <K> The type of the K in the Map.
 */
public class JQMLListModelImpl<K> extends AbstractJQMLModel implements JQMLListModel<K> {

	private static class ModelChangeListener implements InvokeCallback {

		private final List<Runnable> listeners = new ArrayList<>();
		private boolean locked = false;
		private boolean pendingCallback = false;

		public void addListener(final Runnable l) {
			listeners.add(l);
		}

		public void removeListener(final Runnable l) {
			listeners.remove(l);
		}

		public boolean hasListeners() {
			return !listeners.isEmpty();
		}

		@Override
		public void invoke() {
			if (!locked) {
				for (final Runnable l : listeners) {
					l.run();
				}
			} else {
				pendingCallback = true;
			}
		}

		public void lock() {
			if (!locked) {
				locked = true;
			}
		}

		public void unlock() {
			if (locked) {
				locked = false;
				if (pendingCallback) {
					pendingCallback = false;
					for (final Runnable l : listeners) {
						l.run();
					}
				}
			}
		}

	}

	private final String modelName;
	private final PutMode putMode;
	private final long modelPointer;

	private final Set<K> keySet;
	private final AtomicReference<Thread> eventLoopThread;
	private final ListAccessor accessor;

	private final Map<String, Integer> indexLookup = new HashMap<>();

	private final List<Map<K, JVariant>> mapRefs = new ArrayList<>();

	private final List<ListListener<K>> listeners = new ArrayList<>();
	private final ModelChangeListener changeCallback = new ModelChangeListener();

	/**
	 * Constructor.
	 *
	 * @param modelName       The name of the model. This is the name that the model
	 *                        is made available to QML as.
	 * @param keys            Set of keys this model can use.
	 * @param eventLoopThread Reference to the Qt Thread.
	 * @param accessor        Accessor this model will use to to access the C++
	 *                        portion of this model.
	 * @param putMode         Specifies how put operations behave.
	 */
	public JQMLListModelImpl(final String modelName, final Set<K> keys, final AtomicReference<Thread> eventLoopThread,
			final ListAccessor accessor, final PutMode putMode) {
		super(eventLoopThread);
		this.eventLoopThread = eventLoopThread;
		this.putMode = Objects.requireNonNull(putMode, "putMode is null");
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
		modelPointer = ListModelFunctions.createGenericListModel(modelName, roleArray, indicesArray);

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
	@Override
	public Map<K, JVariant> add(final ImmutableMap<K, JVariant> map) {
		Objects.requireNonNull(map, "map is null");
		verifyEventLoopThread();
		for (final Entry<K, JVariant> entry : map.entrySet()) {
			entry.getValue().sendToQML(indexLookup.get(entry.getKey().toString()).intValue());
		}

		try {
			changeCallback.lock();
			final int newIndex = ListModelFunctions.appendGenericListModelData(modelPointer);

			final JQMLListModelMap<K> temp = new JQMLListModelMap<>(modelName, keySet, eventLoopThread,
					accessor.copy(newIndex), indexLookup, putMode);
			mapRefs.add(temp);

			fireAddEvent(mapRefs.size() - 1, temp);

			return temp;
		} finally {
			changeCallback.unlock();
		}
	}

	/**
	 * Inserts the map's data at index into this list model.
	 *
	 * @param index The index in the list model to insert the data at. If index is
	 *              &gt; than the length of the list, empty data is automatically
	 *              appended to the model.
	 * @param map   The data to insert into the model.
	 *
	 * @return A Map reference that can be used to modify the data stored at the new
	 *         Map in the list model.
	 */
	@Override
	public Map<K, JVariant> add(final int index, final ImmutableMap<K, JVariant> map) {
		Objects.requireNonNull(map, "map is null");
		verifyEventLoopThread();

		for (final Entry<K, JVariant> entry : map.entrySet()) {
			entry.getValue().sendToQML(indexLookup.get(entry.getKey().toString()).intValue());
		}

		try {
			changeCallback.lock();
			ListModelFunctions.insertGenericListModelData(modelPointer, index);

			final JQMLListModelMap<K> temp = new JQMLListModelMap<>(modelName, keySet, eventLoopThread,
					accessor.copy(index), indexLookup, putMode);
			mapRefs.add(index, temp);

			resetMapIndicies();

			fireAddEvent(index, temp);

			return temp;
		} finally {
			changeCallback.unlock();
		}
	}

	/**
	 * Inserts the data at index into this list model.
	 *
	 * @param index The index in the list model to insert the data at. If index is
	 *              &gt; than the length of the list, empty data is automatically
	 *              appended to the model.
	 * @param data  The data to insert into the model.
	 * @param role  The role to associate with the data.
	 *
	 * @return A Map reference that can be used to modify the data stored at the new
	 *         Map in the list model.
	 */
	@Override
	public Map<K, JVariant> add(final int index, final JVariant data, final K role) {
		Objects.requireNonNull(data, "data is null");
		Objects.requireNonNull(role, "role is null");
		verifyEventLoopThread();

		data.sendToQML(indexLookup.get(role.toString()).intValue());
		try {
			changeCallback.lock();
			ListModelFunctions.insertGenericListModelData(modelPointer, index);

			final JQMLListModelMap<K> temp = new JQMLListModelMap<>(modelName, keySet, eventLoopThread,
					accessor.copy(index), indexLookup, putMode);
			mapRefs.add(index, temp);

			resetMapIndicies();

			fireAddEvent(index, temp);

			return temp;
		} finally {
			changeCallback.unlock();
		}
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
	@Override
	public Map<K, JVariant> add(final JVariant data, final K role) {
		Objects.requireNonNull(data, "data is null");
		Objects.requireNonNull(role, "role is null");

		verifyEventLoopThread();
		data.sendToQML(indexLookup.get(role.toString()).intValue());
		try {
			changeCallback.lock();
			final int newIndex = ListModelFunctions.appendGenericListModelData(modelPointer);

			final JQMLListModelMap<K> map = new JQMLListModelMap<>(modelName, keySet, eventLoopThread,
					accessor.copy(newIndex), indexLookup, putMode);
			mapRefs.add(map);

			fireAddEvent(mapRefs.size() - 1, map);

			return map;
		} finally {
			changeCallback.unlock();
		}
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
	@Override
	public void clear(final int index) {
		verifyEventLoopThread();
		ListModelFunctions.clearAllGenericListModelData(modelPointer, index);

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

	private void fireRemoveEvent(final int index, final Map<K, JVariant> map) {
		for (final ListListener<K> l : listeners) {
			l.removed(index, map);
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
	@Override
	public Optional<JVariant> getData(final int index, final K role) {
		return Optional.ofNullable(get(index).get(role));
	}

	/**
	 * @return The name of this model.
	 */
	@Override
	public String getModelName() {
		return modelName;
	}

	/**
	 * Returns a value from the root value map.
	 *
	 * @param key Key of the value to return.
	 * @return The key's value or Optional.empty().
	 */
	@Override
	public Optional<JVariant> getRootValue(final String key) {
		final JVariant data = ListModelFunctions.getRootValueFromListModel(modelPointer, key);
		return Optional.ofNullable(data);
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
	@Override
	public boolean isPresent(final int index, final K role) {
		Objects.requireNonNull(role, "role is null");

		verifyEventLoopThread();
		final boolean a = ListModelFunctions.isGenericListModelRolePresent(modelPointer, index,
				indexLookup.get(role.toString()).intValue());

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
	@Override
	public void putRootValue(final String key, final JVariant data) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(data, "data is null");
		data.sendToQML(0);
		ListModelFunctions.putRootValueIntoListModel(modelPointer, key);

	}

	/**
	 * @param l ListListener to receive callbacks on this list changing.
	 */
	@Override
	public void registerListener(final ListListener<K> l) {
		verifyEventLoopThread();
		listeners.add(Objects.requireNonNull(l, "l is null"));
	}

	@Override
	public Map<K, JVariant> remove(final int index) {
		final JQMLListModelMap<K> old = (JQMLListModelMap<K>) mapRefs.get(index);
		final Map<K, JVariant> copy = new HashMap<>(old);

		verifyEventLoopThread();
		try {
			changeCallback.lock();
			ListModelFunctions.eraseGenericListModelData(modelPointer, index);

			mapRefs.remove(index);
			old.setIndex(-1);
			resetMapIndicies();

			fireRemoveEvent(index, copy);

			return copy;
		} finally {
			changeCallback.unlock();
		}
	}

	/**
	 * Removes data from the Map stored at index for key role.
	 *
	 * @param index Index in the list for the map to modify.
	 * @param role  Key remove from the Map.
	 */
	@Override
	public void remove(final int index, final K role) {
		Objects.requireNonNull(role, "role is null");

		verifyEventLoopThread();
		ListModelFunctions.clearGenericListModelData(modelPointer, index, indexLookup.get(role.toString()).intValue());

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
	@Override
	public void removeRootValue(final String key) {
		Objects.requireNonNull(key, "key is null");
		ListModelFunctions.removeRootValueFromListModel(modelPointer, key);

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
	@Override
	public void setData(final int index, final K role, final JVariant data) {
		Objects.requireNonNull(data, "data is null");
		Objects.requireNonNull(role, "role is null");

		verifyEventLoopThread();

		accessor.setListIndex(index);
		accessor.set(data, indexLookup.get(role.toString()).intValue());

		boolean added = false;
		try {
			changeCallback.lock();
			while (mapRefs.size() <= index) {
				final JQMLListModelMap<K> map = new JQMLListModelMap<>(modelName, keySet, eventLoopThread,
						accessor.copy(mapRefs.size()), indexLookup, putMode);
				mapRefs.add(map);

				fireAddEvent(mapRefs.size() - 1, map);

				added = true;
			}

			if (added) {
				resetMapIndicies();
			}
		} finally {
			changeCallback.unlock();
		}
	}

	/**
	 * Copies the data from the Map to the index.
	 *
	 * @param index The index in the list to place the data.
	 * @param data  The data to store.
	 */
	@Override
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
		final int a = ListModelFunctions.getGenericListModelSize(modelPointer);

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

		try {
			changeCallback.lock();
			ListModelFunctions.reorderGenericListModel(modelPointer, ordering);

			resetMapIndicies();
		} finally {
			changeCallback.unlock();
		}
	}

	@Override
	public List<Map<K, JVariant>> subList(final int fromIndex, final int toIndex) {
		return mapRefs.subList(fromIndex, toIndex);
	}

	@Override
	public String toString() {
		return mapRefs.toString();
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
	@Override
	public void unregisterListener(final ListListener<?> l) {
		verifyEventLoopThread();
		listeners.remove(l);
	}

	@Override
	public void assign(final List<Map<K, JVariant>> list) {
		verifyEventLoopThread();

		try {
			changeCallback.lock();
			if (list.isEmpty()) {
				clear();
			} else {
				final int reuseCount = Math.min(mapRefs.size(), list.size());
				for (int i = 0; i < reuseCount; ++i) {
					final JQMLListModelMap<K> ref = (JQMLListModelMap<K>) mapRefs.get(i);
					ref.assign(list.get(i));
				}

				addAll(list.subList(reuseCount, list.size()));

				while (mapRefs.size() > list.size()) {
					remove(mapRefs.size() - 1);
				}
			}
		} finally {
			changeCallback.unlock();
		}
	}

	@Override
	public void assign(final int index, final Map<K, JVariant> map) {
		verifyEventLoopThread();
		final JQMLListModelMap<K> ref = (JQMLListModelMap<K>) mapRefs.get(index);
		ref.assign(map);
	}

	@Override
	public SignalLock lockSignals() {
		ListModelFunctions.lockDataChangedSignal(modelPointer);
		return new SignalLock(this);
	}

	void unlockSignals() {
		ListModelFunctions.unlockDataChangedSignal(modelPointer);
	}

	@Override
	public <L> ImmutableList<L> asMappedList(final K key, final Class<L> t) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(t, "t is null");
		return stream().map(m -> {
			final JVariant var = m.get(key);
			Preconditions.checkArgument(var != null, "Missing data in map for key %s", key);
			final Optional<L> opt = var.asType(t);
			Preconditions.checkArgument(opt.isPresent(), "Data %s cannot be cast to %s", var, t.getName());
			return opt.get();
		}).collect(ImmutableList.toImmutableList());
	}

	@Override
	public <L> ImmutableList<L> asMappedList(final K key, final Class<L> t, final L defaultValue) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(t, "t is null");
		return stream().map(m -> {
			final JVariant var = m.get(key);
			if (var != null) {
				return var.asType(t, defaultValue);
			} else {
				return defaultValue;
			}
		}).collect(ImmutableList.toImmutableList());
	}

	@Override
	public void swap(final int source, final int destination) {
		final int length = size();
		Preconditions.checkArgument(0 <= source && source < length, "Source outside valid range, %s", source);
		Preconditions.checkArgument(0 <= destination && destination < length, "Destination outside valid range, %s",
				destination);
		if (source != destination) {
			final Map<K, JVariant> sourceMap = ImmutableMap.copyOf(get(source));
			final Map<K, JVariant> destinationMap = get(destination);

			assign(source, destinationMap);
			assign(destination, sourceMap);
		}
	}

	@Override
	public void serialize(final OutputStream stream, final JSONObject additionalJSON,
			final ImmutableSet<String> rootKeysToPersist) throws IOException {
		final JSONObject root = new JSONObject();

		final JSONObject rootValues = new JSONObject();
		for (final String s : rootKeysToPersist) {
			final Optional<JVariant> var = getRootValue(s);
			if (var.isPresent()) {
				rootValues.put(s, var.get().toJSON());
			}
		}
		root.put("root", rootValues);

		final JSONArray array = new JSONArray();
		for (final Map<K, JVariant> map : this) {
			final JSONObject itemObj = new JSONObject();
			for (final Map.Entry<K, JVariant> entry : map.entrySet()) {
				itemObj.put(entry.getKey().toString(), entry.getValue().toJSON());
			}
			array.put(itemObj);
		}
		root.put("list", array);
		if (additionalJSON != null) {
			root.put("additional", additionalJSON);
		}

		final String s = root.toString(1);
		stream.write(s.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public JSONObject deserialize(final InputStream stream, final ImmutableSet<String> rootKeysToPersist)
			throws IOException {
		final JSONTokener tokener = new JSONTokener(stream);
		final JSONObject object = new JSONObject(tokener);
		final JSONArray array = object.getJSONArray("list");

		final JSONObject rootValues = object.optJSONObject("root");
		if (rootValues != null) {
			for (final String s : rootKeysToPersist) {
				final JSONObject nullableObj = rootValues.optJSONObject(s);
				if (nullableObj != null) {
					final Optional<JVariant> opt = JVariant.fromJSON(nullableObj);
					if (opt.isPresent()) {
						putRootValue(s, opt.get());
					} else {
						removeRootValue(s);
					}
				} else {
					removeRootValue(s);
				}
			}
		} else {
			for (final String s : rootKeysToPersist) {
				removeRootValue(s);
			}
		}

		final ImmutableList.Builder<Map<K, JVariant>> list = ImmutableList.builder();
		for (int i = 0; i < array.length(); ++i) {
			final JSONObject sub = array.getJSONObject(i);
			final ImmutableMap.Builder<K, JVariant> b = ImmutableMap.builder();
			for (final String k : sub.keySet()) {
				K kObj = null;
				for (final K temp : keySet) {
					if (temp.toString().equals(k)) {
						kObj = temp;
						break;
					}
				}

				final Optional<JVariant> opt = JVariant.fromJSON(sub.getJSONObject(k));
				if (kObj != null && opt.isPresent()) {
					b.put(kObj, opt.get());
				}
			}
			list.add(b.build());
		}

		assign(list.build());

		if (Arrays.asList(JSONObject.getNames(object)).contains("additional")) {
			return object.getJSONObject("additional");
		} else {
			return null;
		}
	}

	@Override
	public void registerModelChangedListener(final Runnable r) {
		verifyEventLoopThread();
		if (!changeCallback.hasListeners()) {
			ListModelFunctions.registerModelChangedCallback(modelPointer, changeCallback);
		}
		changeCallback.addListener(Objects.requireNonNull(r, "r is null"));
	}

	@Override
	public void unregisterModelChangedListener(final Runnable r) {
		verifyEventLoopThread();
		changeCallback.removeListener(Objects.requireNonNull(r, "r is null"));
	}

}
