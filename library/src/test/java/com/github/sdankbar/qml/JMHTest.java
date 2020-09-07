/**
 * The MIT License
 * Copyright © 2019 Stephen Dankbar
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
package com.github.sdankbar.qml;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.eventing.NullEventProcessor;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;

public class JMHTest {

	private enum Role {
		R1,
		R2,
		R3;
	}

	/**
	 * Shared state.
	 */
	@State(Scope.Thread)
	public static class BenchmarkState {
		JQMLApplication<NullEventProcessor> app;
		JQMLSingletonModel<Role> singletonModel;
		JQMLListModel<Role> listModel;

		/**
		 * Sets up shared state.
		 */
		@Setup(Level.Trial)
		public void setup() {
			app = JQMLApplication.create(new String[0], new NullEventFactory<>());
			singletonModel = app.getModelFactory().createSingletonModel("singleton_model", Role.class,
					PutMode.RETURN_NULL);
			listModel = app.getModelFactory().createListModel("list_model", Role.class, PutMode.RETURN_NULL);
			listModel.add(new JVariant(1), Role.R1);
			listModel.add(new JVariant("ABCDEFGHIJKLMNOPQRSTUVWXYZ"), Role.R2);
		}

		/**
		 * Cleanup after benchmark
		 */
		@TearDown(Level.Trial)
		public void teardown() {
			JQMLApplication.delete();
		}
	}

	@Benchmark
	public void benchmark_singletonModelSetInteger(final BenchmarkState state) {
		state.singletonModel.put(Role.R1, new JVariant(1));
	}

	@Benchmark
	public void benchmark_singletonModelSetString(final BenchmarkState state) {
		state.singletonModel.put(Role.R1, new JVariant("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
	}

	@Benchmark
	public void benchmark_listModelSetInteger(final BenchmarkState state) {
		state.listModel.get(0).put(Role.R1, new JVariant(1));
	}

	@Benchmark
	public void benchmark_listModelSetString(final BenchmarkState state) {
		state.listModel.get(0).put(Role.R1, new JVariant("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
	}

	@Benchmark
	public void benchmark_listModelGetInteger(final BenchmarkState state) {
		assertEquals(1, state.listModel.get(0).get(Role.R1).asInteger());
	}

	@Benchmark
	public void benchmark_listModelGetString(final BenchmarkState state) {
		assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", state.listModel.get(1).get(Role.R2).asString());
	}

	@Test
	public void runBenchmarks() throws RunnerException {
		final Options options = new OptionsBuilder().include(JMHTest.class.getName() + ".*").mode(Mode.Throughput)
				.timeUnit(TimeUnit.MICROSECONDS).warmupTime(TimeValue.seconds(1)).warmupIterations(5).threads(1)
				.measurementIterations(5).forks(1).shouldFailOnError(false).shouldDoGC(true).build();

		new Runner(options).run();
	}

}
