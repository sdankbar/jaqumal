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
package com.sdankbar.jaqumal.rcc_plugin;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Runs rcc on the provided qrc file.
 */
@Mojo(name = "rcc_plugin", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class RccPlugin extends AbstractMojo {
	/**
	 * Location of input qrc file.
	 *
	 * @required
	 */
	@Parameter(required = true)
	private File qrcFile;

	/**
	 * Location to write the rcc file to.
	 *
	 * @required
	 */
	@Parameter(required = true)
	private String outputRccFile;

	@Override
	public void execute() throws MojoExecutionException {
		final File resourcesDir = new File("src/main/resources");
		if (!resourcesDir.exists()) {
			System.out.println(resourcesDir.getPath() + " does not exist");
			throw new MojoExecutionException("Must manuall create " + resourcesDir.getPath());
		}

		final ProcessBuilder b = new ProcessBuilder("rcc", "-binary", "-o",
				new File(resourcesDir, outputRccFile).getAbsolutePath(), qrcFile.getAbsolutePath());
		b.inheritIO();
		try {
			final Process p = b.start();
			final int result = p.waitFor();
			if (result != 0) {
				throw new MojoExecutionException("Error generating rcc file");
			}
		} catch (final IOException | InterruptedException e) {
			throw new MojoExecutionException("Error generating rcc file", e);
		}
	}
}
