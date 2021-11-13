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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;
import com.github.sdankbar.qml.models.table.JQMLTableModel;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests the DelayedMap class.
 */
public class ModelPersistenceTest {

	private static final int SLEEP = 100;

	/**
	 *
	 */
	public interface EventProcessor {
		// Empty Implementation
	}

	private enum Roles {
		R1, R2, R3, R4, R5, row, column;
	}

	/**
	 * @throws IOException          Error deleting files.
	 * @throws InterruptedException
	 *
	 */
	@After
	public void cleanup() throws IOException, InterruptedException {
		Thread.sleep(100);
		JQMLApplication.delete();
		FileUtils.deleteDirectory(new File("persistenceTest"));
		Thread.sleep(100);
	}

	/**
	 * @throws InterruptedException
	 * @throws IOException
	 *
	 */
	@Test
	public void test_shutdown() throws InterruptedException, IOException {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);
		app.getModelFactory().enablePersistence(Duration.ofSeconds(3), new File("persistenceTest"));
		app.getModelFactory().enableAutoPersistenceForModel(model);

		model.put(Roles.R1, new JVariant(1));
		model.put(Roles.R3, new JVariant(ImmutableList.of(new Point2D.Double(1, 2), new Point2D.Double(3, 4))));

		Thread.sleep(SLEEP);

		assertFalse(new File("persistenceTest/other.json").exists());
		app.getModelFactory().enablePersistence(Duration.ofSeconds(3), new File("persistenceTest2"));
		assertTrue(new File("persistenceTest/other.json").exists());
	}

	/**
	 * @throws InterruptedException
	 * @throws IOException
	 *
	 */
	@Test
	public void test_flush() throws InterruptedException, IOException {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model1 = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);
		final JQMLSingletonModel<Roles> model2 = app.getModelFactory().createSingletonModel("other_2",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);
		app.getModelFactory().enablePersistence(Duration.ofSeconds(3), new File("persistenceTest"));
		app.getModelFactory().enableAutoPersistenceForModel(model1);
		app.getModelFactory().enableAutoPersistenceForModel(model2);

		model1.put(Roles.R1, new JVariant(1));
		model1.put(Roles.R3, new JVariant(ImmutableList.of(new Point2D.Double(1, 2), new Point2D.Double(3, 4))));

		model2.put(Roles.R1, new JVariant(2));
		model2.put(Roles.R3, new JVariant(ImmutableList.of(new Point2D.Double(1, 2), new Point2D.Double(3, 4))));

		Thread.sleep(SLEEP);

		assertFalse(new File("persistenceTest/other.json").exists());
		assertFalse(new File("persistenceTest/other_2.json").exists());
		app.getModelFactory().flushPersistence();
		assertTrue(new File("persistenceTest/other.json").exists());
		assertTrue(new File("persistenceTest/other_2.json").exists());
	}

	/**
	 * @throws InterruptedException
	 * @throws IOException
	 *
	 */
	@Test
	public void test_writeData_singleton() throws InterruptedException, IOException {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);
		app.getModelFactory().enablePersistence(Duration.ZERO, new File("persistenceTest"));
		app.getModelFactory().enableAutoPersistenceForModel(model);

		model.put(Roles.R1, new JVariant(1));
		model.put(Roles.R3, new JVariant(ImmutableList.of(new Point2D.Double(1, 2), new Point2D.Double(3, 4))));

		Thread.sleep(SLEEP);

		assertTrue(new File("persistenceTest/other.json").exists());
		final List<String> lines = Files.readAllLines(new File("persistenceTest", "other.json").toPath());

		assertEquals(lines.get(0), "{");
		assertEquals(lines.get(14), " \"R1\": {");
		assertEquals(lines.get(15), "  \"type\": \"INT\",");
		assertEquals(lines.get(16), "  \"value\": 1");
		assertEquals(lines.get(17), " }");
		assertEquals(lines.get(18), "}");

		app.getModelFactory().restoreModel(model);

		assertEquals(model.get(Roles.R1), new JVariant(1));
	}

	/**
	 * @throws InterruptedException
	 * @throws IOException
	 *
	 */
	@Test
	public void test_readData_singleton() throws InterruptedException, IOException {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);
		app.getModelFactory().enablePersistence(Duration.ZERO, new File("persistenceTest"));

		model.put(Roles.R1, new JVariant(1));
		model.put(Roles.R3, new JVariant(ImmutableList.of(new Point2D.Double(1, 2), new Point2D.Double(3, 4))));

		final Map<Roles, JVariant> copy = new HashMap<>(model);

		app.getModelFactory().persistModel(model);

		assertTrue(new File("persistenceTest/other.json").exists());

		model.clear();
		assertEquals(model.size(), 0);

		assertTrue(app.getModelFactory().restoreModel(model));

		assertEquals(model.size(), 2);
		assertEquals(model, copy);
	}

	/**
	 * @throws InterruptedException
	 * @throws IOException
	 *
	 */
	@Test
	public void test_writeData_list() throws InterruptedException, IOException {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other2", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		app.getModelFactory().enablePersistence(Duration.ZERO, new File("persistenceTest"));
		app.getModelFactory().enableAutoPersistenceForModel(model);

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

		Thread.sleep(SLEEP);

		assertTrue(new File("persistenceTest/other2.json").exists());
		final List<String> lines = Files.readAllLines(new File("persistenceTest", "other2.json").toPath());

		assertEquals(lines.size(), 25);

		app.getModelFactory().restoreModel(model);

		assertEquals(model.get(0).get(Roles.R5), new JVariant(5));
	}

	/**
	 * @throws InterruptedException
	 * @throws IOException
	 *
	 */
	@Test
	public void test_writeData_list2() throws InterruptedException, IOException {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other3", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		app.getModelFactory().enablePersistence(Duration.ZERO, new File("persistenceTest"));
		app.getModelFactory().enableAutoPersistenceForModel(model, ImmutableSet.of("rootTest"));

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

		model.get(0).put(Roles.R1, new JVariant(42));
		model.get(1).put(Roles.R5, new JVariant("ABC"));

		model.putRootValue("rootTest", new JVariant("rootValue"));

		Thread.sleep(SLEEP);

		assertTrue(new File("persistenceTest/other3.json").exists());
		final List<String> lines = Files.readAllLines(new File("persistenceTest", "other3.json").toPath());

		assertEquals(lines.size(), 32);

		app.getModelFactory().restoreModel(model, ImmutableSet.of("rootTest"));

		assertEquals(model.get(0).get(Roles.R1), new JVariant(42));
		assertEquals(model.get(0).get(Roles.R5), new JVariant(5));

		assertEquals(model.get(1).get(Roles.R2), new JVariant(2));
		assertEquals(model.get(1).get(Roles.R4), new JVariant(4));
		assertEquals(model.get(1).get(Roles.R5), new JVariant("ABC"));

		assertEquals("rootValue", model.getRootValue("rootTest").get().asString());
	}

	/**
	 * @throws InterruptedException
	 * @throws IOException
	 *
	 */
	@Test
	public void test_readData_list() throws InterruptedException, IOException {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLListModel<Roles> model = app.getModelFactory().createListModel("other4", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		app.getModelFactory().enablePersistence(Duration.ZERO, new File("persistenceTest"));

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
		final ImmutableList<HashMap<Roles, JVariant>> copy = model.stream().map(HashMap::new)
				.collect(ImmutableList.toImmutableList());

		app.getModelFactory().persistModel(model);

		assertTrue(new File("persistenceTest/other4.json").exists());

		model.clear();
		assertEquals(model.size(), 0);

		assertTrue(app.getModelFactory().restoreModel(model));

		assertEquals(model.size(), 2);
		assertEquals(model.get(0), copy.get(0));
		assertEquals(model.get(1), copy.get(1));
	}

	/**
	 * @throws InterruptedException
	 * @throws IOException
	 *
	 */
	@Test
	public void test_writeData_table() throws InterruptedException, IOException {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLTableModel<Roles> model = app.getModelFactory().createTableModel("table1", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		app.getModelFactory().enablePersistence(Duration.ZERO, new File("persistenceTest"));
		app.getModelFactory().enableAutoPersistenceForModel(model);

		model.addRow();
		model.addRow();
		model.addRow();

		model.addColumn();
		model.addColumn();
		model.addColumn();

		{
			final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
			data.put(Roles.R1, new JVariant(1));
			data.put(Roles.R5, new JVariant(5));

			model.setData(0, 0, data.build());
		}
		{
			final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
			data.put(Roles.R2, new JVariant(2));
			data.put(Roles.R4, new JVariant(4));

			model.setData(2, 2, data.build());
		}

		Thread.sleep(SLEEP);

		assertTrue(new File("persistenceTest/table1.json").exists());
		final List<String> lines = Files.readAllLines(new File("persistenceTest", "table1.json").toPath());

		assertEquals(lines.size(), 145);

		app.getModelFactory().restoreModel(model);

		assertEquals(model.get(2, 2).get(Roles.R4), new JVariant(4));
		assertEquals(3, model.getRowCount());
		assertEquals(4, model.getColumnCount());
	}

	/**
	 * @throws InterruptedException
	 * @throws IOException
	 *
	 */
	@Test
	public void test_readData_table() throws InterruptedException, IOException {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLTableModel<Roles> model = app.getModelFactory().createTableModel("table2", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		app.getModelFactory().enablePersistence(Duration.ZERO, new File("persistenceTest"));

		model.addRow();
		model.addRow();
		model.addRow();

		model.addColumn();
		model.addColumn();
		model.addColumn();

		{
			final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
			data.put(Roles.R1, new JVariant(1));
			data.put(Roles.R5, new JVariant(5));

			model.setData(0, 0, data.build());
		}
		{
			final ImmutableMap.Builder<Roles, JVariant> data = ImmutableMap.builder();
			data.put(Roles.R2, new JVariant(2));
			data.put(Roles.R4, new JVariant(4));

			model.setData(2, 2, data.build());
		}

		app.getModelFactory().persistModel(model);

		assertTrue(new File("persistenceTest/table2.json").exists());

		model.setData(0, 0, ImmutableMap.of());
		model.setData(2, 2, ImmutableMap.of());

		assertTrue(app.getModelFactory().restoreModel(model));

		assertEquals(model.getRowCount(), 3);
		assertEquals(model.getColumnCount(), 4);
		assertEquals(model.get(2, 2).get(Roles.R4), new JVariant(4));
		assertEquals(model.get(2, 2).get(Roles.R2), new JVariant(2));
		assertEquals(model.get(1, 1).get(Roles.R4), null);
	}

}
