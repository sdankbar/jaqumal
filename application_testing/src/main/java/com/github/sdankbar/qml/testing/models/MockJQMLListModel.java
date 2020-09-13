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
package com.github.sdankbar.qml.testing.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.github.sdankbar.qml.models.list.ListListener;
import com.github.sdankbar.qml.models.list.SignalLock;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class MockJQMLListModel<K> implements JQMLListModel<K> {

	private final String modelName;
	private final List<Map<K, JVariant>> delegate = new ArrayList<>();
	private final Map<String, JVariant> rootData = new HashMap<>();

	public MockJQMLListModel(final String name) {
		this.modelName = Objects.requireNonNull(name, "name is null");
	}

	@Override
	public Map<K, JVariant> add(final ImmutableMap<K, JVariant> map) {
		final Map<K, JVariant> copy = new HashMap<>(map);
		delegate.add(copy);
		return copy;
	}

	@Override
	public Map<K, JVariant> add(final int index, final ImmutableMap<K, JVariant> map) {
		final Map<K, JVariant> copy = new HashMap<>(map);
		delegate.add(index, copy);
		return copy;
	}

	@Override
	public Map<K, JVariant> add(final int index, final JVariant data, final K role) {
		final Map<K, JVariant> copy = new HashMap<>(ImmutableMap.of(role, data));
		delegate.add(index, copy);
		return copy;
	}

	@Override
	public void add(final int index, final Map<K, JVariant> element) {
		final Map<K, JVariant> copy = new HashMap<>(element);
		delegate.add(index, copy);
	}

	@Override
	public Map<K, JVariant> add(final JVariant data, final K role) {
		final Map<K, JVariant> copy = new HashMap<>(ImmutableMap.of(role, data));
		delegate.add(copy);
		return copy;
	}

	@Override
	public boolean add(final Map<K, JVariant> e) {
		final Map<K, JVariant> copy = new HashMap<>(e);
		delegate.add(copy);
		return true;
	}

	@Override
	public boolean addAll(final Collection<? extends Map<K, JVariant>> arg0) {
		return delegate.addAll(arg0.stream().map(HashMap::new).collect(ImmutableList.toImmutableList()));
	}

	@Override
	public boolean addAll(final int arg0, final Collection<? extends Map<K, JVariant>> arg1) {
		return delegate.addAll(arg0, arg1.stream().map(HashMap::new).collect(ImmutableList.toImmutableList()));
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public void clear(final int index) {
		delegate.get(index).clear();
	}

	@Override
	public boolean contains(final Object arg0) {
		return delegate.contains(arg0);
	}

	@Override
	public boolean containsAll(final Collection<?> arg0) {
		return delegate.containsAll(arg0);
	}

	@Override
	public Map<K, JVariant> get(final int arg0) {
		return delegate.get(arg0);
	}

	@Override
	public Optional<JVariant> getData(final int index, final K role) {
		return Optional.ofNullable(delegate.get(index).get(role));
	}

	@Override
	public String getModelName() {
		return modelName;
	}

	@Override
	public Optional<JVariant> getRootValue(final String key) {
		return Optional.ofNullable(rootData.get(key));
	}

	@Override
	public int indexOf(final Object arg0) {
		return delegate.indexOf(arg0);
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public boolean isPresent(final int index, final K role) {
		return delegate.get(index).containsKey(role);
	}

	@Override
	public Iterator<Map<K, JVariant>> iterator() {
		return delegate.iterator();
	}

	@Override
	public int lastIndexOf(final Object arg0) {
		return delegate.lastIndexOf(arg0);
	}

	@Override
	public ListIterator<Map<K, JVariant>> listIterator() {
		return delegate.listIterator();
	}

	@Override
	public ListIterator<Map<K, JVariant>> listIterator(final int arg0) {
		return delegate.listIterator(arg0);
	}

	@Override
	public void putRootValue(final String key, final JVariant data) {
		rootData.put(key, data);
	}

	@Override
	public void registerListener(final ListListener<K> l) {
		// TODO Auto-generated method stub
	}

	@Override
	public Map<K, JVariant> remove(final int arg0) {
		return delegate.remove(arg0);
	}

	@Override
	public void remove(final int index, final K role) {
		delegate.get(index).remove(role);
	}

	@Override
	public boolean remove(final Object arg0) {
		return delegate.remove(arg0);
	}

	@Override
	public boolean removeAll(final Collection<?> arg0) {
		return delegate.removeAll(arg0);
	}

	@Override
	public void removeRootValue(final String key) {
		rootData.remove(key);
	}

	@Override
	public boolean retainAll(final Collection<?> arg0) {
		return delegate.retainAll(arg0);
	}

	@Override
	public Map<K, JVariant> set(final int index, final Map<K, JVariant> element) {
		final Map<K, JVariant> copy = new HashMap<>(element);
		delegate.set(index, copy);
		return copy;
	}

	@Override
	public void setData(final int index, final K role, final JVariant data) {
		delegate.get(index).put(role, data);
	}

	@Override
	public void setData(final int index, final Map<K, JVariant> data) {
		final Map<K, JVariant> copy = new HashMap<>(data);
		delegate.set(index, copy);
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public List<Map<K, JVariant>> subList(final int arg0, final int arg1) {
		return delegate.subList(arg0, arg1);
	}

	@Override
	public Object[] toArray() {
		return delegate.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] arg0) {
		return delegate.toArray(arg0);
	}

	@Override
	public void unregisterListener(final ListListener<K> l) {
		// TODO Auto-generated method stub
	}

	@Override
	public void assign(final List<Map<K, JVariant>> list) {
		delegate.clear();
		delegate.addAll(list);
	}

	@Override
	public void assign(final int row, final Map<K, JVariant> map) {
		delegate.get(row).clear();
		delegate.get(row).putAll(map);
	}

	@Override
	public SignalLock lockSignals() {
		return new SignalLock(null);
	}

}
