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

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.github.sdankbar.qml.cpp.jni.FontFunctions;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

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
		private int fontWeight = Weight.Normal.value;

		private Builder() {
			// Empty Implementation
		}

		/**
		 * @return The JFont for this builder's settings. May return an existing JFont.
		 */
		public JFont build() {
			return cache.getFont(this);
		}

		JFont privateBuild(final int fontIndex) {
			return new JFont(FontFunctions.getQFontToString(fontIndex, family, pointSize, pixelSize, bold, italic,
					overline, strikeout, underline, fixedPitch, kerning, fontWeight, wordSpacing, letterSpacing,
					letterSpacingType.value, capitalization.value, hintingPreference.value, stretch.value, style.value,
					styleName, styleHint.value, styleStrategyMask()), fontIndex);
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

		public Builder setWeight(final int w) {
			Preconditions.checkArgument(w >= 0, "Weight must be > 0");
			fontWeight = w;
			return this;
		}

		public Builder setWeight(final Weight w) {
			fontWeight = Objects.requireNonNull(w, "w is null").value;
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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (bold ? 1231 : 1237);
			result = prime * result + ((capitalization == null) ? 0 : capitalization.hashCode());
			result = prime * result + ((family == null) ? 0 : family.hashCode());
			result = prime * result + (fixedPitch ? 1231 : 1237);
			result = prime * result + fontWeight;
			result = prime * result + ((hintingPreference == null) ? 0 : hintingPreference.hashCode());
			result = prime * result + (italic ? 1231 : 1237);
			result = prime * result + (kerning ? 1231 : 1237);
			long temp;
			temp = Double.doubleToLongBits(letterSpacing);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result + ((letterSpacingType == null) ? 0 : letterSpacingType.hashCode());
			result = prime * result + (overline ? 1231 : 1237);
			result = prime * result + pixelSize;
			result = prime * result + pointSize;
			result = prime * result + ((stretch == null) ? 0 : stretch.hashCode());
			result = prime * result + (strikeout ? 1231 : 1237);
			result = prime * result + ((style == null) ? 0 : style.hashCode());
			result = prime * result + ((styleHint == null) ? 0 : styleHint.hashCode());
			result = prime * result + ((styleName == null) ? 0 : styleName.hashCode());
			result = prime * result + ((styleStrategy == null) ? 0 : styleStrategy.hashCode());
			result = prime * result + (underline ? 1231 : 1237);
			temp = Double.doubleToLongBits(wordSpacing);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

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
			final Builder other = (Builder) obj;
			if (bold != other.bold) {
				return false;
			}
			if (capitalization != other.capitalization) {
				return false;
			}
			if (family == null) {
				if (other.family != null) {
					return false;
				}
			} else if (!family.equals(other.family)) {
				return false;
			}
			if (fixedPitch != other.fixedPitch) {
				return false;
			}
			if (fontWeight != other.fontWeight) {
				return false;
			}
			if (hintingPreference != other.hintingPreference) {
				return false;
			}
			if (italic != other.italic) {
				return false;
			}
			if (kerning != other.kerning) {
				return false;
			}
			if (Double.doubleToLongBits(letterSpacing) != Double.doubleToLongBits(other.letterSpacing)) {
				return false;
			}
			if (letterSpacingType != other.letterSpacingType) {
				return false;
			}
			if (overline != other.overline) {
				return false;
			}
			if (pixelSize != other.pixelSize) {
				return false;
			}
			if (pointSize != other.pointSize) {
				return false;
			}
			if (stretch != other.stretch) {
				return false;
			}
			if (strikeout != other.strikeout) {
				return false;
			}
			if (style != other.style) {
				return false;
			}
			if (styleHint != other.styleHint) {
				return false;
			}
			if (styleName == null) {
				if (other.styleName != null) {
					return false;
				}
			} else if (!styleName.equals(other.styleName)) {
				return false;
			}
			if (styleStrategy == null) {
				if (other.styleStrategy != null) {
					return false;
				}
			} else if (!styleStrategy.equals(other.styleStrategy)) {
				return false;
			}
			if (underline != other.underline) {
				return false;
			}
			if (Double.doubleToLongBits(wordSpacing) != Double.doubleToLongBits(other.wordSpacing)) {
				return false;
			}
			return true;
		}

	}

	public enum Capitalization {
		MixedCase(0), AllUppercase(1), AllLowercase(2), SmallCaps(3), Capitalize(4);

		private int value;

		private Capitalization(final int v) {
			value = v;
		}
	}

	public enum HintingPreference {
		PreferDefaultHinting(0), PreferNoHinting(1), PreferVerticalHinting(2), PreferFullHinting(3);

		private int value;

		private HintingPreference(final int v) {
			value = v;
		}

	}

	public enum SpacingType {
		PercentageSpacing(0), AbsoluteSpacing(1);

		private int value;

		private SpacingType(final int v) {
			value = v;
		}
	}

	public enum Stretch {
		AnyStretch(0), UltraCondensed(50), ExtraCondensed(62), Condensed(75), SemiCondensed(87), Unstretched(100),
		SemiExpanded(112), Expanded(125), ExtraExpanded(150), UltraExpanded(200);

		private int value;

		private Stretch(final int v) {
			value = v;
		}

	}

	public enum Style {
		StyleNormal(0), StyleItalic(1), StyleOblique(2);

		static Style fromValue(final int v) {
			switch (v) {
			case 0:
				return StyleNormal;
			case 1:
				return StyleItalic;
			case 2:
				return StyleOblique;
			default:
				throw new IllegalArgumentException();
			}
		}

		private int value;

		private Style(final int v) {
			value = v;
		}

	}

	public enum StyleHint {
		AnyStyle(5), SansSerif(0), Helvetica(0), Serif(1), Times(1), TypeWriter(2), Courier(2), OldEnglish(3),
		Decorative(3), Monospace(7), Fantasy(8), Cursive(6), System(4);

		static StyleHint fromValue(final int v) {
			switch (v) {
			case 0:
				return SansSerif;
			case 1:
				return Serif;
			case 2:
				return Courier;
			case 3:
				return Decorative;
			case 4:
				return System;
			case 5:
				return AnyStyle;
			case 6:
				return Cursive;
			case 7:
				return Monospace;
			case 8:
				return Fantasy;
			default:
				throw new IllegalArgumentException();
			}
		}

		private int value;

		private StyleHint(final int v) {
			value = v;
		}

	}

	public enum StyleStrategy {
		PreferDefault(0x0001), PreferBitmap(0x0002), PreferDevice(0x0004), PreferOutline(0x0008), ForceOutline(0x0010),
		NoAntialias(0x0100), NoSubpixelAntialias(0x0800), PreferAntialias(0x0080), OpenGLCompatible(0x0200),
		NoFontMerging(0x8000), PreferNoShaping(0x1000), PreferMatch(0x0020), PreferQuality(0x0040),
		ForceIntegerMetrics(0x0400);

		private int value;

		private StyleStrategy(final int v) {
			value = v;
		}
	}

	public enum Weight {
		Thin(0), ExtraLight(12), Light(25), Normal(50), Medium(57), DemiBold(63), Bold(75), ExtraBold(81), Black(87);

		private final int value;

		private Weight(final int v) {
			value = v;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public static JFont fromString(final String str) {
		Objects.requireNonNull(str, "str is null");

		if (str.matches("\\w*,-?\\d+,-?\\d+,-?\\d+,-?\\d+,-?\\d+,-?\\d+,-?\\d+,-?\\d+,-?\\d+")) {
			return new JFont(str, -1);
		} else {
			throw new IllegalArgumentException(str + " is not a valid font string");
		}
	}

	private static final JFontCache cache = new JFontCache();

	private final String fontToString;

	private final String family;// 0
	private final int pointSize;// 1
	private final int pixelSize;// 2
	private final StyleHint styleHint;// 3
	private final int weight;// 4
	private final Style style;// 5
	private final boolean underline;// 6
	private final boolean strikeOut;// 7
	private final boolean fixedPitch;// 8
	private final Optional<String> styleName;

	private final int fontIndex;
	private JFontInfo cachedInfo = null;
	private JFontMetrics cachedMetrics = null;

	private JFont(final String toStr, final int fontIndex) {
		fontToString = toStr;
		this.fontIndex = fontIndex;
		final String[] tokens = fontToString.split(",");
		Preconditions.checkArgument(tokens.length == 10 || tokens.length == 11,
				"FontToString is not 10 or 11 comma separated values");
		family = tokens[0];
		pointSize = Integer.parseInt(tokens[1]);
		pixelSize = Integer.parseInt(tokens[2]);
		styleHint = StyleHint.fromValue(Integer.parseInt(tokens[3]));
		weight = Integer.parseInt(tokens[4]);
		style = Style.fromValue(Integer.parseInt(tokens[5]));
		underline = tokens[6].equals("1");
		strikeOut = tokens[7].equals("1");
		fixedPitch = tokens[8].equals("1");
		// token[9] always false
		if (tokens.length == 11) {
			styleName = Optional.of(tokens[10]);
		} else {
			styleName = Optional.empty();
		}
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

	public String getFamily() {
		return family;
	}

	public JFontInfo getJFontInfo() {
		if (cachedInfo == null) {
			cachedInfo = JFontInfo.fromString(FontFunctions.getQFontInfo(fontToString));
		}
		return cachedInfo;
	}

	public JFontMetrics getJFontMetrics() {
		if (cachedMetrics == null) {
			cachedMetrics = JFontMetrics.from(fontToString, FontFunctions.getQFontMetrics(fontToString));
		}
		return cachedMetrics;
	}

	public int getPixelSize() {
		return pixelSize;
	}

	public int getPointSize() {
		return pointSize;
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

	public boolean isMonospacedFont() {
		return getJFontMetrics().isMonospacedFont();
	}

	public Builder toBuilder() {
		final Builder b = new Builder();
		b.setFamily(family);// 0
		if (pointSize > 0) {
			b.setPointSize(pointSize); // 1
		} else {
			b.setPixelSize(pixelSize); // 2
		}
		b.setStyleHint(styleHint, ImmutableSet.of()); // 3
		b.setWeight(weight); // 4
		b.setStyle(style);// 5
		b.setUnderline(underline);// 6
		b.setStrikeout(strikeOut);// 7
		b.setFixedPitch(fixedPitch);// 8
		if (styleName.isPresent()) {
			b.setStyleName(styleName.get());// 10
		}
		return b;
	}

	/**
	 * @return This JFont's index. Can be used for fast lookups. Guaranteed to be
	 *         unique for a given set of font settings.
	 */
	public int getFontIndex() {
		return fontIndex;
	}

	@Override
	public String toString() {
		return fontToString;
	}
}
