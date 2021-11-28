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
#pragma once

#include <memory>
#include <jni.h>
#include <unordered_map>

#include <QImage>
#include <QString>
#include <QStaticText>

class QPainter;

class PainterInstructions
{
public:

    PainterInstructions();
    PainterInstructions(unsigned int length, unsigned char* instructions);

    void paint(QPainter& p);

    jbyteArray cloneIntoJavaArray(JNIEnv* env) const;

private:

    enum PainterFunctions {
        drawArcInteger, drawChordInteger, drawConvexPolygonInteger, drawEllipseInteger, drawImageInteger,
        drawLineInteger, drawLinesInteger, drawPieInteger, drawPointInteger, drawPointsInteger, drawPolygonInteger,
        drawPolylineInteger, drawRectInteger, drawRoundedRectInteger, drawStaticText, drawTextSimple, drawTextComplex,
        eraseRect, fillRectInteger, resetTransform, restore, rotate, save, scale, setClipRectInteger, setClipping,
        setCompositionMode, setFont, setOpacity, setPen, setPenColor, setRenderHint, shear, translate,
        drawArcDouble, drawChordDouble, drawConvexPolygonDouble, drawEllipseDouble, drawImageDouble, drawLineDouble,
        drawLinesDouble, drawPieDouble, drawPointDouble, drawPointsDouble, drawPolygonDouble, drawPolylineDouble,
        drawRectDouble, drawRoundedRectDouble, drawStaticTextDouble, drawTextSimpleDouble, drawTextComplexDouble,
        fillRectDouble, setClipRectDouble,
        none = 9999
    };

    inline PainterFunctions getNextFunction(unsigned char*& ptr) const;
    inline unsigned char getByte(unsigned char*& ptr) const;
    inline int32_t getInteger(unsigned char*& ptr) const;
    inline double getDouble(unsigned char*& ptr) const;
    inline QString getString(unsigned char*& ptr) const;

    void paint(QPainter& p, PainterFunctions func, unsigned char*& ptr);

    unsigned int m_length;
    std::shared_ptr<unsigned char> m_instructions;
    unsigned char* m_end;
    std::unordered_map<unsigned char*, QImage> m_cachedImages;
    std::unordered_map<unsigned char*, QStaticText> m_cachedStaticText;
};

