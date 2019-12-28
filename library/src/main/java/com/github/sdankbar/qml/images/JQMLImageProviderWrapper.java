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
