package com.github.sdankbar.qml;

import java.io.File;

import org.junit.Test;

import com.github.sdankbar.qml.testing.QMLTestRunner;
import com.google.common.collect.ImmutableList;

/**
 * Test the QMLTestRunner class.
 */
public class QMLTestRunnerTest {

	/**
	 * Test successful unit tests.
	 */
	@Test
	public void testRun() {
		final QMLTestRunner runner = new QMLTestRunner(new File("src/test/qml_success"), ImmutableList.of());
		runner.run();
	}

	/**
	 * Test failed unit tests
	 */
	@Test(expected = AssertionError.class)
	public void testRunFail() {
		final QMLTestRunner runner = new QMLTestRunner(new File("src/test/qml_failure"), ImmutableList.of());
		runner.run();
	}

	/**
	 * Test successful unit tests that import.
	 */
	@Test
	public void testRunWithImport() {
		final QMLTestRunner runner = new QMLTestRunner(new File("src/test/qml_success2"),
				ImmutableList.of(new File("src/main/qml")));
		runner.run();
	}
}
