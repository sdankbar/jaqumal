package com.github.sdankbar.qml.cpp.jni.interfaces;

import java.awt.image.BufferedImage;

/**
 * Interface for classes to be called when an image is requested.
 *
 */
public interface ImageProviderCallback {
	/**
	 * @param id Identifier of the requested image
	 * @param w  Requested width in pixels.
	 * @param h  Requested height in pixels.
	 * @return Pointer to the serialized image, see JVariant format. Pointer is null
	 *         when image is not found.
	 */
	BufferedImage invoke(String id, int w, int h);
}