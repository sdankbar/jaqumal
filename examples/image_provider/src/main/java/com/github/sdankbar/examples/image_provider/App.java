/**
 * The MIT License
 * Copyright © 2019 Stephen Dankbar
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
package com.github.sdankbar.examples.image_provider;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.eventing.NullEventProcessor;
import com.github.sdankbar.qml.images.JQMLImageProvider;

/**
 * Example using the JQMLImageProvider.
 */
public class App {

	private static class TestImageProvider implements JQMLImageProvider {

		@Override
		public BufferedImage requestImage(final String id, final Dimension requestedSize) {
			if (id.equals("red")) {
				final BufferedImage image = new BufferedImage(requestedSize.width, requestedSize.height,
						BufferedImage.TYPE_INT_ARGB);
				for (int i = 0; i < requestedSize.width; ++i) {
					for (int j = 0; j < requestedSize.height; ++j) {
						image.setRGB(i, j, Color.red.getRGB());
					}
				}
				return image;
			} else if (id.equals("test.png")) {
				try {
					return ImageIO.read(new File("test_image.png"));
				} catch (final IOException e) {
					e.printStackTrace();
					return null;
				}
			} else {
				return null;
			}
		}

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final JQMLApplication<NullEventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());

		app.registerImageProvider(new TestImageProvider(), "test_provider");

		app.loadAndWatchQMLFile("./src/main/qml/main.qml");
		app.execute();
	}
}
