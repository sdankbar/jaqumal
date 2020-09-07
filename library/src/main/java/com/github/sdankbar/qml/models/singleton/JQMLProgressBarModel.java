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

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.interfaces.ChangeListener;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

public class JQMLProgressBarModel {

	public enum Roles {
		ModelName, //
		From, //
		Indeterminate, //
		Position, //
		To, //
		Value, //
		VisualPosition, //
		Antialiasing, //
		Clip, //
		Enabled, //
		Height, //
		Width, //
		Opacity, //
		Rotation, //
		Scale, //
		Smooth, //
		Visible, //
		X, //
		Y, //
		Z;
	}

	private final JQMLSingletonModel<Roles> model;

	private final Multimap<Roles, ChangeListener> listenersMap = MultimapBuilder.enumKeys(Roles.class).arrayListValues()
			.build();

	public JQMLProgressBarModel(final String modelName, final JQMLApplication<?> app) {
		model = app.getModelFactory().createSingletonModel(modelName, Roles.class, PutMode.RETURN_PREVIOUS_VALUE);

		model.put(Roles.ModelName, new JVariant(modelName));

		model.put(Roles.From, new JVariant(0.0));
		model.put(Roles.Value, new JVariant(0.0));
		model.put(Roles.To, new JVariant(1.0));
		model.put(Roles.Indeterminate, new JVariant(false));
		model.put(Roles.Position, new JVariant(0.0));
		model.put(Roles.VisualPosition, new JVariant(0.0));

		model.put(Roles.Clip, new JVariant(false));
		model.put(Roles.Enabled, new JVariant(true));
		model.put(Roles.Opacity, new JVariant(1.0));
		model.put(Roles.Visible, new JVariant(true));
		model.put(Roles.Width, new JVariant(100));
		model.put(Roles.Height, new JVariant(50));
		model.put(Roles.X, new JVariant(0));
		model.put(Roles.Y, new JVariant(0));
		model.put(Roles.Z, new JVariant(0));

		model.registerChangeListener((key, newValue) -> {
			try {
				final Roles r = Roles.valueOf(key);
				synchronized (listenersMap) {
					for (final ChangeListener l : listenersMap.get(r)) {
						l.valueChanged(key, newValue);
					}
				}
			} catch (final IllegalArgumentException e) {
				// TODO log
			}
		});
	}

	public boolean getAntialiasing() {
		return model.get(Roles.Antialiasing).asType(Boolean.class, Boolean.FALSE).booleanValue();
	}

	public boolean getClip() {
		return model.get(Roles.Clip).asType(Boolean.class, Boolean.FALSE).booleanValue();
	}

	public boolean getEnabled() {
		return model.get(Roles.Enabled).asType(Boolean.class, Boolean.FALSE).booleanValue();
	}

	public double getFrom() {
		return model.get(Roles.From).asType(Double.class, Double.valueOf(0.0)).doubleValue();
	}

	public int getHeight() {
		return model.get(Roles.Height).asType(Integer.class, Integer.valueOf(0)).intValue();
	}

	public boolean getIndeterminate() {
		return model.get(Roles.Indeterminate).asType(Boolean.class, Boolean.FALSE).booleanValue();
	}

	public String getModelName() {
		return model.getModelName();
	}

	public double getOpacity() {
		return model.get(Roles.Opacity).asType(Double.class, Double.valueOf(1.0)).doubleValue();
	}

	public double getPosition() {
		return model.get(Roles.Position).asType(Double.class, Double.valueOf(0.0)).doubleValue();
	}

	public double getRotation() {
		return model.get(Roles.Rotation).asType(Double.class, Double.valueOf(0)).doubleValue();
	}

	public double getScale() {
		return model.get(Roles.Scale).asType(Double.class, Double.valueOf(0)).doubleValue();
	}

	public double getTo() {
		return model.get(Roles.To).asType(Double.class, Double.valueOf(0.0)).doubleValue();
	}

	public double getValue() {
		return model.get(Roles.Value).asType(Double.class, Double.valueOf(0.0)).doubleValue();
	}

	public boolean getVisible() {
		return model.get(Roles.Visible).asType(Boolean.class, Boolean.FALSE).booleanValue();
	}

	public double getVisualPosition() {
		return model.get(Roles.VisualPosition).asType(Double.class, Double.valueOf(0.0)).doubleValue();
	}

	public int getWidth() {
		return model.get(Roles.Width).asType(Integer.class, Integer.valueOf(0)).intValue();
	}

	public int getX() {
		return model.get(Roles.X).asType(Integer.class, Integer.valueOf(0)).intValue();
	}

	public int getY() {
		return model.get(Roles.Y).asType(Integer.class, Integer.valueOf(0)).intValue();
	}

	public int getZ() {
		return model.get(Roles.Z).asType(Integer.class, Integer.valueOf(0)).intValue();
	}

	public void registerListener(final Roles r, final ChangeListener l) {
		synchronized (listenersMap) {
			listenersMap.put(r, l);
		}
	}

	public void setAntialiasing(final boolean antialiasing) {
		model.put(Roles.Antialiasing, new JVariant(antialiasing));
	}

	public void setClip(final boolean clip) {
		model.put(Roles.Clip, new JVariant(clip));
	}

	public void setEnabled(final boolean enabled) {
		model.put(Roles.Enabled, new JVariant(enabled));
	}

	public void setFrom(final double value) {
		model.put(Roles.From, new JVariant(value));
	}

	public void setHeight(final int height) {
		model.put(Roles.Height, new JVariant(height));
	}

	public void setIndeterminate(final boolean indeterminate) {
		model.put(Roles.Indeterminate, new JVariant(indeterminate));
	}

	public void setOpacity(final double value) {
		model.put(Roles.Opacity, new JVariant(value));
	}

	public void setRotation(final double rotation) {
		model.put(Roles.Rotation, new JVariant(rotation));
	}

	public void setScale(final double scale) {
		model.put(Roles.Scale, new JVariant(scale));
	}

	public void setSmooth(final boolean smooth) {
		model.put(Roles.Smooth, new JVariant(smooth));
	}

	public void setTo(final double value) {
		model.put(Roles.To, new JVariant(value));
	}

	public void setValue(final double value) {
		model.put(Roles.Value, new JVariant(value));
	}

	public void setVisible(final boolean visible) {
		model.put(Roles.Visible, new JVariant(visible));
	}

	public void setWidth(final int width) {
		model.put(Roles.Width, new JVariant(width));
	}

	public void setX(final int x) {
		model.put(Roles.X, new JVariant(x));
	}

	public void setY(final int y) {
		model.put(Roles.Y, new JVariant(y));
	}

	public void setZ(final int z) {
		model.put(Roles.Z, new JVariant(z));
	}

	public void unregisterListener(final Roles r, final ChangeListener l) {
		synchronized (listenersMap) {
			while (listenersMap.remove(r, l)) {
				// Empty Implementation
			}
		}
	}

}
