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

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.eventing.NullEventProcessor;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.table.JQMLTableModel;

/**
 * Example application that allow for editing a set of colors.
 *
 */
public class App {

	public enum TableRole {
		text, row, column
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

		app.loadAndWatchQMLFile("./src/main/qml/main.qml");

		app.execute();
	}
}
