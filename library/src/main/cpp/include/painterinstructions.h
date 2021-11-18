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

#include <QString>

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
        setCompositionMode, setFont, setOpacity, setPen, setPenColor, setRenderHint, shear, translate, none = 9999
    };

    PainterFunctions getNextFunction(unsigned char*& ptr);
    unsigned char getByte(unsigned char*& ptr);
    int32_t getInteger(unsigned char*& ptr);
    double getDouble(unsigned char*& ptr);
    QString getString(unsigned char*& ptr);

    void paint(QPainter& p, PainterFunctions func, unsigned char*& ptr);

    unsigned int m_length;
    std::shared_ptr<unsigned char> m_instructions;
    unsigned char* m_end;
};

