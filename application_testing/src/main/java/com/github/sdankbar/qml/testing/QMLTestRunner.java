package com.github.sdankbar.qml.testing;

import java.io.File;
import java.util.List;
import java.util.Objects;

import com.github.sdankbar.qml.cpp.ApiInstance;
import com.github.sdankbar.qml.cpp.jni.ApplicationFunctions;

public class QMLTestRunner {

	private final File pathToQMLTests;
	private final String[] importPaths;

	public QMLTestRunner(final File path, final List<File> importPaths) {
		pathToQMLTests = Objects.requireNonNull(path, "path is null");

		Objects.requireNonNull(importPaths, "importPaths is null");
		this.importPaths = new String[importPaths.size()];
		for (int i = 0; i < importPaths.size(); ++i) {
			this.importPaths[i] = importPaths.get(i).getAbsolutePath();
		}
	}

	public void run() {
		final int r = ApplicationFunctions.runQMLTest(pathToQMLTests.getAbsolutePath(), importPaths);
		if (r != 0) {
			throw new AssertionError("QML unit test failure(s)");
		}
	}

}
