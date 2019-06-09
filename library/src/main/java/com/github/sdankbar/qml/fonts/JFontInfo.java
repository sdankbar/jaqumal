package com.github.sdankbar.qml.fonts;

import com.google.common.base.Preconditions;

public class JFontInfo {
	static JFontInfo fromString(final String serializedFormat) {
		final String[] tokens = serializedFormat.split(",");
		Preconditions.checkArgument(tokens.length == 12, "Not formatted into 12 tokens");
		return new JFontInfo(tokens[0], // Family
				Integer.parseInt(tokens[1]), // point size
				Integer.parseInt(tokens[2]), // pixel size
				"1".equals(tokens[3]), // bold
				"1".equals(tokens[4]), // exact match
				"1".equals(tokens[5]), // fixedPitch
				"1".equals(tokens[6]), // italic
				"1".equals(tokens[7]), // rawMode
				JFont.Style.fromValue(Integer.parseInt(tokens[8])), // Style
				JFont.StyleHint.fromValue(Integer.parseInt(tokens[9])), // Style Hint
				tokens[10], // Style Name
				Integer.parseInt(tokens[11])); // Weight
	}

	private final String family;
	private final int pointSize;
	private final int pixelSize;
	private final boolean bold;
	private final boolean exactMatch;
	private final boolean fixedPitch;
	private final boolean italic;
	private final boolean rawMode;
	private final JFont.Style style;
	private final JFont.StyleHint hint;
	private final String styleName;
	private final int weight;

	private JFontInfo(final String family, final int pointSize, final int pixelSize, final boolean bold,
			final boolean exactMatch, final boolean fixedPitch, final boolean italic, final boolean rawMode,
			final JFont.Style style, final JFont.StyleHint hint, final String styleName, final int weight) {
		this.family = family;
		this.pointSize = pointSize;
		this.pixelSize = pixelSize;
		this.bold = bold;
		this.exactMatch = exactMatch;
		this.fixedPitch = fixedPitch;
		this.italic = italic;
		this.rawMode = rawMode;
		this.style = style;
		this.hint = hint;
		this.styleName = styleName;
		this.weight = weight;
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
		final JFontInfo other = (JFontInfo) obj;
		if (bold != other.bold) {
			return false;
		}
		if (exactMatch != other.exactMatch) {
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
		if (hint != other.hint) {
			return false;
		}
		if (italic != other.italic) {
			return false;
		}
		if (pixelSize != other.pixelSize) {
			return false;
		}
		if (pointSize != other.pointSize) {
			return false;
		}
		if (rawMode != other.rawMode) {
			return false;
		}
		if (style != other.style) {
			return false;
		}
		if (styleName == null) {
			if (other.styleName != null) {
				return false;
			}
		} else if (!styleName.equals(other.styleName)) {
			return false;
		}
		if (weight != other.weight) {
			return false;
		}
		return true;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (bold ? 1231 : 1237);
		result = prime * result + (exactMatch ? 1231 : 1237);
		result = prime * result + ((family == null) ? 0 : family.hashCode());
		result = prime * result + (fixedPitch ? 1231 : 1237);
		result = prime * result + ((hint == null) ? 0 : hint.hashCode());
		result = prime * result + (italic ? 1231 : 1237);
		result = prime * result + pixelSize;
		result = prime * result + pointSize;
		result = prime * result + (rawMode ? 1231 : 1237);
		result = prime * result + ((style == null) ? 0 : style.hashCode());
		result = prime * result + ((styleName == null) ? 0 : styleName.hashCode());
		result = prime * result + weight;
		return result;
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

	/**
	 * @return the rawMode
	 */
	public boolean isRawMode() {
		return rawMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JFontInfo [family=" + family + ", pointSize=" + pointSize + ", pixelSize=" + pixelSize + ", bold="
				+ bold + ", exactMatch=" + exactMatch + ", fixedPitch=" + fixedPitch + ", italic=" + italic
				+ ", rawMode=" + rawMode + ", style=" + style + ", hint=" + hint + ", styleName=" + styleName
				+ ", weight=" + weight + "]";
	}
}
