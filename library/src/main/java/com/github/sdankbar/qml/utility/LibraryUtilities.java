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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.Adler32;

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
		BufferedInputStream stream = getResourceStream(loadDir + "/" + libName + extension);
		try {
			final File libraryFile = new File(libName + extension);

			boolean alreadyUpToDate = false;
			if (libraryFile.exists()) {
				final Adler32 hash = new Adler32();
				hash.update(Files.readAllBytes(libraryFile.toPath()));
				final long existingHash = hash.getValue();

				hash.reset();
				int b;
				while ((b = stream.read()) != -1) {
					hash.update(b);
				}
				final long newHash = hash.getValue();

				alreadyUpToDate = existingHash == newHash;
				if (!alreadyUpToDate) {
					// Reset the stream so it can be read again
					stream = getResourceStream(loadDir + "/" + libName + extension);
				}
			}

			if (!alreadyUpToDate) {
				logger.info("Storing " + libraryFile);
				libraryFile.delete();
				try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(libraryFile))) {
					int b;
					while ((b = stream.read()) != -1) {
						writer.write(b);
					}
				}
			} else {
				logger.info("Check sum of " + libraryFile + " matches, using as is.");
			}

			System.load(libraryFile.getAbsolutePath());
		} catch (final IOException e) {
			logger.error("Failed to load " + libName + extension, e);
		}
		logger.info("Finish - Load " + libName + extension);
	}

	private static BufferedInputStream getResourceStream(final String resource) {
		final InputStream windowsLib = ClassLoader.getSystemResourceAsStream(resource);
		return new BufferedInputStream(windowsLib);
	}

	private LibraryUtilities() {
		// Empty Implementation
	}

}
