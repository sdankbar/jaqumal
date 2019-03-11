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

/**
 * Represents an Event that has occurred in the application. Classes extend this
 * class to create specific event types. Events are generally generated by QML
 * and the Java listens to them by registering processor(s) for a type of Event.
 * An Event is passed to the registered processors in their priority order until
 * all processors have received the Event or the Event is consumed by one of the
 * processors.
 *
 * @param <T> The type of the processor that handles this Event.
 */
public abstract class Event<T> {

	private boolean isConsumed = false;

	/**
	 * Marks this Event as having been consumed.
	 *
	 */
	public void consume() {
		isConsumed = true;
	}

	/**
	 * Method to be implemented by subclasses that passes this Event to processor.
	 *
	 * @param processor The processor that will handle this Event.
	 */
	abstract public void handle(T processor);

	/**
	 * @return True if this Event has been consumed.
	 */
	public boolean isConsumed() {
		return isConsumed;
	}

}
