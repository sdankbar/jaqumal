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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.Test;

/**
 * Tests the PreemptingThreadPool class.
 */
public class PreemptingThreadPoolTest {

	public static int getSize(final List<Integer> list) {
		synchronized (list) {
			return list.size();
		}
	}

	/**
	 * @throws InterruptedException e
	 *
	 */
	@Test
	public void test_submit() throws InterruptedException {
		final PreemptingThreadPool ordered = new PreemptingThreadPool(4);

		final int length = 100_000;
		final int preemptCount = length / 100;

		final long s = System.currentTimeMillis();

		final List<Integer> list = new ArrayList<>(length);
		final List<Future<?>> futures = new ArrayList<>(length);
		for (int i = 0; i < length; ++i) {
			final int temp = i;
			futures.add(ordered.submit(() -> {
				try {
					if (temp % 23 == 0) {
						Thread.sleep(0, 100);
					}
					synchronized (list) {
						list.add(temp);
					}
				} catch (final InterruptedException e1) {
					e1.printStackTrace();
				}
			}));
		}

		ordered.preempt(futures.subList(length - preemptCount, length));

		while (getSize(list) < length) {
			Thread.sleep(100);
		}

		for (int i = 0; i < preemptCount; ++i) {
			assertTrue(list.indexOf(length - i) < (length - 5 * preemptCount));
		}

		list.sort(Integer::compare);

		for (int i = 0; i < length; ++i) {
			assertEquals(i, list.get(i).intValue());
		}

		final long e = System.currentTimeMillis();
		System.out.println("Took " + (e - s) + " milliseconds");
	}

}
