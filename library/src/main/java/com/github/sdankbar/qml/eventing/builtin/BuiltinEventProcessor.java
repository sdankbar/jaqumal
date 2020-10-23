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

/**
 * Interface for classes wishing to handle BuiltingEvents to implement.
 */
public interface BuiltinEventProcessor {

	/**
	 * Called when a ButtonActivateEvent is generated.
	 *
	 * @param e The BuiltinEvent to handle.
	 */
	default void handle(final ButtonActivateEvent e) {
		// Empty Implementation
	}

	/**
	 * Called when a ButtonClickEvent is generated.
	 *
	 * @param e The BuiltinEvent to handle.
	 */
	default void handle(final ButtonClickEvent e) {
		// Empty Implementation
	}

	/**
	 * Called when a ListSelectionChangedEvent is generated.
	 *
	 * @param e The BuiltinEvent to handle.
	 */
	default void handle(final ListSelectionChangedEvent e) {
		// Empty Implementation
	}

	/**
	 * Called when a MouseClickEvent is generated.
	 *
	 * @param e The BuiltinEvent to handle.
	 */
	default void handle(final MouseClickEvent e) {
		// Empty Implementation
	}

	/**
	 * Called when a MouseWheelEvent is generated.
	 *
	 * @param e The BuiltinEvent to handle.
	 */
	default void handle(final MouseWheelEvent e) {
		// Empty Implementation
	}

	/**
	 * Called when a PerformanceEvent is generated.
	 *
	 * @param e The BuiltinEvent to handle.
	 */
	default void handle(final RenderEvent e) {
		// Empty Implementation
	}

	/**
	 * Called when a TextInputAcceptedEvent is generated.
	 *
	 * @param e The BuiltinEvent to handle.
	 */
	default void handle(final TextInputAcceptedEvent e) {
		// Empty Implementation
	}

	/**
	 * Called when a TextInputEditingFinishedEvent is generated.
	 *
	 * @param e The BuiltinEvent to handle.
	 */
	default void handle(final TextInputEditingFinishedEvent e) {
		// Empty Implementation
	}

	/**
	 * Sent to QML to cause a ListView to scroll.
	 *
	 * @param e The BuiltinEvent to handle.
	 */
	default void handle(final RequestScrollListToPositionEvent e) {
		// Empty Implementation
	}
}
