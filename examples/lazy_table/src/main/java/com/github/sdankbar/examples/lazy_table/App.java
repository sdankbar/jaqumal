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
package com.github.sdankbar.examples.lazy_table;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.eventing.NullEventProcessor;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.lazy.LazyListModel;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.google.common.collect.ImmutableMap;

/**
 * Example application that allow for editing a set of colors.
 *
 */
public class App {

	public enum ListRole {
		pos, temp, text, text1, text2, text3, text4, text5, text6;
	}

	public enum ColumnHeaderRole {
		text
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final JQMLApplication<NullEventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final LazyListModel<String, ListRole> model = new LazyListModel<>(app.getModelFactory(),
				app.getInvokableDispatcher(), "lazy_model", ListRole.class, 40, 600,
				ImmutableMap.of(ListRole.pos, new JVariant(-100), ListRole.text, new JVariant("UNINITIALIZED")));

		final JQMLListModel<ColumnHeaderRole> headers = app.getModelFactory().createListModel("headers",
				ColumnHeaderRole.class, PutMode.RETURN_NULL);
		headers.add(ImmutableMap.of(ColumnHeaderRole.text, new JVariant("text")));
		headers.add(ImmutableMap.of(ColumnHeaderRole.text, new JVariant("text1")));
		headers.add(ImmutableMap.of(ColumnHeaderRole.text, new JVariant("text2")));
		headers.add(ImmutableMap.of(ColumnHeaderRole.text, new JVariant("text3")));
		headers.add(ImmutableMap.of(ColumnHeaderRole.text, new JVariant("text4")));
		headers.add(ImmutableMap.of(ColumnHeaderRole.text, new JVariant("text5")));
		headers.add(ImmutableMap.of(ColumnHeaderRole.text, new JVariant("text6")));

		model.setExclusionFunction(m -> (m.get(ListRole.temp).asInteger() % 3 == 0));

		{
			final long s = System.currentTimeMillis();
			final Map<String, ImmutableMap<ListRole, JVariant>> dataMap = new LinkedHashMap<>();
			for (int i = 0; i < 20000; ++i) {
				final ImmutableMap.Builder<ListRole, JVariant> b = ImmutableMap.builder();
				b.put(ListRole.temp, new JVariant(i));
				b.put(ListRole.text, new JVariant("Test " + Integer.toString(i)));
				b.put(ListRole.text1, new JVariant("Test " + Integer.toString(20000 - i)));
				b.put(ListRole.text2, new JVariant("Test " + Integer.toString(3 * i)));
				b.put(ListRole.text3, new JVariant("Test " + Integer.toString(4 * i)));
				b.put(ListRole.text4, new JVariant("Test " + Integer.toString(5 * i)));
				b.put(ListRole.text5, new JVariant("Test " + Integer.toString(6 * i)));
				b.put(ListRole.text6, new JVariant("Test " + Integer.toString(7 * i)));
				dataMap.put(Integer.toString(i), b.build());
			}
			model.upsertAll(dataMap);
			System.out.println("Took " + (System.currentTimeMillis() - s) + " milliseconds");
		}

		final AtomicInteger offsetRef = new AtomicInteger();
		app.getQMLThreadExecutor().scheduleAtFixedRate(() -> {
			final long s = System.currentTimeMillis();
			final Map<String, ImmutableMap<ListRole, JVariant>> dataMap = new LinkedHashMap<>();
			final int offset = offsetRef.getAndIncrement();
			for (int i = 0; i < 20000; ++i) {
				final ImmutableMap.Builder<ListRole, JVariant> b = ImmutableMap.builder();
				b.put(ListRole.temp, new JVariant(i));
				b.put(ListRole.text, new JVariant("Test " + Integer.toString(i + offset)));
				b.put(ListRole.text1, new JVariant("Test " + Integer.toString(20000 - i + offset)));
				b.put(ListRole.text2, new JVariant("Test " + Integer.toString(3 * i + offset)));
				b.put(ListRole.text3, new JVariant("Test " + Integer.toString(4 * i + offset)));
				b.put(ListRole.text4, new JVariant("Test " + Integer.toString(5 * i + offset)));
				b.put(ListRole.text5, new JVariant("Test " + Integer.toString(6 * i + offset)));
				b.put(ListRole.text6, new JVariant("Test " + Integer.toString(7 * i + offset)));
				dataMap.put(Integer.toString(i), b.build());
			}
			model.upsertAll(dataMap);
			System.out.println(Instant.now() + " - Took " + (System.currentTimeMillis() - s) + " milliseconds");
		}, 2000, 100, TimeUnit.MILLISECONDS);

		app.loadAndWatchQMLFile("./src/main/qml/main.qml");

		app.execute();
	}
}
