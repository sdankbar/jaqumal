package com.github.sdankbar.examples.shared_qml;

import com.github.sdankbar.qml.JQMLApplication;

public class QMLResourceLoader {

	public static void loadResources() {
		JQMLApplication.registerResourceFromSystemResource("shared.rcc", "");
	}

	private QMLResourceLoader() {
		// Empty Implementation
	}

}
