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
package com.github.sdankbar.examples.color_editor;

import java.util.Objects;

import com.github.sdankbar.examples.color_editor.App.EventProcessor;

import com.github.sdankbar.qml.eventing.Event;

/**
 * Event is fired when a color name is changed.
 */
public class PresetColorNameEditedEvent extends Event<EventProcessor> {

	private final int index;
	private final String newName;

	/**
	 * @param index Index into the color model.
	 * @param name  New color name.
	 */
	public PresetColorNameEditedEvent(final int index, final String name) {
		this.index = index;
		newName = Objects.requireNonNull(name, "name is null");
	}

	/**
	 * @return Index into the color model.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return The new color name.
	 */
	public String getNewName() {
		return newName;
	}

	@Override
	public void handle(final EventProcessor processor) {
		processor.handle(this);
	}

}
