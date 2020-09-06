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

import java.util.Objects;

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.models.interfaces.ChangeListener;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

public class JQMLCheckBoxModel {

	public enum CheckedState {
		UnChecked, //
		PartiallyChecked, //
		Checked; //
	}

	public enum Roles {
		ModelName, //
		CheckedState, //
		Checked, //
		PartiallyCheckedEnabled, //
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

	public JQMLCheckBoxModel(final String modelName, final JQMLApplication<?> app) {
		model = app.getModelFactory().createSingletonModel(modelName, Roles.class);

		model.put(Roles.ModelName, new JVariant(modelName));
		model.put(Roles.CheckedState, new JVariant(CheckedState.UnChecked.ordinal()));
		model.put(Roles.Checked, new JVariant(false));
		model.put(Roles.PartiallyCheckedEnabled, new JVariant(false));
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
			final Roles r = Roles.valueOf(key);
			synchronized (listenersMap) {
				for (final ChangeListener l : listenersMap.get(r)) {
					l.valueChanged(key, newValue);
				}
			}
		});
	}

	public boolean getAntialiasing() {
		return model.get(Roles.Antialiasing).asType(Boolean.class, Boolean.FALSE).booleanValue();
	}

	public boolean getChecked() {
		return model.get(Roles.Checked).asType(Boolean.class, Boolean.FALSE).booleanValue();
	}

	public CheckedState getCheckedState() {
		final int ordinal = model.get(Roles.CheckedState).asType(Integer.class, Integer.valueOf(0)).intValue();
		if (0 <= ordinal && ordinal < 3) {
			return CheckedState.values()[ordinal];
		} else {
			return CheckedState.UnChecked;
		}
	}

	public boolean getClip() {
		return model.get(Roles.Clip).asType(Boolean.class, Boolean.FALSE).booleanValue();
	}

	public boolean getEnabled() {
		return model.get(Roles.Enabled).asType(Boolean.class, Boolean.FALSE).booleanValue();
	}

	public int getHeight() {
		return model.get(Roles.Height).asType(Integer.class, Integer.valueOf(0)).intValue();
	}

	public String getModelName() {
		return model.getModelName();
	}

	public double getOpacity() {
		return model.get(Roles.Opacity).asType(Double.class, Double.valueOf(1.0)).doubleValue();
	}

	public boolean getPartiallyCheckedEnabled() {
		return model.get(Roles.PartiallyCheckedEnabled).asType(Boolean.class, Boolean.FALSE).booleanValue();
	}

	public double getRotation() {
		return model.get(Roles.Rotation).asType(Double.class, Double.valueOf(0)).doubleValue();
	}

	public double getScale() {
		return model.get(Roles.Scale).asType(Double.class, Double.valueOf(0)).doubleValue();
	}

	public boolean getVisible() {
		return model.get(Roles.Visible).asType(Boolean.class, Boolean.FALSE).booleanValue();
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

	public void setChecked(final boolean checked) {
		model.put(Roles.Checked, new JVariant(checked));
	}

	public void setCheckedState(final CheckedState s) {
		Objects.requireNonNull(s, "s is null");
		model.put(Roles.Checked, new JVariant(s.ordinal()));
	}

	public void setClip(final boolean clip) {
		model.put(Roles.Clip, new JVariant(clip));
	}

	public void setEnabled(final boolean enabled) {
		model.put(Roles.Enabled, new JVariant(enabled));
	}

	public void setHeight(final int height) {
		model.put(Roles.Height, new JVariant(height));
	}

	public void setOpacity(final double value) {
		model.put(Roles.Opacity, new JVariant(value));
	}

	public void setPartiallyCheckedEnabled(final boolean enabled) {
		model.put(Roles.PartiallyCheckedEnabled, new JVariant(enabled));
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
