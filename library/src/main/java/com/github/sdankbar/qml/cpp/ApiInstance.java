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
package com.github.sdankbar.qml.cpp;

import com.github.sdankbar.qml.cpp.jna.OldApplicationFunctions;
import com.github.sdankbar.qml.cpp.jna.flat_tree.FlatTreeQMLAPI;
import com.github.sdankbar.qml.cpp.jna.singleton.SingletonQMLAPI;
import com.sun.jna.Native;

/**
 * Class initializes instances of the C++ APIs.
 */
public class ApiInstance {

	public static final OldApplicationFunctions LIB_INSTANCE = Native.load("Jaqumal", OldApplicationFunctions.class);
	
	/**
	 * C++ API for creating and modifying Flat Tree models.
	 */
	public static final FlatTreeQMLAPI FLAT_TREE_LIB_INSTANCE = Native.load("Jaqumal", FlatTreeQMLAPI.class);

	/**
	 * C++ API for creating and modifying Singleton models.
	 */
	public static final SingletonQMLAPI SINGLETON_LIB_INSTANCE = Native.load("Jaqumal", SingletonQMLAPI.class);

	private ApiInstance() {
		// Empty Implementation
	}

}
