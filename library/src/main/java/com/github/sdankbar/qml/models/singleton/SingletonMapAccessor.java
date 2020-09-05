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
package com.github.sdankbar.qml.models.singleton;

import java.util.Map;
import java.util.Optional;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.cpp.jni.singleton.SingletonModelFunctions;
import com.github.sdankbar.qml.models.MapAccessor;

/**
 * Implementation of MapAccessor that is used to modify the map in an
 * SingletonModel.
 */
public class SingletonMapAccessor extends MapAccessor {

	@Override
	public void clear() {
		SingletonModelFunctions.clearGenericObjectModel(modelPointer);

	}

	@Override
	public Optional<JVariant> get(final int roleIndex) {
		final JVariant received = SingletonModelFunctions.getGenericObjectModelData(modelPointer, roleIndex);
		return Optional.ofNullable(received);
	}

	@Override
	public Optional<JVariant> remove(final int roleIndex) {
		final Optional<JVariant> existingValue = get(roleIndex);

		SingletonModelFunctions.clearGenericObjectModelRole(modelPointer, roleIndex);

		return existingValue;
	}

	@Override
	public void set(final JVariant value, final int roleIndex) {
		value.sendToQML(roleIndex);
		SingletonModelFunctions.setGenericObjectModelData(modelPointer);
	}

	@Override
	public void set(final Map<Integer, JVariant> valuesMap) {
		for (final Map.Entry<Integer, JVariant> e : valuesMap.entrySet()) {
			e.getValue().sendToQML(e.getKey().intValue());
		}

		SingletonModelFunctions.setGenericObjectModelData(modelPointer);

	}

}
