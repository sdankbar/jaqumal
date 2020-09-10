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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

import org.junit.After;
import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.google.common.collect.ImmutableMap;

/**
 * Tests JQMLListModel.
 */
public class JQMLListModelTest {

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
		R5;
	}

	/**
	 *
	 */
	@Test
	public void append() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
		data.put(Roles.R1, new JVariant(1));
		data.put(Roles.R5, new JVariant(5));

		model.add(data.build());

		assertEquals(new JVariant(1), model.getData(0, Roles.R1).get());
		assertEquals(new JVariant(5), model.getData(0, Roles.R5).get());
	}

	/**
	 *
	 */
	@Test
	public void append_all_map() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		final Collection<Map<Roles, JVariant>> l = new ArrayList<>();
		final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
		data.put(Roles.R1, new JVariant(1));
		data.put(Roles.R5, new JVariant(5));
		l.add(data.build());

		data.put(Roles.R2, new JVariant(2));
		l.add(data.build());

		model.addAll(l);

		assertEquals(new JVariant(1), model.getData(0, Roles.R1).get());
		assertEquals(new JVariant(5), model.getData(0, Roles.R5).get());

		assertEquals(new JVariant(1), model.getData(1, Roles.R1).get());
		assertEquals(new JVariant(2), model.getData(1, Roles.R2).get());
		assertEquals(new JVariant(5), model.getData(1, Roles.R5).get());
	}

	/**
	 *
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void append_all_mutable_map() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		final Collection<Map<Roles, JVariant>> l = new ArrayList<>();
		final Map<Roles, JVariant> data = new HashMap<>();
		data.put(Roles.R1, new JVariant(1));
		data.put(Roles.R5, new JVariant(5));
		l.add(data);

		model.addAll(l);
	}

	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void append_map() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
		data.put(Roles.R1, new JVariant(1));
		data.put(Roles.R5, new JVariant(5));

		model.add((Map<Roles, JVariant>) data.build());

		assertEquals(new JVariant(1), model.getData(0, Roles.R1).get());
		assertEquals(new JVariant(5), model.getData(0, Roles.R5).get());
	}

	/**
	 *
	 */
	@Test
	public void assign_map() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		{
			final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
			data.put(Roles.R1, new JVariant(1));
			data.put(Roles.R5, new JVariant(5));

			model.add(data.build());
		}

		{
			final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
			data.put(Roles.R2, new JVariant(2));
			data.put(Roles.R4, new JVariant(4));

			model.add(data.build());
		}

		assertEquals(new JVariant(1), model.getData(0, Roles.R1).get());
		assertEquals(new JVariant(5), model.getData(0, Roles.R5).get());
		assertEquals(new JVariant(2), model.getData(1, Roles.R2).get());
		assertEquals(new JVariant(4), model.getData(1, Roles.R4).get());

		model.assign(0, ImmutableMap.of(Roles.R3, new JVariant(3)));
		model.assign(1, ImmutableMap.of(Roles.R3, new JVariant(9)));

		assertEquals(new JVariant(3), model.getData(0, Roles.R3).get());
		assertEquals(1, model.get(0).size());
		assertEquals(new JVariant(9), model.getData(1, Roles.R3).get());
		assertEquals(1, model.get(1).size());
	}

	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	@Test(expected = UnsupportedOperationException.class)
	public void append_map_error() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		final Map<Roles, JVariant> data = new HashMap<>();
		data.put(Roles.R1, new JVariant(1));
		data.put(Roles.R5, new JVariant(5));

		model.add(data);
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
	public void clear() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(3), Roles.R3);

		assertEquals(new JVariant(1), model.getData(0, Roles.R1).get());
		assertEquals(new JVariant(3), model.getData(1, Roles.R3).get());

		model.clear(0);
		model.remove(1, Roles.R3);
		assertFalse(model.getData(0, Roles.R1).isPresent());
		assertFalse(model.getData(1, Roles.R3).isPresent());
	}

	/**
	 *
	 */
	@Test
	public void clearFully() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
		data.put(Roles.R1, new JVariant(1));
		data.put(Roles.R5, new JVariant(5));

		model.add(data.build());

		assertEquals(new JVariant(1), model.getData(0, Roles.R1).get());
		assertEquals(new JVariant(5), model.getData(0, Roles.R5).get());

		model.clear();

		assertEquals(0, model.size());
	}

	/**
	 *
	 */
	@Test
	public void clearMap() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(3), Roles.R3);

		final Map<Roles, JVariant> m0 = model.get(0);
		final Map<Roles, JVariant> m1 = model.get(1);

		assertEquals(new JVariant(1), m0.get(Roles.R1));
		assertEquals(null, m0.get(Roles.R2));
		assertEquals(null, m0.get(Roles.R3));
		assertEquals(null, m0.get(Roles.R4));
		assertEquals(null, m0.get(Roles.R5));

		assertEquals(null, m1.get(Roles.R1));
		assertEquals(null, m1.get(Roles.R2));
		assertEquals(new JVariant(3), m1.get(Roles.R3));
		assertEquals(null, m1.get(Roles.R4));
		assertEquals(null, m1.get(Roles.R5));

		m0.clear();

		assertEquals(null, m0.get(Roles.R1));
		assertEquals(null, m0.get(Roles.R2));
		assertEquals(null, m0.get(Roles.R3));
		assertEquals(null, m0.get(Roles.R4));
		assertEquals(null, m0.get(Roles.R5));
	}

	/**
	 *
	 */
	@Test
	public void contains() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(3), Roles.R3);

		assertFalse(model.contains(null));
		assertTrue(model.contains(ImmutableMap.of(Roles.R1, new JVariant(1))));
		assertFalse(model.contains(ImmutableMap.of(Roles.R5, new JVariant(5))));
	}

	/**
	 *
	 */
	@Test
	public void containsAll() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(3), Roles.R3);

		assertFalse(model.contains(null));
		assertTrue(model.containsAll(
				Arrays.asList(ImmutableMap.of(Roles.R1, new JVariant(1)), ImmutableMap.of(Roles.R3, new JVariant(3)))));
		assertFalse(model.containsAll(Arrays.asList(ImmutableMap.of(Roles.R1, new JVariant(1)),
				ImmutableMap.of(Roles.R5, new JVariant(5)), ImmutableMap.of(Roles.R3, new JVariant(3)))));
	}

	/**
	 *
	 */
	@Test
	public void erase() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(3), Roles.R3);

		model.remove(1);

		assertEquals(model.size(), 1);
	}

	/**
	 *
	 */
	@Test
	public void get() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		assertTrue(model.isEmpty());

		model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(3), Roles.R3);

		assertFalse(model.isEmpty());

		final Map<Roles, JVariant> m0 = model.get(0);
		final Map<Roles, JVariant> m1 = model.get(1);

		assertEquals(new JVariant(1), m0.get(Roles.R1));
		assertEquals(null, m0.get(Roles.R2));
		assertEquals(null, m0.get(Roles.R3));
		assertEquals(null, m0.get(Roles.R4));
		assertEquals(null, m0.get(Roles.R5));

		assertEquals(null, m1.get(Roles.R1));
		assertEquals(null, m1.get(Roles.R2));
		assertEquals(new JVariant(3), m1.get(Roles.R3));
		assertEquals(null, m1.get(Roles.R4));
		assertEquals(null, m1.get(Roles.R5));
	}

	/**
	 *
	 */
	@Test
	public void getModelName() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		assertEquals("other", model.getModelName());
	}

	/**
	 *
	 */
	@Test
	public void getSetValues() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(3), Roles.R3);
		model.setData(0, Roles.R5, new JVariant(5));
		model.setData(2, Roles.R4, new JVariant(4));

		assertEquals(new JVariant(1), model.getData(0, Roles.R1).get());
		assertEquals(new JVariant(3), model.getData(1, Roles.R3).get());
		assertEquals(new JVariant(4), model.get(2).get(Roles.R4));
		assertEquals(new JVariant(5), model.getData(0, Roles.R5).get());
	}

	/**
	 *
	 */
	@Test
	public void indexOf() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(3), Roles.R3);

		assertEquals(-1, model.indexOf(null));
		assertEquals(0, model.indexOf(ImmutableMap.of(Roles.R1, new JVariant(1))));
		assertEquals(1, model.indexOf(ImmutableMap.of(Roles.R3, new JVariant(3))));
	}

	/**
	 *
	 */
	@Test
	public void insert() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", EnumSet.allOf(Roles.class),
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(0, new JVariant(1), Roles.R1);
		model.add(0, new JVariant(3), Roles.R3);
		model.add(0, new JVariant(5), Roles.R5);

		assertEquals(new JVariant(5), model.getData(0, Roles.R5).get());
		assertEquals(new JVariant(3), model.getData(1, Roles.R3).get());
		assertEquals(new JVariant(1), model.getData(2, Roles.R1).get());
	}

	/**
	 *
	 */
	@Test
	public void insert_all_map() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		final Collection<Map<Roles, JVariant>> l = new ArrayList<>();
		final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
		data.put(Roles.R1, new JVariant(1));
		data.put(Roles.R5, new JVariant(5));
		l.add(data.build());

		data.put(Roles.R2, new JVariant(2));
		l.add(data.build());

		model.addAll(0, l);

		assertEquals(new JVariant(1), model.getData(0, Roles.R1).get());
		assertEquals(new JVariant(5), model.getData(0, Roles.R5).get());

		assertEquals(new JVariant(1), model.getData(1, Roles.R1).get());
		assertEquals(new JVariant(2), model.getData(1, Roles.R2).get());
		assertEquals(new JVariant(5), model.getData(1, Roles.R5).get());
	}

	/**
	 *
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void insert_all_mutable_map() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		final Collection<Map<Roles, JVariant>> l = new ArrayList<>();
		final Map<Roles, JVariant> data = new HashMap<>();
		data.put(Roles.R1, new JVariant(1));
		data.put(Roles.R5, new JVariant(5));
		l.add(data);

		model.addAll(0, l);
	}

	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void insert_map() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
		data.put(Roles.R1, new JVariant(1));
		data.put(Roles.R5, new JVariant(5));

		model.add(0, (Map<Roles, JVariant>) data.build());

		assertEquals(1, model.size());

		assertEquals(new JVariant(1), model.getData(0, Roles.R1).get());
		assertEquals(new JVariant(5), model.getData(0, Roles.R5).get());
	}

	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	@Test(expected = UnsupportedOperationException.class)
	public void insert_mutable_map() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		final Map<Roles, JVariant> data = new HashMap<>();
		data.put(Roles.R1, new JVariant(1));
		data.put(Roles.R5, new JVariant(5));

		model.add(0, data);
	}

	/**
	 *
	 */
	@Test
	public void isPresent() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", EnumSet.allOf(Roles.class),
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(0, new JVariant(1), Roles.R1);
		model.add(0, new JVariant(3), Roles.R3);
		model.add(0, new JVariant(5), Roles.R5);

		assertTrue(model.isPresent(0, Roles.R5));
		assertFalse(model.isPresent(1, Roles.R5));
	}

	/**
	 *
	 */
	@Test
	public void iterator() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(2), Roles.R2);

		int count = 0;
		for (final Map<Roles, JVariant> m : model) {
			if (m.equals(ImmutableMap.of(Roles.R1, new JVariant(1)))) {
				// Empty Implementation
			} else if (m.equals(ImmutableMap.of(Roles.R2, new JVariant(2)))) {
				// Empty Implementation
			} else {
				assertTrue(false);
			}
			++count;
		}

		assertEquals(2, count);
	}

	/**
	 *
	 */
	@Test
	public void lastIndexOf() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(1), Roles.R1);

		assertEquals(-1, model.lastIndexOf(null));
		assertEquals(1, model.lastIndexOf(ImmutableMap.of(Roles.R1, new JVariant(1))));
		assertEquals(-1, model.lastIndexOf(ImmutableMap.of(Roles.R1, new JVariant(2))));
	}

	/**
	 *
	 */
	@Test
	public void listIterator() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(2), Roles.R2);

		int count = 0;
		final ListIterator<Map<Roles, JVariant>> iter = model.listIterator();
		while (iter.hasNext()) {
			final Map<Roles, JVariant> m = iter.next();
			if (m.equals(ImmutableMap.of(Roles.R1, new JVariant(1)))) {
				// Empty Implementation
			} else if (m.equals(ImmutableMap.of(Roles.R2, new JVariant(2)))) {
				// Empty Implementation
			} else {
				assertTrue(false);
			}
			++count;
		}

		assertEquals(2, count);

		try {
			iter.remove();
			assertTrue(false);
		} catch (final UnsupportedOperationException e) {
			// Expected
		}

		count = 0;
		final ListIterator<Map<Roles, JVariant>> iter2 = model.listIterator(1);
		while (iter2.hasNext()) {
			final Map<Roles, JVariant> m = iter2.next();
			if (m.equals(ImmutableMap.of(Roles.R2, new JVariant(2)))) {
				// Empty Implementation
			} else {
				assertTrue(false);
			}
			++count;
		}

		assertEquals(1, count);
	}

	/**
	 *
	 */
	@Test
	public void putAndGetRootValues() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.putRootValue("TEST1", new JVariant("ABC"));
		assertEquals(new JVariant("ABC"), model.getRootValue("TEST1").get());
		assertEquals(Optional.empty(), model.getRootValue("TEST2"));
	}

	/**
	 *
	 */
	@Test
	public void removeAll() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(3), Roles.R3);

		assertEquals(2, model.size());

		assertTrue(model.removeAll(Arrays.asList(ImmutableMap.of(Roles.R1, new JVariant(1)),
				ImmutableMap.of(Roles.R3, new JVariant(3)), ImmutableMap.of(Roles.R5, new JVariant(5)))));

		assertEquals(0, model.size());
	}

	/**
	 *
	 */
	@Test
	public void removeMap() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);
		model.setData(0, Roles.R3, new JVariant(3));

		final Map<Roles, JVariant> m0 = model.get(0);

		assertEquals(new JVariant(1), m0.get(Roles.R1));
		assertEquals(null, m0.get(Roles.R2));
		assertEquals(new JVariant(3), m0.get(Roles.R3));
		assertEquals(null, m0.get(Roles.R4));
		assertEquals(null, m0.get(Roles.R5));

		m0.remove(Roles.R1);

		assertEquals(null, m0.get(Roles.R1));
		assertEquals(null, m0.get(Roles.R2));
		assertEquals(new JVariant(3), m0.get(Roles.R3));
		assertEquals(null, m0.get(Roles.R4));
		assertEquals(null, m0.get(Roles.R5));
	}

	/**
	 *
	 */
	@Test
	public void removeObject() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		final Map<Roles, JVariant> map = model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(3), Roles.R3);

		assertEquals(2, model.size());

		assertTrue(model.remove(ImmutableMap.of(Roles.R1, new JVariant(1))));

		assertEquals(1, model.size());

		assertFalse(model.remove(ImmutableMap.of(Roles.R3, new JVariant(4))));

		assertEquals(1, model.size());

		try {
			map.get(Roles.R1);
			assertTrue(false);
		} catch (final IllegalStateException e) {
			// Expected
		}
	}

	/**
	 *
	 */
	@Test
	public void removeRootValues() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.putRootValue("TEST1", new JVariant("ABC"));
		assertEquals(new JVariant("ABC"), model.getRootValue("TEST1").get());
		model.removeRootValue("TEST1");
		assertEquals(Optional.empty(), model.getRootValue("TEST1"));
		model.removeRootValue("TEST2");
	}

	/**
	 *
	 */
	@Test
	public void retainAll() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(3), Roles.R3);

		assertEquals(2, model.size());

		assertTrue(model.retainAll(
				Arrays.asList(ImmutableMap.of(Roles.R1, new JVariant(1)), ImmutableMap.of(Roles.R5, new JVariant(5)))));

		assertEquals(1, model.size());
	}

	/**
	 *
	 */
	@Test
	public void set() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		assertEquals(0, model.size());

		model.setData(0, ImmutableMap.of(Roles.R1, new JVariant(1)));
		model.get(0).putAll(ImmutableMap.of(Roles.R3, new JVariant(3)));

		assertEquals(1, model.size());

		assertEquals(new JVariant(1), model.getData(0, Roles.R1).get());
		assertEquals(new JVariant(3), model.getData(0, Roles.R3).get());
	}

	/**
	 *
	 */
	@Test
	public void setData() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		assertEquals(0, model.size());

		model.setData(0, ImmutableMap.of(Roles.R1, new JVariant(1)));
		model.setData(0, ImmutableMap.of(Roles.R3, new JVariant(3)));

		assertEquals(1, model.size());

		assertEquals(new JVariant(3), model.getData(0, Roles.R3).get());
	}

	/**
	 *
	 */
	@Test
	public void sublist() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.setData(0, ImmutableMap.of(Roles.R1, new JVariant(1)));
		model.setData(1, ImmutableMap.of(Roles.R3, new JVariant(3)));

		assertEquals(2, model.size());

		final List<Map<Roles, JVariant>> sub = model.subList(1, 2);

		assertEquals(1, sub.size());
		assertEquals(new JVariant(3), sub.get(0).get(Roles.R3));
	}

	/**
	 *
	 */
	@Test
	public void test_sort() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);// 0
		model.add(new JVariant(3), Roles.R1);// 1
		model.add(new JVariant(2), Roles.R1);// 2
		model.add(new JVariant(7), Roles.R1);// 3
		model.add(new JVariant(4), Roles.R1);// 4
		model.add(new JVariant(6), Roles.R1);// 5
		model.add(new JVariant(5), Roles.R1);// 6

		model.sort((o1, o2) -> Integer.compare(o1.get(Roles.R1).asInteger(), o2.get(Roles.R1).asInteger()));

		assertEquals(new JVariant(1), model.getData(0, Roles.R1).get());
		assertEquals(new JVariant(2), model.getData(1, Roles.R1).get());
		assertEquals(new JVariant(3), model.getData(2, Roles.R1).get());
		assertEquals(new JVariant(4), model.getData(3, Roles.R1).get());
		assertEquals(new JVariant(5), model.getData(4, Roles.R1).get());
		assertEquals(new JVariant(6), model.getData(5, Roles.R1).get());
		assertEquals(new JVariant(7), model.getData(6, Roles.R1).get());
	}

	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	@Test(expected = UnsupportedOperationException.class)
	public void test_unsupported_set() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.set(0, new HashMap<>());
	}

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void toArray() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(2), Roles.R2);

		int count = 0;
		for (final Object o : model.toArray()) {
			final Map<Roles, JVariant> m = (Map<Roles, JVariant>) o;
			if (m.equals(ImmutableMap.of(Roles.R1, new JVariant(1)))) {
				// Empty Implementation
			} else if (m.equals(ImmutableMap.of(Roles.R2, new JVariant(2)))) {
				// Empty Implementation
			} else {
				assertTrue(false);
			}
			++count;
		}

		assertEquals(2, count);
	}

	/**
	 *
	 */
	@Test
	public void toArray2() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.add(new JVariant(1), Roles.R1);
		model.add(new JVariant(2), Roles.R2);

		int count = 0;
		for (final Map<Roles, JVariant> m : model.toArray(new Map[2])) {
			if (m.equals(ImmutableMap.of(Roles.R1, new JVariant(1)))) {
				// Empty Implementation
			} else if (m.equals(ImmutableMap.of(Roles.R2, new JVariant(2)))) {
				// Empty Implementation
			} else {
				assertTrue(false);
			}
			++count;
		}

		assertEquals(2, count);
	}
}
