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
import java.util.Objects;

import com.github.sdankbar.qml.JQMLModelFactory;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.JVariant.Type;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Model that can be used for drawing scatter/line graphs with Qml Charts.
 */
public class JQMLXYSeriesModel {

	/**
	 * Valid keys for this model.
	 */
	private enum SeriesRoles {
		/**
		 * Graph X coordinate
		 */
		X,
		/**
		 * Graph Y coordinate
		 */
		Y;
	}

	private static ImmutableMap<SeriesRoles, JVariant> toMap(final Point2D p) {
		Objects.requireNonNull(p, "p is null");
		return ImmutableMap.of(SeriesRoles.X, new JVariant(p.getX()), SeriesRoles.Y, new JVariant(p.getY()));
	}

	private final JQMLListModel<SeriesRoles> model;

	/**
	 * Constructs a new model.
	 *
	 * @param modelName The model's name.
	 * @param factory   Model factory to make the list model that backs this model.
	 */
	public JQMLXYSeriesModel(final String modelName, final JQMLModelFactory factory) {
		model = factory.createListModel(modelName, SeriesRoles.class, PutMode.RETURN_PREVIOUS_VALUE);
	}

	/**
	 * Appends a list of points to the graph.
	 *
	 * @param points The points to add.
	 */
	public void addAllPoints(final List<Point2D> points) {
		Objects.requireNonNull(points, "points is null");
		final ImmutableList<Map<SeriesRoles, JVariant>> dataList = points.stream().map(JQMLXYSeriesModel::toMap)
				.collect(ImmutableList.toImmutableList());
		model.addAll(dataList);
	}

	/**
	 * Adds a point at index, shifting the existing point at index to the right.
	 *
	 * @param index Index to insert at.
	 * @param p     Point to insert.
	 */
	public void addPoint(final int index, final Point2D p) {
		model.add(index, toMap(p));
	}

	/**
	 * Appends a point to the graph.
	 *
	 * @param p Point to add.
	 */
	public void addPoint(final Point2D p) {
		model.add(toMap(p));
	}

	/**
	 * Removes all points from the graph.
	 */
	public void clearAllPoints() {
		model.clear();
	}

	/**
	 * Removes the point at index from the graph.
	 *
	 * @param index Index of the point to remove.
	 * @return The point that was removed or null if no point was removed.
	 */
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
