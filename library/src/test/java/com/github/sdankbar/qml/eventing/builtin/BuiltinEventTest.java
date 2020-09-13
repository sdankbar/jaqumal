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
package com.github.sdankbar.qml.eventing.builtin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import com.github.sdankbar.qml.eventing.builtin.RenderEvent.EventType;
import com.google.common.collect.ImmutableSet;

/**
 *
 *
 */
public class BuiltinEventTest {

	/**
	 *
	 *
	 */
	public class Processor implements BuiltinEventProcessor {

		private final Map<Class<?>, Integer> callcounts = new HashMap<>();

		@Override
		public void handle(final ButtonActivateEvent e) {
			callcounts.compute(e.getClass(),
					(k, v) -> v == null ? Integer.valueOf(1) : Integer.valueOf(v.intValue() + 1));
		}

		@Override
		public void handle(final ButtonClickEvent e) {
			callcounts.compute(e.getClass(),
					(k, v) -> v == null ? Integer.valueOf(1) : Integer.valueOf(v.intValue() + 1));
		}

		@Override
		public void handle(final MouseClickEvent e) {
			callcounts.compute(e.getClass(),
					(k, v) -> v == null ? Integer.valueOf(1) : Integer.valueOf(v.intValue() + 1));
		}

		@Override
		public void handle(final MouseWheelEvent e) {
			callcounts.compute(e.getClass(),
					(k, v) -> v == null ? Integer.valueOf(1) : Integer.valueOf(v.intValue() + 1));
		}

		@Override
		public void handle(final RenderEvent e) {
			callcounts.compute(e.getClass(),
					(k, v) -> v == null ? Integer.valueOf(1) : Integer.valueOf(v.intValue() + 1));
		}

		@Override
		public void handle(final TextInputAcceptedEvent e) {
			callcounts.compute(e.getClass(),
					(k, v) -> v == null ? Integer.valueOf(1) : Integer.valueOf(v.intValue() + 1));
		}

		@Override
		public void handle(final TextInputEditingFinishedEvent e) {
			callcounts.compute(e.getClass(),
					(k, v) -> v == null ? Integer.valueOf(1) : Integer.valueOf(v.intValue() + 1));
		}
	}

	/**
	 *
	 */
	@Test
	public void testButtonActivateEvent() {
		final ButtonActivateEvent e = new ButtonActivateEvent("A");
		assertEquals("A", e.getButtonName());

		final Processor p = new Processor();
		e.handle(p);
		assertEquals(1, p.callcounts.get(ButtonActivateEvent.class).intValue());
	}

	/**
	 *
	 */
	@Test
	public void testButtonClickEvent() {
		final ButtonClickEvent e = new ButtonClickEvent("A");
		assertEquals("A", e.getButtonName());

		final Processor p = new Processor();
		e.handle(p);
		assertEquals(1, p.callcounts.get(ButtonClickEvent.class).intValue());
	}

	/**
	 *
	 */
	@Test
	public void testKeyModifier() {
		assertEquals(ImmutableSet.of(), KeyModifier.fromMask(0));

		assertEquals(ImmutableSet.of(KeyModifier.SHIFT, KeyModifier.CONTROL), KeyModifier.fromMask(0x06000000));
	}

	/**
	 *
	 */
	@Test
	public void testMouseButton() {
		assertEquals(ImmutableSet.of(), MouseButton.fromMask(0));
		assertEquals(ImmutableSet.of(MouseButton.LEFT, MouseButton.RIGHT), MouseButton.fromMask(3));

		assertEquals(Optional.empty(), MouseButton.fromFlag(0));
		assertEquals(Optional.of(MouseButton.MIDDLE), MouseButton.fromFlag(4));
	}

	/**
	 *
	 */
	@Test
	public void testMouseClickEvent() {
		final MouseClickEvent e = new MouseClickEvent("A", 1, 2, 1, 1, 0, false);
		assertEquals("A", e.getObjectName());
		assertEquals(1, e.getX());
		assertEquals(2, e.getY());
		assertEquals(MouseButton.LEFT, e.getButton());
		assertEquals(ImmutableSet.of(MouseButton.LEFT), e.getButtons());
		assertEquals(ImmutableSet.of(), e.getModifiers());

		final Processor p = new Processor();
		e.handle(p);
		assertEquals(1, p.callcounts.get(MouseClickEvent.class).intValue());
	}

	/**
	 *
	 */
	@Test
	public void testMouseWheelEvent() {
		final MouseWheelEvent e = new MouseWheelEvent("A", 70, 71, 1, 0, 2, 3);
		assertEquals("A", e.getObjectName());
		assertEquals(2, e.getX());
		assertEquals(3, e.getY());
		assertEquals(ImmutableSet.of(MouseButton.LEFT), e.getButtons());
		assertEquals(ImmutableSet.of(), e.getModifiers());
		assertEquals(70, e.getAngleDeltaX());
		assertEquals(71, e.getAngleDeltaY());

		final Processor p = new Processor();
		e.handle(p);
		assertEquals(1, p.callcounts.get(MouseWheelEvent.class).intValue());
	}

	/**
	 *
	 */
	@Test
	public void testRenderEvent() {
		final RenderEvent e = new RenderEvent(EventType.AFTER_RENDER);
		assertNotNull(e.getEventTime());
		assertEquals(EventType.AFTER_RENDER, e.getType());

		final Processor p = new Processor();
		e.handle(p);
		assertEquals(1, p.callcounts.get(RenderEvent.class).intValue());
	}

	/**
	 *
	 */
	@Test
	public void testTextInputAcceptedEvent() {
		final TextInputAcceptedEvent e = new TextInputAcceptedEvent("A");
		assertEquals("A", e.getObjectName());

		final Processor p = new Processor();
		e.handle(p);
		assertEquals(1, p.callcounts.get(TextInputAcceptedEvent.class).intValue());
	}

	/**
	 *
	 */
	@Test
	public void testTextInputEditingFinishedEvent() {
		final TextInputEditingFinishedEvent e = new TextInputEditingFinishedEvent("A");
		assertEquals("A", e.getObjectName());

		final Processor p = new Processor();
		e.handle(p);
		assertEquals(1, p.callcounts.get(TextInputEditingFinishedEvent.class).intValue());
	}

}
