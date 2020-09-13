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
import java.util.Set;

/**
 * Enum for the various special keys that can be pressed to modify another key,
 * such as Shift.
 */
public enum KeyModifier {
	/**
	 * Modifier enum for the Shift key
	 */
	SHIFT(0x02000000),
	/**
	 * Modifier enum for the Ctrl key
	 */
	CONTROL(0x04000000),
	/**
	 * Modifier enum for the Alt key
	 */
	ALT(0x08000000),
	/**
	 * Modifier enum for the Meta key
	 */
	META(0x10000000),
	/**
	 * Modifier enum for a keypad key
	 */
	KEYPAD(0x20000000);

	static Set<KeyModifier> fromMask(final int mask) {
		final Set<KeyModifier> set = EnumSet.noneOf(KeyModifier.class);
		for (final KeyModifier b : KeyModifier.values()) {
			if ((b.mask & mask) != 0) {
				set.add(b);
			}
		}
		return set;
	}

	private final int mask;

	private KeyModifier(final int mask) {
		this.mask = mask;
	}
}