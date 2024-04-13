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
package com.github.sdankbar.examples.painter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.github.sdankbar.qml.JQMLApplication;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.eventing.NullEventFactory;
import com.github.sdankbar.qml.eventing.NullEventProcessor;
import com.github.sdankbar.qml.images.JQMLImageProvider;
import com.github.sdankbar.qml.models.AbstractJQMLMapModel.PutMode;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;
import com.github.sdankbar.qml.painting.PainterInstructions;
import com.github.sdankbar.qml.painting.PainterInstructionsBuilder;

/**
 * Example application that allow for using a QPainter style of drawing.
 *
 */
public class App {

	private static BufferedImage IMAGE = null;

	private enum Roles {
		drawable, drawable2
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final JQMLApplication<NullEventProcessor> app = JQMLApplication.create(args, new NullEventFactory<>());
		final JQMLSingletonModel<Roles> model = app.getModelFactory().createSingletonModel("drawModel", Roles.class,
				PutMode.RETURN_PREVIOUS_VALUE);

		app.registerImageProvider(new JQMLImageProvider() {

			@Override
			public BufferedImage requestImage(final String id, final Dimension requestedSize) {
				return IMAGE;
			}
		}, "imageGetter");

		final PainterInstructionsBuilder b = new PainterInstructionsBuilder();
		b.setPen(new Color(255, 0, 0));
		b.drawRect(5, 5, 50, 20);
		b.setPen(new Color(0, 255, 0));
		b.drawRect(100, 100, 50, 20);
		model.put(Roles.drawable, new JVariant(b.build()));
		final BufferedImage image = b.build().render(300, 300);
		IMAGE = image;

		final PainterInstructionsBuilder b2 = new PainterInstructionsBuilder();
		b2.drawImage(new Rectangle(0, 0, 300, 300), new Rectangle(0, 0, 300, 300), image);
		final PainterInstructions i = b2.build();
		model.put(Roles.drawable2, new JVariant(i));

		System.out.println("Register=" + JQMLApplication.registerResourceFromSystemResource("painter.rcc", ""));

		System.out.print("Loading...");
		app.loadQMLFile(":/painter.qml");
		System.out.println("Done");
		app.execute();
	}
}
