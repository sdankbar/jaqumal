package com.github.sdankbar.qml;

import java.util.Objects;

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

		private Weight fontWeight = Weight.Normal;

		private Builder() {

		}

		public JFont build() {
			return new JFont(ApiInstance.LIB_INSTANCE.getQFontToString(family, pointSize, pixelSize, bold, italic,
					overline, strikeout, underline, fontWeight.getValue()));
		}

		public Builder setBold(final boolean b) {
			bold = b;
			return this;
		}

		// TODO add setCapitalization
		// setFixedPitch
		// set hinitingPreference
		// setKerning
		// setLeterSpacing
		// setOverline
		// setStretch
		// setStyle
		// setStyleHint
		// setStyleName
		// setStyleStrategy
		// setWordSpacing

		public Builder setFamily(final String family) {
			this.family = Objects.requireNonNull(family, "family is null");
			return this;
		}

		public Builder setItalic(final boolean b) {
			italic = b;
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

		public Builder setStrikeout(final boolean b) {
			strikeout = b;
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

	}

	public static enum Capitalization {
		MixedCase, AllUppercase, AllLowercase, SmallCaps, Capitalize
	}

	public static enum HintingPreference {
		PreferDefaultHinting, PreferNoHinting, PreferVerticalHinting, PreferFullHinting
	}

	public static enum SpacingType {
		PercentageSpacing, AbsoluteSpacing
	}

	public static enum Stretch {
		AnyStretch, UltraCondensed, ExtraCondensed, Condensed, SemiCondensed, Unstretched, SemiExpanded, Expanded,
		ExtraExpanded, UltraExpanded
	}

	public static enum Style {
		StyleNormal, StyleItalic, StyleOblique
	}

	public static enum StyleHint {
		AnyStyle, SansSerif, Helvetica, Serif, Times, TypeWriter, Courier, OldEnglish, Decorative, Monospace, Fantasy,
		Cursive, System
	}

	public static enum StyleStrategy {
		PreferDefault, PreferBitmap, PreferDevice, PreferOutline, ForceOutline, NoAntialias, NoSubpixelAntialias,
		PreferAntialias, OpenGLCompatible, NoFontMerging, PreferNoShaping, PreferMatch, PreferQuality,
		ForceIntegerMetrics
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
