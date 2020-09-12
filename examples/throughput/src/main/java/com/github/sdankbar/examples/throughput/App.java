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
package com.github.sdankbar.examples.throughput;

import java.util.Random;

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.eventing.NullEventProcessor;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.github.sdankbar.qml.models.list.SignalLock;
import com.google.common.collect.ImmutableMap;

/**
 * Traffic light GUI. Shows the usage of a JQMLSingletonModel and
 * QMLThreadExecutor.
 */
public class App {

	private enum Roles {
		text, //
		x, //
		y,
		R1,
		R2,
		R3,
		R4,
		R5,
		R6,
		R7,
		R8,
		R9,
		R10,
		R11,
		R12,
		R13,
		R14,
		R15,
		R16
	}

	private static final int SIZE = 250;
	private static final int maxCoord = 800;
	private static final Random rand = new Random();

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final JQMLApplication<NullEventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("list_model", Roles.class,
				PutMode.RETURN_NULL);

		while (model.size() < SIZE) {
			final ImmutableMap<Roles, JVariant> data = ImmutableMap.of(Roles.text,
					new JVariant(Integer.toString(model.size())), Roles.x, new JVariant(rand.nextInt(maxCoord)),
					Roles.y, new JVariant(rand.nextInt(maxCoord)));
			model.add(data);
		}

		app.loadAndWatchQMLFile("./src/main/qml/main.qml");

		app.getQMLThreadExecutor().execute(() -> updateModel(app, model));

		app.execute();
	}

	private static void updateModel(final JQMLApplication<NullEventProcessor> app, final JQMLListModel<Roles> model) {

		final long s = System.currentTimeMillis();
		try (final SignalLock c = model.lockSignals()) {
			for (int i = 0; i < SIZE; ++i) {
				final ImmutableMap.Builder<Roles, JVariant> builder = ImmutableMap.builder();
				builder.put(Roles.x, new JVariant(rand.nextInt(maxCoord)));
				builder.put(Roles.y, new JVariant(rand.nextInt(maxCoord)));
				builder.put(Roles.R1, new JVariant(rand.nextInt(32)));
				builder.put(Roles.R2, new JVariant(rand.nextInt(32)));
				builder.put(Roles.R3, new JVariant(rand.nextInt(32)));
				builder.put(Roles.R4, new JVariant(rand.nextInt(32)));
				builder.put(Roles.R5, new JVariant(rand.nextInt(32)));
				builder.put(Roles.R6, new JVariant(rand.nextInt(32)));
				builder.put(Roles.R7, new JVariant(rand.nextInt(32)));
				builder.put(Roles.R8, new JVariant(rand.nextInt(32)));
				builder.put(Roles.R9, new JVariant(rand.nextInt(32)));
				builder.put(Roles.R10, new JVariant(rand.nextInt(32)));
				builder.put(Roles.R11, new JVariant(rand.nextInt(32)));
				builder.put(Roles.R12, new JVariant(rand.nextInt(32)));
				builder.put(Roles.R13, new JVariant(rand.nextInt(32)));
				builder.put(Roles.R14, new JVariant(rand.nextInt(32)));
				builder.put(Roles.R15, new JVariant(rand.nextInt(32)));
				builder.put(Roles.R16, new JVariant(rand.nextInt(32)));
				model.get(i).putAll(builder.build());
			}
		}
		final long e = System.currentTimeMillis();
		System.out.println(e - s + " milli");

		app.getQMLThreadExecutor().execute(() -> updateModel(app, model));
	}
}
