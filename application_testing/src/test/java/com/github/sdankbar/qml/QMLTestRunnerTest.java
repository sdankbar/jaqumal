package com.github.sdankbar.qml;

import java.io.File;

import org.junit.Test;

import com.github.sdankbar.qml.testing.QMLTestRunner;

public class QMLTestRunnerTest {

	@Test
	public void testRun() {
		final QMLTestRunner runner = new QMLTestRunner(new File("."));
		runner.run();
	}

}
