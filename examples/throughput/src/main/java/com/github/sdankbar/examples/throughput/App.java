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
		R16,
		R17,
		R18,
		R19,
		R20,
		R21,
		R22,
		R23,
		R24,
		R25,
		R26,
		R27,
		R28,
		R29,
		R30,
		R31,
		R32,
		R33,
		R34,
		R35,
		R36,
		R37,
		R38,
		R39,
		R40,
		R41,
		R42,
		R43,
		R44,
		R45,
		R46,
		R47,
		R48,
		R49,
		R50,
		R51,
		R52,
		R53,
		R54,
		R55,
		R56,
		R57,
		R58,
		R59,
		R60,
		R61,
		R62,
		R63,
		R64,
		R65,
		R66,
		R67,
		R68,
		R69,
		R70,
		R71,
		R72,
		R73,
		R74,
		R75,
		R76,
		R77,
		R78,
		R79,
		R80,
		R81,
		R82,
		R83,
		R84,
		R85,
		R86,
		R87,
		R88,
		R89,
		R90,
		R91,
		R92,
		R93,
		R94,
		R95,
		R96,
		R97,
		R98,
		R99,
	}

	private static final int SIZE = 250;
	private static final int maxCoord = 800;
	private static final Random rand = new Random();

	private static long secondStart = System.currentTimeMillis();
	private static int fpsCount = 0;
	private static long totalCount = 0;

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
				for (final Roles r : Roles.values()) {
					if (r != Roles.x && r != Roles.y) {
						builder.put(r, new JVariant(Integer.toString(rand.nextInt(32))));
					}
				}
				model.get(i).putAll(builder.build());
			}
		}
		final long e = System.currentTimeMillis();
		System.out.println(e - s + " milli");

		++fpsCount;
		++totalCount;
		final long delta = e - secondStart;
		if (delta >= 1000) {
			System.out.println("          FPS=" + fpsCount + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			secondStart = e;
			fpsCount = 0;
		}

		app.getQMLThreadExecutor().execute(() -> updateModel(app, model));
	}
}
