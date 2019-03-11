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

import org.junit.Test;

import com.github.sdankbar.qml.models.TreePath;

/**
 * Tests the TreePath class.
 */
public class TreePathTest {

	/**
	 * Tests the various TreePath.of(...) methods.
	 */
	@Test
	public void constructors() {
		final TreePath root = TreePath.of();
		assertEquals(0, root.getCount());

		final TreePath one = TreePath.of(2);
		assertEquals(1, one.getCount());
		assertEquals(2, one.getIndex(0));

		final TreePath two = TreePath.of(2, 3);
		assertEquals(2, two.getCount());
		assertEquals(2, two.getIndex(0));
		assertEquals(3, two.getIndex(1));

		final TreePath three = TreePath.of(2, 3, 0);
		assertEquals(3, three.getCount());
		assertEquals(2, three.getIndex(0));
		assertEquals(3, three.getIndex(1));
		assertEquals(0, three.getIndex(2));

		final TreePath four = TreePath.of(2, 3, 0, 259);
		assertEquals(4, four.getCount());
		assertEquals(2, four.getIndex(0));
		assertEquals(3, four.getIndex(1));
		assertEquals(0, four.getIndex(2));
		assertEquals(259, four.getIndex(3));

		final TreePath combo = TreePath.of(TreePath.of(), 5);
		assertEquals(1, combo.getCount());
		assertEquals(5, combo.getIndex(0));

		try {
			TreePath.of(-1);
			assertTrue(false);
		} catch (final IllegalArgumentException e) {
			// Expected
		}

		try {
			TreePath.of(-1, 0);
			assertTrue(false);
		} catch (final IllegalArgumentException e) {
			// Expected
		}
		try {
			TreePath.of(0, -1);
			assertTrue(false);
		} catch (final IllegalArgumentException e) {
			// Expected
		}
	}

	/**
	 * Tests the getIndex method.
	 */
	@Test
	public void getIndex() {
		final TreePath temp = TreePath.of(2, 3, 0);
		assertEquals(3, temp.getCount());
		assertEquals(3, temp.getIndex(1));

		try {
			TreePath.of().getIndex(0);
			assertTrue(false);
		} catch (final IllegalArgumentException e) {
			// Expected
		}
	}

	/**
	 * Tests the getLast method.
	 */
	@Test
	public void getLast() {
		final TreePath temp = TreePath.of(2, 3, 0);
		assertEquals(3, temp.getCount());
		assertEquals(0, temp.getLast());

		try {
			TreePath.of().getLast();
			assertTrue(false);
		} catch (final IllegalArgumentException e) {
			// Expected
		}
	}

	/**
	 * Tests the removeFirst method.
	 */
	@Test
	public void removeFirst() {
		final TreePath temp = TreePath.of(2, 3, 0).removeFirst();
		assertEquals(2, temp.getCount());
		assertEquals(3, temp.getIndex(0));
		assertEquals(0, temp.getIndex(1));

		final TreePath temp2 = TreePath.of().removeFirst();
		assertEquals(0, temp2.getCount());
	}

	/**
	 * Tests the removeLast method.
	 */
	@Test
	public void removeLast() {
		final TreePath temp = TreePath.of(2, 3, 0).removeLast();
		assertEquals(2, temp.getCount());
		assertEquals(2, temp.getIndex(0));
		assertEquals(3, temp.getIndex(1));

		final TreePath temp2 = TreePath.of().removeLast();
		assertEquals(0, temp2.getCount());
	}

	/**
	 *
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals() {
		final TreePath temp1 = TreePath.of(2, 3, 0);
		final TreePath temp2 = TreePath.of(2, 3, 0);
		final TreePath temp3 = TreePath.of();
		final TreePath temp4 = TreePath.of(2, 3, 1);

		assertTrue(temp1.equals(temp2));
		assertEquals(temp1.hashCode(), temp2.hashCode());

		assertFalse(temp1.equals(temp3));
		assertFalse(temp1.equals(temp4));
		assertFalse(temp1.equals(null));
		assertFalse(temp1.equals(Integer.valueOf(0)));

		assertEquals(temp1.toString(), temp2.toString());
		assertEquals("TreePath [2, 3, 0]", temp2.toString());
	}
}
