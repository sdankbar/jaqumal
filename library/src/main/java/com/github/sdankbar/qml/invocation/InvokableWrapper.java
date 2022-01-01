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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.JInvokable;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.exceptions.QMLException;
import com.github.sdankbar.qml.fonts.JFont;
import com.github.sdankbar.qml.painting.JPoint;
import com.github.sdankbar.qml.painting.JPointReal;
import com.github.sdankbar.qml.painting.JRect;
import com.github.sdankbar.qml.painting.JRectReal;
import com.github.sdankbar.qml.utility.QMLRequestParser;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class InvokableWrapper {

	private static final Logger log = LoggerFactory.getLogger(InvokableWrapper.class);
	private static Set<Class<?>> ALLOWED_PARAM_TYPE_SET = ImmutableSet.of(boolean.class, Boolean.class, Color.class,
			Dimension.class, double.class, Double.class, float.class, Float.class, Instant.class, int.class,
			Integer.class, long.class, Long.class, Point2D.class, Rectangle2D.class, JPoint.class, JPointReal.class,
			JRect.class, JRectReal.class, String.class);

	private static Set<Class<?>> ALLOWED_RETURN_TYPE_SET = ImmutableSet.of(boolean.class, Boolean.class,
			BufferedImage.class, byte[].class, Color.class, Dimension.class, double.class, Double.class, float.class,
			Float.class, Instant.class, int.class, Integer.class, JFont.class, long.class, Long.class, Line2D.class,
			Pattern.class, Point2D.class, Rectangle2D.class, JPoint.class, JPointReal.class, JRect.class,
			JRectReal.class, String.class, URL.class, UUID.class, void.class, JVariant.class);

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

			Preconditions.checkArgument(ALLOWED_RETURN_TYPE_SET.contains(m.getReturnType()),
					"Class not supported as method return type: {}", m.getReturnType());

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

	public JVariant invoke(final String methodName, final QMLRequestParser parser) {
		final Method handle = handlesMap.get(methodName);

		Object ret = null;
		if (handle != null) {
			final Class<?>[] type = handle.getParameterTypes();
			final Object[] parameterList = new Object[type.length];

			for (int i = 0; i < type.length; ++i) {
				parameterList[i] = parser.getDataBasedOnClass(type[i]);
			}

			try {
				ret = handle.invoke(invokable, parameterList);
			} catch (final Throwable e) {
				log.warn("Caught Throwable while invoking method", e);
			}
		} else {
			log.warn("[{}] is not an invokable method on [{}]", methodName, invokable);
		}
		return toJVariant(ret);
	}

	private JVariant toJVariant(final Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof Boolean) {
			return new JVariant((Boolean) obj);
		} else if (obj instanceof BufferedImage) {
			return new JVariant((BufferedImage) obj);
		} else if (obj instanceof byte[]) {
			return new JVariant((byte[]) obj);
		} else if (obj instanceof Color) {
			return new JVariant((Color) obj);
		} else if (obj instanceof Dimension) {
			return new JVariant((Dimension) obj);
		} else if (obj instanceof Double) {
			return new JVariant((Double) obj);
		} else if (obj instanceof Float) {
			return new JVariant((Float) obj);
		}
		// ImmutableList<Point2D> not supported
		else if (obj instanceof Instant) {
			return new JVariant((Instant) obj);
		} else if (obj instanceof Integer) {
			return new JVariant((Integer) obj);
		} else if (obj instanceof JFont) {
			return new JVariant((JFont) obj);
		} else if (obj instanceof Line2D) {
			return new JVariant((Line2D) obj);
		} else if (obj instanceof Long) {
			return new JVariant((Long) obj);
		} else if (obj instanceof Pattern) {
			return new JVariant((Pattern) obj);
		} else if (obj instanceof Point2D) {
			return new JVariant((Point2D) obj);
		} else if (obj instanceof JPoint) {
			return new JVariant((JPoint) obj);
		} else if (obj instanceof JPointReal) {
			return new JVariant((JPointReal) obj);
		} else if (obj instanceof Rectangle2D) {
			return new JVariant((Rectangle2D) obj);
		} else if (obj instanceof JRect) {
			return new JVariant((JRect) obj);
		} else if (obj instanceof JRectReal) {
			return new JVariant((JRectReal) obj);
		} else if (obj instanceof String) {
			return new JVariant((String) obj);
		} else if (obj instanceof URL) {
			return new JVariant((URL) obj);
		} else if (obj instanceof UUID) {
			return new JVariant((UUID) obj);
		} else if (obj instanceof JVariant) {
			return (JVariant) obj;
		} else {
			return null;
		}
	}

}
