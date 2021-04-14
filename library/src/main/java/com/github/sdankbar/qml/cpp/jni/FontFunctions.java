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
package com.github.sdankbar.qml.cpp.jni;

import java.awt.Rectangle;

public class FontFunctions {
	public static native Rectangle getBoundingRect(String fontToString, String text);

	public static native Rectangle getBoundingRect2(String fontToString, int x, int y, int w, int h, int alignFlags,
			int textFlags, String text);

	public static native String getQFontInfo(String fontToString);

	public static native String getQFontMetrics(String fontToString);

	/**
	 * \return The value of QFront::toString() after returning QFont constructed
	 * from the various parameters passed to this function.
	 */
	public static native String getQFontToString(int fontIndex, String family, int pointSize, int pixelSize,
			boolean bold, boolean italic, boolean overline, boolean strikeout, boolean underline, boolean fixedPitch,
			boolean kerning, int fontWeight, double wordSpacing, double letteringSpacing, int letterSpacingType,
			int capitalization, int hintingPreference, int stretch, int style, String styleName, int styleHint,
			int styleStrategy);

	public static native int getStringWidth(String fontToString, String text);

	public static native Rectangle getTightBoundingRect(String fontToString, String text);

	/**
	 * @param fontToString Serialized font to test.
	 * @param character    The character to test.
	 * @return True if the font contains the character.
	 */
	public static native boolean inFont(String fontToString, int character);

	public static native int scaleToFit(final int w, final int h, final String inputString, final int fontIndex,
			final int minimumPointSize);

	public static native int loadFont(String filePath);
}
