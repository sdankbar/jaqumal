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
package com.github.sdankbar.qml.invocation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.JInvokable;
import com.github.sdankbar.qml.exceptions.QMLException;
import com.github.sdankbar.qml.utility.QMLRequestParser;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class InvokableWrapper {

	private static final Logger log = LoggerFactory.getLogger(InvokableWrapper.class);
	private static Set<Class<?>> ALLOWED_PARAM_TYPE_SET = ImmutableSet.of(boolean.class, Boolean.class, Color.class,
			Dimension.class, double.class, Double.class, float.class, Float.class, Instant.class, int.class,
			Integer.class, long.class, Long.class, Point2D.class, Rectangle2D.class, String.class);

	private static Map<String, Method> findAnnotatedFunctions(final Object invokable) {
		final Map<String, Method> methodMap = new HashMap<>();
		for (final Method m : invokable.getClass().getDeclaredMethods()) {
			if (m.isAnnotationPresent(JInvokable.class)) {
				methodMap.put(m.getName(), validateMethod(m));
			}
		}

		return methodMap;
	}

	private static Method validateMethod(final Method m) {
		try {
			m.setAccessible(true);

			final Class<?>[] params = m.getParameterTypes();
			if (params.length == 1) {
				final Class<?> arg = params[0];
				Preconditions.checkArgument(ALLOWED_PARAM_TYPE_SET.contains(arg) || arg.equals(QMLRequestParser.class),
						"Class not supported as method parameter: {}", arg);
			} else {
				for (final Class<?> arg : params) {
					Preconditions.checkArgument(ALLOWED_PARAM_TYPE_SET.contains(arg),
							"Class not supported as method parameter: {}", arg);
				}
			}

			return m;
		} catch (SecurityException | IllegalArgumentException e) {
			throw new QMLException("Unable to reflect method for " + m, e);
		}
	}

	private final Object invokable;
	private final ImmutableMap<String, Method> handlesMap;

	public InvokableWrapper(final Object invokable) {
		this.invokable = Objects.requireNonNull(invokable, "invokable is null");
		handlesMap = ImmutableMap.copyOf(findAnnotatedFunctions(invokable));
	}

	public Object getInvokedObject() {
		return invokable;
	}

	public void invoke(final String methodName, final QMLRequestParser parser) {
		final Method handle = handlesMap.get(methodName);
		if (handle != null) {
			final Class<?>[] type = handle.getParameterTypes();
			final Object[] parameterList = new Object[type.length];

			for (int i = 0; i < type.length; ++i) {
				parameterList[i] = parser.getDataBasedOnClass(type[i]);
			}

			try {
				handle.invoke(invokable, parameterList);
			} catch (final Throwable e) {
				log.warn("Caught Throwable while invoking method", e);
			}
		} else {
			log.warn("[{}] is not an invokable method on [{}]", methodName, invokable);
		}
	}

}
