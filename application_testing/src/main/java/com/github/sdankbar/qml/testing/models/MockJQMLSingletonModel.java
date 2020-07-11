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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.models.interfaces.ChangeListener;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;

public class MockJQMLSingletonModel<K> implements JQMLSingletonModel<K> {

	private final String modelName;
	private Map<K, JVariant> delegate = new HashMap<>();

	public MockJQMLSingletonModel(String name) {
		this.modelName = Objects.requireNonNull(name, "name is null");
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public boolean containsKey(Object arg0) {
		return delegate.containsKey(arg0);
	}

	@Override
	public boolean containsValue(Object arg0) {
		return delegate.containsValue(arg0);
	}

	@Override
	public Set<Entry<K, JVariant>> entrySet() {
		return delegate.entrySet();
	}

	@Override
	public JVariant get(Object arg0) {
		return delegate.get(arg0);
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<K> keySet() {
		return delegate.keySet();
	}

	@Override
	public JVariant put(K arg0, JVariant arg1) {
		return delegate.put(arg0, arg1);
	}

	@Override
	public void putAll(Map<? extends K, ? extends JVariant> arg0) {
		delegate.putAll(arg0);
	}

	@Override
	public JVariant remove(Object arg0) {
		return delegate.remove(arg0);
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public Collection<JVariant> values() {
		return delegate.values();
	}

	@Override
	public void registerChangeListener(ChangeListener l) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getModelName() {
		return modelName;
	}

}
