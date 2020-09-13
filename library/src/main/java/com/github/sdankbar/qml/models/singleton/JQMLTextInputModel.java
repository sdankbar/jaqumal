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
package com.github.sdankbar.qml.models.singleton;

import java.awt.Point;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import com.github.sdankbar.qml.JQMLModelFactory;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.eventing.EventDispatcher;
import com.github.sdankbar.qml.eventing.builtin.BuiltinEventProcessor;
import com.github.sdankbar.qml.eventing.builtin.TextInputAcceptedEvent;
import com.github.sdankbar.qml.eventing.builtin.TextInputEditingFinishedEvent;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.interfaces.BooleanListener;

public class JQMLTextInputModel implements BuiltinEventProcessor {

	private enum TextInputRoles {
		ModelName, //
		Height, //
		Width, //
		Focus, //
		ActiveFocus, // Read Only
		Enabled, //
		Opacity, //
		Visible, //
		InputMask, //
		MaximumLength, //
		ReadOnly, //
		Text, //
		Regex, //
		SelectionStartStop //
	}

	private final JQMLSingletonModel<TextInputRoles> model;

	private final List<Runnable> acceptedListeners = new CopyOnWriteArrayList<>();

	private final List<BooleanListener> focusListeners = new CopyOnWriteArrayList<>();

	public JQMLTextInputModel(final String modelName, final JQMLModelFactory factory,
			final EventDispatcher<?> dispatcher) {
		model = factory.createSingletonModel(modelName, TextInputRoles.class, PutMode.RETURN_PREVIOUS_VALUE);

		model.put(TextInputRoles.ModelName, new JVariant(modelName));
		model.put(TextInputRoles.Text, new JVariant(""));
		model.put(TextInputRoles.Enabled, new JVariant(true));
		model.put(TextInputRoles.Opacity, new JVariant(1.0));
		model.put(TextInputRoles.Visible, new JVariant(true));
		model.put(TextInputRoles.Width, new JVariant(100));
		model.put(TextInputRoles.Height, new JVariant(50));
		model.put(TextInputRoles.Focus, new JVariant(false));
		model.put(TextInputRoles.ActiveFocus, new JVariant(false));
		model.put(TextInputRoles.InputMask, new JVariant(""));
		model.put(TextInputRoles.MaximumLength, new JVariant(Integer.MAX_VALUE));
		model.put(TextInputRoles.ReadOnly, new JVariant(false));
		model.put(TextInputRoles.Regex, new JVariant(Pattern.compile(".*")));
		model.put(TextInputRoles.SelectionStartStop, new JVariant(new Point(0, 0)));

		model.registerChangeListener((key, newValue) -> {
			switch (key) {
				case "ActiveFocus":
					for (final BooleanListener l : focusListeners) {
						l.changed(newValue.asBoolean());
					}
					break;
			}
		});

		dispatcher.register(TextInputAcceptedEvent.class, this);
		dispatcher.register(TextInputEditingFinishedEvent.class, this);
	}

	public String getText() {
		final JVariant var = model.get(TextInputRoles.Text);
		if (var != null) {
			return var.asString();
		} else {
			return "";
		}
	}

	@Override
	public void handle(final TextInputAcceptedEvent e) {
		if (e.getObjectName().equals(model.getModelName())) {
			for (final Runnable r : acceptedListeners) {
				r.run();
			}
		}
	}

	public void registerAccepted(final Runnable r) {
		acceptedListeners.add(Objects.requireNonNull(r, "r is null"));
	}

	public void registerFocusChanged(final BooleanListener l) {
		focusListeners.add(Objects.requireNonNull(l, "l is null"));
	}

	public void setEnabled(final boolean enabled) {
		model.put(TextInputRoles.Enabled, new JVariant(enabled));
	}

	public void setFocus(final boolean focused) {
		model.put(TextInputRoles.Focus, new JVariant(focused));
	}

	public void setOpacity(final double value) {
		model.put(TextInputRoles.Opacity, new JVariant(value));
	}

	public void setSelection(final int startIndex, final int endIndex) {
		model.put(TextInputRoles.SelectionStartStop, new JVariant(new Point(startIndex, endIndex)));
	}

	public void setText(final String text) {
		model.put(TextInputRoles.Text, new JVariant(text));
	}

	public void setVisible(final boolean visible) {
		model.put(TextInputRoles.Visible, new JVariant(visible));
	}

}
