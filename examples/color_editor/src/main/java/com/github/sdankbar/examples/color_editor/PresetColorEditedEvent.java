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
package com.github.sdankbar.examples.color_editor;

import java.awt.Color;
import java.util.Objects;

import com.github.sdankbar.examples.color_editor.App.EventProcessor;

/**
 * Event is fired when QML edits a color.
 */
public class PresetColorEditedEvent extends com.github.sdankbar.qml.eventing.Event<EventProcessor> {

	private final int index;
	private final Color newColor;

	/**
	 * @param index The index in the color model.
	 * @param c     The new color.
	 */
	public PresetColorEditedEvent(final int index, final Color c) {
		this.index = index;
		newColor = Objects.requireNonNull(c, "c is null");
	}

	/**
	 * @return Index into the color model.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return The new color.
	 */
	public Color getNewColor() {
		return newColor;
	}

	@Override
	public void handle(final EventProcessor processor) {
		processor.handle(this);
	}

}
