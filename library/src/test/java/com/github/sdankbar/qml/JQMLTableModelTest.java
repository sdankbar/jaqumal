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
package com.github.sdankbar.qml;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.table.JQMLTableModel;
import com.google.common.collect.ImmutableMap;

/**
 * Tests JQMLTableModel.
 */
public class JQMLTableModelTest {

	/**
	 *
	 */
	public interface EventProcessor {
		// Empty Implementation
	}

	private enum Roles {
		R1, R2, R3, R4, R5;
	}

	@After
	public void cleanup() {
		JQMLApplication.delete();
	}

	@Test
	public void addRow() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLTableModel<Roles> model = app.getModelFactory().createTableModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		assertEquals(1, model.getColumnCount());
		assertEquals(0, model.getRowCount());

		model.addRow();

		assertEquals(1, model.getColumnCount());
		assertEquals(1, model.getRowCount());

		model.setData(0, 0, ImmutableMap.of(Roles.R1, new JVariant(0)));
		model.addRow(0);
		model.setData(0, 0, ImmutableMap.of(Roles.R1, new JVariant(1)));

		assertEquals(1, model.getColumnCount());
		assertEquals(2, model.getRowCount());

		assertEquals(1, model.get(0, 0).get(Roles.R1).asInteger());
		assertEquals(0, model.get(1, 0).get(Roles.R1).asInteger());
	}

	@Test
	public void addColumn() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLTableModel<Roles> model = app.getModelFactory().createTableModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		assertEquals(1, model.getColumnCount());
		assertEquals(0, model.getRowCount());

		model.addRow();
		model.addRow();
		model.addRow();

		model.addColumn();
		model.addColumn();

		assertEquals(3, model.getColumnCount());
		assertEquals(3, model.getRowCount());

		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 3; ++col) {
				model.setData(row, col, ImmutableMap.of(Roles.R1, new JVariant(row)));
			}
		}

		model.addColumn(2);

		assertEquals(4, model.getColumnCount());
		assertEquals(3, model.getRowCount());

		for (int row = 0; row < 3; ++row) {
			assertEquals(row, model.get(row, 0).get(Roles.R1).asInteger());
			assertEquals(row, model.get(row, 1).get(Roles.R1).asInteger());
			assertEquals(null, model.get(row, 2).get(Roles.R1));
			assertEquals(row, model.get(row, 3).get(Roles.R1).asInteger());
		}

		model.addRow(1);

		assertEquals(0, model.get(0, 0).get(Roles.R1).asInteger());
		assertEquals(0, model.get(0, 1).get(Roles.R1).asInteger());
		assertEquals(null, model.get(0, 2).get(Roles.R1));
		assertEquals(0, model.get(0, 3).get(Roles.R1).asInteger());

		assertEquals(null, model.get(1, 0).get(Roles.R1));
		assertEquals(null, model.get(1, 1).get(Roles.R1));
		assertEquals(null, model.get(1, 2).get(Roles.R1));
		assertEquals(null, model.get(1, 3).get(Roles.R1));

		assertEquals(1, model.get(2, 0).get(Roles.R1).asInteger());
		assertEquals(1, model.get(2, 1).get(Roles.R1).asInteger());
		assertEquals(null, model.get(2, 2).get(Roles.R1));
		assertEquals(1, model.get(2, 3).get(Roles.R1).asInteger());

		assertEquals(2, model.get(3, 0).get(Roles.R1).asInteger());
		assertEquals(2, model.get(3, 1).get(Roles.R1).asInteger());
		assertEquals(null, model.get(3, 2).get(Roles.R1));
		assertEquals(2, model.get(3, 3).get(Roles.R1).asInteger());

		model.removeRow(1);
	}

}
