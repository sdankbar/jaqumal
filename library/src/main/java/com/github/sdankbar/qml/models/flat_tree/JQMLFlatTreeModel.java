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
package com.github.sdankbar.qml.models.flat_tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.jni.flat_tree.FlatTreeModelFunctions;
import com.github.sdankbar.qml.models.AbstractJQMLModel;
import com.github.sdankbar.qml.models.TreePath;
import com.google.common.base.Preconditions;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * A model that is available to QML. Represents a tree of Maps from the key type
 * K, to a JVariant.
 *
 * @param <K> The type of the K in the Map.
 */
public class JQMLFlatTreeModel<K> extends AbstractJQMLModel implements Iterable<Map<K, JVariant>> {

	private static class FlatTreeIterator<K> implements Iterator<Map<K, JVariant>> {

		private TreePath p = TreePath.of(0);
		private final JQMLFlatTreeModel<K> parent;

		public FlatTreeIterator(final JQMLFlatTreeModel<K> parent) {
			this.parent = parent;
		}

		@Override
		public boolean hasNext() {
			final Optional<Node<K>> n = parent.getNode(parent.root, p, 0);
			return n.isPresent();
		}

		@Override
		public JQMLFlatTreeModelMap<K> next() {
			final Optional<Node<K>> n = parent.getNode(parent.root, p, 0);
			if (!n.isPresent()) {
				throw new NoSuchElementException();
			}
			final JQMLFlatTreeModelMap<K> model = n.get().getMap();

			if (n.get().getChildrenList().isEmpty()) {
				final int i = p.getIndex(p.getCount() - 1);
				p = TreePath.of(p.removeLast(), i + 1);
			} else {
				p = TreePath.of(p, 0);
			}

			return model;
		}

	}

	private static class Node<K> {
		private final JQMLFlatTreeModelMap<K> values;
		private final List<Node<K>> children = new ArrayList<>();

		public Node(final JQMLFlatTreeModelMap<K> values) {
			this.values = values;
		}

		public List<Node<K>> getChildrenList() {
			return children;
		}

		public JQMLFlatTreeModelMap<K> getMap() {
			return values;
		}
	}

	private final String modelName;
	private final Pointer modelPointer;
	private final AtomicReference<Thread> eventLoopThread;
	private final IntByReference length = new IntByReference();
	private final Map<String, Integer> indexLookup = new HashMap<>();
	private final Set<K> keySet = new HashSet<>();

	private final FlatTreeAccessor accessor;

	private final Node<K> root = new Node<>(null);

	/**
	 * Constructor.
	 *
	 * @param modelName       The name of the model. This is the name that the model
	 *                        is made available to QML as.
	 * @param keys            Set of keys this model can use.
	 * @param eventLoopThread Reference to the Qt Thread.
	 * @param accessor        Accessor this model will use to to access the C++
	 *                        portion of this model.
	 */
	public JQMLFlatTreeModel(final String modelName, final Set<K> keys, final AtomicReference<Thread> eventLoopThread,
			final FlatTreeAccessor accessor) {
		super(eventLoopThread);
		this.modelName = Objects.requireNonNull(modelName, "modelName is null");
		this.keySet.addAll(keys);
		this.accessor = accessor;
		this.eventLoopThread = eventLoopThread;

		final int roleCount = keySet.size() + 3;
		final String[] roleArray = new String[roleCount];
		final int[] indicesArray = new int[roleCount];
		int roleIndex = AbstractJQMLModel.USER_ROLE_STARTING_INDEX;
		int i = 0;
		for (final K v : keySet) {
			final int temp = roleIndex++;
			final String k = v.toString();
			roleArray[i] = k.toString();
			indicesArray[i] = temp;

			indexLookup.put(k, Integer.valueOf(temp));

			++i;
		}
		roleArray[keySet.size()] = "SUBMODEL";
		indicesArray[keySet.size()] = roleIndex++;
		indexLookup.put("SUBMODEL", Integer.valueOf(indicesArray[keySet.size()]));

		roleArray[keySet.size() + 1] = "PARENT_INDEX";
		indicesArray[keySet.size() + 1] = roleIndex++;
		indexLookup.put("PARENT_INDEX", Integer.valueOf(indicesArray[keySet.size() + 1]));

		roleArray[keySet.size() + 2] = "FLAT_INDEX_INDEX";
		indicesArray[keySet.size() + 2] = roleIndex++;
		indexLookup.put("FLAT_INDEX_INDEX", Integer.valueOf(indicesArray[keySet.size() + 2]));

		verifyEventLoopThread();
		modelPointer = new Pointer(
				FlatTreeModelFunctions.createGenericFlatTreeModel(modelName, roleArray, indicesArray));

		accessor.setModelPointer(modelPointer);
	}

	/**
	 * Appends data to the end the list of child nodes given by TreePath.
	 *
	 * @param p    Path to append the data to.
	 * @param role Role to store the data under.
	 * @param data Data to store.
	 * @return Index the data was stored at or -1 if unable to append.
	 */
	public int append(final TreePath p, final K role, final JVariant data) {
		Objects.requireNonNull(data, "data is null");
		Objects.requireNonNull(role, "role is null");
		Objects.requireNonNull(p, "p is null");

		verifyEventLoopThread();
		data.sendToQML(indexLookup.get(role.toString()).intValue());
		final int newIndex = FlatTreeModelFunctions.appendGenericFlatTreeModelData(Pointer.nativeValue(modelPointer),
				p.toArray());

		createNode(root, TreePath.of(p, newIndex), 0);

		return newIndex;
	}

	/**
	 * Appends a copy of the Map data to the end the list of child nodes given by
	 * TreePath.
	 *
	 * @param p    Path to append the data to.
	 * @param data Map of data to store.
	 * @return Index the data was stored at or -1 if unable to append.
	 */
	public int append(final TreePath p, final Map<K, JVariant> data) {
		Objects.requireNonNull(data, "data is null");
		Objects.requireNonNull(p, "p is null");
		verifyEventLoopThread();

		TreePath path = p;
		for (final Entry<K, JVariant> entry : data.entrySet()) {
			entry.getValue().sendToQML(indexLookup.get(entry.getKey().toString()).intValue());
		}

		final int newIndex = FlatTreeModelFunctions.appendGenericFlatTreeModelData(Pointer.nativeValue(modelPointer),
				p.toArray());

		path = TreePath.of(path, newIndex);

		createNode(root, path, 0);

		return newIndex;
	}

	/**
	 * Clears all data stored in the Map at TreePath. The Map remains valid, in
	 * comparison to remove.
	 *
	 * @param p The TreePath to clear stored data from
	 */
	public void clear(final TreePath p) {
		Objects.requireNonNull(p, "p is null");
		verifyEventLoopThread();

		accessor.setTreePath(p);
		accessor.clear();
	}

	private Node<K> createNode(final Node<K> searchPoint, final TreePath p, final int depth) {
		final TreePath parent = p.removeLast();
		final int i = p.getIndex(depth);
		while (i >= searchPoint.getChildrenList().size()) {
			final JQMLFlatTreeModelMap<K> map = new JQMLFlatTreeModelMap<>(getModelName(), keySet, eventLoopThread,
					accessor.copy(TreePath.of(parent, searchPoint.getChildrenList().size())), indexLookup);
			final Node<K> n = new Node<>(map);

			searchPoint.getChildrenList().add(n);
		}

		if (depth < parent.getCount()) {
			return createNode(searchPoint.getChildrenList().get(i), p, depth + 1);
		} else {
			return searchPoint;
		}
	}

	/**
	 * Returns a Map that can be used to access data at the given TreePath.
	 *
	 * @param p TreePath into this model to get the data for.
	 * @return The Map at TreePath p or null if no data is at TreePath.
	 */
	public JQMLFlatTreeModelMap<K> get(final TreePath p) {
		Objects.requireNonNull(p, "p is null");
		final Optional<Node<K>> n = getNode(root, p, 0);
		if (n.isPresent()) {
			return n.get().getMap();
		} else {
			return null;
		}
	}

	/**
	 * Returns data stored at TreePath and under the Role.
	 *
	 * @param p    TreePath to get the data for.
	 * @param role Role to get the data for.
	 * @return The stored data or Optional.empty() if no data is stored.
	 */
	public Optional<JVariant> getData(final TreePath p, final K role) {
		Objects.requireNonNull(p, "p is null");
		Objects.requireNonNull(role, "key is null");

		verifyEventLoopThread();
		accessor.setTreePath(p);
		return accessor.get(indexLookup.get(role.toString()).intValue(), length);
	}

	/**
	 * @return The name of this model.
	 */
	public String getModelName() {
		return modelName;
	}

	private Optional<Node<K>> getNode(final Node<K> searchPoint, final TreePath p, final int depth) {
		if (depth < p.getCount()) {
			final int i = p.getIndex(depth);
			if (i < searchPoint.getChildrenList().size()) {
				return getNode(searchPoint.getChildrenList().get(i), p, depth + 1);
			} else {
				return Optional.empty();
			}
		} else {
			return Optional.of(searchPoint);
		}
	}

	/**
	 * Inserts data at TreePath, shifting any existing Maps right.
	 *
	 * @param p    TreePath to insert at.
	 * @param data Data to insert.
	 * @param role Role to insert the data under.
	 */
	public void insert(final TreePath p, final K role, final JVariant data) {
		Objects.requireNonNull(data, "data is null");
		Objects.requireNonNull(role, "role is null");
		Objects.requireNonNull(p, "p is null");

		verifyEventLoopThread();
		data.sendToQML(indexLookup.get(role.toString()).intValue());
		FlatTreeModelFunctions.insertGenericFlatTreeModelData(Pointer.nativeValue(modelPointer), p.toArray());

		createNode(root, p, 0);
		resetMapIndicies(p.removeLast());

	}

	private void invalidateAllMaps(final Node<K> n) {
		n.getMap().setIndex(null);
		for (int i = 0; i < n.getChildrenList().size(); ++i) {
			invalidateAllMaps(n.getChildrenList().get(i));
		}
	}

	/**
	 * Returns true if there is data stored under the role at the given TreePath.
	 *
	 * @param p    Path to check the data for.
	 * @param role Role to check for data.
	 * @return True if data is present.
	 */
	public boolean isPresent(final TreePath p, final K role) {
		Objects.requireNonNull(role, "role is null");
		Objects.requireNonNull(p, "p is null");

		verifyEventLoopThread();
		final Pointer indiciesMem = p.serialize();
		final boolean a = FlatTreeModelFunctions.isGenericFlatTreeModelRolePresent(Pointer.nativeValue(modelPointer),
				p.toArray(), indexLookup.get(role.toString()).intValue());

		return a;
	}

	@Override
	public Iterator<Map<K, JVariant>> iterator() {
		return new FlatTreeIterator<>(this);
	}

	/**
	 * Removes the Map of Roles stored at TreePath. The removed Map is no longer
	 * valid after this called.
	 *
	 * @param p Path to the Map to remove from the model.
	 */
	public void remove(final TreePath p) {
		Objects.requireNonNull(p, "p is null");
		Preconditions.checkArgument(!p.equals(TreePath.of()), "Cannot remove root node");
		verifyEventLoopThread();
		final Pointer indiciesMem = p.serialize();
		FlatTreeModelFunctions.eraseGenericFlatTreeModelData(Pointer.nativeValue(modelPointer), p.toArray());

		final Optional<Node<K>> n = removeNode(root, p, 0);
		if (n.isPresent()) {
			invalidateAllMaps(n.get());
			resetMapIndicies(p.removeLast());
		}
	}

	/**
	 * Removes the data stored under Role in the Map at TreePath.
	 *
	 * @param p    The Path to remove the data from.
	 * @param role The Role to remove the data from.
	 */
	public void remove(final TreePath p, final K role) {
		Objects.requireNonNull(p, "p is null");
		Objects.requireNonNull(role, "role is null");

		verifyEventLoopThread();
		accessor.remove(indexLookup.get(role.toString()).intValue(), length);
	}

	private Optional<Node<K>> removeNode(final Node<K> searchPoint, final TreePath p, final int depth) {
		// Not at depth yet
		if (depth < p.getCount() - 1) {
			final int i = p.getIndex(depth);
			if (i < searchPoint.getChildrenList().size()) {
				return removeNode(searchPoint.getChildrenList().get(i), p, depth + 1);
			} else {
				return Optional.empty();
			}
			// At depth
		} else {
			final int i = p.getIndex(depth);
			if (i < searchPoint.getChildrenList().size()) {
				return Optional.of(searchPoint.getChildrenList().remove(i));
			} else {
				return Optional.empty();
			}
		}
	}

	private void resetMapIndicies(final TreePath parentPath) {
		final Optional<Node<K>> n = getNode(root, parentPath, 0);

		if (n.isPresent()) {
			for (int i = 0; i < n.get().getChildrenList().size(); ++i) {
				n.get().getChildrenList().get(i).getMap().setIndex(TreePath.of(parentPath, i));
			}
		}
	}

	/**
	 * Sets the data at the TreePath for the Role.
	 *
	 * @param p    The path to place the data.
	 * @param role The Role to associate with the data.
	 * @param data The data to store.
	 */
	public void setData(final TreePath p, final K role, final JVariant data) {
		Objects.requireNonNull(data, "data is null");
		Objects.requireNonNull(role, "role is null");
		Objects.requireNonNull(p, "p is null");

		verifyEventLoopThread();
		accessor.setTreePath(p);
		accessor.set(data, indexLookup.get(role.toString()).intValue());
	}

	/**
	 * Sets the copies the data in the Map to TreePath.
	 *
	 * @param p    The path to place the data.
	 * @param data A Map to copy data from.
	 */
	public void setData(final TreePath p, final Map<K, JVariant> data) {
		Objects.requireNonNull(data, "data is null");
		Objects.requireNonNull(p, "p is null");

		verifyEventLoopThread();

		final Map<Integer, JVariant> valuesMap = new HashMap<>();
		for (final Entry<K, JVariant> entry : data.entrySet()) {
			valuesMap.put(indexLookup.get(entry.getKey().toString()), entry.getValue());
		}
		accessor.setTreePath(p);
		accessor.set(valuesMap);
	}

	/**
	 * Returns the number of children of the node at TreePath p.
	 *
	 * @param p Path to the parent node.
	 * @return The number of children of TreePath p.
	 */
	public int size(final TreePath p) {
		Objects.requireNonNull(p, "p is null");
		verifyEventLoopThread();
		final Pointer indiciesMem = p.serialize();
		final int a = FlatTreeModelFunctions.getGenericFlatTreeModelSize(Pointer.nativeValue(modelPointer),
				p.toArray());

		return a;
	}

	/**
	 * Sorts the child of TreePath p using the provided comparator.
	 *
	 * @param p Path to the parent of the children to sort.
	 * @param c Comparator to sort the tree nodes.
	 */
	public void sort(final TreePath p, final Comparator<? super Map<K, JVariant>> c) {
		Objects.requireNonNull(p, "p is null");
		verifyEventLoopThread();

		final Optional<Node<K>> node = getNode(root, p, 0);
		if (node.isPresent()) {
			final List<Map<K, JVariant>> children = new ArrayList<>(node.get().getChildrenList().size());
			for (final Node<K> n : node.get().getChildrenList()) {
				children.add(n.getMap());
			}

			children.sort(c);

			final int[] ordering = new int[children.size()];
			for (int i = 0; i < children.size(); ++i) {
				final JQMLFlatTreeModelMap<K> map = (JQMLFlatTreeModelMap<K>) children.get(i);
				ordering[i] = map.getIndex().getLast();
			}

			final Pointer indiciesMem = p.serialize();
			FlatTreeModelFunctions.reorderGenericFlatTreeModel(Pointer.nativeValue(modelPointer), p.toArray(),
					ordering);

			resetMapIndicies(p);
		}
	}
}
