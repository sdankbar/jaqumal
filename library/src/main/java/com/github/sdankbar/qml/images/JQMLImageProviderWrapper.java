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
package com.github.sdankbar.qml.images;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Objects;

import com.github.sdankbar.qml.cpp.jni.ApplicationFunctions;
import com.github.sdankbar.qml.cpp.jni.interfaces.ImageProviderCallback;

/**
 * Wraps a JQMLImageProvider, allowing it to be called from C++.
 */
public class JQMLImageProviderWrapper implements ImageProviderCallback {

	private final String id;
	private final JQMLImageProvider provider;

	/**
	 * Constructs new wrapper.
	 *
	 * @param id       Identifier of the provider.
	 * @param provider The provider to wrap.
	 */
	public JQMLImageProviderWrapper(final String id, final JQMLImageProvider provider) {
		this.id = Objects.requireNonNull(id, "id null");
		this.provider = Objects.requireNonNull(provider, "provider is null");

		ApplicationFunctions.addImageProvider(id, this);
	}

	/**
	 * @return This provider's identifier.
	 */
	public String getProviderID() {
		return id;
	}

	@Override
	public BufferedImage invoke(final String imageID, final int w, final int h) {
		final BufferedImage image = provider.requestImage(imageID, new Dimension(w, h));
		if (image != null) {
			return image;
		} else {
			return null;
		}
	}

}
