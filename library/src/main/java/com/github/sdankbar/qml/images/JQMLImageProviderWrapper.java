package com.github.sdankbar.qml.images;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Objects;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.ApiInstance;
import com.github.sdankbar.qml.cpp.jna.CppInterface.ImageProviderCallback;
import com.github.sdankbar.qml.cpp.memory.SharedJavaCppMemory;
import com.sun.jna.Pointer;

public class JQMLImageProviderWrapper implements ImageProviderCallback {

	private final JQMLImageProvider provider;

	private final SharedJavaCppMemory memory = new SharedJavaCppMemory(2);

	public JQMLImageProviderWrapper(final String id, final JQMLImageProvider provider) {
		Objects.requireNonNull(id, "id null");
		this.provider = Objects.requireNonNull(provider, "provider is null");

		ApiInstance.LIB_INSTANCE.addImageProvider(id, this);
	}

	@Override
	public Pointer invoke(final String id, final int w, final int h) {
		final BufferedImage image = provider.requestImage(id, new Dimension(w, h));
		final JVariant var = new JVariant(image);
		var.serialize(memory);
		return memory.getPointer();
	}

}
