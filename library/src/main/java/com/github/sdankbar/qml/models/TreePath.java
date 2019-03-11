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

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Preconditions;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;

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
		p.m.setInt(0, index0);
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
		p.m.setInt(0, index0);
		p.m.setInt(4, index1);

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
		p.m.setInt(0, index0);
		p.m.setInt(4, index1);
		p.m.setInt(8, index2);
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
		p.m.setInt(0, index0);
		p.m.setInt(4, index1);
		p.m.setInt(8, index2);
		int offset = 12;
		for (final int i : other) {
			Preconditions.checkArgument(i >= 0, "Index cannot be negative");

			p.m.setInt(offset, i);
			offset += 4;
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
		final int lastOffset = 4 * path.getCount();
		int offset = 0;
		for (; offset < lastOffset; offset += 4) {
			p.m.setInt(offset, path.m.getInt(offset));
		}
		p.m.setInt(offset, index0);
		return p;
	}

	private final Memory m;
	private final int size;

	private TreePath() {
		m = null;
		size = 0;
	}

	private TreePath(final int count) {
		m = new Memory(4 * count);
		size = count;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof TreePath) {
			final TreePath arg = (TreePath) obj;
			if (arg.size != size) {
				return false;
			} else {
				for (int i = 0; i < size; ++i) {
					if (m.getInt(4 * i) != arg.m.getInt(4 * i)) {
						return false;
					}
				}
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * @return The number of indices contained in this TreePath.
	 */
	public int getCount() {
		return size;
	}

	/**
	 * @param depth The depth of the TreePath to get the index for.
	 * @return The index for the given depth.
	 * @throws IllegalArgumentException Thrown if depth is outside of the range of
	 *                                  valid depths for this TreePath.
	 */
	public int getIndex(final int depth) {
		if (0 <= depth && depth < size) {
			return m.getInt(4 * depth);
		} else {
			throw new IllegalArgumentException("Depth [" + depth + "] is not valid for this TreePath");
		}
	}

	/**
	 * @return The last index in the TreePath.
	 */
	public int getLast() {
		if (size > 0) {
			return m.getInt(4 * (size - 1));
		} else {
			throw new IllegalArgumentException("Cannot call on root TreePath");
		}
	}

	@Override
	public int hashCode() {
		final HashCodeBuilder builder = new HashCodeBuilder();
		for (int i = 0; i < size; ++i) {
			builder.append(m.getInt(i * 4));
		}
		return builder.toHashCode();
	}

	/**
	 * @return A new TreePath that matches this TreePath except that it has had its
	 *         first index removed. If this TreePath has depth <= 1, the root
	 *         TreePath is returned.
	 */
	public TreePath removeFirst() {
		if (size <= 1) {
			return ROOT_PATH;
		} else {
			final TreePath p = new TreePath(size - 1);
			final int lastOffset = 4 * p.getCount();
			for (int offset = 0; offset < lastOffset; offset += 4) {
				p.m.setInt(offset, m.getInt(offset + 4));
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
		if (size <= 1) {
			return ROOT_PATH;
		} else {
			final TreePath p = new TreePath(size - 1);
			final int lastOffset = 4 * p.getCount();
			for (int offset = 0; offset < lastOffset; offset += 4) {
				p.m.setInt(offset, m.getInt(offset));
			}
			return p;
		}
	}

	/**
	 * @return This TreePath in its serialized form.
	 */
	public Pointer serialize() {
		if (m == null) {
			return Pointer.NULL;
		} else {
			return m;
		}
	}

	@Override
	public String toString() {
		final StringBuilder b = new StringBuilder("TreePath [");
		for (int i = 0; i < size; ++i) {
			b.append(m.getInt(i * 4));
			if (i != (size - 1)) {
				b.append(", ");
			}
		}
		b.append("]");
		return b.toString();
	}
}
