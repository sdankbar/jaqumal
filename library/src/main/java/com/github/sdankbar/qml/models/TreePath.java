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
package com.github.sdankbar.qml.models;

import java.util.Arrays;

import com.google.common.base.Preconditions;

/**
 * Represents an item in a tree that is an array of arrays. The path to the item
 * in the tree is stored as the index into each level's array. So if a tree has
 * 2 items in its root and 3 items on each branch on the second level, to get
 * the first item on the right branch of the tree, the path would be (1, 0). The
 * first index, 1, get the right branch and the second index, 0, gets the first
 * item in that branch's array.
 *
 * The serialized format of a TreePath is 4 native endian order bytes for its
 */
public class TreePath {

	private static final TreePath ROOT_PATH = new TreePath();

	/**
	 * @return A TreePath representing the root of the tree. Cannot be used to
	 *         access any data from a tree.
	 */
	public static TreePath of() {
		return ROOT_PATH;
	}

	/**
	 * @param index0 Index at depth 0.
	 * @return TreePath with a depth of 1.
	 */
	public static TreePath of(final int index0) {
		Preconditions.checkArgument(index0 >= 0, "Index cannot be negative");

		final TreePath p = new TreePath(1);
		p.m[0] = index0;
		return p;
	}

	/**
	 * @param index0 Index at depth 0.
	 * @param index1 Index at depth 1.
	 * @return TreePath with a depth of 2.
	 */
	public static TreePath of(final int index0, final int index1) {
		Preconditions.checkArgument(index0 >= 0, "Index cannot be negative");
		Preconditions.checkArgument(index1 >= 0, "Index cannot be negative");

		final TreePath p = new TreePath(2);
		p.m[0] = index0;
		p.m[1] = index1;

		return p;
	}

	/**
	 * @param index0 Index at depth 0.
	 * @param index1 Index at depth 1.
	 * @param index2 Index at depth 2.
	 * @return TreePath with a depth of 3.
	 */
	public static TreePath of(final int index0, final int index1, final int index2) {
		Preconditions.checkArgument(index0 >= 0, "Index cannot be negative");
		Preconditions.checkArgument(index1 >= 0, "Index cannot be negative");
		Preconditions.checkArgument(index2 >= 0, "Index cannot be negative");

		final TreePath p = new TreePath(3);
		p.m[0] = index0;
		p.m[1] = index1;
		p.m[2] = index2;
		return p;
	}

	/**
	 * @param index0 Index at depth 0.
	 * @param index1 Index at depth 1.
	 * @param index2 Index at depth 2.
	 * @param other  Indices for depths 3 and above.
	 * @return TreePath with a depth of 4 or more.
	 */
	public static TreePath of(final int index0, final int index1, final int index2, final int... other) {
		Preconditions.checkArgument(index0 >= 0, "Index cannot be negative");
		Preconditions.checkArgument(index1 >= 0, "Index cannot be negative");
		Preconditions.checkArgument(index2 >= 0, "Index cannot be negative");
		for (final int i : other) {
			Preconditions.checkArgument(i >= 0, "Index cannot be negative");
		}

		final TreePath p = new TreePath(3 + other.length);
		p.m[0] = index0;
		p.m[1] = index1;
		p.m[2] = index2;
		int index = 3;
		for (final int i : other) {
			Preconditions.checkArgument(i >= 0, "Index cannot be negative");
			p.m[index++] = i;
		}
		return p;
	}

	/**
	 * Creates a new TreePath by appending a new index to the end.
	 *
	 * @param path   The starting TreePath
	 * @param index0 The new index to append to the end.
	 * @return The new TreePath.
	 */
	public static TreePath of(final TreePath path, final int index0) {
		final TreePath p = new TreePath(path.getCount() + 1);
		for (int i = 0; i < path.getCount(); ++i) {
			p.m[i] = path.m[i];
		}
		p.m[p.getCount() - 1] = index0;
		return p;
	}

	private final int[] m;

	private TreePath() {
		m = new int[0];
	}

	private TreePath(final int count) {
		m = new int[count];
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TreePath other = (TreePath) obj;
		if (!Arrays.equals(m, other.m)) {
			return false;
		}
		return true;
	}

	/**
	 * @return The number of indices contained in this TreePath.
	 */
	public int getCount() {
		return m.length;
	}

	/**
	 * @param depth The depth of the TreePath to get the index for.
	 * @return The index for the given depth.
	 * @throws IllegalArgumentException Thrown if depth is outside of the range of
	 *                                  valid depths for this TreePath.
	 */
	public int getIndex(final int depth) {
		if (0 <= depth && depth < getCount()) {
			return m[depth];
		} else {
			throw new IllegalArgumentException("Depth [" + depth + "] is not valid for this TreePath");
		}
	}

	/**
	 * @return The last index in the TreePath.
	 */
	public int getLast() {
		final int size = getCount();
		if (size > 0) {
			return m[size - 1];
		} else {
			throw new IllegalArgumentException("Cannot call on root TreePath");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(m);
		return result;
	}

	/**
	 * @return A new TreePath that matches this TreePath except that it has had its
	 *         first index removed. If this TreePath has depth <= 1, the root
	 *         TreePath is returned.
	 */
	public TreePath removeFirst() {
		final int size = getCount();
		if (size <= 1) {
			return ROOT_PATH;
		} else {
			final TreePath p = new TreePath(size - 1);
			for (int i = 1; i < p.getCount(); ++i) {
				p.m[i - 1] = m[i];
			}
			return p;
		}
	}

	/**
	 * @return A new TreePath that matches this TreePath except that it has had its
	 *         last index removed. If this TreePath has depth <= 1, the root
	 *         TreePath is returned.
	 */
	public TreePath removeLast() {
		final int size = getCount();
		if (size <= 1) {
			return ROOT_PATH;
		} else {
			final TreePath p = new TreePath(size - 1);
			for (int i = 0; i < p.getCount(); ++i) {
				p.m[i] = m[i];
			}
			return p;
		}
	}

	public int[] toArray() {
		return Arrays.copyOf(m, m.length);
	}

	@Override
	public String toString() {
		final int size = getCount();
		final StringBuilder b = new StringBuilder("TreePath [");
		for (int i = 0; i < size; ++i) {
			b.append(m[i]);
			if (i != (size - 1)) {
				b.append(", ");
			}
		}
		b.append("]");
		return b.toString();
	}
}
