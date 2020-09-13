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

import com.github.sdankbar.qml.eventing.Event;
import com.github.sdankbar.qml.eventing.EventFactory;
import com.github.sdankbar.qml.eventing.EventParser;
import com.github.sdankbar.qml.eventing.builtin.RenderEvent.EventType;

/**
 * Factory for building BuiltinEvents.
 */
public class BuiltinEventFactory implements EventFactory<BuiltinEventProcessor> {

	static private final EventType[] PERF_EVENT_ARRAY = EventType.values();

	@Override
	public Event<BuiltinEventProcessor> create(final String type, final EventParser parser) {
		switch (type) {
		case "Builtin-MouseClick":
			return new MouseClickEvent(parser.getString(), parser.getInteger(), parser.getInteger(),
					parser.getInteger(), parser.getInteger(), parser.getInteger(), parser.getBoolean());
		case "Builtin-MouseWheel":
			return new MouseWheelEvent(parser.getString(), parser.getInteger(), parser.getInteger(),
					parser.getInteger(), parser.getInteger(), parser.getInteger(), parser.getInteger());
		case "Builtin-ButtonClick":
			return new ButtonClickEvent(parser.getString());
		case "Builtin-TextInputAccepted":
			return new TextInputAcceptedEvent(parser.getString());
		case "Builtin-TextInputEditingFinished":
			return new TextInputEditingFinishedEvent(parser.getString());
		case "Builtin-PerformanceEvent":
			return new RenderEvent(PERF_EVENT_ARRAY[parser.getInteger()]);
		case "ListSelectionChangedEvent":
			return new ListSelectionChangedEvent(parser.getBoolean(), parser.getInteger(), parser.getString());
		default:
			return null;
		}
	}

}
