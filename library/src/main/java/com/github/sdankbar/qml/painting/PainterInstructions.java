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

import java.util.Arrays;
import java.util.Objects;

public class PainterInstructions {
	public enum FillMode {
		OddEvenFill, WindingFill
	}

	public enum RenderHint {
		Antialiasing(0x01), TextAntialiasing(0x02), SmoothPixmapTransform(0x04), HighQualityAntialiasing(0x08),
		NonCosmeticDefaultPen(0x10), Qt4CompatiblePainting(0x20), LosslessImageRendering(0x40);

		private final int mask;

		private RenderHint(final int mask) {
			this.mask = mask;
		}

		public int getMask() {
			return mask;
		}
	}

	public enum ClipOperation {
		NoClip, ReplaceClip, IntersectClip;
	}

	public enum CompositionMode {
		CompositionMode_SourceOver, CompositionMode_DestinationOver, CompositionMode_Clear, CompositionMode_Source,
		CompositionMode_Destination, CompositionMode_SourceIn, CompositionMode_DestinationIn, CompositionMode_SourceOut,
		CompositionMode_DestinationOut, CompositionMode_SourceAtop, CompositionMode_DestinationAtop, //
		CompositionMode_Xor, CompositionMode_Plus, CompositionMode_Multiply, CompositionMode_Screen,
		CompositionMode_Overlay, CompositionMode_Darken, CompositionMode_Lighten, CompositionMode_ColorDodge,
		CompositionMode_ColorBurn, CompositionMode_HardLight, CompositionMode_SoftLight, CompositionMode_Difference,
		CompositionMode_Exclusion, RasterOp_SourceOrDestination, RasterOp_SourceAndDestination,
		RasterOp_SourceXorDestination, RasterOp_NotSourceAndNotDestination, RasterOp_NotSourceOrNotDestination,
		RasterOp_NotSourceXorDestination, RasterOp_NotSource, RasterOp_NotSourceAndDestination,
		RasterOp_SourceAndNotDestination, RasterOp_NotSourceOrDestination, RasterOp_SourceOrNotDestination,
		RasterOp_ClearDestination, RasterOp_SetDestination, RasterOp_NotDestination;

	}

	public enum PainterFunction {
		drawArcInteger, drawChordInteger, drawConvexPolygonInteger, drawEllipseInteger, drawImageInteger,
		drawLineInteger, drawLinesInteger, drawPieInteger, drawPointInteger, drawPointsInteger, drawPolygonInteger,
		drawPolylineInteger, drawRectInteger, drawRoundedRectInteger, drawStaticText, drawTextSimple, drawTextComplex,
		eraseRect, fillRectInteger, resetTransform, restore, rotate, save, scale, setClipRectInteger, setClipping,
		setCompositionMode, setFont, setOpacity, setPen, setPenColor, setRenderHint, shear, translate, drawArcDouble,
		drawChordDouble, drawConvexPolygonDouble, drawEllipseDouble, drawImageDouble, drawLineDouble, drawLinesDouble,
		drawPieDouble, drawPointDouble, drawPointsDouble, drawPolygonDouble, drawPolylineDouble, drawRectDouble,
		drawRoundedRectDouble, drawStaticTextDouble, drawTextSimpleDouble, drawTextComplexDouble, fillRectDouble,
		setClipRectDouble;
	}

	private final byte[] data;
	private int hash = 0;

	public PainterInstructions(final byte[] data) {
		this.data = Objects.requireNonNull(data, "data is null");
	}

	public byte[] getArray() {
		return data;
	}

	@Override
	public int hashCode() {
		int h = hash;
		if (h == 0) {
			h = Arrays.hashCode(data);
			hash = h;
		}
		return h;
	}

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
		final PainterInstructions other = (PainterInstructions) obj;
		return Arrays.equals(data, other.data);
	}

}
