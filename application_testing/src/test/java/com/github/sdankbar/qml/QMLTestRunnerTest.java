package com.github.sdankbar.qml;

import java.io.File;

import org.junit.Test;

import com.github.sdankbar.qml.testing.QMLTestRunner;

/**
 * Test the QMLTestRunner class.
 */
public class QMLTestRunnerTest {

	/**
	 * Test successful unit tests.
	 */
	@Test
	public void testRun() {
		final QMLTestRunner runner = new QMLTestRunner(new File("src/test/qml_success"));
		runner.run();
	}

	/**
	 * Test failed unit tests
	 */
	@Test(expected = AssertionError.class)
	public void testRunFail() {
		final QMLTestRunner runner = new QMLTestRunner(new File("src/test/qml_failure"));
		runner.run();
	}
}
