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

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.jni.EventFunctions;
import com.github.sdankbar.qml.eventing.builtin.BuiltinEventProcessor;
import com.google.common.collect.ImmutableList;

/**
 * Handles receiving events and sending them to the registered listeners.
 *
 * @param <T> The type that handles the events this dispatcher routes.
 */
public class EventDispatcher<T> {

	static private class ProcessorPair<T> implements Comparable<ProcessorPair<T>> {
		public final T processor;
		public final int order;

		public ProcessorPair(final T p, final int o) {
			processor = p;
			order = o;
		}

		@Override
		public int compareTo(final ProcessorPair<T> arg0) {
			return Integer.compare(order, arg0.order);
		}

	}

	private static final Logger log = LoggerFactory.getLogger(EventDispatcher.class);

	private static final int DEFAULT_ORDER = 0;

	private static <T> ImmutableList<Class<? extends Event<T>>> getHandledEvents(final Class<T> processorClass) {
		final ImmutableList.Builder<Class<? extends Event<T>>> list = ImmutableList.builder();
		for (final Method m : processorClass.getDeclaredMethods()) {
			final Optional<Class<? extends Event<T>>> e = isSingleEventParameterMethod(m);
			if (e.isPresent()) {
				list.add(e.get());
			}
		}
		return list.build();
	}

	private static <P> Optional<JVariant> handle(final Event<P> e, final List<ProcessorPair<P>> list) {
		try {
			for (final ProcessorPair<P> p : list) {
				if (e.isConsumed()) {
					break;
				} else {
					e.handle(p.processor);
				}
			}

			if (!e.isConsumed() && QMLReceivableEvent.class.isInstance(e)) {
				final QMLReceivableEvent<P> castEvent = (QMLReceivableEvent<P>) e;
				final Map<String, JVariant> args = castEvent.getParameters();
				final String[] keys = new String[args.size()];
				int i = 0;
				for (final Entry<String, JVariant> entry : args.entrySet()) {
					keys[i] = entry.getKey();
					entry.getValue().sendToQML(i);
					++i;
				}
				EventFunctions.sendQMLEvent(castEvent.getClass().getSimpleName(), keys);
			}
		} catch (final Exception excp) {
			log.warn("Exception caught processing event " + e, excp);
		}

		return e.getResult();
	}

	@SuppressWarnings("unchecked")
	static <T> Optional<Class<? extends Event<T>>> isSingleEventParameterMethod(final Method m) {
		final Parameter[] params = m.getParameters();
		if (params.length == 1) {
			for (final Parameter p : params) {
				if (Event.class.isAssignableFrom(p.getType())) {
					return Optional.of((Class<? extends Event<T>>) p.getType());
				}
			}
			return Optional.empty();
		} else {
			return Optional.empty();
		}
	}

	private final Map<Class<? extends Event<T>>, List<ProcessorPair<T>>> processors = new HashMap<>();

	private final Map<Class<? extends Event<BuiltinEventProcessor>>, List<ProcessorPair<BuiltinEventProcessor>>> builtInProcessors = new HashMap<>();

	/**
	 * Registers a processor for built in events of Class type. Order is the default
	 * order (0).
	 *
	 * @param type      The Class for the types of Events the processor will
	 *                  receive.
	 * @param processor The processor to send Events of Class type to.
	 */
	public void register(final Class<? extends Event<BuiltinEventProcessor>> type,
			final BuiltinEventProcessor processor) {
		synchronized (builtInProcessors) {
			final List<ProcessorPair<BuiltinEventProcessor>> list = builtInProcessors.computeIfAbsent(type,
					k -> new ArrayList<>());

			list.add(new ProcessorPair<>(processor, DEFAULT_ORDER));
			Collections.sort(list);
		}
	}

	/**
	 * Registers a processor for built in events of Class type.
	 *
	 * @param type      The Class for the types of Events the processor will
	 *                  receive.
	 * @param processor The processor to send Events of Class type to.
	 * @param order     Specifies the relative order that processors registered for
	 *                  the same Event type will be called. Smaller values are
	 *                  called before processors with larger order values.
	 */
	public void register(final Class<? extends Event<BuiltinEventProcessor>> type,
			final BuiltinEventProcessor processor, final int order) {
		synchronized (builtInProcessors) {
			final List<ProcessorPair<BuiltinEventProcessor>> list = builtInProcessors.computeIfAbsent(type,
					k -> new ArrayList<>());

			list.add(new ProcessorPair<>(processor, order));
			Collections.sort(list);
		}
	}

	/**
	 * Registers a processor for events of Class type. Order is the default order
	 * (0).
	 *
	 * @param type      The Class for the types of Events the processor will
	 *                  receive.
	 * @param processor The processor to send Events of Class type to.
	 */
	public void register(final Class<? extends Event<T>> type, final T processor) {
		synchronized (processors) {
			final List<ProcessorPair<T>> list = processors.computeIfAbsent(type, k -> new ArrayList<>());

			list.add(new ProcessorPair<>(processor, DEFAULT_ORDER));
			Collections.sort(list);
		}
	}

	/**
	 * Registers a processor for events of Class type.
	 *
	 * @param type      The Class for the types of Events the processor will
	 *                  receive.
	 * @param processor The processor to send Events of Class type to.
	 * @param order     Specifies the relative order that processors registered for
	 *                  the same Event type will be called. Smaller values are
	 *                  called before processors with larger order values.
	 */
	public void register(final Class<? extends Event<T>> type, final T processor, final int order) {
		synchronized (processors) {
			final List<ProcessorPair<T>> list = processors.computeIfAbsent(type, k -> new ArrayList<>());

			list.add(new ProcessorPair<>(processor, order));
			Collections.sort(list);
		}
	}

	/**
	 * Registers a processor for all built in events that the processor has declared
	 * methods to handle. Order is the default order (0).
	 *
	 * @param processor The processor to send Events of Class type to.
	 */
	@SuppressWarnings("unchecked")
	public void registerAll(final BuiltinEventProcessor processor) {
		synchronized (builtInProcessors) {
			for (final Class<? extends Event<BuiltinEventProcessor>> type : getHandledEvents(
					(Class<BuiltinEventProcessor>) processor.getClass())) {
				final List<ProcessorPair<BuiltinEventProcessor>> list = builtInProcessors.computeIfAbsent(type,
						k -> new ArrayList<>());

				list.add(new ProcessorPair<>(processor, DEFAULT_ORDER));
				Collections.sort(list);
			}
		}
	}

	/**
	 * Registers a processor for events of all types that the processor has declared
	 * methods to handle. Order is the default order (0).
	 *
	 * @param processor The processor to send Events of Class type to.
	 */
	@SuppressWarnings("unchecked")
	public void registerAll(final T processor) {
		synchronized (processors) {
			for (final Class<? extends Event<T>> type : getHandledEvents((Class<T>) processor.getClass())) {
				final List<ProcessorPair<T>> list = processors.computeIfAbsent(type, k -> new ArrayList<>());

				list.add(new ProcessorPair<>(processor, DEFAULT_ORDER));
				Collections.sort(list);
			}
		}
	}

	/**
	 * Submits an Event to be dispatched to processors.
	 *
	 * @param e Event to dispatch.
	 *
	 * @return Optional result of the event.
	 */
	public Optional<JVariant> submit(final Event<T> e) {
		List<ProcessorPair<T>> list;
		synchronized (processors) {
			list = processors.getOrDefault(e.getClass(), ImmutableList.of());
		}
		return handle(e, list);
	}

	/**
	 * Submits a built in Event to be dispatched to processors.
	 *
	 * @param e Event to dispatch.
	 *
	 * @return Optional result of the event.
	 */
	public Optional<JVariant> submitBuiltin(final Event<BuiltinEventProcessor> e) {
		List<ProcessorPair<BuiltinEventProcessor>> list;
		synchronized (builtInProcessors) {
			list = builtInProcessors.getOrDefault(e.getClass(), ImmutableList.of());
		}
		return handle(e, list);
	}
}
