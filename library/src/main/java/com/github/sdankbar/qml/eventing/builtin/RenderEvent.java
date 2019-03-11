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
package com.github.sdankbar.qml.eventing.builtin;

import java.time.Instant;
import java.util.Objects;

import com.github.sdankbar.qml.eventing.Event;

/**
 * Event that is generated at each stage of rendering.
 */
public class RenderEvent extends Event<BuiltinEventProcessor> {

	/**
	 * Enumeration of the rendering stages.
	 *
	 * @see <a href=
	 *      "http://doc.qt.io/qt-5/qtquick-visualcanvas-scenegraph.html#scene-graph-and-rendering">QML
	 *      Scene Graph</a>
	 */
	public enum EventType {
		/**
		 * Beginning synchronization between GUI thread and render thread.
		 */
		BEFORE_SYNC, //
		/**
		 * Finished synchronization, beginning rendering.
		 */
		BEFORE_RENDER, //
		/**
		 * Finished rendering.
		 */
		AFTER_RENDER, //
		/**
		 * Swapped buffers/frame.
		 */
		FRAME_SWAP
	}

	private final EventType type;
	private final Instant eventTime;

	/**
	 * Create new Event.
	 *
	 * @param t The type of rendering event.
	 */
	public RenderEvent(final EventType t) {
		type = Objects.requireNonNull(t, "t is null");
		eventTime = Instant.now();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final RenderEvent other = (RenderEvent) obj;
		if (eventTime == null) {
			if (other.eventTime != null) {
				return false;
			}
		} else if (!eventTime.equals(other.eventTime)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	/**
	 * @return The time the event occurred.
	 */
	public Instant getEventTime() {
		return eventTime;
	}

	/**
	 * @return The type of rendering event.
	 */
	public EventType getType() {
		return type;
	}

	@Override
	public void handle(final BuiltinEventProcessor processor) {
		processor.handle(this);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventTime == null) ? 0 : eventTime.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RenderEvent [type=" + type + ", eventTime=" + eventTime + "]";
	}

}
