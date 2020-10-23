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
package com.github.sdankbar.qml.models.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.eventing.EventDispatcher;
import com.github.sdankbar.qml.eventing.builtin.BuiltinEventProcessor;
import com.github.sdankbar.qml.eventing.builtin.ListSelectionChangedEvent;
import com.github.sdankbar.qml.eventing.builtin.RequestScrollListToPositionEvent;
import com.github.sdankbar.qml.eventing.builtin.RequestScrollListToPositionEvent.PositionMode;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.google.common.base.Preconditions;
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

	/**
	 * Listener for selection changes in a JQMLListViewModel
	 *
	 * @param <K> Key type for the list model.
	 */
	public interface SelectionListener<K> {
		/**
		 * @param changedIndices  Set of indices whose selection changed. For the case
		 *                        of a removed value causing the selection to change,
		 *                        the index is the index of the value before it was
		 *                        removed.
		 * @param selectedIndices Set of indices that are selected after the change.
		 * @param selected        Set of items that are selected after the change.
		 */
		void selectionChanged(ImmutableSet<Integer> changedIndices, ImmutableList<Integer> selectedIndices,
				ImmutableList<Map<K, JVariant>> selected);
	}

	/**
	 * Enumeration of the modes that assign can be in.
	 */
	public enum AssignMode {
		/**
		 * No items are selected after assign() is called.
		 */
		CLEAR_SELECTION,
		/**
		 * The items that were selected before the assign() remain selected afterwards.
		 * One of the values in entry is used to determine which items to reselect,
		 * rather than using indicies.
		 */
		MAINTAIN_SELECTION
	}

	/**
	 * Enum of the various selection modes supported by the JQMLListViewModel.
	 */
	public enum SelectionMode {
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

	private static final String SELECTION_COUNT_KEY = "selectionCount";

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
	private final EventDispatcher<?> dispatcher;

	private final List<SelectionListener<K>> selectionListeners = new ArrayList<>();

	private final K isSelectedKey;

	public JQMLListViewModel(final String modelName, final ImmutableSet<K> keys, final JQMLApplication<?> app,
			final SelectionMode mode, final PutMode putMode) {
		selectionMode = Objects.requireNonNull(mode, "mode is null");
		isSelectedKey = getKey(keys, "is_selected");
		listModel = app.getModelFactory().createListModel(modelName, keys, putMode);
		listModel.putRootValue(SELECTION_COUNT_KEY, JVariant.NULL_INT);
		listModel.registerListener(new ListListener<K>() {

			@Override
			public void added(final int index, final Map<K, JVariant> map) {
				handleAddedElement(map);
			}

			@Override
			public void removed(final int index, final Map<K, JVariant> map) {
				handleRemovdElement(index, map);
			}

		});

		dispatcher = app.getEventDispatcher();
		dispatcher.register(ListSelectionChangedEvent.class, this);
	}

	/**
	 * Assigns the data in list to this list. Equivalent to clear and addAll.
	 *
	 * @param list  List to assign to this list.
	 * @param idKey Key in the Map used to determine which items to reselect. Values
	 *              of the key should be unique.
	 * @param mode  The selection mode for the assign operation. Affects if items
	 *              are reselected after the assignment.
	 */
	public void assign(final List<Map<K, JVariant>> list, final K idKey, final AssignMode mode) {
		Objects.requireNonNull(list, "list is null");
		Objects.requireNonNull(idKey, "idKey is null");
		Objects.requireNonNull(mode, "mode is null");

		final Set<JVariant> selectedValules;
		final boolean maintainSelection = mode == AssignMode.MAINTAIN_SELECTION;
		if (maintainSelection) {
			selectedValules = getSelected().stream().map(m -> m.get(idKey)).filter(v -> v != null)
					.collect(ImmutableSet.toImmutableSet());
		} else {
			selectedValules = ImmutableSet.of();
		}

		getModel().assign(list);

		if (maintainSelection) {
			int i = 0;
			for (final Map<K, JVariant> map : getModel()) {
				final JVariant value = map.get(idKey);
				if (selectedValules.contains(value)) {
					select(i);
				} else {
					map.put(isSelectedKey, JVariant.FALSE);
				}
				++i;
			}
		} else {
			for (final Map<K, JVariant> map : getModel()) {
				map.put(isSelectedKey, JVariant.FALSE);
			}
		}
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

	public <L> L getSelected(final K key, final Class<L> t) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(t, "t is null");

		final ImmutableList<Map<K, JVariant>> selected = getSelected();
		Preconditions.checkArgument(selected.size() == 1, "Number of selected is %s, must be 1", selected.size());
		final JVariant value = selected.get(0).get(key);
		Preconditions.checkArgument(value != null, "value for key on selected item isn't present");
		final Optional<L> opt = value.asType(t);
		Preconditions.checkArgument(opt.isPresent(), "value for key cannot be converted to %s", t.getName());
		return opt.get();
	}

	public <L> Optional<L> getSelectedOptional(final K key, final Class<L> t) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(t, "t is null");

		final ImmutableList<Map<K, JVariant>> selected = getSelected();
		if (selected.size() != 1) {
			return Optional.empty();
		}
		final JVariant value = selected.get(0).get(key);
		if (value != null) {
			return value.asType(t);
		} else {
			return Optional.empty();
		}
	}

	public <L> ImmutableList<L> getAllSelected(final K key, final Class<L> t) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(t, "t is null");
		final ImmutableList<Map<K, JVariant>> selected = getSelected();
		return selected.stream().map(m -> m.get(key)).filter(var -> var != null && var.isInstanceOf(t))
				.map(m -> m.asType(t).get()).collect(ImmutableList.toImmutableList());
	}

	public <L> ImmutableList<L> getAllSelected(final K key, final Class<L> t, final L defaultValue) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(t, "t is null");
		final ImmutableList<Map<K, JVariant>> selected = getSelected();
		return selected.stream().map(m -> m.get(key)).map(var -> {
			if (var == null || !var.isInstanceOf(t)) {
				return defaultValue;
			} else {
				return var.asType(t, defaultValue);
			}
		}).collect(ImmutableList.toImmutableList());
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

	private void handleRemovdElement(final int index, final Map<K, JVariant> map) {
		final JVariant v = map.get(isSelectedKey);
		// If removed element was seleted, fire selection changed event
		if (v != null && v.asBoolean(false)) {
			fireSelectionEvent(ImmutableSet.of(Integer.valueOf(index)));
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
	 * Scrolls the ListView attached to this model to the provided index.
	 *
	 * @param index Index to scroll to.
	 * @param mode  Scrolling behavior.
	 */
	public void positionViewAtIndex(final int index, final PositionMode mode) {
		dispatcher.submitBuiltin(new RequestScrollListToPositionEvent(listModel.getModelName(), index, mode));
	}

	/**
	 * Scrolls the ListView attached to this model to the first selected item in the
	 * list.  If no item is selected, no action is taken.
	 *
	 * @param mode Scrolling behavior.
	 */
	public void positionViewAtFirstSelectedIndex(final PositionMode mode) {
		final ImmutableList<Integer> indices = getSelectedIndices();

		if (!indices.isEmpty()) {
			final int index = indices.get(0).intValue();
			positionViewAtIndex(index, mode);
		}
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
			final boolean oldState = map.getOrDefault(isSelectedKey, JVariant.FALSE).asBoolean(false);

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
