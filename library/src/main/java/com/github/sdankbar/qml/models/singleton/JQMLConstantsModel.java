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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import com.github.sdankbar.qml.JQMLModelFactory;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.google.common.collect.ImmutableMap;

public class JQMLConstantsModel {

	private static String getName(final Class<?> c) {
		Objects.requireNonNull(c, "c is null");
		return c.getSimpleName();
	}

	public static ImmutableMap<String, JVariant> getValues(final Class<?> c) {
		Objects.requireNonNull(c, "c is null");
		final ImmutableMap.Builder<String, JVariant> builder = ImmutableMap.builder();

		for (final Field f : c.getDeclaredFields()) {
			if (Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers())
					&& Modifier.isPublic(f.getModifiers())) {
				final JVariant v = toVariant(f);
				if (v != null) {
					builder.put(f.getName(), v);
				}
			}
		}

		return builder.build();

	}

	private static JVariant toVariant(final Field f) {
		try {
			final Object v = f.get(null);
			return JVariant.toVariant(v);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}

	private final JQMLSingletonModel<String> model;

	public JQMLConstantsModel(final JQMLModelFactory factory, final String name,
			final ImmutableMap<String, JVariant> values) {
		Objects.requireNonNull(factory, "factory is null");
		Objects.requireNonNull(name, "name is null");
		Objects.requireNonNull(values, "values is null");

		model = factory.createSingletonModel(name, values.keySet(), PutMode.RETURN_NULL);
		model.putAll(values);
	}

	public JQMLConstantsModel(final JQMLModelFactory factory, final Class<?> constants) {
		this(factory, getName(constants), getValues(constants));
	}

	public String getModelName() {
		return model.getModelName();
	}

}
