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
#include "painterinstructions.h"
#include <QPainter>

PainterInstructions::PainterInstructions() :
    m_length(0),
    m_instructions()
{
  // Empty Implementation
}

PainterInstructions::PainterInstructions(unsigned int length, unsigned char* instructions) :
    m_length(length),
    m_instructions(instructions)
{
    // Empty Implementation
}

void PainterInstructions::paint(QPainter& p)
{
    unsigned int index = 0;
    PainterFunctions nextFunc = getNextFunction(index);
    while (nextFunc != none)
    {
        paint(p, nextFunc, index);
        nextFunc = getNextFunction(index);
    }
}

void PainterInstructions::paint(QPainter& p, PainterFunctions func, unsigned int& index)
{
    switch (func) {
    case drawArcInteger:
        p.drawArc(getInteger(index),
                  getInteger(index),
                  getInteger(index),
                  getInteger(index),
                  getInteger(index),
                  getInteger(index));
        break;
    case drawChordInteger:
        p.drawChord(getInteger(index),
                  getInteger(index),
                  getInteger(index),
                  getInteger(index),
                  getInteger(index),
                  getInteger(index));
        break;
    case drawConvexPolygonInteger:
        break;
    case drawEllipseInteger:
        p.drawEllipse(getInteger(index),
                      getInteger(index),
                      getInteger(index),
                      getInteger(index));
        break;
    case drawImageInteger:
        break;
    case drawLineInteger:
        p.drawLine(getInteger(index),
                   getInteger(index),
                   getInteger(index),
                   getInteger(index));
        break;
    case drawLinesInteger:
        break;
    case drawPieInteger:
        break;
    case drawPointInteger:
        p.drawPoint(getInteger(index),
                    getInteger(index));
        break;
    case drawPointsInteger:
        break;
    case drawPolygonInteger:
        break;
    case drawPolylineInteger:
        break;
    case drawRectInteger:
        p.drawRect(getInteger(index),
                   getInteger(index),
                   getInteger(index),
                   getInteger(index));
        break;
    case drawRoundedRectInteger:
        p.drawRoundedRect(getInteger(index),
                          getInteger(index),
                          getInteger(index),
                          getInteger(index),
                          getInteger(index),
                          getInteger(index));
        break;
    case drawStaticText:
        break;
    case drawTextSimple:
        break;
    case drawTextComplex:
        break;
    case eraseRect:
        p.eraseRect(getInteger(index),
                    getInteger(index),
                    getInteger(index),
                    getInteger(index));
        break;
    case fillRectInteger:
        p.fillRect(getInteger(index),
                   getInteger(index),
                   getInteger(index),
                   getInteger(index),
                   getInteger(index));
        break;
    case resetTransform:
        p.resetTransform();
        break;
    case restore:
        p.restore();
        break;
    case rotate:
        p.rotate(getDouble(index));
        break;
    case save:
        p.save();
        break;
    case scale:
        p.scale(getDouble(index),
                getDouble(index));
        break;
    case setClipRectInteger:
        break;
    case setClipping:
        break;
    case setCompositionMode:
        break;
    case setFont:
        break;
    case setOpacity:
        p.setOpacity(getDouble(index));
        break;
    case setPen:
        break;
    case setRenderHint:
        break;
    case shear:
        p.shear(getDouble(index), getDouble(index));
        break;
    case translate:
         p.translate(getDouble(index), getDouble(index));
        break;
    case none:
        break;
    }
}

PainterInstructions::PainterFunctions PainterInstructions::getNextFunction(unsigned int& index)
{
    if (index + sizeof(jint) <= m_length) {
        unsigned char* ptr = static_cast<unsigned char*>(m_instructions.get());
        PainterFunctions func = *(PainterFunctions*)(ptr + index);
        index += sizeof(jint);
        return func;
    } else {
        return none;
    }
}
int32_t PainterInstructions::getInteger(unsigned int& index)
{
    if (index + sizeof(jint) <= m_length) {
        unsigned char* ptr = static_cast<unsigned char*>(m_instructions.get());
        int32_t v = *(int32_t*)(ptr + index);
        index += sizeof(jint);
        return v;
    } else {
        return 0;
    }
}
double PainterInstructions::getDouble(unsigned int& index)
{
    if (index + sizeof(jdouble) <= m_length) {
        unsigned char* ptr = static_cast<unsigned char*>(m_instructions.get());
        int32_t v = *(double*)(ptr + index);
        index += sizeof(jdouble);
        return v;
    } else {
        return 0;
    }
}

jbyteArray PainterInstructions::cloneIntoJavaArray(JNIEnv* env) const
{
    jbyteArray javaObject = env->NewByteArray(m_length);
    jbyte* mem = env->GetByteArrayElements(javaObject, nullptr);
    memcpy(mem, m_instructions.get(), m_length);
    env->ReleaseByteArrayElements(javaObject, mem, 0);// Commit and release
    return javaObject;
}
