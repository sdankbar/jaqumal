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
 * Goal which touches a timestamp file.
 *
 * @goal touch
 *
 * @phase process-sources
 */
@Mojo(name = "RccPlugin", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class RccPlugin extends AbstractMojo {
	/**
	 * Location of qrc file.
	 *
	 * @required
	 */
	@Parameter(required = true)
	private File qrcFile;

	/**
	 * Location to write the rcc file.
	 *
	 * @required
	 */
	@Parameter(required = true)
	private File outputRccFile;

	@Override
	public void execute() throws MojoExecutionException {
		final ProcessBuilder b = new ProcessBuilder("rcc -binary -o src/main/resources/painter.rcc painter2.qrc");
		try {
			final Process p = b.start();
			final int result = p.waitFor();
		} catch (final IOException | InterruptedException e) {
			throw new MojoExecutionException("Error generating rcc file", e);
		}
	}
}
