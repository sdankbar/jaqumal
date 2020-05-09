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
package com.github.sdankbar.qml.eventing.builtin;

import com.github.sdankbar.qml.eventing.Event;

/**
 * Event that is generated when an item in a JListView is changing its selection
 * state
 */
public class ListSelectionChangedEvent extends Event<BuiltinEventProcessor> {

	private final boolean newIsSelected;
	private final int index;
	private final String modelName;

	public ListSelectionChangedEvent(final boolean newIsSelected, final int index, final String modelName) {
		this.newIsSelected = newIsSelected;
		this.index = index;
		this.modelName = modelName;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
		final ListSelectionChangedEvent other = (ListSelectionChangedEvent) obj;
		if (index != other.index) {
			return false;
		}
		if (modelName == null) {
			if (other.modelName != null) {
				return false;
			}
		} else if (!modelName.equals(other.modelName)) {
			return false;
		}
		if (newIsSelected != other.newIsSelected) {
			return false;
		}
		return true;
	}

	public String getModelName() {
		return modelName;
	}

	public int getSelectedIndex() {
		return index;
	}

	@Override
	public void handle(final BuiltinEventProcessor processor) {
		processor.handle(this);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((modelName == null) ? 0 : modelName.hashCode());
		result = prime * result + (newIsSelected ? 1231 : 1237);
		return result;
	}

	public boolean isSelected() {
		return newIsSelected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ListSelectionChangedEvent [newIsSelected=" + newIsSelected + ", index=" + index + ", modelName="
				+ modelName + "]";
	}

}
