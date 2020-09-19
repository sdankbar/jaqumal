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
package com.github.sdankbar.qml.invocation;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.QtThread;
import com.github.sdankbar.qml.cpp.jni.InvocationFunctions;
import com.github.sdankbar.qml.utility.QMLRequestParser;
import com.google.common.base.Preconditions;

public class InvokableDispatcher {

	private static final Logger log = LoggerFactory.getLogger(InvokableDispatcher.class);

	private final Map<String, InvokableWrapper> invokables = new HashMap<>();

	public InvokableDispatcher() {
		// Empty Implementation
	}

	@QtThread
	public void registerInvokable(final String name, final Object obj) {
		Objects.requireNonNull(name, "name is null");
		Objects.requireNonNull(obj, "obj is null");
		Preconditions.checkArgument(!invokables.containsKey(name), "[%s] is already registered", name);

		InvocationFunctions.addInvokable(name);

		invokables.put(name, new InvokableWrapper(obj));
	}

	private boolean invoke(final String invokableName, final String methodName, final ByteBuffer data) {
		try {
			final QMLRequestParser parser = new QMLRequestParser(data);
			final InvokableWrapper wrapper = invokables.get(invokableName);
			if (wrapper != null) {
				final JVariant result = wrapper.invoke(methodName, parser);
				if (result != null) {
					result.sendToQML(0);
					return true;
				} else {
					return false;
				}
			} else {
				log.warn("[{}] is not a valid invokable", invokableName);
				return false;
			}
		} catch (final Exception e) {
			log.warn("Exception caught during invoke", e);
			return false;
		}
	}

	public JVariant invoke(final String target, final Map<String, JVariant> args) {
		Objects.requireNonNull(target, "target is null");
		Objects.requireNonNull(args, "args is null");
		Preconditions.checkArgument(!args.containsKey(null), "Contains null key");
		Preconditions.checkArgument(!args.containsValue(null), "Contains null value");

		final String[] keys = new String[args.size()];
		int i = 0;
		for (final Entry<String, JVariant> entry : args.entrySet()) {
			keys[i] = entry.getKey();
			entry.getValue().sendToQML(i);
			++i;
		}

		// Send request to c++
		return InvocationFunctions.invokeQML(target, keys);
	}

}
