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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class MockJQMLListModel<K> implements JQMLListModel<K> {

	private final String modelName;
	private List<Map<K, JVariant>> delegate = new ArrayList<>();
	private Map<String, JVariant> rootData = new HashMap<>();

	public MockJQMLListModel(String name) {
		this.modelName = Objects.requireNonNull(name, "name is null");
	}

	@Override
	public boolean addAll(Collection<? extends Map<K, JVariant>> arg0) {
		return delegate.addAll(arg0.stream().map(m -> new HashMap<>(m)).collect(ImmutableList.toImmutableList()));
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends Map<K, JVariant>> arg1) {
		return delegate.addAll(arg0, arg1.stream().map(m -> new HashMap<>(m)).collect(ImmutableList.toImmutableList()));
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return delegate.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return delegate.containsAll(arg0);
	}

	@Override
	public Map<K, JVariant> get(int arg0) {
		return delegate.get(arg0);
	}

	@Override
	public int indexOf(Object arg0) {
		return delegate.indexOf(arg0);
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public Iterator<Map<K, JVariant>> iterator() {
		return delegate.iterator();
	}

	@Override
	public int lastIndexOf(Object arg0) {
		return delegate.lastIndexOf(arg0);
	}

	@Override
	public ListIterator<Map<K, JVariant>> listIterator() {
		return delegate.listIterator();
	}

	@Override
	public ListIterator<Map<K, JVariant>> listIterator(int arg0) {
		return delegate.listIterator(arg0);
	}

	@Override
	public boolean remove(Object arg0) {
		return delegate.remove(arg0);
	}

	@Override
	public Map<K, JVariant> remove(int arg0) {
		return delegate.remove(arg0);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return delegate.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return delegate.retainAll(arg0);
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public List<Map<K, JVariant>> subList(int arg0, int arg1) {
		return delegate.subList(arg0, arg1);
	}

	@Override
	public Object[] toArray() {
		return delegate.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return delegate.toArray(arg0);
	}

	@Override
	public Map<K, JVariant> add(ImmutableMap<K, JVariant> map) {
		Map<K, JVariant> copy = new HashMap<>(map);
		delegate.add(copy);
		return copy;
	}

	@Override
	public Map<K, JVariant> add(int index, ImmutableMap<K, JVariant> map) {
		Map<K, JVariant> copy = new HashMap<>(map);
		delegate.add(index, copy);
		return copy;
	}

	@Override
	public Map<K, JVariant> add(int index, JVariant data, K role) {
		Map<K, JVariant> copy = new HashMap<>(ImmutableMap.of(role, data));
		delegate.add(index, copy);
		return copy;
	}

	@Override
	public void add(int index, Map<K, JVariant> element) {
		Map<K, JVariant> copy = new HashMap<>(element);
		delegate.add(index, copy);
	}

	@Override
	public Map<K, JVariant> add(JVariant data, K role) {
		Map<K, JVariant> copy = new HashMap<>(ImmutableMap.of(role, data));
		delegate.add(copy);
		return copy;
	}

	@Override
	public boolean add(Map<K, JVariant> e) {
		Map<K, JVariant> copy = new HashMap<>(e);
		delegate.add(copy);
		return true;
	}

	@Override
	public void clear(int index) {
		delegate.get(index).clear();
	}

	@Override
	public Optional<JVariant> getData(int index, K role) {
		return Optional.ofNullable(delegate.get(index).get(role));
	}

	@Override
	public String getModelName() {
		return modelName;
	}

	@Override
	public Optional<JVariant> getRootValue(String key) {
		return Optional.ofNullable(rootData.get(key));
	}

	@Override
	public boolean isPresent(int index, K role) {
		return delegate.get(index).containsKey(role);
	}

	@Override
	public void putRootValue(String key, JVariant data) {
		rootData.put(key, data);
	}

	@Override
	public void registerListener(ListListener<K> l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(int index, K role) {
		delegate.get(index).remove(role);
	}

	@Override
	public void removeRootValue(String key) {
		rootData.remove(key);
	}

	@Override
	public Map<K, JVariant> set(int index, Map<K, JVariant> element) {
		Map<K, JVariant> copy = new HashMap<>(element);
		delegate.set(index, copy);
		return copy;
	}

	@Override
	public void setData(int index, K role, JVariant data) {
		delegate.get(index).put(role, data);
	}

	@Override
	public void setData(int index, Map<K, JVariant> data) {
		Map<K, JVariant> copy = new HashMap<>(data);
		delegate.set(index, copy);
	}

	@Override
	public void unregisterListener(ListListener<K> l) {
		// TODO Auto-generated method stub

	}

}
