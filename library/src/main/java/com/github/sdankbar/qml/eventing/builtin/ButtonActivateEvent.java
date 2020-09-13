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

import java.util.Objects;

import com.github.sdankbar.qml.eventing.Event;

/**
 * Event that is generated when a QML DelayButton is activated.
 */
public class ButtonActivateEvent extends Event<BuiltinEventProcessor> {

	private final String buttonName;

	/**
	 * @param buttonName Name of the button that generated the Event.
	 */
	public ButtonActivateEvent(final String buttonName) {
		this.buttonName = Objects.requireNonNull(buttonName, "buttonName is null");
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
		final ButtonActivateEvent other = (ButtonActivateEvent) obj;
		if (buttonName == null) {
			if (other.buttonName != null) {
				return false;
			}
		} else if (!buttonName.equals(other.buttonName)) {
			return false;
		}
		return true;
	}

	/**
	 * @return The name of the button that generated this Event.
	 */
	public String getButtonName() {
		return buttonName;
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
		result = prime * result + ((buttonName == null) ? 0 : buttonName.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ButtonActivateEvent [buttonName=" + buttonName + "]";
	}

}
