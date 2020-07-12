package com.github.sdankbar.qml.testing;

import java.io.File;
import java.util.Objects;

import com.github.sdankbar.qml.cpp.ApiInstance;

public class QMLTestRunner {

	private final File pathToQMLTests;

	public QMLTestRunner(final File path) {
		pathToQMLTests = Objects.requireNonNull(path, "path is null");
	}

	public void run() {
		if (!ApiInstance.LIB_INSTANCE.runQMLTest(pathToQMLTests.getAbsolutePath())) {
			throw new AssertionError("QML unit test failure(s)");
		}
	}

}
