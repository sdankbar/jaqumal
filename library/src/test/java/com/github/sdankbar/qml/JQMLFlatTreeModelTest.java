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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.exceptions.QMLException;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.TreePath;
import com.github.sdankbar.qml.models.flat_tree.JQMLFlatTreeModel;
import com.github.sdankbar.qml.models.flat_tree.JQMLFlatTreeModelMap;

/**
 * Test JQMLFlatTreeModel
 */
public class JQMLFlatTreeModelTest {

	/**
	 * @author me
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
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		final Map<Roles, JVariant> data = new HashMap<>();
		data.put(Roles.R1, new JVariant(1));
		data.put(Roles.R2, new JVariant(2));

		model.append(TreePath.of(), data);

		assertEquals(new JVariant(1), model.getData(TreePath.of(0), Roles.R1).get());
		assertEquals(new JVariant(2), model.getData(TreePath.of(0), Roles.R2).get());
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
	public void clearPath() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		model.append(TreePath.of(), Roles.R1, new JVariant(1));
		model.append(TreePath.of(), Roles.R3, new JVariant(3));
		model.append(TreePath.of(0), Roles.R5, new JVariant(5));

		assertEquals(new JVariant(1), model.getData(TreePath.of(0), Roles.R1).get());
		assertEquals(new JVariant(3), model.getData(TreePath.of(1), Roles.R3).get());
		assertEquals(new JVariant(5), model.getData(TreePath.of(0, 0), Roles.R5).get());

		model.clear(TreePath.of(0));

		assertEquals(false, model.getData(TreePath.of(0), Roles.R1).isPresent());
		assertEquals(new JVariant(3), model.getData(TreePath.of(1), Roles.R3).get());
		assertEquals(false, model.getData(TreePath.of(0, 0), Roles.R5).isPresent());// Also clears the submodel
	}

	/**
	 *
	 */
	@SuppressWarnings("unused")
	@Test(expected = QMLException.class)
	public void duplicateModelName() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model1 = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);
		final JQMLFlatTreeModel<Roles> model2 = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);
	}

	/**
	 *
	 */
	@Test
	public void forEach() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.append(TreePath.of(), Roles.R1, new JVariant(1));
		model.append(TreePath.of(0), Roles.R2, new JVariant(2));
		model.append(TreePath.of(0), Roles.R3, new JVariant(3));

		final List<Map<Roles, JVariant>> l = new ArrayList<>();
		for (final Map<Roles, JVariant> m : model) {
			l.add(m);
		}

		assertEquals(new JVariant(1), l.get(0).get(Roles.R1));
		assertEquals(new JVariant(2), l.get(1).get(Roles.R2));
		assertEquals(new JVariant(3), l.get(2).get(Roles.R3));

		l.clear();

		// TODO test more
	}

	/**
	 *
	 */
	@Test
	public void get() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.append(TreePath.of(), Roles.R1, new JVariant(1));

		final JQMLFlatTreeModelMap<Roles> map = model.get(TreePath.of(0));
		assertNotNull(map);
		assertEquals(new JVariant(1), map.get(Roles.R1));
		assertEquals(null, map.get(Roles.R2));
		assertEquals(null, map.get(Roles.R3));
		assertEquals(null, map.get(Roles.R4));
		assertEquals(null, map.get(Roles.R5));

		assertNull(model.get(TreePath.of(1, 2)));
	}

	/**
	 *
	 */
	@Test
	public void insert() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.append(TreePath.of(), Roles.R1, new JVariant(1));
		model.append(TreePath.of(0), Roles.R2, new JVariant(2));
		model.insert(TreePath.of(0, 0), Roles.R1, new JVariant(3));

		final JQMLFlatTreeModelMap<Roles> map = model.get(TreePath.of(0));
		assertEquals(new JVariant(1), map.get(Roles.R1));
		assertEquals(null, map.get(Roles.R2));
	}

	/**
	 *
	 */
	@Test
	public void isPresent() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.append(TreePath.of(), Roles.R1, new JVariant(1));
		model.append(TreePath.of(0), Roles.R1, new JVariant(1));
		model.append(TreePath.of(0), Roles.R1, new JVariant(1));

		assertTrue(model.isPresent(TreePath.of(0, 0), Roles.R1));
		assertFalse(model.isPresent(TreePath.of(0, 0), Roles.R2));
	}

	/**
	 *
	 */
	@Test(expected = NoSuchElementException.class)
	public void iteratorError() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.iterator().next();
	}

	/**
	 *
	 */
	@Test
	public void remove() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.append(TreePath.of(), Roles.R1, new JVariant(1));
		model.append(TreePath.of(0), Roles.R1, new JVariant(1));
		model.append(TreePath.of(0), Roles.R2, new JVariant(2));

		assertEquals(1, model.size(TreePath.of()));
		assertEquals(2, model.size(TreePath.of(0)));

		model.remove(TreePath.of(0, 0));

		assertEquals(1, model.size(TreePath.of(0)));

		assertEquals(new JVariant(2), model.get(TreePath.of(0, 0)).get(Roles.R2));
	}

	/**
	 *
	 */
	@Test
	public void removeChildren() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.append(TreePath.of(), Roles.R1, new JVariant(1));
		model.append(TreePath.of(0), Roles.R1, new JVariant(1));
		model.append(TreePath.of(0, 0), Roles.R2, new JVariant(2));

		assertEquals(1, model.size(TreePath.of()));
		assertEquals(1, model.size(TreePath.of(0)));
		assertEquals(1, model.size(TreePath.of(0, 0)));

		model.remove(TreePath.of(0));

		assertEquals(0, model.size(TreePath.of(0)));
	}

	/**
	 *
	 */
	@Test(expected = IllegalArgumentException.class)
	public void removeEmptyModel() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.remove(TreePath.of());
	}

	/**
	 *
	 */
	@Test
	public void removeIntermediateInvalidTreePath() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.append(TreePath.of(), Roles.R1, new JVariant(1));
		model.append(TreePath.of(0), Roles.R1, new JVariant(1));
		model.append(TreePath.of(0, 0), Roles.R2, new JVariant(2));

		model.remove(TreePath.of(6, 0));

		assertEquals(1, model.size(TreePath.of(0, 0)));
	}

	/**
	 *
	 */
	@Test(expected = IllegalStateException.class)
	public void removeInvalidatesMap() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.append(TreePath.of(), Roles.R1, new JVariant(1));

		assertEquals(1, model.size(TreePath.of()));

		final Map<Roles, JVariant> m = model.get(TreePath.of(0));

		model.remove(TreePath.of(0));

		m.get(Roles.R1);
	}

	/**
	 *
	 */
	@Test
	public void removeInvalidTreePath() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.append(TreePath.of(), Roles.R1, new JVariant(1));
		model.append(TreePath.of(0), Roles.R1, new JVariant(1));
		model.append(TreePath.of(0, 0), Roles.R2, new JVariant(2));

		assertEquals(1, model.size(TreePath.of()));
		assertEquals(1, model.size(TreePath.of(0)));
		assertEquals(1, model.size(TreePath.of(0, 0)));

		model.remove(TreePath.of(0, 7));

		assertEquals(1, model.size(TreePath.of(0)));
	}

	/**
	 *
	 */
	@Test
	public void removeRole() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.append(TreePath.of(), Roles.R1, new JVariant(1));
		model.setData(TreePath.of(0), Roles.R3, new JVariant(3));

		assertEquals(new JVariant(1), model.getData(TreePath.of(0), Roles.R1).get());
		assertEquals(new JVariant(3), model.getData(TreePath.of(0), Roles.R3).get());

		model.remove(TreePath.of(0), Roles.R1);

		assertFalse(model.getData(TreePath.of(0), Roles.R1).isPresent());
		assertEquals(new JVariant(3), model.getData(TreePath.of(0), Roles.R3).get());
	}

	/**
	 *
	 */
	@Test
	public void setDataMap() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		final Map<Roles, JVariant> d = new HashMap<>();
		d.put(Roles.R1, new JVariant(1));
		d.put(Roles.R3, new JVariant(3));
		model.setData(TreePath.of(0), d);

		assertEquals(new JVariant(1), model.getData(TreePath.of(0), Roles.R1).get());
		assertEquals(new JVariant(3), model.getData(TreePath.of(0), Roles.R3).get());
	}

	/**
	 *
	 */
	@Test
	public void size() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.append(TreePath.of(), Roles.R1, new JVariant(1));
		model.append(TreePath.of(0), Roles.R1, new JVariant(1));
		model.append(TreePath.of(0), Roles.R1, new JVariant(1));

		assertEquals(1, model.size(TreePath.of()));
		assertEquals(2, model.size(TreePath.of(0)));
	}

	/**
	 *
	 */
	@Test
	public void test_sorting() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLFlatTreeModel<Roles> model = app.getModelFactory().createFlatTreeModel("other", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		model.append(TreePath.of(), Roles.R1, new JVariant(0));
		model.append(TreePath.of(0), Roles.R1, new JVariant(2));
		model.append(TreePath.of(0), Roles.R1, new JVariant(3));
		model.append(TreePath.of(0), Roles.R1, new JVariant(7));
		model.append(TreePath.of(0), Roles.R1, new JVariant(1));
		model.append(TreePath.of(0), Roles.R1, new JVariant(4));
		model.append(TreePath.of(0), Roles.R1, new JVariant(6));
		model.append(TreePath.of(0), Roles.R1, new JVariant(5));

		model.sort(TreePath.of(0), (l, r) -> Integer.compare(l.get(Roles.R1).asInteger(), r.get(Roles.R1).asInteger()));

		assertEquals(new JVariant(1), model.get(TreePath.of(0, 0)).get(Roles.R1));
		assertEquals(new JVariant(2), model.get(TreePath.of(0, 1)).get(Roles.R1));
		assertEquals(new JVariant(3), model.get(TreePath.of(0, 2)).get(Roles.R1));
		assertEquals(new JVariant(4), model.get(TreePath.of(0, 3)).get(Roles.R1));
		assertEquals(new JVariant(5), model.get(TreePath.of(0, 4)).get(Roles.R1));
		assertEquals(new JVariant(6), model.get(TreePath.of(0, 5)).get(Roles.R1));
		assertEquals(new JVariant(7), model.get(TreePath.of(0, 6)).get(Roles.R1));
	}
}
