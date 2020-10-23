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

import java.util.Map;
import java.util.Objects;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.eventing.QMLReceivableEvent;
import com.google.common.collect.ImmutableMap;

/**
 * Event created to cause a QML ListView to scroll, ie call
 * positionViewAtIndex().
 */
public class RequestScrollListToPositionEvent extends QMLReceivableEvent<BuiltinEventProcessor> {

	/**
	 * Maps to QML ListView.PositionMode.
	 */
	public enum PositionMode {
		/**
		 * See ListView.Beginning in QML documentation
		 */
		Beginning,
		/**
		 * See ListView.Center in QML documentation
		 */
		Center,
		/**
		 * See ListView.End in QML documentation
		 */
		End,
		/**
		 * See ListView.Visible in QML documentation
		 */
		Visible,
		/**
		 * See ListView.Contain in QML documentation
		 */
		Contain,
		/**
		 * See ListView.SnapPosition in QML documentation
		 */
		SnapPosition;
	}

	private final String modelName;
	private final int index;
	private final PositionMode mode;

	/**
	 * Creates a new Event.
	 *
	 * @param modelName Name of the list model attached to the ListView to scroll.
	 * @param index     Index to scroll to.
	 * @param mode      PositionMode to use when scrolling.
	 *
	 */
	public RequestScrollListToPositionEvent(final String modelName, final int index, final PositionMode mode) {
		this.modelName = Objects.requireNonNull(modelName, "modelName is null");
		this.index = index;
		this.mode = Objects.requireNonNull(mode, "mode is null");
	}

	@Override
	public Map<String, JVariant> getParameters() {
		return ImmutableMap.of("model_name", new JVariant(modelName), "index", new JVariant(index), "position_mode",
				new JVariant(mode.ordinal()));
	}

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
		final RequestScrollListToPositionEvent other = (RequestScrollListToPositionEvent) obj;
		if (index != other.index) {
			return false;
		}
		if (mode != other.mode) {
			return false;
		}
		if (modelName == null) {
			if (other.modelName != null) {
				return false;
			}
		} else if (!modelName.equals(other.modelName)) {
			return false;
		}
		return true;
	}

	@Override
	public void handle(final BuiltinEventProcessor processor) {
		processor.handle(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + ((modelName == null) ? 0 : modelName.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "RequestScrollListToPositionEvent [modelName=" + modelName + ", index=" + index + ", mode=" + mode + "]";
	}

}
