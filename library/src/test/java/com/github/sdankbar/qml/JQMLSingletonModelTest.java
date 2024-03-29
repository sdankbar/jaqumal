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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Test;

import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.exceptions.IllegalKeyException;
import com.github.sdankbar.qml.fonts.JFont;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;
import com.github.sdankbar.qml.painting.JPoint;
import com.github.sdankbar.qml.painting.JPointReal;
import com.github.sdankbar.qml.painting.JRect;
import com.github.sdankbar.qml.painting.PainterInstructions;
import com.github.sdankbar.qml.painting.PainterInstructionsBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Tests the JQMLSingletonModel class.
 */
public class JQMLSingletonModelTest {

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
	 *
	 */
	@After
	public void cleanup() {
		JQMLApplication.delete();
	}

	/**
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void getSetValues() throws InterruptedException {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		model.put(Roles.R1, new JVariant(1));
		model.put(Roles.R3, new JVariant(ImmutableList.of(new Point2D.Double(1, 2), new Point2D.Double(3, 4))));

		assertEquals(new JVariant(1), model.get(Roles.R1));
		assertEquals(null, model.get(Roles.R2));
		assertEquals(new JVariant(ImmutableList.of(new Point2D.Double(1, 2), new Point2D.Double(3, 4))),
				model.get(Roles.R3));
		assertEquals(null, model.get(Roles.R4));
		assertEquals(null, model.get(Roles.R5));
		try {
			model.get(null);
			assertTrue(false);
		} catch (@SuppressWarnings("unused") final IllegalKeyException e) {
			// Expected
		}
		try {
			model.get(Integer.valueOf(55));
			assertTrue(false);
		} catch (@SuppressWarnings("unused") final IllegalKeyException e) {
			// Expected
		}
	}

	/**
	 *
	 */
	@Test
	public void assignValues() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		model.put(Roles.R1, new JVariant(1));
		model.put(Roles.R3, new JVariant(ImmutableList.of(new Point2D.Double(1, 2), new Point2D.Double(3, 4))));

		assertEquals(new JVariant(1), model.get(Roles.R1));
		assertEquals(new JVariant(ImmutableList.of(new Point2D.Double(1, 2), new Point2D.Double(3, 4))),
				model.get(Roles.R3));

		model.assign(ImmutableMap.of(Roles.R1, new JVariant(2), Roles.R2, new JVariant(42)));

		assertEquals(new JVariant(2), model.get(Roles.R1));
		assertEquals(new JVariant(42), model.get(Roles.R2));
		assertEquals(null, model.get(Roles.R3));
	}

	/**
	 *
	 */
	@Test
	public void testChangeListener() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		final AtomicInteger calls = new AtomicInteger();
		model.registerChangeListener((k, v) -> calls.incrementAndGet());

		model.put(Roles.R1, new JVariant(1));
		model.put(Roles.R1, null);

		assertEquals(2, calls.get());
	}

	/**
	 *
	 */
	@Test
	public void testClear() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		model.put(Roles.R2, new JVariant(2));
		model.put(Roles.R4, new JVariant(4));

		model.clear();

		assertEquals(null, model.get(Roles.R1));
		assertEquals(null, model.get(Roles.R2));
		assertEquals(null, model.get(Roles.R3));
		assertEquals(null, model.get(Roles.R4));
		assertEquals(null, model.get(Roles.R5));
	}

	/**
	 *
	 */
	@SuppressWarnings({ "unlikely-arg-type", "boxing" })
	@Test
	public void testContainsKey() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		model.put(Roles.R1, new JVariant(1));
		model.put(Roles.R5, new JVariant(5));

		assertTrue(model.containsKey(Roles.R1));
		assertFalse(model.containsKey(Roles.R2));
		assertFalse(model.containsKey(Roles.R3));
		assertFalse(model.containsKey(Roles.R4));
		assertTrue(model.containsKey(Roles.R5));
		assertFalse(model.containsKey(55));
		assertFalse(model.containsKey(null));
	}

	/**
	 *
	 */
	@Test
	public void testContainsValue() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		model.put(Roles.R1, new JVariant(1));
		model.put(Roles.R3, new JVariant(3));

		assertTrue(model.containsValue(new JVariant(1)));
		assertFalse(model.containsValue(new JVariant(2)));
		assertTrue(model.containsValue(new JVariant(3)));
		assertFalse(model.containsValue(null));
	}

	/**
	 *
	 */
	@Test
	public void testEntrySet() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		final Set<Map.Entry<Roles, JVariant>> set = model.entrySet();

		assertEquals(0, set.size());
		assertFalse(set.iterator().hasNext());

		final Map<Roles, JVariant> all = new HashMap<>();
		all.put(Roles.R1, new JVariant(1));
		all.put(Roles.R3, new JVariant(3));

		model.putAll(all);

		assertEquals(2, set.size());

		final Iterator<Map.Entry<Roles, JVariant>> iter = set.iterator();
		final Map.Entry<Roles, JVariant> e1 = iter.next();
		assertEquals(Roles.R1, e1.getKey());
		assertEquals(new JVariant(1), e1.getValue());

		final Map.Entry<Roles, JVariant> e2 = iter.next();
		assertEquals(Roles.R3, e2.getKey());
		assertEquals(new JVariant(3), e2.getValue());

		assertFalse(iter.hasNext());
	}

	/**
	 *
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model1 = app.getModelFactory().createSingletonModel("other1",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);
		final JQMLSingletonModel<Roles> model2 = app.getModelFactory().createSingletonModel("other2",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);
		final JQMLSingletonModel<Roles> model3 = app.getModelFactory().createSingletonModel("other3",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);
		final JQMLSingletonModel<Roles> model4 = app.getModelFactory().createSingletonModel("other4",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		model1.put(Roles.R1, new JVariant(1));
		model1.put(Roles.R3, new JVariant(3));

		model2.put(Roles.R1, new JVariant(1));
		model2.put(Roles.R3, new JVariant(3));

		model4.put(Roles.R1, new JVariant(1));
		model4.put(Roles.R3, new JVariant(4));

		assertFalse(model1.equals(null));
		assertFalse(model1.equals(Integer.valueOf(0)));
		assertFalse(model1.equals(model3));
		assertFalse(model1.equals(model4));
		assertTrue(model1.equals(model2));
		assertTrue(model1.equals(model1));
	}

	/**
	 *
	 */
	@Test
	public void testGetModelName() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		assertEquals("other", model.getModelName());
	}

	/**
	 *
	 */
	@Test
	public void testHashcode() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model1 = app.getModelFactory().createSingletonModel("other1",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);
		final JQMLSingletonModel<Roles> model2 = app.getModelFactory().createSingletonModel("other2",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		model1.put(Roles.R1, new JVariant(1));
		model1.put(Roles.R3, new JVariant(3));

		model2.put(Roles.R1, new JVariant(1));
		model2.put(Roles.R3, new JVariant(3));

		assertEquals(model1.hashCode(), model2.hashCode());
	}

	/**
	 *
	 */
	@Test
	public void testKeySet() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		final Set<Roles> set = model.keySet();

		assertEquals(0, set.size());
		assertFalse(set.iterator().hasNext());

		final Map<Roles, JVariant> all = new HashMap<>();
		all.put(Roles.R1, new JVariant(1));
		all.put(Roles.R3, new JVariant(3));

		model.putAll(all);

		assertEquals(2, set.size());

		final Iterator<Roles> iter = set.iterator();
		assertTrue(iter.hasNext());
		assertEquals(Roles.R1, iter.next());
		assertEquals(Roles.R3, iter.next());
		assertFalse(iter.hasNext());

		try {
			iter.next();
			assertTrue(false);
		} catch (@SuppressWarnings("unused") final NoSuchElementException e) {
			// Expected
		}
	}

	/**
	 *
	 */
	@Test
	public void testPutAll() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		final Map<Roles, JVariant> all = new HashMap<>();
		all.put(Roles.R1, new JVariant(1));
		all.put(Roles.R3, new JVariant(3));

		model.putAll(all);

		assertEquals(new JVariant(1), model.get(Roles.R1));
		assertEquals(null, model.get(Roles.R2));
		assertEquals(new JVariant(3), model.get(Roles.R3));
		assertEquals(null, model.get(Roles.R4));
		assertEquals(null, model.get(Roles.R5));
		try {
			assertEquals(null, model.get(null));
			assertTrue(false);
		} catch (@SuppressWarnings("unused") final IllegalKeyException e) {
			// Expected
		}
	}

	/**
	 *
	 */
	@Test
	public void testRemove() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		final Map<Roles, JVariant> all = new HashMap<>();
		all.put(Roles.R1, new JVariant(1));
		all.put(Roles.R3, new JVariant(3));
		all.put(Roles.R5, new JVariant(5));

		model.putAll(all);

		assertEquals(new JVariant(1), model.get(Roles.R1));
		assertEquals(null, model.get(Roles.R2));
		assertEquals(new JVariant(3), model.get(Roles.R3));
		assertEquals(null, model.get(Roles.R4));
		assertEquals(new JVariant(5), model.get(Roles.R5));

		model.remove(Roles.R5);

		assertEquals(new JVariant(1), model.get(Roles.R1));
		assertEquals(null, model.get(Roles.R2));
		assertEquals(new JVariant(3), model.get(Roles.R3));
		assertEquals(null, model.get(Roles.R4));
		assertEquals(null, model.get(Roles.R5));
		try {
			assertEquals(null, model.get(null));
			assertTrue(false);
		} catch (@SuppressWarnings("unused") final IllegalKeyException e) {
			// Expected
		}
	}

	/**
	 *
	 */
	@Test
	public void testSize() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		final Map<Roles, JVariant> all = new HashMap<>();
		all.put(Roles.R1, new JVariant(1));
		all.put(Roles.R3, new JVariant(3));

		model.putAll(all);

		assertEquals(2, model.size());
		assertFalse(model.isEmpty());

		model.clear();

		assertEquals(0, model.size());
		assertTrue(model.isEmpty());
	}

	/**
	 *
	 */
	@Test
	public void testValues() {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);

		final Collection<JVariant> values = model.values();

		assertEquals(0, values.size());
		assertFalse(values.iterator().hasNext());

		final Map<Roles, JVariant> all = new HashMap<>();
		all.put(Roles.R1, new JVariant(1));
		all.put(Roles.R3, new JVariant(3));

		model.putAll(all);

		assertEquals(2, values.size());

		final Iterator<JVariant> iter = values.iterator();
		assertEquals(new JVariant(1), iter.next());
		assertEquals(new JVariant(3), iter.next());
		assertFalse(iter.hasNext());
	}

	/**
	 * @throws MalformedURLException
	 *
	 */
	@Test
	public void testGetPutAllTypes() throws MalformedURLException {
		final String[] args = new String[0];
		final JQMLApplication<EventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("other",
				EnumSet.allOf(Roles.class), PutMode.RETURN_PREVIOUS_VALUE);
		model.put(Roles.R1, new JVariant(true));
		assertEquals(true, model.get(Roles.R1).asBoolean());
		model.put(Roles.R1, new JVariant(new byte[122]));
		assertArrayEquals(new byte[122], model.get(Roles.R1).asByteArray());
		model.put(Roles.R1, new JVariant(new Color(1, 2, 3)));
		assertEquals(new Color(1, 2, 3), model.get(Roles.R1).asColor());
		model.put(Roles.R1, new JVariant(Instant.ofEpochSecond(123)));
		assertEquals(Instant.ofEpochSecond(123), model.get(Roles.R1).asDateTime());
		model.put(Roles.R1, new JVariant(1.23));
		assertEquals(1.23, model.get(Roles.R1).asDouble(), 0.001);

		{
			final BufferedImage image = new BufferedImage(100, 200, BufferedImage.TYPE_INT_RGB);
			final Graphics2D g = image.createGraphics();
			g.setColor(Color.BLUE);
			g.fillRect(0, 0, 100, 200);
			model.put(Roles.R1, new JVariant(image));
			assertEquals(image.getWidth(), model.get(Roles.R1).asImage().getWidth());
			assertEquals(image.getHeight(), model.get(Roles.R1).asImage().getHeight());
			assertEquals(Color.BLUE.getRGB(), model.get(Roles.R1).asImage().getRGB(33, 44));
		}

		model.put(Roles.R1, new JVariant(33));
		assertEquals(33, model.get(Roles.R1).asInteger());
		model.put(Roles.R1, new JVariant(new Line2D.Double(1, 2, 3, 4)));
		assertEquals(new Line2D.Double(1, 2, 3, 4).y2, model.get(Roles.R1).asLine().getY2(), 0.001);
		model.put(Roles.R1, new JVariant(Long.MAX_VALUE));
		assertEquals(Long.MAX_VALUE, model.get(Roles.R1).asLong());
		model.put(Roles.R1, new JVariant(new Point(1, 2)));
		assertEquals(new Point(1, 2), model.get(Roles.R1).asPoint());
		model.put(Roles.R1, new JVariant(new Point2D.Double(1, 2)));
		assertEquals(JPoint.point(1, 2), model.get(Roles.R1).asJPoint());
		model.put(Roles.R1, new JVariant(JPointReal.point(1, 2)));
		assertEquals(JPointReal.point(1, 2), model.get(Roles.R1).asJPointReal());
		model.put(Roles.R1, new JVariant(new Rectangle(1, 2, 3, 4)));
		assertEquals(JRect.rect(1, 2, 3, 4), model.get(Roles.R1).asJRect());
		model.put(Roles.R1, new JVariant(Pattern.compile(".*")));
		assertEquals(Pattern.compile(".*").pattern(), model.get(Roles.R1).asRegularExpression().pattern());
		model.put(Roles.R1, new JVariant("ABCXYZ"));
		assertEquals("ABCXYZ", model.get(Roles.R1).asString());
		model.put(Roles.R1, new JVariant(new URL("https://doc.qt.io/qt-5/qdatetime.html")));
		assertEquals(new URL("https://doc.qt.io/qt-5/qdatetime.html"), model.get(Roles.R1).asURL());

		final UUID id = UUID.randomUUID();
		model.put(Roles.R1, new JVariant(id));
		assertEquals(id, model.get(Roles.R1).asUUID());
		model.put(Roles.R1, new JVariant(JFont.builder().build()));
		assertEquals(JFont.builder().build(), model.get(Roles.R1).asFont());

		final PainterInstructionsBuilder b = new PainterInstructionsBuilder();
		b.drawText(1, 2, "345");
		final PainterInstructions instr = b.build();
		model.put(Roles.R1, new JVariant(instr));
		assertEquals(instr, model.get(Roles.R1).asPainterInstructions());
	}

}
