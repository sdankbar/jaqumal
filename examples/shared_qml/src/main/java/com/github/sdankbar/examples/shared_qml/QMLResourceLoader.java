package com.github.sdankbar.examples.shared_qml;

import java.util.Objects;

import com.github.sdankbar.qml.JQMLApplication;

public class QMLResourceLoader {

	public static void loadResources(final JQMLApplication<?> app) {
		Objects.requireNonNull(app, "app is null");
		JQMLApplication.registerResourceFromSystemResource("shared.rcc", "");
		app.addImportPath(":/shared_qml");
	}

	private QMLResourceLoader() {
		// Empty Implementation
	}

}
