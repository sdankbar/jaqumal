package com.github.sdankbar.qml.testing;

import java.io.File;
import java.util.List;
import java.util.Objects;

import com.github.sdankbar.qml.cpp.ApiInstance;

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
		final int r = ApiInstance.LIB_INSTANCE.runQMLTest(pathToQMLTests.getAbsolutePath(), importPaths,
				importPaths.length);
		if (r != 0) {
			throw new AssertionError("QML unit test failure(s)");
		}
	}

}
