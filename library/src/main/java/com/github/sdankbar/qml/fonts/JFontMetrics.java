package com.github.sdankbar.qml.fonts;

import java.awt.geom.Rectangle2D;
import java.util.Objects;

import com.github.sdankbar.qml.cpp.ApiInstance;
import com.google.common.base.Preconditions;
import com.sun.jna.Pointer;

public class JFontMetrics {

	static JFontMetrics from(final String fontToString, final String formattedString) {
		final String[] tokens = formattedString.split(",");
		Preconditions.checkArgument(tokens.length == 13, "Formatted strip does not contain 13 tokens");
		return new JFontMetrics(fontToString, //
				Integer.parseInt(tokens[0]), // ascent
				Integer.parseInt(tokens[1]), // averageCharWidth
				Integer.parseInt(tokens[2]), // descent
				Integer.parseInt(tokens[3]), // height
				Integer.parseInt(tokens[4]), // leading
				Integer.parseInt(tokens[5]), // lineSpacing
				Integer.parseInt(tokens[6]), // maxWidth
				Integer.parseInt(tokens[7]), // minLeftBearing
				Integer.parseInt(tokens[8]), // minRightBearing
				Integer.parseInt(tokens[9]), // overlinePosition
				Integer.parseInt(tokens[10]), // strikeOutPos
				Integer.parseInt(tokens[11]), // UnderlinePos
				Integer.parseInt(tokens[12]));// xHeight
	}

	private final String fontToString;

	private final int ascent;
	private final int averageCharWidth;
	private final int descent;
	private final int height;
	private final int leading;
	private final int lineSpacing;
	private final int maxWidth;
	private final int minLeftBearing;
	private final int minRightBearing;
	private final int overlinePosition;
	private final int strikeOutPos;
	private final int underlinePos;
	private final int xHeight;

	private JFontMetrics(final String fontToString, final int ascent, final int averageCharWidth, final int descent,
			final int height, final int leading, final int lineSpacing, final int maxWidth, final int minLeftBearing,
			final int minRightBearing, final int overlinePosition, final int strikeOutPos, final int underlinePos,
			final int xHeight) {
		this.fontToString = fontToString;
		this.ascent = ascent;
		this.averageCharWidth = averageCharWidth;
		this.descent = descent;
		this.height = height;
		this.leading = leading;
		this.lineSpacing = lineSpacing;
		this.maxWidth = maxWidth;
		this.minLeftBearing = minLeftBearing;
		this.minRightBearing = minRightBearing;
		this.overlinePosition = overlinePosition;
		this.strikeOutPos = strikeOutPos;
		this.underlinePos = underlinePos;
		this.xHeight = xHeight;
	}

	public int getAscent() {
		return ascent;
	}

	public int getAverageCharWidth() {
		return averageCharWidth;
	}

	public Rectangle2D getBoundingRect(final String text) {
		Objects.requireNonNull(text, "text is null");
		final Pointer p = ApiInstance.LIB_INSTANCE.getBoundingRect(fontToString, text);
		if (p.equals(Pointer.NULL)) {
			throw new IllegalStateException();
		}
		return new Rectangle2D.Double(p.getInt(0), p.getInt(1), p.getInt(2), p.getInt(3));
	}

	public int getDescent() {
		return descent;
	}

	public int getHeight() {
		return height;
	}

	public int getLeading() {
		return leading;
	}

	public int getLineSpacing() {
		return lineSpacing;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public int getMinLeftBearing() {
		return minLeftBearing;
	}

	public int getMinRightBearing() {
		return minRightBearing;
	}

	public int getOverlinePosition() {
		return overlinePosition;
	}

	public int getStrikeOutPos() {
		return strikeOutPos;
	}

	public Rectangle2D getTightBoundingRect(final String text) {
		Objects.requireNonNull(text, "text is null");
		final Pointer p = ApiInstance.LIB_INSTANCE.getTightBoundingRect(fontToString, text);
		if (p.equals(Pointer.NULL)) {
			throw new IllegalStateException();
		}
		return new Rectangle2D.Double(p.getInt(0), p.getInt(1), p.getInt(2), p.getInt(3));
	}

	public int getUnderlinePos() {
		return underlinePos;
	}

	public int getWidth(final String text) {
		Objects.requireNonNull(text, "text is null");
		return ApiInstance.LIB_INSTANCE.getStringWidth(fontToString, text);
	}

	public int getXHeight() {
		return xHeight;
	}

	public boolean inFont(final char c) {
		return ApiInstance.LIB_INSTANCE.inFont(fontToString, c);
	}
}