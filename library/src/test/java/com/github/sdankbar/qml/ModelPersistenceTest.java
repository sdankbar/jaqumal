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
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Tests the DelayedMap class.
 */
public class ModelPersistenceTest {

	/**
	 *
	 */
	public interface EventProcessor {
		// Empty Implementation
	}

	private enum Roles {
		R1, R2, R3, R4, R5;
	}

	/**
	 * @throws IOException Error deleting files.
	 *
	 */
	@After
	public void cleanup() throws IOException {
		JQMLApplication.delete();
		FileUtils.deleteDirectory(new File("persistenceTest"));
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
		app.getModelFactory().enablePersistenceForModel(model);

		model.put(Roles.R1, new JVariant(1));
		model.put(Roles.R3, new JVariant(ImmutableList.of(new Point2D.Double(1, 2), new Point2D.Double(3, 4))));

		Thread.sleep(50);

		assertTrue(new File("persistenceTest/other.json").exists());
		final List<String> lines = Files.readAllLines(Path.of("persistenceTest", "other.json"));

		assertEquals(lines.get(0), "{");
		assertEquals(lines.get(1), " \"R3\": {\"type\": \"POLYLINE\"},");
		assertEquals(lines.get(2), " \"R1\": {");
		assertEquals(lines.get(3), "  \"type\": \"INT\",");
		assertEquals(lines.get(4), "  \"value\": 1");
		assertEquals(lines.get(5), " }");
		assertEquals(lines.get(6), "}");
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
		app.getModelFactory().enablePersistenceForModel(model);

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
		Thread.sleep(50);

		assertTrue(new File("persistenceTest/other2.json").exists());
		final List<String> lines = Files.readAllLines(Path.of("persistenceTest", "other2.json"));

		assertEquals(lines.size(), 22);
	}

}
