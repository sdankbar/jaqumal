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
package com.github.sdankbar.qml.images;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.ApiInstance;
import com.github.sdankbar.qml.cpp.jna.CppInterface.ImageProviderCallback;
import com.github.sdankbar.qml.cpp.memory.SharedJavaCppMemory;
import com.sun.jna.Pointer;

public class JQMLImageProviderWrapper implements ImageProviderCallback {

	private static final Logger log = LoggerFactory.getLogger(JQMLImageProviderWrapper.class);

	private final String id;
	private final JQMLImageProvider provider;
	private final SharedJavaCppMemory memory = new SharedJavaCppMemory(16 * 1024 * 1024);

	public JQMLImageProviderWrapper(final String id, final JQMLImageProvider provider) {
		this.id = Objects.requireNonNull(id, "id null");
		this.provider = Objects.requireNonNull(provider, "provider is null");

		ApiInstance.LIB_INSTANCE.addImageProvider(id, this);
	}

	@Override
	public Pointer invoke(final String imageID, final int w, final int h) {
		final BufferedImage image = provider.requestImage(imageID, new Dimension(w, h));
		if (image != null) {
			final JVariant var = new JVariant(image);
			var.serialize(memory);
			return memory.getPointer();
		} else {
			return Pointer.NULL;
		}
	}

}
