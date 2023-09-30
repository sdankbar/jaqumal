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
package com.github.sdankbar.qml.fonts;

import java.util.Collection;
import java.util.Objects;

public enum TextAlignment {

	AlignLeft(0x0001), //
	AlignRight(0x0002), //
	AlignHCenter(0x0004), //
	AlignJustify(0x0008), //

	AlignTop(0x0020), //
	AlignBottom(0x0040), //
	AlignVCenter(0x0080), //
	AlignBaseline(0x0100), //

	AlignCenter(AlignHCenter.flag | AlignVCenter.flag);

	public static int setToFlags(final Collection<TextAlignment> set) {
		Objects.requireNonNull(set, "set is null");
		int combinedFlag = 0;
		for (final TextAlignment t : set) {
			Objects.requireNonNull(t, "Collection contains null");
			combinedFlag = combinedFlag | t.flag;
		}
		return combinedFlag;
	}

	private final int flag;

	TextAlignment(final int flag) {
		this.flag = flag;
	}

	public int getFlagValue() {
		return flag;
	}
}
