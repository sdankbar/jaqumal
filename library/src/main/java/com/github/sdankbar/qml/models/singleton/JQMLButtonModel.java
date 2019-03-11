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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.sdankbar.qml.JQMLModelFactory;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.JVariant.Type;
import com.github.sdankbar.qml.eventing.EventDispatcher;
import com.github.sdankbar.qml.eventing.builtin.BuiltinEventProcessor;
import com.github.sdankbar.qml.eventing.builtin.ButtonClickEvent;
import com.github.sdankbar.qml.models.interfaces.BooleanListener;

/**
 * QML model for getting and setting properties for a QML Button. The
 * JButton.qml file has a model property and setting it to a model of this type
 * provides automatic synchronization between the QML and the model.
 */
public class JQMLButtonModel {

	public enum ButtonRoles {
		ModelName, //
		ActiveFocusOnPressed, //
		Checkable, //
		Checked, //
		Hovered, // Read Only
		IconName, //
		IconSource, //
		IsDefault, //
		Pressed, // Read Only
		Text, //
		Tooltip, //
		ActiveFocus, // Read Only
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

	private static class ClickListener implements BuiltinEventProcessor {
		private final List<Runnable> clickListeners = new ArrayList<>();
		private final String modelName;

		public ClickListener(final String modelName) {
			this.modelName = modelName;
		}

		public void add(final Runnable r) {
			clickListeners.add(r);
		}

		@Override
		public void handle(final ButtonClickEvent e) {
			if (Objects.equals(modelName, e.getButtonName())) {
				for (final Runnable r : clickListeners) {
					r.run();
				}
			}
		}
	}

	private final JQMLSingletonModel<ButtonRoles> model;

	private final ClickListener clickListener;

	private final List<BooleanListener> hoverListeners = new CopyOnWriteArrayList<>();
	private final List<BooleanListener> focusListeners = new CopyOnWriteArrayList<>();
	private final List<BooleanListener> pressedListeners = new CopyOnWriteArrayList<>();

	public JQMLButtonModel(final String modelName, final JQMLModelFactory factory,
			final EventDispatcher<?> dispatcher) {
		model = factory.createSingletonModel(modelName, ButtonRoles.class);

		model.put(ButtonRoles.ModelName, new JVariant(modelName));
		model.put(ButtonRoles.ActiveFocusOnPressed, new JVariant(true));
		model.put(ButtonRoles.Checkable, new JVariant(false));
		model.put(ButtonRoles.Checked, new JVariant(false));
		model.put(ButtonRoles.IconName, new JVariant(""));
		model.put(ButtonRoles.IconSource, new JVariant(""));
		model.put(ButtonRoles.IsDefault, new JVariant(false));
		model.put(ButtonRoles.Text, new JVariant(""));
		model.put(ButtonRoles.Tooltip, new JVariant(""));
		model.put(ButtonRoles.Clip, new JVariant(false));
		model.put(ButtonRoles.Enabled, new JVariant(true));
		model.put(ButtonRoles.Opacity, new JVariant(1.0));
		model.put(ButtonRoles.Visible, new JVariant(true));
		model.put(ButtonRoles.Width, new JVariant(100));
		model.put(ButtonRoles.Height, new JVariant(50));
		model.put(ButtonRoles.X, new JVariant(0));
		model.put(ButtonRoles.Y, new JVariant(0));
		model.put(ButtonRoles.Z, new JVariant(0));

		model.registerChangeListener((key, newValue) -> {
			switch (key) {
			case "Hovered":
				for (final BooleanListener l : hoverListeners) {
					l.changed(newValue.asBoolean());
				}
				break;
			case "ActiveFocus":
				for (final BooleanListener l : focusListeners) {
					l.changed(newValue.asBoolean());
				}
				break;
			case "Pressed":
				for (final BooleanListener l : pressedListeners) {
					l.changed(newValue.asBoolean());
				}
				break;
			}
		});

		clickListener = new ClickListener(modelName);
		dispatcher.register(ButtonClickEvent.class, clickListener);
	}

	/**
	 * @return The state of the button's activeFocus property.
	 */
	public boolean getActiveFocus() {
		final JVariant value = model.get(ButtonRoles.ActiveFocus);
		if (value.isInstanceOf(Type.BOOL)) {
			return value.asBoolean();
		} else {
			return false;
		}
	}

	/**
	 * @return The state of the button's activeFocusOnPressed property.
	 */
	public boolean getActiveFocusOnPressed() {
		final JVariant value = model.get(ButtonRoles.ActiveFocusOnPressed);
		if (value.isInstanceOf(Type.BOOL)) {
			return value.asBoolean();
		} else {
			return false;
		}
	}

	/**
	 * @return The state of the button's anti-aliasing property.
	 */
	public boolean getAntialiasing() {
		final JVariant value = model.get(ButtonRoles.Antialiasing);
		if (value.isInstanceOf(Type.BOOL)) {
			return value.asBoolean();
		} else {
			return false;
		}
	}

	/**
	 * @return The state of the button's checkable property.
	 */
	public boolean getCheckable() {
		final JVariant value = model.get(ButtonRoles.Checkable);
		if (value.isInstanceOf(Type.BOOL)) {
			return value.asBoolean();
		} else {
			return false;
		}
	}

	/**
	 * @return The state of the button's checked property.
	 */
	public boolean getChecked() {
		final JVariant value = model.get(ButtonRoles.Checked);
		if (value.isInstanceOf(Type.BOOL)) {
			return value.asBoolean();
		} else {
			return false;
		}
	}

	/**
	 * @return The state of the button's clip property.
	 */
	public boolean getClip() {
		final JVariant value = model.get(ButtonRoles.Clip);
		if (value.isInstanceOf(Type.BOOL)) {
			return value.asBoolean();
		} else {
			return false;
		}
	}

	/**
	 * @return The state of the button's enabled property.
	 */
	public boolean getEnabled() {
		final JVariant value = model.get(ButtonRoles.Enabled);
		if (value.isInstanceOf(Type.BOOL)) {
			return value.asBoolean();
		} else {
			return false;
		}
	}

	/**
	 * @return The state of the button's height property.
	 */
	public int getHeight() {
		final JVariant value = model.get(ButtonRoles.Height);
		if (value.isInstanceOf(Type.INT)) {
			return value.asInteger();
		} else {
			return 0;
		}
	}

	/**
	 * @return The state of the button's hovered property.
	 */
	public boolean getHovered() {
		final JVariant value = model.get(ButtonRoles.Hovered);
		if (value.isInstanceOf(Type.BOOL)) {
			return value.asBoolean();
		} else {
			return false;
		}
	}

	/**
	 * @return The state of the button's iconName property.
	 */
	public String getIconName() {
		final JVariant value = model.get(ButtonRoles.IconName);
		if (value.isInstanceOf(Type.STRING)) {
			return value.asString();
		} else {
			return "";
		}
	}

	/**
	 * @return The state of the button's iconSource property.
	 */
	public String getIconSource() {
		final JVariant value = model.get(ButtonRoles.IconSource);
		if (value.isInstanceOf(Type.STRING)) {
			return value.asString();
		} else {
			return "";
		}
	}

	/**
	 * @return The state of the button's isDefault property.
	 */
	public boolean getIsDefault() {
		final JVariant value = model.get(ButtonRoles.IsDefault);
		if (value.isInstanceOf(Type.BOOL)) {
			return value.asBoolean();
		} else {
			return false;
		}
	}

	/**
	 * @return The name of the model.
	 */
	public String getModelName() {
		return model.getModelName();
	}

	/**
	 * @return The state of the button's opacity property.
	 */
	public double getOpacity() {
		final JVariant value = model.get(ButtonRoles.Opacity);
		if (value.isInstanceOf(Type.DOUBLE)) {
			return value.asDouble();
		} else {
			return 1.0;
		}
	}

	/**
	 * @return The state of the button's pressed property.
	 */
	public boolean getPressed() {
		final JVariant value = model.get(ButtonRoles.Pressed);
		if (value.isInstanceOf(Type.BOOL)) {
			return value.asBoolean();
		} else {
			return false;
		}
	}

	/**
	 * @return The state of the button's rotation property.
	 */
	public double getRotation() {
		final JVariant value = model.get(ButtonRoles.Rotation);
		if (value.isInstanceOf(Type.DOUBLE)) {
			return value.asDouble();
		} else {
			return 0;
		}
	}

	/**
	 * @return The state of the button's scale property.
	 */
	public double getScale() {
		final JVariant value = model.get(ButtonRoles.Scale);
		if (value.isInstanceOf(Type.DOUBLE)) {
			return value.asDouble();
		} else {
			return 0;
		}
	}

	/**
	 * @return The state of the button's text property.
	 */
	public String getText() {
		final JVariant value = model.get(ButtonRoles.Text);
		if (value.isInstanceOf(Type.STRING)) {
			return value.asString();
		} else {
			return "";
		}
	}

	/**
	 * @return The state of the button's tooltip property.
	 */
	public String getTooltip() {
		final JVariant value = model.get(ButtonRoles.Tooltip);
		if (value.isInstanceOf(Type.STRING)) {
			return value.asString();
		} else {
			return "";
		}
	}

	/**
	 * @return The state of the button's visible property.
	 */
	public boolean getVisible() {
		final JVariant value = model.get(ButtonRoles.Visible);
		if (value.isInstanceOf(Type.BOOL)) {
			return value.asBoolean();
		} else {
			return false;
		}
	}

	/**
	 * @return The state of the button's width property.
	 */
	public int getWidth() {
		final JVariant value = model.get(ButtonRoles.Width);
		if (value.isInstanceOf(Type.INT)) {
			return value.asInteger();
		} else {
			return 0;
		}
	}

	/**
	 * @return The state of the button's x property.
	 */
	public int getX() {
		final JVariant value = model.get(ButtonRoles.X);
		if (value.isInstanceOf(Type.INT)) {
			return value.asInteger();
		} else {
			return 0;
		}
	}

	/**
	 * @return The state of the button's y property.
	 */
	public int getY() {
		final JVariant value = model.get(ButtonRoles.Y);
		if (value.isInstanceOf(Type.INT)) {
			return value.asInteger();
		} else {
			return 0;
		}
	}

	/**
	 * @return The state of the button's z property.
	 */
	public int getZ() {
		final JVariant value = model.get(ButtonRoles.Z);
		if (value.isInstanceOf(Type.INT)) {
			return value.asInteger();
		} else {
			return 0;
		}
	}

	public void registerFocusChanged(final BooleanListener l) {
		focusListeners.add(Objects.requireNonNull(l, "l is null"));
	}

	public void registerHoverChanged(final BooleanListener l) {
		hoverListeners.add(Objects.requireNonNull(l, "l is null"));
	}

	public void registerOnClicked(final Runnable r) {
		clickListener.add(Objects.requireNonNull(r, "r is null"));
	}

	public void registerPressedChanged(final BooleanListener l) {
		pressedListeners.add(Objects.requireNonNull(l, "l is null"));
	}

	public void setActiveFocusOnPressed(final boolean active) {
		model.put(ButtonRoles.ActiveFocusOnPressed, new JVariant(active));
	}

	public void setAntialiasing(final boolean antialiasing) {
		model.put(ButtonRoles.Antialiasing, new JVariant(antialiasing));
	}

	public void setCheckable(final boolean checkable) {
		model.put(ButtonRoles.Checkable, new JVariant(checkable));
	}

	public void setChecked(final boolean checked) {
		model.put(ButtonRoles.Checked, new JVariant(checked));
	}

	public void setClip(final boolean clip) {
		model.put(ButtonRoles.Clip, new JVariant(clip));
	}

	public void setEnabled(final boolean enabled) {
		model.put(ButtonRoles.Enabled, new JVariant(enabled));
	}

	public void setHeight(final int height) {
		model.put(ButtonRoles.Height, new JVariant(height));
	}

	public void setIconName(final String iconName) {
		model.put(ButtonRoles.IconName, new JVariant(iconName));
	}

	public void setIconSource(final String iconSource) {
		model.put(ButtonRoles.IconSource, new JVariant(iconSource));
	}

	public void setIsDefault(final boolean isDefault) {
		model.put(ButtonRoles.IsDefault, new JVariant(isDefault));
	}

	public void setOpacity(final double value) {
		model.put(ButtonRoles.Opacity, new JVariant(value));
	}

	public void setRotation(final double rotation) {
		model.put(ButtonRoles.Rotation, new JVariant(rotation));
	}

	public void setScale(final double scale) {
		model.put(ButtonRoles.Scale, new JVariant(scale));
	}

	public void setSmooth(final boolean smooth) {
		model.put(ButtonRoles.Smooth, new JVariant(smooth));
	}

	public void setText(final String text) {
		model.put(ButtonRoles.Text, new JVariant(text));
	}

	public void setTooltip(final String tooltip) {
		model.put(ButtonRoles.Tooltip, new JVariant(tooltip));
	}

	public void setVisible(final boolean visible) {
		model.put(ButtonRoles.Visible, new JVariant(visible));
	}

	public void setWidth(final int width) {
		model.put(ButtonRoles.Width, new JVariant(width));
	}

	public void setX(final int x) {
		model.put(ButtonRoles.X, new JVariant(x));
	}

	public void setY(final int y) {
		model.put(ButtonRoles.Y, new JVariant(y));
	}

	public void setZ(final int z) {
		model.put(ButtonRoles.Z, new JVariant(z));
	}

}
