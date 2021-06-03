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
package com.github.sdankbar.qml.utility;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.junit.Test;

/**
 * Tests the OrderedThreadPool class.
 */
public class OrderedThreadPoolTest {

	/**
	 * @throws InterruptedException e
	 *
	 */
	@Test
	public void test_submit() throws InterruptedException {
		final OrderedThreadPool<Integer> ordered = new OrderedThreadPool<>(Executors.newFixedThreadPool(16),
				Executors.newSingleThreadExecutor());

		final int length = 1_000_000;

		final long s = System.currentTimeMillis();

		final List<Integer> list = new ArrayList<>(length);
		for (int i = 0; i < length; ++i) {
			final int temp = i;
			ordered.submit(() -> {
				if (temp % 803 == 0) {
					try {
						Thread.sleep(0, 1);
					} catch (final InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				return temp;
			}, j -> {
				list.add(j);
			});
		}

		while (list.size() < length) {
			Thread.sleep(100);
		}

		for (int i = 0; i < length; ++i) {
			assertEquals(i, list.get(i).intValue());
		}

		final long e = System.currentTimeMillis();
		System.out.println("Took " + (e - s) + " milliseconds");
	}

}
