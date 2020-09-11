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
package com.github.sdankbar.qml;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.After;
import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.list.JQMLListViewModel;
import com.github.sdankbar.qml.models.list.JQMLListViewModel.AssignMode;
import com.github.sdankbar.qml.models.list.JQMLListViewModel.SelectionMode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Tests JQMLListViewModel.
 */
public class JQMLListViewModelTest {

	/**
	 *
	 */
	public interface EventProcessor {
		// Empty Implementation
	}

	private enum Roles {
		R1,
		R2,
		R3,
		R4,
		R5,
		is_selected;
	}

	/**
	 *
	 */
	@After
	public void cleanup() {
		JQMLApplication.delete();
	}

	/**
	 *
	 */
	@Test
	public void assign_mapClearSelection() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListViewModel<Roles> model = JQMLListViewModel.create("list_view", Roles.class, app,
				SelectionMode.SINGLE, PutMode.RETURN_PREVIOUS_VALUE);

		{
			final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
			data.put(Roles.R1, new JVariant("A"));
			data.put(Roles.R5, new JVariant(5));

			model.getModel().add(data.build());
		}

		{
			final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
			data.put(Roles.R1, new JVariant("B"));
			data.put(Roles.R4, new JVariant(4));

			model.getModel().add(data.build());
		}

		model.select(1);

		assertEquals(new JVariant("A"), model.getModel().getData(0, Roles.R1).get());
		assertEquals(new JVariant(5), model.getModel().getData(0, Roles.R5).get());
		assertEquals(new JVariant("B"), model.getModel().getData(1, Roles.R1).get());
		assertEquals(new JVariant(4), model.getModel().getData(1, Roles.R4).get());

		assertEquals(ImmutableList.of(Integer.valueOf(1)), model.getSelectedIndices());
		final ImmutableList<Map<Roles, JVariant>> selected1 = model.getSelected();
		assertEquals(ImmutableList.of(ImmutableMap.of(Roles.R1, new JVariant("B"), Roles.R4, new JVariant(4),
				Roles.is_selected, JVariant.TRUE)), selected1);

		model.assign(
				ImmutableList.of(ImmutableMap.of(Roles.R1, new JVariant("A"), Roles.R3, new JVariant(3)),
						ImmutableMap.of(Roles.R1, new JVariant("B"), Roles.R3, new JVariant(9))),
				Roles.R1, AssignMode.CLEAR_SELECTION);

		assertEquals(2, model.getModel().size());
		assertEquals(new JVariant(3), model.getModel().getData(0, Roles.R3).get());
		assertEquals(3, model.getModel().get(0).size());
		assertEquals(new JVariant(9), model.getModel().getData(1, Roles.R3).get());
		assertEquals(3, model.getModel().get(1).size());

		assertEquals(ImmutableList.of(), model.getSelectedIndices());
		assertEquals(ImmutableList.of(), model.getSelected());
	}

	/**
	 *
	 */
	@Test
	public void assign_mapMaintainSelection() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListViewModel<Roles> model = JQMLListViewModel.create("list_view", Roles.class, app,
				SelectionMode.SINGLE, PutMode.RETURN_PREVIOUS_VALUE);

		{
			final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
			data.put(Roles.R1, new JVariant("A"));
			data.put(Roles.R5, new JVariant(5));

			model.getModel().add(data.build());
		}

		{
			final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
			data.put(Roles.R1, new JVariant("B"));
			data.put(Roles.R4, new JVariant(4));

			model.getModel().add(data.build());
		}

		model.select(1);

		assertEquals(new JVariant("A"), model.getModel().getData(0, Roles.R1).get());
		assertEquals(new JVariant(5), model.getModel().getData(0, Roles.R5).get());
		assertEquals(new JVariant("B"), model.getModel().getData(1, Roles.R1).get());
		assertEquals(new JVariant(4), model.getModel().getData(1, Roles.R4).get());

		assertEquals(ImmutableList.of(Integer.valueOf(1)), model.getSelectedIndices());
		assertEquals(ImmutableList.of(ImmutableMap.of(Roles.R1, new JVariant("B"), Roles.R4, new JVariant(4),
				Roles.is_selected, JVariant.TRUE)), model.getSelected());

		model.assign(
				ImmutableList.of(ImmutableMap.of(Roles.R1, new JVariant("A"), Roles.R3, new JVariant(3)),
						ImmutableMap.of(Roles.R1, new JVariant("B"), Roles.R3, new JVariant(9))),
				Roles.R1, AssignMode.MAINTAIN_SELECTION);

		assertEquals(2, model.getModel().size());
		assertEquals(new JVariant(3), model.getModel().getData(0, Roles.R3).get());
		assertEquals(3, model.getModel().get(0).size());
		assertEquals(new JVariant(9), model.getModel().getData(1, Roles.R3).get());
		assertEquals(3, model.getModel().get(1).size());

		assertEquals(ImmutableList.of(Integer.valueOf(1)), model.getSelectedIndices());
		assertEquals(ImmutableList.of(ImmutableMap.of(Roles.R1, new JVariant("B"), Roles.R3, new JVariant(9),
				Roles.is_selected, JVariant.TRUE)), model.getSelected());
	}

}
