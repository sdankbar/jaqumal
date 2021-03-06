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
package com.github.sdankbar.qml.eventing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.exceptions.QMLException;
import com.github.sdankbar.qml.utility.QMLRequestParser;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

/**
 * EventFactory implementation that uses Reflection to construct events.
 * Requires that event name passed from QML matches the simple name of the event
 * class and that the order of the fields sent from QML matches the order of the
 * arguments on the class's single constructor.
 *
 * Supports Class's whose single Constructor takes the follow types as
 * parameters:
 *
 * - boolean/Boolean - Color - Dimension - double/Double - float/Float - Instant
 * - int/Integer - long/Long - Point2D - Rectangle2D - String
 *
 * @param <T> EventProcessor type.
 */
public class ReflectiveEventFactory<T> implements EventFactory<T> {

	private static final Logger logger = LoggerFactory.getLogger(ReflectiveEventFactory.class);
	private static Set<Class<?>> ALLOWED_PARAM_TYPE_SET = ImmutableSet.of(boolean.class, Boolean.class, Color.class,
			Dimension.class, double.class, Double.class, float.class, Float.class, Instant.class, int.class,
			Integer.class, long.class, Long.class, Point2D.class, Rectangle2D.class, String.class);

	/**
	 * Given an interface containing functions that take Event&lt;T&gt; subclasses as
	 * arguments, returns a ReflectiveEventFactory that can construct all of the
	 * Event types that are handled by that interface and are supported by
	 * ReflectiveEventFactory.
	 *
	 * @param interfaceClass Interface class that handles Events.
	 * @return ReflectiveEventFactory that constructs Events of the types handled by
	 *         interfaceClass.
	 */
	public static <T> ReflectiveEventFactory<T> createFromInterface(final Class<T> interfaceClass) {
		Objects.requireNonNull(interfaceClass, "interfaceClass is null");
		Preconditions.checkArgument(interfaceClass.isInterface(), "Is not an interface: {}", interfaceClass);

		final MethodHandles.Lookup lookup = MethodHandles.publicLookup();

		final List<Class<? extends Event<T>>> classList = new ArrayList<>();
		for (final Method m : interfaceClass.getDeclaredMethods()) {
			final Optional<Class<? extends Event<T>>> type = EventDispatcher.isSingleEventParameterMethod(m);
			if (type.isPresent() && isSupportedConstructor(type.get(), lookup)) {
				classList.add(type.get());
			}
		}

		return new ReflectiveEventFactory<>(classList);
	}

	private static <T> MethodHandle findConstructor(final Class<? extends Event<T>> c,
			final MethodHandles.Lookup lookup) {
		try {
			Preconditions.checkArgument(c.getConstructors().length == 1, "Class must have exactly 1 constructor: ", c);
			final MethodHandle h = lookup.unreflectConstructor(c.getConstructors()[0]);

			if (h.type().parameterCount() == 1) {
				final Class<?> arg = h.type().parameterType(0);
				Preconditions.checkArgument(ALLOWED_PARAM_TYPE_SET.contains(arg) || arg.equals(QMLRequestParser.class),
						"Class not supported as constructor parameter: {}", arg);
			} else {
				for (final Class<?> arg : h.type().parameterList()) {
					Preconditions.checkArgument(ALLOWED_PARAM_TYPE_SET.contains(arg),
							"Class not supported as constructor parameter: {}", arg);
				}
			}

			return h;
		} catch (IllegalAccessException | SecurityException | IllegalArgumentException e) {
			throw new QMLException("Unable to reflect constructor for " + c, e);
		}
	}

	private static <T> boolean isSupportedConstructor(final Class<? extends Event<T>> c,
			final MethodHandles.Lookup lookup) {
		try {
			findConstructor(c, lookup);
			return true;
		} catch (final QMLException e) {
			return false;
		}
	}

	private final Map<String, MethodHandle> constructorLookup = new HashMap<>();

	/**
	 * Creates a new ReflectiveEventFactory
	 *
	 * @param c1 Class to use reflection to construct.
	 */
	public ReflectiveEventFactory(final Class<? extends Event<T>> c1) {
		Objects.requireNonNull(c1, "c1 is null");
		final MethodHandles.Lookup lookup = MethodHandles.publicLookup();

		constructorLookup.put(c1.getSimpleName(), findConstructor(c1, lookup));
	}

	/**
	 * Creates a new ReflectiveEventFactory
	 *
	 * @param classList List of Classes to use reflection to construct.
	 */
	public ReflectiveEventFactory(final List<Class<? extends Event<T>>> classList) {
		Objects.requireNonNull(classList, "classList is null");
		final MethodHandles.Lookup lookup = MethodHandles.publicLookup();

		for (final Class<? extends Event<T>> c : classList) {
			Objects.requireNonNull(c, "classList contains null");
			constructorLookup.put(c.getSimpleName(), findConstructor(c, lookup));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Event<T> create(final String type, final QMLRequestParser parser) {
		final MethodHandle h = constructorLookup.get(type);
		if (h == null) {
			return null;
		} else {
			final List<Object> parameterList = new ArrayList<>(h.type().parameterList().size());
			for (final Class<?> argClass : h.type().parameterList()) {
				parameterList.add(parser.getDataBasedOnClass(argClass));
			}
			try {
				return (Event<T>) h.invokeWithArguments(parameterList);
			} catch (final Throwable e) {
				logger.warn("Caught Throwable while invoking constructor", e);
				return null;
			}
		}
	}

}
