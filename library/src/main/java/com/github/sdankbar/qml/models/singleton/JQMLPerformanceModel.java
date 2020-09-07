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
package com.github.sdankbar.qml.models.singleton;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.github.sdankbar.qml.JQMLModelFactory;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.eventing.builtin.BuiltinEventProcessor;
import com.github.sdankbar.qml.eventing.builtin.RenderEvent;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;

/**
 * Model for measuring various QML/Qt metrics.
 */
public class JQMLPerformanceModel implements BuiltinEventProcessor {

	private enum Roles {
		ModelName, //
		AVERAGE_RENDER_TIME_MILLI, //
		AVERAGE_TOTAL_TIME_MILLI, //
		NINETY_FIFTH_TOTAL_TIME_MILLI, //
		NINETY_FIFTH_RENDER_TIME_MILLI,
	}

	private static final int WINDOW_SIZE = 100;

	private final JQMLSingletonModel<Roles> model;

	private Instant lastBeforeSync;
	private Instant lastBeforeRender;
	private Instant lastAfterRender;
	private Instant lastFrameSwapped;

	private final DescriptiveStatistics totalTime = new DescriptiveStatistics(WINDOW_SIZE);
	private final DescriptiveStatistics renderTime = new DescriptiveStatistics(WINDOW_SIZE);

	/**
	 * Model constructor.
	 *
	 * @param modelName Name of the model.
	 * @param factory   Factory for building QML models.
	 */
	public JQMLPerformanceModel(final String modelName, final JQMLModelFactory factory) {
		Objects.requireNonNull(factory, "factory is null");

		model = factory.createSingletonModel(modelName, Roles.class, PutMode.RETURN_PREVIOUS_VALUE);

		model.put(Roles.ModelName, new JVariant(modelName));
		model.put(Roles.AVERAGE_RENDER_TIME_MILLI, new JVariant(1));
		model.put(Roles.AVERAGE_TOTAL_TIME_MILLI, new JVariant(1));

		model.put(Roles.NINETY_FIFTH_RENDER_TIME_MILLI, new JVariant(1));
		model.put(Roles.NINETY_FIFTH_TOTAL_TIME_MILLI, new JVariant(1));
	}

	@Override
	public void handle(final RenderEvent e) {
		switch (e.getType()) {
			case AFTER_RENDER:
				lastAfterRender = e.getEventTime();
				break;
			case BEFORE_RENDER:
				lastBeforeRender = e.getEventTime();
				break;
			case BEFORE_SYNC:
				lastBeforeSync = e.getEventTime();
				break;
			case FRAME_SWAP:
				lastFrameSwapped = e.getEventTime();
				updateStatistics();
				break;
		}
	}

	private void updateStatistics() {
		if (lastAfterRender != null && lastBeforeRender != null && lastBeforeSync != null && lastFrameSwapped != null) {
			final Duration totalTime = Duration.between(lastBeforeSync, lastFrameSwapped);
			final Duration renderTime = Duration.between(lastBeforeRender, lastAfterRender);

			this.totalTime.addValue(totalTime.toMillis());
			this.renderTime.addValue(renderTime.toMillis());

			lastAfterRender = null;
			lastBeforeRender = null;
			lastBeforeSync = null;
			lastFrameSwapped = null;

			model.put(Roles.AVERAGE_RENDER_TIME_MILLI, new JVariant(Math.max(1, this.renderTime.getPercentile(50))));
			model.put(Roles.AVERAGE_TOTAL_TIME_MILLI, new JVariant(Math.max(1, this.totalTime.getPercentile(50))));
			model.put(Roles.NINETY_FIFTH_RENDER_TIME_MILLI,
					new JVariant(Math.max(1, this.renderTime.getPercentile(95))));
			model.put(Roles.NINETY_FIFTH_TOTAL_TIME_MILLI, new JVariant(Math.max(1, this.totalTime.getPercentile(95))));
		}
	}
}
