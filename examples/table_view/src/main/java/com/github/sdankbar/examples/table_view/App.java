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
package com.github.sdankbar.examples.table_view;

import java.io.File;

import javax.imageio.ImageIO;

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.eventing.NullEventProcessor;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.table.JQMLTableModel;
import com.google.common.collect.ImmutableMap;

/**
 * Example application that allow for editing a set of colors.
 *
 */
public class App {

	public enum TableRole {
		text, is_checked, delegate, row, column
	}

	private static JVariant getDelegate(final int column) {
		if (column == 1) {
			return new JVariant("CheckboxDelegate.qml");
		} else {
			return new JVariant("TextDelegate.qml");
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final JQMLApplication<NullEventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLTableModel<TableRole> model = app.getModelFactory().createTableModel("table_model", TableRole.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.addRow();
		model.addRow();
		model.addRow();

		model.addColumn();
		model.addColumn();

		for (int r = 0; r < model.getRowCount(); ++r) {
			for (int c = 0; c < model.getColumnCount(); ++c) {
				model.setData(r, c, ImmutableMap.of(TableRole.text, new JVariant(r + "," + c), TableRole.is_checked,
						JVariant.valueOf(r == 1 && c == 2), TableRole.delegate, getDelegate(c)));
			}
		}

		app.setWindowIcon(ImageIO.read(new File("./icon.png")));

		app.loadAndWatchQMLFile("./src/main/qml/main.qml");

		app.execute();
	}
}
