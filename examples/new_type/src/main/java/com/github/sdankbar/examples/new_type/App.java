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
package com.github.sdankbar.examples.new_type;

import java.util.Random;

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.eventing.NullEventProcessor;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.google.common.collect.ImmutableMap;

/**
 * Example application that allow for editing a set of colors.
 *
 */
public class App {

	private enum Roles {
		data
	}

	private static final int SIZE = 250;
	private static final int maxCoord = 800;
	private static final Random rand = new Random();

	private static long secondStart = System.currentTimeMillis();
	private static int fpsCount = 0;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final JQMLApplication<NullEventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("list_model", Roles.class,
				PutMode.RETURN_NULL);

		while (model.size() < SIZE) {
			final TestStorable storable = new TestStorable(Integer.toString(model.size()), rand.nextInt(maxCoord),
					rand.nextInt(maxCoord));
			final ImmutableMap<Roles, JVariant> data = ImmutableMap.of(Roles.data, new JVariant(storable));
			model.add(data);
		}

		app.loadQMLFile("./src/main/qml/main.qml");

		app.getQMLThreadExecutor().execute(() -> updateModel(app, model));

		app.execute();
	}

	private static void updateModel(final JQMLApplication<NullEventProcessor> app, final JQMLListModel<Roles> model) {
		for (int i = 0; i < SIZE; ++i) {
			final TestStorable storable = new TestStorable(Integer.toString(rand.nextInt(32)), rand.nextInt(maxCoord),
					rand.nextInt(maxCoord));
			model.get(i).put(Roles.data, new JVariant(storable));
		}
		final long e = System.currentTimeMillis();

		++fpsCount;
		final long delta = e - secondStart;
		if (delta >= 1000) {
			System.out.println("          FPS=" + fpsCount);
			secondStart = e;
			fpsCount = 0;
		}

		app.getQMLThreadExecutor().execute(() -> updateModel(app, model));
	}
}
