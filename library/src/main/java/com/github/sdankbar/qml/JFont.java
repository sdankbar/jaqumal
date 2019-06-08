package com.github.sdankbar.qml;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import com.github.sdankbar.qml.cpp.ApiInstance;
import com.google.common.base.Preconditions;

public class JFont {

	public static class Builder {
		private String family = "";
		private int pointSize = -1;
		private int pixelSize = -1;

		private boolean bold = false;
		private boolean italic = false;
		private boolean overline = false;
		private boolean strikeout = false;
		private boolean underline = false;
		private boolean fixedPitch = false;
		private boolean kerning = false;

		private double wordSpacing = 0;

		private double letterSpacing = 0;
		private SpacingType letterSpacingType = SpacingType.AbsoluteSpacing;

		private Capitalization capitalization = Capitalization.MixedCase;
		private HintingPreference hintingPreference = HintingPreference.PreferNoHinting;
		private Stretch stretch = Stretch.Unstretched;
		private Style style = Style.StyleNormal;
		private String styleName = "";
		private StyleHint styleHint = StyleHint.AnyStyle;
		private final EnumSet<StyleStrategy> styleStrategy = EnumSet.of(StyleStrategy.PreferDefault);
		private Weight fontWeight = Weight.Normal;

		private Builder() {
			// Empty Implementation
		}

		public JFont build() {
			return new JFont(ApiInstance.LIB_INSTANCE.getQFontToString(family, pointSize, pixelSize, bold, italic,
					overline, strikeout, underline, fixedPitch, kerning, fontWeight.getValue(), wordSpacing,
					letterSpacing, letterSpacingType.value, capitalization.value, hintingPreference.value,
					stretch.value, style.value, styleName, styleHint.value, styleStrategyMask()));
		}

		public Builder setBold(final boolean b) {
			bold = b;
			return this;
		}

		public Builder setCapitalization(final Capitalization c) {
			capitalization = Objects.requireNonNull(c, "c is null");
			return this;
		}

		public Builder setFamily(final String family) {
			this.family = Objects.requireNonNull(family, "family is null");
			return this;
		}

		public Builder setFixedPitch(final boolean fixedPitch) {
			this.fixedPitch = fixedPitch;
			return this;
		}

		public Builder setHintingPreference(final HintingPreference h) {
			hintingPreference = Objects.requireNonNull(h, "h is null");
			return this;
		}

		public Builder setItalic(final boolean b) {
			italic = b;
			return this;
		}

		public Builder setKerning(final boolean kerning) {
			this.kerning = kerning;
			return this;
		}

		public Builder setLetterSpacing(final SpacingType t, final double s) {
			letterSpacingType = Objects.requireNonNull(t, "t is null");
			letterSpacing = s;
			return this;
		}

		public Builder setOverline(final boolean b) {
			overline = b;
			return this;
		}

		public Builder setPixelSize(final int size) {
			Preconditions.checkArgument(size > 0, "size must be > 0");
			pointSize = -1;
			pixelSize = size;
			return this;
		}

		public Builder setPointSize(final int size) {
			Preconditions.checkArgument(size > 0, "size must be > 0");
			pointSize = size;
			pixelSize = -1;
			return this;
		}

		public Builder setStretch(final Stretch s) {
			stretch = Objects.requireNonNull(s, "s is null");
			return this;
		}

		public Builder setStrikeout(final boolean b) {
			strikeout = b;
			return this;
		}

		public Builder setStyle(final Style s) {
			style = Objects.requireNonNull(s, "s is null");
			return this;
		}

		public Builder setStyleHint(final StyleHint h, final Set<StyleStrategy> s) {
			styleHint = Objects.requireNonNull(h, "h is null");
			styleStrategy.clear();
			styleStrategy.addAll(Objects.requireNonNull(s, "s is null"));
			return this;
		}

		public Builder setStyleName(final String n) {
			styleName = Objects.requireNonNull(n, "n is null");
			return this;
		}

		public Builder setUnderline(final boolean b) {
			underline = b;
			return this;
		}

		public Builder setWeight(final Weight w) {
			fontWeight = Objects.requireNonNull(w, "w is null");
			return this;
		}

		public Builder setWordSpacing(final double s) {
			wordSpacing = s;
			return this;
		}

		private int styleStrategyMask() {
			int mask = 0;
			for (final StyleStrategy s : styleStrategy) {
				mask = mask | s.value;
			}
			return mask;
		}

	}

	public static enum Capitalization {
		MixedCase(0), AllUppercase(1), AllLowercase(2), SmallCaps(3), Capitalize(4);

		private int value;

		private Capitalization(final int v) {
			value = v;
		}

		public int getValue() {
			return value;
		}
	}

	public static enum HintingPreference {
		PreferDefaultHinting(0), PreferNoHinting(1), PreferVerticalHinting(2), PreferFullHinting(3);

		private int value;

		private HintingPreference(final int v) {
			value = v;
		}

		public int getValue() {
			return value;
		}
	}

	public static enum SpacingType {
		PercentageSpacing(0), AbsoluteSpacing(1);

		private int value;

		private SpacingType(final int v) {
			value = v;
		}

		public int getValue() {
			return value;
		}
	}

	public static enum Stretch {
		AnyStretch(0), UltraCondensed(50), ExtraCondensed(62), Condensed(75), SemiCondensed(87), Unstretched(100),
		SemiExpanded(112), Expanded(125), ExtraExpanded(150), UltraExpanded(200);

		private int value;

		private Stretch(final int v) {
			value = v;
		}

		public int getValue() {
			return value;
		}
	}

	public static enum Style {
		StyleNormal(0), StyleItalic(1), StyleOblique(2);

		private int value;

		private Style(final int v) {
			value = v;
		}

		public int getValue() {
			return value;
		}
	}

	public static enum StyleHint {
		AnyStyle(5), SansSerif(0), Helvetica(0), Serif(1), Times(1), TypeWriter(2), Courier(2), OldEnglish(3),
		Decorative(3), Monospace(7), Fantasy(8), Cursive(6), System(4);

		private int value;

		private StyleHint(final int v) {
			value = v;
		}

		public int getValue() {
			return value;
		}

	}

	public static enum StyleStrategy {
		PreferDefault(0x0001), PreferBitmap(0x0002), PreferDevice(0x0004), PreferOutline(0x0008), ForceOutline(0x0010),
		NoAntialias(0x0100), NoSubpixelAntialias(0x0800), PreferAntialias(0x0080), OpenGLCompatible(0x0200),
		NoFontMerging(0x8000), PreferNoShaping(0x1000), PreferMatch(0x0020), PreferQuality(0x0040),
		ForceIntegerMetrics(0x0400);

		private int value;

		private StyleStrategy(final int v) {
			value = v;
		}

		public int getValue() {
			return value;
		}
	}

	public static enum Weight {
		Thin(0), ExtraLight(12), Light(25), Normal(50), Medium(57), DemiBold(63), Bold(75), ExtraBold(81), Black(87);

		private final int value;

		private Weight(final int v) {
			value = v;
		}

		public int getValue() {
			return value;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public static JFont fromString(final String str) {
		Objects.requireNonNull(str, "str is null");

		if (str.matches("\\w*,-?\\d+,-?\\d+,-?\\d+,-?\\d+,-?\\d+,-?\\d+,-?\\d+,-?\\d+,-?\\d+")) {
			return new JFont(str);
		} else {
			throw new IllegalArgumentException(str + " is not a valid font string");
		}
	}

	private final String fontToString;

	private JFont(final String toStr) {
		fontToString = toStr;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final JFont other = (JFont) obj;
		if (fontToString == null) {
			if (other.fontToString != null) {
				return false;
			}
		} else if (!fontToString.equals(other.fontToString)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fontToString == null) ? 0 : fontToString.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return fontToString;
	}
}
