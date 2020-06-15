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
package com.github.sdankbar.qml.models.list;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.eventing.builtin.BuiltinEventProcessor;
import com.github.sdankbar.qml.eventing.builtin.ListSelectionChangedEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * QML model for getting and setting properties in a list model. Manages
 * selection of items in the list. Intended to be paired with the JListView QML
 * type. Methods on the JListView type allow for selecting and deselecting items
 * in the list. Users of this class must not directly modify the "is_selected"
 * value since the selection constraints will not be enforced and any callbacks
 * fired.
 *
 * @param <K> Key type for the list model. Must contain the key "is_selected".
 */
public class JQMLListViewModel<K> implements BuiltinEventProcessor {

	private static final String SELECTION_COUNT_KEY = "selectionCount";

	/**
	 * Listener for selection changes in a JQMLListViewModel
	 *
	 * @param <K> Key type for the list model.
	 */
	public interface SelectionListener<K> {
		/**
		 * @param changedIndices  Set of indices whose selection changed.
		 * @param selectedIndices Set of indices that are selected after the change.
		 * @param selected        Set of items that are selected after the change.
		 */
		void selectionChanged(ImmutableSet<Integer> changedIndices, ImmutableList<Integer> selectedIndices,
				ImmutableList<Map<K, JVariant>> selected);
	}

	/**
	 * Enum of the various selection modes supported by the JQMLListViewModel.
	 */
	public static enum SelectionMode {
		/**
		 * Selection is not allowed
		 */
		NONE,
		/**
		 * Only 0 or 1 item may be selected
		 */
		SINGLE,
		/**
		 * 0 or more items may be selected
		 */
		MULTIPLE
	}

	private static <K> void checkKeySet(final ImmutableSet<K> keys) {
		for (final K v : keys) {
			if (v.toString().equals("is_selected")) {
				return;
			}
		}
		throw new IllegalArgumentException("Key set must contain \"is_selected\"");
	}

	/**
	 * Creates a new model.
	 *
	 * @param modelName Name of the model. Must be unique.
	 * @param keyClass  Class of the Enum that is used as the new model's key.
	 * @param app       JQMLApplication object for this application.
	 * @param mode      The selection mode for the model.
	 * @return The new model.
	 */
	public static <T extends Enum<T>> JQMLListViewModel<T> create(final String modelName, final Class<T> keyClass,
			final JQMLApplication<?> app, final SelectionMode mode) {
		final ImmutableSet<T> userKeys = ImmutableSet.copyOf(EnumSet.allOf(keyClass));
		checkKeySet(userKeys);
		return new JQMLListViewModel<>(modelName, userKeys, app, mode);
	}

	/**
	 * Creates a new model.
	 *
	 * @param modelName Name of the model. Must be unique.
	 * @param keySet    The set of keys that can be used by the new model.
	 * @param app       JQMLApplication object for this application.
	 * @param mode      The selection mode for the model.
	 * @return The new model.
	 */
	public static <T> JQMLListViewModel<T> create(final String modelName, final ImmutableSet<T> keySet,
			final JQMLApplication<?> app, final SelectionMode mode) {
		checkKeySet(keySet);
		return new JQMLListViewModel<>(modelName, keySet, app, mode);
	}

	private static <K> K getKey(final ImmutableSet<K> keys, final String keyName) {
		for (final K v : keys) {
			if (v.toString().equals(keyName)) {
				return v;
			}
		}
		throw new IllegalArgumentException("Failed to find key: " + keyName);
	}

	private final JQMLListModel<K> listModel;
	private final SelectionMode selectionMode;

	private final List<SelectionListener<K>> selectionListeners = new ArrayList<>();

	private final K isSelectedKey;

	private JQMLListViewModel(final String modelName, final ImmutableSet<K> keys, final JQMLApplication<?> app,
			final SelectionMode mode) {
		selectionMode = Objects.requireNonNull(mode, "mode is null");
		isSelectedKey = getKey(keys, "is_selected");
		listModel = app.getModelFactory().createListModel(modelName, keys);
		listModel.putRootValue(SELECTION_COUNT_KEY, JVariant.NULL_INT);
		listModel.registerListener((index, map) -> {
			handleAddedElement(map);
		});

		app.getEventDispatcher().register(ListSelectionChangedEvent.class, this);
	}

	/**
	 * Deselects the item at the index. If SelectionMode is NONE, no change is made.
	 *
	 * @param index Index of the item to deselect.
	 */
	public void deselect(final int index) {
		setSelection(index, false);
	}

	/**
	 * Deselects all items in the list. If SelectionMode is NONE, no change is made.
	 */
	public void deselectAll() {
		if (selectionMode != SelectionMode.NONE) {
			final ImmutableSet.Builder<Integer> changedIndicesBuilder = ImmutableSet.builder();

			deselectAllSelected(changedIndicesBuilder);

			fireSelectionEvent(changedIndicesBuilder.build());
		}
	}

	private void deselectAllSelected(final ImmutableSet.Builder<Integer> changedIndicesBuilder) {
		for (int i = 0; i < listModel.size(); ++i) {
			final Map<K, JVariant> entry = listModel.get(i);
			final JVariant v = entry.get(isSelectedKey);
			if (v != null && v.asBoolean(false)) {
				entry.put(isSelectedKey, JVariant.FALSE);
				changedIndicesBuilder.add(Integer.valueOf(i));
			}
		}
	}

	private void fireSelectionEvent(final ImmutableSet<Integer> changedIndices) {
		if (!changedIndices.isEmpty()) {
			final ImmutableList<Map<K, JVariant>> selected = getSelected();
			final ImmutableList<Integer> selectedIndices = getSelectedIndices();

			listModel.putRootValue(SELECTION_COUNT_KEY, new JVariant(selected.size()));

			for (final SelectionListener<K> l : selectionListeners) {
				l.selectionChanged(changedIndices, selectedIndices, selected);
			}
		}
	}

	/**
	 * @return The underlaying JQMLListModel.
	 */
	public JQMLListModel<K> getModel() {
		return listModel;
	}

	/**
	 * @return List of the selected items. Returned list is in the same order as the
	 *         model list.
	 */
	public ImmutableList<Map<K, JVariant>> getSelected() {
		final ImmutableList.Builder<Map<K, JVariant>> builder = ImmutableList.builder();
		for (int i = 0; i < listModel.size(); ++i) {
			final Map<K, JVariant> map = listModel.get(i);
			final JVariant value = map.get(isSelectedKey);
			if (value != null && value.asBoolean(false)) {
				builder.add(map);
			}
		}
		return builder.build();
	}

	/**
	 * @return List of the selected item indices. Returned list is in the same order
	 *         as the model list.
	 */
	public ImmutableList<Integer> getSelectedIndices() {
		final ImmutableList.Builder<Integer> builder = ImmutableList.builder();
		for (int i = 0; i < listModel.size(); ++i) {
			final Map<K, JVariant> map = listModel.get(i);
			final JVariant value = map.get(isSelectedKey);
			if (value != null && value.asBoolean(false)) {
				builder.add(Integer.valueOf(i));
			}
		}
		return builder.build();
	}

	/**
	 * Callback for the ListSelectionChangedEvent.
	 *
	 * @param e Event object
	 */
	@Override
	public void handle(final ListSelectionChangedEvent e) {
		if (isEventValidForModel(e)) {
			setSelection(e.getSelectedIndex(), e.isSelected());
		}
	}

	private void handleAddedElement(final Map<K, JVariant> map) {
		if (map.get(isSelectedKey) == null) {
			map.put(isSelectedKey, JVariant.FALSE);
		}
	}

	private boolean isEventValidForModel(final ListSelectionChangedEvent e) {
		return e.getModelName().equals(listModel.getModelName());
	}

	/**
	 * Registers a listener for selection changed callbacks.
	 *
	 * @param l The new listener.
	 */
	public void registerSelectionListener(final SelectionListener<K> l) {
		selectionListeners.add(Objects.requireNonNull(l, "l is null"));
	}

	/**
	 * Selects the item at the index. If SelectionMode is NONE, no change is made.
	 *
	 * @param index Index of the item to select.
	 */
	public void select(final int index) {
		setSelection(index, true);
	}

	/**
	 * Sets the selection of the item at the index to isSelected. If SelectionMode
	 * is NONE, no change is made.
	 *
	 * @param index      Index of the item to update its selection.
	 * @param isSelected The item's new selection value.
	 */
	public void setSelection(final int index, final boolean isSelected) {
		if (selectionMode != SelectionMode.NONE && (0 <= index && index < listModel.size())) {
			final Map<K, JVariant> map = listModel.get(index);
			final boolean oldState = map.get(isSelectedKey).asBoolean(false);

			if (oldState != isSelected) {

				final ImmutableSet.Builder<Integer> changedIndicesBuilder = ImmutableSet.builder();
				if (isSelected && selectionMode == SelectionMode.SINGLE) {
					// If only 1 item can be selected at a time and selecting
					// a new item, deselect all items.
					deselectAllSelected(changedIndicesBuilder);
				}
				changedIndicesBuilder.add(Integer.valueOf(index));

				map.put(isSelectedKey, JVariant.valueOf(isSelected));
				fireSelectionEvent(changedIndicesBuilder.build());
			}
		}
	}

	/**
	 * Unregisters a selection listener.
	 *
	 * @param l The listener to unregister.
	 */
	public void unregisterSelectionListener(final SelectionListener<K> l) {
		selectionListeners.remove(l);
	}
}
