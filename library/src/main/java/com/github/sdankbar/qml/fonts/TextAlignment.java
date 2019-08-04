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

	private TextAlignment(final int flag) {
		this.flag = flag;
	}

	public int getFlagValue() {
		return flag;
	}
}
