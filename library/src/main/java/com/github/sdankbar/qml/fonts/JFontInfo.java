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

import java.util.Objects;

import com.google.common.base.Preconditions;

public class JFontInfo {

	static JFontInfo fromString(final String serializedFormat) {
		final String[] tokens = serializedFormat.split(",");
		Preconditions.checkArgument(tokens.length == 11, "Not formatted into 11 tokens (%s)", serializedFormat);
		return new JFontInfo(tokens[0], // Family
				Integer.parseInt(tokens[1]), // point size
				Integer.parseInt(tokens[2]), // pixel size
				"1".equals(tokens[3]), // bold
				"1".equals(tokens[4]), // exact match
				"1".equals(tokens[5]), // fixedPitch
				"1".equals(tokens[6]), // italic
				JFont.Style.fromValue(Integer.parseInt(tokens[7])), // Style
				JFont.StyleHint.fromValue(Integer.parseInt(tokens[8])), // Style Hint
				tokens[9], // Style Name
				Integer.parseInt(tokens[10])); // Weight
	}

	private final String family;
	private final int pointSize;
	private final int pixelSize;
	private final boolean bold;
	private final boolean exactMatch;
	private final boolean fixedPitch;
	private final boolean italic;
	private final JFont.Style style;
	private final JFont.StyleHint hint;
	private final String styleName;
	private final int weight;

	private JFontInfo(final String family, final int pointSize, final int pixelSize, final boolean bold,
			final boolean exactMatch, final boolean fixedPitch, final boolean italic, final JFont.Style style,
			final JFont.StyleHint hint, final String styleName, final int weight) {
		this.family = family;
		this.pointSize = pointSize;
		this.pixelSize = pixelSize;
		this.bold = bold;
		this.exactMatch = exactMatch;
		this.fixedPitch = fixedPitch;
		this.italic = italic;
		this.style = style;
		this.hint = hint;
		this.styleName = styleName;
		this.weight = weight;
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
		final JFontInfo other = (JFontInfo) obj;
		return bold == other.bold && exactMatch == other.exactMatch && Objects.equals(family, other.family)
				&& fixedPitch == other.fixedPitch && hint == other.hint && italic == other.italic
				&& pixelSize == other.pixelSize && pointSize == other.pointSize && style == other.style
				&& Objects.equals(styleName, other.styleName) && weight == other.weight;
	}

	/**
	 * @return the family
	 */
	public String getFamily() {
		return family;
	}

	/**
	 * @return the hint
	 */
	public JFont.StyleHint getHint() {
		return hint;
	}

	/**
	 * @return the pixelSize
	 */
	public int getPixelSize() {
		return pixelSize;
	}

	/**
	 * @return the pointSize
	 */
	public int getPointSize() {
		return pointSize;
	}

	/**
	 * @return the style
	 */
	public JFont.Style getStyle() {
		return style;
	}

	/**
	 * @return the styleName
	 */
	public String getStyleName() {
		return styleName;
	}

	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}

	@Override
	public int hashCode() {
		return Objects.hash(bold, exactMatch, family, fixedPitch, hint, italic, pixelSize, pointSize, style, styleName,
				weight);
	}

	/**
	 * @return the bold
	 */
	public boolean isBold() {
		return bold;
	}

	/**
	 * @return the exactMatch
	 */
	public boolean isExactMatch() {
		return exactMatch;
	}

	/**
	 * @return the fixedPitch
	 */
	public boolean isFixedPitch() {
		return fixedPitch;
	}

	/**
	 * @return the italic
	 */
	public boolean isItalic() {
		return italic;
	}

	@Override
	public String toString() {
		return "JFontInfo [family=" + family + ", pointSize=" + pointSize + ", pixelSize=" + pixelSize + ", bold="
				+ bold + ", exactMatch=" + exactMatch + ", fixedPitch=" + fixedPitch + ", italic=" + italic + ", style="
				+ style + ", hint=" + hint + ", styleName=" + styleName + ", weight=" + weight + "]";
	}
}
