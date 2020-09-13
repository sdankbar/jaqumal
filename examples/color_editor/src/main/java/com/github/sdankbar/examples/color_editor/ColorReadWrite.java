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
package com.github.sdankbar.examples.color_editor;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads and write the color list to file.
 */
public class ColorReadWrite {

	private final Map<String, Color> colorMap = new HashMap<>();

	/**
	 * Removes all colors.
	 */
	public void clear() {
		colorMap.clear();
	}

	/**
	 * @return All colors.
	 */
	public Map<String, Color> getColors() {
		return new HashMap<>(colorMap);
	}

	/**
	 * Store a color.
	 *
	 * @param name The color's name.
	 * @param c    The color.
	 */
	public void put(final String name, final Color c) {
		colorMap.put(name, c);
	}

	/**
	 * @param f File to write the colors to.
	 */
	public void readColors(final File f) {
		try {
			Files.readAllLines(f.toPath()).stream().forEach(l -> {
				final String[] data = l.split("=");
				final int argb = (int) Long.parseLong(data[1].toUpperCase(), 16);
				colorMap.put(data[0], new Color(argb));
			});
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param f File to read the colors from.
	 * @throws IOException
	 */
	public void writeColors(final File f) throws IOException {
		f.delete();
		try (BufferedWriter w = new BufferedWriter(new FileWriter(f))) {
			for (final Map.Entry<String, Color> e : colorMap.entrySet()) {
				w.write(e.getKey() + "=" + Integer.toHexString(e.getValue().getRGB()));
				w.newLine();
			}
		}
	}

}
