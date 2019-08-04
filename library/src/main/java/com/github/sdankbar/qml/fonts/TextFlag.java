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
