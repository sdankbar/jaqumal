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
package com.github.sdankbar.qml.models.singleton;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.github.sdankbar.qml.JQMLModelFactory;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;

public class SingletonInvocationHandler implements InvocationHandler {

	@SuppressWarnings("unchecked")
	public static <K> K createWrapper(final String name, final Class<K> c, final JQMLModelFactory factory) {
		Objects.requireNonNull(c, "c is null");
		Objects.requireNonNull(factory, "factory is null");

		final Set<String> keys = new HashSet<>();
		for (final Method m : c.getDeclaredMethods()) {
			final boolean isGetter = m.getName().startsWith("get");
			final boolean isSetter = m.getName().startsWith("set");
			if (isGetter || isSetter) {
				keys.add(m.getName().substring(3));
			}
		}

		final JQMLSingletonModel<String> model = factory.createSingletonModel(name, keys, PutMode.RETURN_NULL);

		final SingletonInvocationHandler handler = new SingletonInvocationHandler(model);

		return (K) Proxy.newProxyInstance(c.getClassLoader(), new Class[] { c }, handler);
	}

	private final JQMLSingletonModel<String> model;

	private SingletonInvocationHandler(final JQMLSingletonModel<String> model) {
		this.model = Objects.requireNonNull(model, "model is null");
	}

	@Override
	public Object invoke(final Object proxy, final Method m, final Object[] args) throws Throwable {
		final String key = m.getName().substring(3);

		if (m.getName().startsWith("get")) {
			return model.get(key).asType(m.getReturnType()).orElse(null);
		} else if (m.getName().startsWith("set")) {
			final JVariant v = JVariant.toVariant(args[0]);
			if (v != null) {
				model.put(key, v);
			}
			return null;
		} else {
			return null;
		}
	}

}
