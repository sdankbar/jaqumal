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

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * Performance benchmarks.
 */
public class IndexedMapJMHTest {

	/**
	 * Shared state.
	 */
	@State(Scope.Thread)
	public static class BenchmarkState {

		IndexedMap<Integer, String> map;
		int index = 0;

		/**
		 * Sets up shared state.
		 */
		@Setup(Level.Iteration)
		public void setup() {
			map = new IndexedMap<>(14000);
			index = 0;
		}

	}

	/**
	 * @param state
	 */
	@Benchmark
	public void benchmark_put(final BenchmarkState state) {
		final int index = (state.index++ % 14000);
		state.map.put(index, index, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	}

	/**
	 * @param state
	 * @return
	 */
	@Benchmark
	public String benchmark_get(final BenchmarkState state) {
		return state.map.get(0);
	}

	/**
	 * @param state
	 * @return
	 */
	@Benchmark
	public String benchmark_atIndex(final BenchmarkState state) {
		return state.map.atIndex(0);
	}

	/**
	 * @throws RunnerException
	 */
	@Test
	public void runBenchmarks() throws RunnerException {
		final Options options = new OptionsBuilder().include(IndexedMapJMHTest.class.getName() + ".*")
				.mode(Mode.Throughput).timeUnit(TimeUnit.MICROSECONDS).warmupTime(TimeValue.seconds(1))
				.warmupIterations(5).threads(1).measurementIterations(5).forks(1).shouldFailOnError(false)
				.shouldDoGC(true).build();

		new Runner(options).run();
	}

}
