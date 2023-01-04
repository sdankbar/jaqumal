/**
 * The MIT License
 * Copyright © 2020 Stephen Dankbar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.sdankbar.qml.utility;

import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Performance benchmarks.
 */
public class TextWrapperJMHTest {

	private static final List<String> space = Arrays.asList(" ");

	@Benchmark
	public String benmark_wrap() {
		return TextWrapper.wrap("This is a test of the emergency broadcast system. This is only a test.", 20, space);
	}

	/**
	 * @throws RunnerException
	 */
	@Test
	public void runBenchmarks() throws RunnerException {
		final Options options = new OptionsBuilder().include(TextWrapperJMHTest.class.getName() + ".*")
				.mode(Mode.Throughput).timeUnit(TimeUnit.MICROSECONDS).warmupTime(TimeValue.seconds(1))
				.warmupIterations(5).threads(1).measurementIterations(5).forks(1).shouldFailOnError(false)
				.shouldDoGC(true).build();

		new Runner(options).run();
	}

}
