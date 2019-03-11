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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.sdankbar.qml.eventing.Event;
import com.github.sdankbar.qml.eventing.EventDispatcher;
import com.github.sdankbar.qml.eventing.builtin.BuiltinEventProcessor;
import com.github.sdankbar.qml.eventing.builtin.RenderEvent;
import com.github.sdankbar.qml.eventing.builtin.RenderEvent.EventType;

/**
 * Tests the EventDispatcher.
 */
public class EventDispatcherTest {

	/**
	 *
	 */
	public static class Event1 extends Event<EventProcessor> {

		@Override
		public void handle(final EventProcessor processor) {
			processor.handle(this);
		}

	}

	/**
	 *
	 */
	public static class Event2 extends Event<EventProcessor> {

		@Override
		public void handle(final EventProcessor processor) {
			processor.handle(this);
		}

	}

	/**
	 *
	 */
	public static class EventProcessor {

		private static int globalCount1 = 0;

		private int count1 = 0;
		private int count2 = 0;

		private boolean consume = false;
		private boolean throwException = false;

		/**
		 * @param e
		 */
		public void handle(final Event1 e) {
			++globalCount1;
			count1 = globalCount1;
			if (consume) {
				e.consume();
			} else if (throwException) {
				throw new NullPointerException();
			}
		}

		/**
		 * @param e
		 */
		public void handle(final Event2 e) {
			++count2;
		}
	}

	/**
	 *
	 */
	public static class TestBuiltinEventProcessor implements BuiltinEventProcessor {
		private static int globalCount1 = 0;

		private int count1 = 0;

		@Override
		public void handle(final RenderEvent e) {
			++globalCount1;
			count1 = globalCount1;
		}
	}

	/**
	 * @throws InterruptedException e
	 */
	@Test
	public void testConsume() throws InterruptedException {
		final EventDispatcher<EventProcessor> d = new EventDispatcher<>();
		final EventProcessor p1 = new EventProcessor();
		final EventProcessor p2 = new EventProcessor();

		p1.consume = true;

		d.register(Event1.class, p1);
		d.register(Event1.class, p2);

		EventProcessor.globalCount1 = 0;

		d.submit(new Event1());

		assertEquals(1, p1.count1);
		assertEquals(0, p2.count1);
	}

	/**
	 * @throws InterruptedException e
	 */
	@Test
	public void testRegisterBuiltinProcessor() throws InterruptedException {
		final EventDispatcher<EventProcessor> d = new EventDispatcher<>();
		final TestBuiltinEventProcessor p = new TestBuiltinEventProcessor();

		d.submitBuiltin(new RenderEvent(EventType.FRAME_SWAP));

		assertEquals(0, p.count1);

		d.register(RenderEvent.class, p);

		EventProcessor.globalCount1 = 0;

		d.submitBuiltin(new RenderEvent(EventType.FRAME_SWAP));

		assertEquals(1, p.count1);
	}

	/**
	 * @throws InterruptedException e
	 */
	@Test
	public void testRegisterBuiltinSorted() throws InterruptedException {
		final EventDispatcher<EventProcessor> d = new EventDispatcher<>();
		final TestBuiltinEventProcessor p1 = new TestBuiltinEventProcessor();
		final TestBuiltinEventProcessor p2 = new TestBuiltinEventProcessor();
		final TestBuiltinEventProcessor p3 = new TestBuiltinEventProcessor();

		d.register(RenderEvent.class, p1, 1);
		d.register(RenderEvent.class, p3, 3);
		d.register(RenderEvent.class, p2, 2);

		TestBuiltinEventProcessor.globalCount1 = 0;

		d.submitBuiltin(new RenderEvent(EventType.FRAME_SWAP));

		assertEquals(1, p1.count1);
		assertEquals(2, p2.count1);
		assertEquals(3, p3.count1);
	}

	/**
	 * @throws InterruptedException e
	 */
	@Test
	public void testRegisterProcessor() throws InterruptedException {
		final EventDispatcher<EventProcessor> d = new EventDispatcher<>();
		final EventProcessor p = new EventProcessor();

		d.submit(new Event1());
		d.submit(new Event2());

		assertEquals(0, p.count1);
		assertEquals(0, p.count2);

		d.register(Event1.class, p);
		d.register(Event2.class, p);

		EventProcessor.globalCount1 = 0;

		d.submit(new Event1());
		d.submit(new Event2());

		assertEquals(1, p.count1);
		assertEquals(1, p.count2);
	}

	/**
	 * @throws InterruptedException e
	 */
	@Test
	public void testRegisterSorted() throws InterruptedException {
		final EventDispatcher<EventProcessor> d = new EventDispatcher<>();
		final EventProcessor p1 = new EventProcessor();
		final EventProcessor p2 = new EventProcessor();
		final EventProcessor p3 = new EventProcessor();

		d.register(Event1.class, p1, 1);
		d.register(Event1.class, p3, 3);
		d.register(Event1.class, p2, 2);

		EventProcessor.globalCount1 = 0;

		d.submit(new Event1());

		assertEquals(1, p1.count1);
		assertEquals(2, p2.count1);
		assertEquals(3, p3.count1);
	}

	/**
	 * @throws InterruptedException e
	 */
	@Test
	public void testThrowException() throws InterruptedException {
		final EventDispatcher<EventProcessor> d = new EventDispatcher<>();
		final EventProcessor p1 = new EventProcessor();
		final EventProcessor p2 = new EventProcessor();

		p1.throwException = true;

		d.register(Event1.class, p1);
		d.register(Event1.class, p2);

		EventProcessor.globalCount1 = 0;

		d.submit(new Event1());

		assertEquals(1, p1.count1);
		assertEquals(0, p2.count1);
	}

}
