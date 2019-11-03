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
package com.github.sdankbar.qml.models.list;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import com.github.sdankbar.qml.JQMLModelFactory;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.JVariant.Type;
import com.github.sdankbar.qml.eventing.EventDispatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class JQMLXYSeriesModel {

	public enum SeriesRoles {
		X, //
		Y;
	}

	private static ImmutableMap<SeriesRoles, JVariant> toMap(final Point2D p) {
		return ImmutableMap.of(SeriesRoles.X, new JVariant(p.getX()), SeriesRoles.Y, new JVariant(p.getY()));
	}

	private final JQMLListModel<SeriesRoles> model;

	public JQMLXYSeriesModel(final String modelName, final JQMLModelFactory factory,
			final EventDispatcher<?> dispatcher) {
		model = factory.createListModel(modelName, SeriesRoles.class);
	}

	public void addAllPoints(final List<Point2D> points) {
		final ImmutableList<Map<SeriesRoles, JVariant>> dataList = points.stream().map(JQMLXYSeriesModel::toMap)
				.collect(ImmutableList.toImmutableList());
		model.addAll(dataList);
	}

	public void addPoint(final int index, final Point2D p) {
		model.add(index, toMap(p));
	}

	public void addPoint(final Point2D p) {
		model.add(toMap(p));
	}

	public void clearAllPoints() {
		model.clear();
	}

	public Point2D removePoint(final int index) {
		final Map<SeriesRoles, JVariant> oldData = model.remove(index);
		if (oldData != null && oldData.containsKey(SeriesRoles.X) && oldData.containsKey(SeriesRoles.Y)) {
			final JVariant x = oldData.get(SeriesRoles.X);
			final JVariant y = oldData.get(SeriesRoles.Y);
			if (x.isInstanceOf(Type.DOUBLE) && y.isInstanceOf(Type.DOUBLE)) {
				return new Point2D.Double(x.asDouble(), y.asDouble());
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
