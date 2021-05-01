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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	private final Map<K, JVariant> delegate = new HashMap<>();

	public MockJQMLSingletonModel(final String name) {
		this.modelName = Objects.requireNonNull(name, "name is null");
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public boolean containsKey(final Object arg0) {
		return delegate.containsKey(arg0);
	}

	@Override
	public boolean containsValue(final Object arg0) {
		return delegate.containsValue(arg0);
	}

	@Override
	public Set<Entry<K, JVariant>> entrySet() {
		return delegate.entrySet();
	}

	@Override
	public JVariant get(final Object arg0) {
		return delegate.get(arg0);
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return delegate.keySet();
	}

	@Override
	public JVariant put(final K arg0, final JVariant arg1) {
		return delegate.put(arg0, arg1);
	}

	@Override
	public void putAll(final Map<? extends K, ? extends JVariant> arg0) {
		delegate.putAll(arg0);
	}

	@Override
	public JVariant remove(final Object arg0) {
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
	public void registerChangeListener(final ChangeListener l) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unregisterChangeListener(final ChangeListener l) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getModelName() {
		return modelName;
	}

	@Override
	public void assign(final Map<K, JVariant> map) {
		delegate.clear();
		delegate.putAll(map);
	}

	@Override
	public void serialize(final OutputStream stream) throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public void deserialize(final InputStream stream) throws IOException {
		// TODO Auto-generated method stub
	}

}
