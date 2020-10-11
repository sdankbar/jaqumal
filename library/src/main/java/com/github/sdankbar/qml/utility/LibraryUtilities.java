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
package com.github.sdankbar.qml.utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryUtilities {

	private static final Logger logger = LoggerFactory.getLogger(LibraryUtilities.class);

	public static void loadLibrary(final String libName) {
		if (SystemUtils.IS_OS_WINDOWS) {
			loadLibrary(libName, "win32-x86-64", ".dll");
		} else {
			loadLibrary("lib" + libName, "linux-x86-64", ".so");
		}
	}

	private static void loadLibrary(final String libName, final String loadDir, final String extension) {
		logger.info("Load " + libName + extension);
		final InputStream windowsLib = ClassLoader.getSystemResourceAsStream(loadDir + "/" + libName + extension);
		final BufferedInputStream stream = new BufferedInputStream(windowsLib);
		try {
			final File libraryFile = File.createTempFile(libName, extension);
			try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(libraryFile))) {
				int b;
				while ((b = stream.read()) != -1) {
					writer.write(b);
				}
			}

			// Cleanup extracted dlls from previous runs. Only risk is if two Jaqumal apps
			// are started at the same time, could delete each other's extract dlls.
			// Once System.load is called, this is less of an issue.
			final File temporaryDir = libraryFile.getParentFile();
			final File[] oldDlls = temporaryDir.listFiles((FilenameFilter) (dir, name) -> name.startsWith(libName)
					&& name.endsWith(extension) && !name.equals(libraryFile.getName()));
			for (final File f : oldDlls) {
				f.delete();
			}

			System.load(libraryFile.getAbsolutePath());
		} catch (final IOException e) {
			logger.error("Failed to load " + libName + extension, e);
		}
		logger.info("Finish - Load " + libName + extension);
	}

	private LibraryUtilities() {
		// Empty Implementation
	}

}
