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

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * Enum for the buttons on a mouse.
 */
public enum MouseButton {
	/**
	 * Enum for the left mouse button
	 */
	LEFT(1),
	/**
	 * Enum for the middle mouse button
	 */
	MIDDLE(4),
	/**
	 * Enum for the right mouse button
	 */
	RIGHT(2);

	/**
	 * Converts an integer flag into a MouseButton.
	 *
	 * @param flag The flag to convert.
	 * @return MouseButton for the flag, in an Optional, or Optional.empty() if
	 *         unable to convert.
	 */
	public static Optional<MouseButton> fromFlag(final int flag) {
		for (final MouseButton b : MouseButton.values()) {
			if (b.mask == flag) {
				return Optional.of(b);
			}
		}
		return Optional.empty();
	}

	/**
	 * Converts an integer mask into a Set of MouseButton.
	 *
	 * @param mask The mask to convert.
	 * @return A Set of MouseButtons containing all of the MouseButton's contained
	 *         in the mask.
	 */
	public static Set<MouseButton> fromMask(final int mask) {
		final Set<MouseButton> set = EnumSet.noneOf(MouseButton.class);
		for (final MouseButton b : MouseButton.values()) {
			if ((b.mask & mask) != 0) {
				set.add(b);
			}
		}
		return set;
	}

	private final int mask;

	MouseButton(final int mask) {
		this.mask = mask;
	}
}