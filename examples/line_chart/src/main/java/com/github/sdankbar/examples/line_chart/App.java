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
package com.github.sdankbar.examples.line_chart;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.models.list.JQMLXYSeriesModel;

/**
 * Example application that allow for editing a set of colors.
 *
 */
public class App {

	/**
	 *
	 *
	 */
	public static interface EventProcessor {
		// Empty Implementation
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLXYSeriesModel model = app.getModelFactory().createXYSeriesModel("lineSeries");

		update(model);

		app.loadAndWatchQMLFile("./src/main/qml/main.qml");

		app.getQMLThreadExecutor().scheduleWithFixedDelay(() -> update(model), 2, 2, TimeUnit.SECONDS);

		app.execute();
	}

	private static void update(final JQMLXYSeriesModel model) {
		final Random r = new Random();
		model.clearAllPoints();
		for (int i = 0; i < 5; ++i) {
			model.addPoint(new Point2D.Double(i, r.nextInt(10)));
		}
	}
}
