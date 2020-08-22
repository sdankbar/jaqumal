package com.github.sdankbar.qml.cpp.jni;

import java.awt.Rectangle;

import com.sun.jna.Pointer;

public class FontFunctions {
	public static native Rectangle getBoundingRect(String fontToString, String text);

	public static native Rectangle getBoundingRect2(String fontToString, int x, int y, int w, int h, int alignFlags, int textFlags,
			String text);
	
	public static native String getQFontInfo(String fontToString);

	public static native String getQFontMetrics(String fontToString);

	/**
	 * \return The value of QFront::toString() after returning QFont constructed
	 * from the various parameters passed to this function.
	 */
	public static native String getQFontToString(String family, int pointSize, int pixelSize, boolean bold, boolean italic, boolean overline,
			boolean strikeout, boolean underline, boolean fixedPitch, boolean kerning, int fontWeight,
			double wordSpacing, double letteringSpacing, int letterSpacingType, int capitalization,
			int hintingPreference, int stretch, int style, String styleName, int styleHint, int styleStrategy);
	
	public static native int getStringWidth(String fontToString, String text);

	public static native Pointer getTightBoundingRect(String fontToString, String text);

	/**
	 * @param fontToString Serialized font to test.
	 * @param character    The character to test.
	 * @return True if the font contains the character.
	 */
	public static native boolean inFont(String fontToString, int character);
}
