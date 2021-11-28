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
package com.github.sdankbar.qml.painting;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import com.github.sdankbar.qml.fonts.JFont;
import com.github.sdankbar.qml.painting.PainterInstructions.ClipOperation;
import com.github.sdankbar.qml.painting.PainterInstructions.CompositionMode;
import com.github.sdankbar.qml.painting.PainterInstructions.FillMode;
import com.github.sdankbar.qml.painting.PainterInstructions.PainterFunction;
import com.github.sdankbar.qml.painting.PainterInstructions.RenderHint;
import com.github.sdankbar.qml.utility.ResizableByteBuffer;

public class PainterInstructionsBuilder {

	private final ResizableByteBuffer buffer = new ResizableByteBuffer();

	private byte[] bufferedImageToArray(final BufferedImage image) {
		final int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		final ByteBuffer b = ByteBuffer.allocate(4 * pixels.length);
		b.order(ByteOrder.nativeOrder());
		for (final int p : pixels) {
			b.putInt(p);
		}
		return b.array();
	}

	public void drawArc(final int x, final int y, final int width, final int height, final int startAngle,
			final int spanAngle) {
		buffer.putInt(PainterFunction.drawArcInteger.ordinal());
		buffer.putInt(x);
		buffer.putInt(y);
		buffer.putInt(width);
		buffer.putInt(height);
		buffer.putInt(spanAngle);
	}

	public void drawChord(final int x, final int y, final int width, final int height, final int startAngle,
			final int spanAngle) {
		buffer.putInt(PainterFunction.drawChordInteger.ordinal());
		buffer.putInt(x);
		buffer.putInt(y);
		buffer.putInt(width);
		buffer.putInt(height);
		buffer.putInt(startAngle);
		buffer.putInt(spanAngle);
	}

	public void drawConvexPolygon(final List<Point> points) {
		buffer.putInt(PainterFunction.drawConvexPolygonInteger.ordinal());
		buffer.putInt(points.size());
		for (final Point p : points) {
			buffer.putInt(p.x);
			buffer.putInt(p.y);
		}
	}

	public void drawEllipse(final int x, final int y, final int width, final int height) {
		buffer.putInt(PainterFunction.drawEllipseInteger.ordinal());
		buffer.putInt(x);
		buffer.putInt(y);
		buffer.putInt(width);
		buffer.putInt(height);
	}

	public void drawImage(final Rectangle target, final Rectangle source, final BufferedImage image) {
		buffer.putInt(PainterFunction.drawImageInteger.ordinal());
		buffer.putInt(target.x);
		buffer.putInt(target.y);
		buffer.putInt(target.width);
		buffer.putInt(target.height);
		buffer.putInt(source.x);
		buffer.putInt(source.y);
		buffer.putInt(source.width);
		buffer.putInt(source.height);
		buffer.putInt(image.getWidth());
		buffer.putInt(image.getHeight());
		buffer.putBytes(bufferedImageToArray(image));
	}

	public void drawLine(final int x1, final int y1, final int x2, final int y2) {
		buffer.putInt(PainterFunction.drawLineInteger.ordinal());
		buffer.putInt(x1);
		buffer.putInt(y1);
		buffer.putInt(x2);
		buffer.putInt(y2);
	}

	public void drawLines(final List<Point> points) {
		buffer.putInt(PainterFunction.drawLinesInteger.ordinal());
		buffer.putInt(points.size());
		for (final Point p : points) {
			buffer.putInt(p.x);
			buffer.putInt(p.y);
		}
	}

	public void drawPie(final int x, final int y, final int width, final int height, final int startAngle,
			final int spanAngle) {
		buffer.putInt(PainterFunction.drawPieInteger.ordinal());
		buffer.putInt(x);
		buffer.putInt(y);
		buffer.putInt(width);
		buffer.putInt(height);
		buffer.putInt(startAngle);
		buffer.putInt(spanAngle);
	}

	public void drawPoint(final int x, final int y) {
		buffer.putInt(PainterFunction.drawPointInteger.ordinal());
		buffer.putInt(x);
		buffer.putInt(y);
	}

	public void drawPoints(final List<Point> points) {
		buffer.putInt(PainterFunction.drawPointsInteger.ordinal());
		buffer.putInt(points.size());
		for (final Point p : points) {
			buffer.putInt(p.x);
			buffer.putInt(p.y);
		}
	}

	public void drawPolygon(final List<Point> points, final FillMode mode) {
		buffer.putInt(PainterFunction.drawPolygonInteger.ordinal());
		buffer.putInt(mode.ordinal());
		buffer.putInt(points.size());
		for (final Point p : points) {
			buffer.putInt(p.x);
			buffer.putInt(p.y);
		}
	}

	public void drawPolyline(final List<Point> points) {
		buffer.putInt(PainterFunction.drawPolylineInteger.ordinal());
		buffer.putInt(points.size());
		for (final Point p : points) {
			buffer.putInt(p.x);
			buffer.putInt(p.y);
		}
	}

	public void drawRect(final int x, final int y, final int width, final int height) {
		buffer.putInt(PainterFunction.drawRectInteger.ordinal());
		buffer.putInt(x);
		buffer.putInt(y);
		buffer.putInt(width);
		buffer.putInt(height);
	}

	public void drawRoundedRect(final int x, final int y, final int w, final int h, final double xRadius,
			final double yRadius) {
		buffer.putInt(PainterFunction.drawRoundedRectInteger.ordinal());
		buffer.putInt(x);
		buffer.putInt(y);
		buffer.putInt(w);
		buffer.putInt(h);
		buffer.putDouble(xRadius);
		buffer.putDouble(yRadius);
	}

	public void drawStaticText(final int left, final int top, final String staticText) {
		buffer.putInt(PainterFunction.drawStaticText.ordinal());
		buffer.putInt(left);
		buffer.putInt(top);
		buffer.putString(staticText);
	}

	public void drawText(final int x, final int y, final String text) {
		buffer.putInt(PainterFunction.drawTextSimple.ordinal());
		buffer.putInt(x);
		buffer.putInt(y);
		buffer.putString(text);
	}

	public void drawText(final int x, final int y, final int width, final int height, final int flags,
			final String text) {
		buffer.putInt(PainterFunction.drawTextComplex.ordinal());
		buffer.putInt(x);
		buffer.putInt(y);
		buffer.putInt(width);
		buffer.putInt(height);
		buffer.putInt(flags);
		buffer.putString(text);
	}

	public void eraseRect(final int x, final int y, final int width, final int height) {
		buffer.putInt(PainterFunction.eraseRect.ordinal());
		buffer.putInt(x);
		buffer.putInt(y);
		buffer.putInt(width);
		buffer.putInt(height);
	}

	public void fillRect(final int x, final int y, final int width, final int height, final Color color) {
		buffer.putInt(PainterFunction.fillRectInteger.ordinal());
		buffer.putInt(x);
		buffer.putInt(y);
		buffer.putInt(width);
		buffer.putInt(height);
		buffer.putInt(color.getRGB());
	}

	public void resetTransform() {
		buffer.putInt(PainterFunction.resetTransform.ordinal());
	}

	public void restore() {
		buffer.putInt(PainterFunction.restore.ordinal());
	}

	public void rotate(final double angle) {
		buffer.putInt(PainterFunction.rotate.ordinal());
		buffer.putDouble(angle);
	}

	public void save() {
		buffer.putInt(PainterFunction.save.ordinal());
	}

	public void scale(final double sx, final double sy) {
		buffer.putInt(PainterFunction.scale.ordinal());
		buffer.putDouble(sx);
		buffer.putDouble(sy);
	}

	public void setClipRect(final int x, final int y, final int width, final int height,
			final ClipOperation operation) {
		buffer.putInt(PainterFunction.setClipRectInteger.ordinal());
		buffer.putInt(x);
		buffer.putInt(y);
		buffer.putInt(width);
		buffer.putInt(height);
		buffer.putInt(operation.ordinal());
	}

	public void setClipping(final boolean enable) {
		buffer.putInt(PainterFunction.setClipping.ordinal());
		buffer.putBoolean(enable);
	}

	public void setCompositionMode(final CompositionMode mode) {
		buffer.putInt(PainterFunction.setCompositionMode.ordinal());
		buffer.putInt(mode.ordinal());
	}

	public void setFont(final JFont font) {
		buffer.putInt(PainterFunction.setFont.ordinal());
		buffer.putInt(font.getFontIndex());
	}

	public void setOpacity(final double opacity) {
		buffer.putInt(PainterFunction.setOpacity.ordinal());
		buffer.putDouble(opacity);
	}

	public void setPen(final JPen pen) {
		buffer.putInt(PainterFunction.setPen.ordinal());
		pen.serialize(buffer);
	}

	public void setPen(final Color color) {
		buffer.putInt(PainterFunction.setPenColor.ordinal());
		buffer.putInt(color.getRGB());
	}

	public void setRenderHint(final RenderHint hint, final boolean on) {
		buffer.putInt(PainterFunction.setRenderHint.ordinal());
		buffer.putInt(hint.getMask());
		buffer.putBoolean(on);
	}

	public void shear(final double sh, final double sv) {
		buffer.putInt(PainterFunction.shear.ordinal());
		buffer.putDouble(sh);
		buffer.putDouble(sv);
	}

	public void translate(final double dx, final double dy) {
		buffer.putInt(PainterFunction.translate.ordinal());
		buffer.putDouble(dx);
		buffer.putDouble(dy);
	}

	public PainterInstructions build() {
		return new PainterInstructions(buffer.toArray());
	}
}
