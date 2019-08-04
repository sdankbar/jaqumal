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
package com.github.sdankbar.qml.eventing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.ApiInstance;
import com.github.sdankbar.qml.cpp.memory.SharedJavaCppMemory;
import com.github.sdankbar.qml.eventing.builtin.BuiltinEventProcessor;

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

	static private final int DEFAULT_ORDER = 0;

	private static <P> void handle(final Event<P> e, final List<ProcessorPair<P>> list,
			final SharedJavaCppMemory javaToCppMemory) {
		try {
			for (final ProcessorPair<P> p : list) {
				if (e.isConsumed()) {
					break;
				} else {
					e.handle(p.processor);
				}
			}

			if (!e.isConsumed() && e.getClass().isInstance(QMLReceivableEvent.class)) {
				final QMLReceivableEvent<P> castEvent = (QMLReceivableEvent<P>) e;
				final Map<String, JVariant> args = castEvent.getParameters();
				final int argsCount = args.size();
				final String[] keys = args.keySet().toArray(new String[argsCount]);
				JVariant.serialize(new ArrayList<>(args.values()), javaToCppMemory);
				ApiInstance.LIB_INSTANCE.sendQMLEvent(castEvent.getClass().getSimpleName(), keys,
						javaToCppMemory.getPointer(), argsCount);
			}
		} catch (final Exception excp) {
			log.warn("Exception caught processing event " + e, excp);
		}
	}

	private final Map<Class<? extends Event<T>>, List<ProcessorPair<T>>> processors = new HashMap<>();

	private final Map<Class<? extends Event<BuiltinEventProcessor>>, List<ProcessorPair<BuiltinEventProcessor>>> builtInProcessors = new HashMap<>();

	private final SharedJavaCppMemory javaToCppMemory = new SharedJavaCppMemory(16 * 1024 * 1024);

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
					(k) -> new ArrayList<>());

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
					(k) -> new ArrayList<>());

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
			final List<ProcessorPair<T>> list = processors.computeIfAbsent(type, (k) -> new ArrayList<>());

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
			final List<ProcessorPair<T>> list = processors.computeIfAbsent(type, (k) -> new ArrayList<>());

			list.add(new ProcessorPair<>(processor, order));
			Collections.sort(list);
		}
	}

	/**
	 * Submits an Event to be dispatched to processors.
	 *
	 * @param e Event to dispatch.
	 */
	public void submit(final Event<T> e) {
		List<ProcessorPair<T>> list;
		synchronized (processors) {
			if (processors.containsKey(e.getClass())) {
				list = new ArrayList<>(processors.get(e.getClass()));
			} else {
				list = Collections.emptyList();
			}
		}

		handle(e, list, javaToCppMemory);
	}

	/**
	 * Submits a built in Event to be dispatched to processors.
	 *
	 * @param e Event to dispatch.
	 */
	public void submitBuiltin(final Event<BuiltinEventProcessor> e) {
		List<ProcessorPair<BuiltinEventProcessor>> list;
		synchronized (builtInProcessors) {
			if (builtInProcessors.containsKey(e.getClass())) {
				list = new ArrayList<>(builtInProcessors.get(e.getClass()));
			} else {
				list = Collections.emptyList();
			}
		}

		handle(e, list, javaToCppMemory);
	}
}
