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
package com.github.sdankbar.examples.stoplight;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.sdankbar.qml.JInvokable;
import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.eventing.QMLReceivableEvent;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;
import com.google.common.collect.ImmutableMap;

/**
 * Traffic light GUI. Shows the usage of a JQMLSingletonModel and
 * QMLThreadExecutor.
 */
public class App {

	/**
	 *
	 */
	public interface EventProcessor {
		/**
		 * @param e Event to handle
		 */
		default void handle(final TestQMLEvent e) {
			// Empty Implementation
		}
	}

	private enum StopLightRoles {
		lightColor;
	}

	private static class TestQMLEvent extends QMLReceivableEvent<EventProcessor> {

		@Override
		public Map<String, JVariant> getParameters() {
			final Map<String, JVariant> temp = new HashMap<>();
			temp.put("time", new JVariant(System.currentTimeMillis()));
			return temp;
		}

		@Override
		public void handle(final EventProcessor processor) {
			processor.handle(this);
		}

	}

	private static class TestInvokable {

		@JInvokable
		public void function1(final String str) {
			System.out.println("function1=" + str);
		}

		@JInvokable
		public int function2(final int c) {
			System.out.println("function2=" + c);
			return 2 * c;
		}
	}

	private static final String[] lightColors = { "green", "yellow", "red" };
	private static int index = 0;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<StopLightRoles> model = app.getModelFactory().createSingletonModel("model",
				StopLightRoles.class, PutMode.RETURN_PREVIOUS_VALUE);

		final TestInvokable invokable = new TestInvokable();
		app.getInvokableDispatcher().registerInvokable("test_invokable", invokable);

		app.loadAndWatchQMLFile("./src/main/qml/main.qml");

		final Runnable r = new Runnable() {

			@Override
			public void run() {
				model.put(StopLightRoles.lightColor, new JVariant(lightColors[index]));
				if (index == 0) {
					index = 1;
					app.getQMLThreadExecutor().schedule(this, 5, TimeUnit.SECONDS);
				} else if (index == 1) {
					index = 2;
					app.getQMLThreadExecutor().schedule(this, 1, TimeUnit.SECONDS);
				} else if (index == 2) {
					index = 0;
					app.getQMLThreadExecutor().schedule(this, 5, TimeUnit.SECONDS);
				}
				app.getEventDispatcher().submit(new TestQMLEvent());
				final JVariant invokeRet = app.getInvokableDispatcher().invoke("test_invoke_target",
						ImmutableMap.of("data", new JVariant(7)));
				System.err.println("invoke return=" + invokeRet);
			}
		};
		app.getQMLThreadExecutor().execute(r);

		app.execute();
	}
}
