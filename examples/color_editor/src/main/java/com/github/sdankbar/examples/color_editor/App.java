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
package com.github.sdankbar.examples.color_editor;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.github.sdankbar.qml.models.singleton.JQMLButtonModel;

/**
 * Example application that allow for editing a set of colors.
 *
 */
public class App {

	/**
	 *
	 *
	 */
	public interface EventProcessor {

		/**
		 * @param e
		 */
		default void handle(final PresetColorEditedEvent e) {
			// Empty Implementation
		}

		/**
		 * @param e
		 */
		default void handle(final PresetColorNameEditedEvent e) {
			// Empty Implementation
		}
	}

	private enum PresetColorsRoles {
		colorName,
		colorRGB
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new ColorEventFactory());
		final JQMLListModel<PresetColorsRoles> model = app.getModelFactory().createListModel("presetColors",
				PresetColorsRoles.class, PutMode.RETURN_PREVIOUS_VALUE);

		final ColorReadWrite crw = new ColorReadWrite();

		crw.readColors(new File("colors.txt"));

		int i = 0;
		for (final Map.Entry<String, Color> e : crw.getColors().entrySet()) {
			model.setData(i, PresetColorsRoles.colorName, new JVariant(e.getKey()));
			model.setData(i, PresetColorsRoles.colorRGB, new JVariant(e.getValue()));
			++i;
		}

		app.getEventDispatcher().register(PresetColorEditedEvent.class, new EventProcessor() {

			@Override
			public void handle(final PresetColorEditedEvent e) {
				model.setData(e.getIndex(), PresetColorsRoles.colorRGB, new JVariant(e.getNewColor()));
				e.setResult(new JVariant(42));
			}

		});

		app.getEventDispatcher().register(PresetColorNameEditedEvent.class, new EventProcessor() {

			@Override
			public void handle(final PresetColorNameEditedEvent e) {
				model.setData(e.getIndex(), PresetColorsRoles.colorName, new JVariant(e.getNewName()));
				e.setResult(new JVariant("Success"));
			}

		});

		final JQMLButtonModel addColor = app.getModelFactory().createButtonModel("addPresetColor");
		addColor.setText("Add");
		addColor.registerOnClicked(() -> {
			final int index = model.size();
			model.setData(index, PresetColorsRoles.colorName, new JVariant("<NewColor>"));
			model.setData(index, PresetColorsRoles.colorRGB, new JVariant(Color.BLACK));
		});

		final JQMLButtonModel removeColor = app.getModelFactory().createButtonModel("removePresetColor");
		removeColor.setText("Remove");

		final JQMLButtonModel saveColor = app.getModelFactory().createButtonModel("saveColors");
		saveColor.setText("Save");
		saveColor.registerOnClicked(() -> {
			try {
				crw.clear();

				for (int j = 0; j < model.size(); ++j) {
					final JVariant name = model.getData(j, PresetColorsRoles.colorName).get();
					final JVariant hexRGB = model.getData(j, PresetColorsRoles.colorRGB).get();
					crw.put(name.asString(), hexRGB.asColor());
				}

				crw.writeColors(new File("colors.txt"));
			} catch (final IOException e1) {
				e1.printStackTrace();
			}
		});

		app.loadAndWatchQMLFile("./src/main/qml/main.qml");

		app.execute();
	}
}
