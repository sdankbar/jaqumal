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
package com.github.sdankbar.examples.lazy_list;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.eventing.NullEventProcessor;
import com.github.sdankbar.qml.models.lazy.LazyListModel;
import com.google.common.collect.ImmutableMap;

/**
 * Example application that allow for editing a set of colors.
 *
 */
public class App {

	public enum ListRole {
		pos, text, text1, text2, text3, text4, text5, text6;
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

		final long s = System.currentTimeMillis();
		final Map<String, ImmutableMap<ListRole, JVariant>> dataMap = new LinkedHashMap<>();
		for (int i = 0; i < 20000; ++i) {
			final ImmutableMap.Builder<ListRole, JVariant> b = ImmutableMap.builder();
			b.put(ListRole.text, new JVariant("Test " + Integer.toString(i)));
			b.put(ListRole.text1, new JVariant("Test " + Integer.toString(2 * i)));
			b.put(ListRole.text2, new JVariant("Test " + Integer.toString(3 * i)));
			b.put(ListRole.text3, new JVariant("Test " + Integer.toString(4 * i)));
			b.put(ListRole.text4, new JVariant("Test " + Integer.toString(5 * i)));
			b.put(ListRole.text5, new JVariant("Test " + Integer.toString(6 * i)));
			b.put(ListRole.text6, new JVariant("Test " + Integer.toString(7 * i)));
			dataMap.put(Integer.toString(i), b.build());
		}
		model.upsertAll(dataMap);
		System.out.println("Took " + (System.currentTimeMillis() - s));

		app.loadQMLFile("./src/main/qml/main.qml");

		app.execute();
	}
}
