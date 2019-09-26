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
package com.github.sdankbar.qml.fonts;

import java.util.Collection;
import java.util.Objects;

public enum TextFlag {
	TextSingleLine(0x0100), //
	TextDontClip(0x0200), //
	TextExpandTabs(0x0400), //
	TextShowMnemonic(0x0800), //
	TextWordWrap(0x1000), //
	TextWrapAnywhere(0x2000), //
	TextHideMnemonic(0x8000), //
	TextDontPrint(0x4000), //
	TextIncludeTrailingSpaces(0x08000000), //
	IncludeTrailingSpaces(TextFlag.TextIncludeTrailingSpaces.flag), //
	TextJustificationForced(0x10000);

	public static int setToFlags(final Collection<TextFlag> set) {
		Objects.requireNonNull(set, "set is null");
		int combinedFlag = 0;
		for (final TextFlag t : set) {
			Objects.requireNonNull(t, "Collection contains null");
			combinedFlag = combinedFlag | t.flag;
		}
		return combinedFlag;
	}

	private final int flag;

	private TextFlag(final int flag) {
		this.flag = flag;
	}

	public int getFlagValue() {
		return flag;
	}
}
