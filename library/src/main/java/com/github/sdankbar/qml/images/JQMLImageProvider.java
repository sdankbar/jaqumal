package com.github.sdankbar.qml.images;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public interface JQMLImageProvider {

	BufferedImage requestImage(String id, Dimension requestedSize);

}
