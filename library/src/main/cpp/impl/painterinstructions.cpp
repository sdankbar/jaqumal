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
#include "jniutilities.h"
#include <QPainter>
#include <QStaticText>

namespace
{
void cleanupMemory(void* ptr)
{
    delete static_cast<unsigned char*>(ptr);
}
}

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
    case drawConvexPolygonInteger: {
        int32_t length = getInteger(index);
        QPoint* ptr = new QPoint[length];
        for (int32_t i = 0; i < length; ++i)
        {
            ptr[i] = QPoint(getInteger(index), getInteger(index));
        }
        p.drawConvexPolygon(ptr, length);
        delete [] ptr;
        break;
    }
    case drawEllipseInteger:
        p.drawEllipse(getInteger(index),
                      getInteger(index),
                      getInteger(index),
                      getInteger(index));
        break;
    case drawImageInteger: {
        QRect target(getInteger(index),
                     getInteger(index),
                     getInteger(index),
                     getInteger(index));
        QRect source(getInteger(index),
                     getInteger(index),
                     getInteger(index),
                     getInteger(index));
        int32_t w = getInteger(index);
        int32_t h = getInteger(index);
        const int32_t copyLength = 4 * w * h;
        unsigned char* copy = new unsigned char[copyLength];
        memcpy(copy, m_instructions.get() + index, copyLength);
        QImage image(copy, w, h, QImage::Format_ARGB32, &cleanupMemory);
        p.drawImage(target, image, source);
        break;
    }
    case drawLineInteger:
        p.drawLine(getInteger(index),
                   getInteger(index),
                   getInteger(index),
                   getInteger(index));
        break;
    case drawLinesInteger: {
        int32_t length = getInteger(index);
        QPoint* ptr = new QPoint[length];
        for (int32_t i = 0; i < length; ++i)
        {
            ptr[i] = QPoint(getInteger(index), getInteger(index));
        }
        p.drawLines(ptr, length);
        delete [] ptr;
        break;
    }
    case drawPieInteger:
        p.drawPie(getInteger(index),
                  getInteger(index),
                  getInteger(index),
                  getInteger(index),
                  getInteger(index),
                  getInteger(index));
        break;
    case drawPointInteger:
        p.drawPoint(getInteger(index),
                    getInteger(index));
        break;
    case drawPointsInteger: {
        int32_t length = getInteger(index);
        QPoint* ptr = new QPoint[length];
        for (int32_t i = 0; i < length; ++i)
        {
            ptr[i] = QPoint(getInteger(index), getInteger(index));
        }
        p.drawPoints(ptr, length);
        delete [] ptr;
        break;
    }
    case drawPolygonInteger: {
        Qt::FillRule rule = static_cast<Qt::FillRule>(getInteger(index));
        int32_t length = getInteger(index);
        QPoint* ptr = new QPoint[length];
        for (int32_t i = 0; i < length; ++i)
        {
            ptr[i] = QPoint(getInteger(index), getInteger(index));
        }
        p.drawPolygon(ptr, length, rule);
        delete [] ptr;
        break;
    }
    case drawPolylineInteger: {
        int32_t length = getInteger(index);
        QPolygon poly;
        for (int32_t i = 0; i < length; ++i)
        {
            poly << QPoint(getInteger(index), getInteger(index));
        }
        p.drawPolyline(poly);
        break;
    }
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
        p.drawStaticText(getInteger(index), getInteger(index), QStaticText(getString(index)));
        break;
    case drawTextSimple:
        p.drawText(getInteger(index), getInteger(index), getString(index));
        break;
    case drawTextComplex:
        p.drawText(getInteger(index), getInteger(index), getInteger(index),
                   getInteger(index),getInteger(index), getString(index));
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
                   QColor::fromRgba(getInteger(index)));
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
        p.setClipRect(getInteger(index),
                      getInteger(index),
                      getInteger(index),
                      getInteger(index));
        break;
    case setClipping:
        p.setClipping(getByte(index));
        break;
    case setCompositionMode: {
        QPainter::CompositionMode mode = static_cast<QPainter::CompositionMode>(getInteger(index));
        p.setCompositionMode(mode);
        break;
    }
    case setFont: {
        int32_t fontIndex = getInteger(index);
        p.setFont(JNIUtilities::getFont(fontIndex));
        break;
    }
    case setOpacity:
        p.setOpacity(getDouble(index));
        break;
    case setPen:
        p.setPen(QColor::fromRgba(getInteger(index)));
        break;
    case setRenderHint: {
        QPainter::RenderHint hint = static_cast<QPainter::RenderHint>(getInteger(index));
        p.setRenderHint(hint, getByte(index));
        break;
    }
    case shear:
        p.shear(getDouble(index), getDouble(index));
        break;
    case translate:
         p.translate(getDouble(index), getDouble(index));
        break;
    case none:
        // Do nothing
        break;
    }
}

PainterInstructions::PainterFunctions PainterInstructions::getNextFunction(unsigned int& index)
{
    if (index + sizeof(jint) <= m_length) {
        PainterFunctions func = *(PainterFunctions*)(m_instructions.get() + index);
        index += sizeof(jint);
        return func;
    } else {
        return none;
    }
}
unsigned char PainterInstructions::getByte(unsigned int& index)
{
    if (index + sizeof(jbyte) <= m_length) {
        unsigned char v = *(m_instructions.get() + index);
        index += sizeof(jbyte);
        return v;
    } else {
        return 0;
    }
}
int32_t PainterInstructions::getInteger(unsigned int& index)
{
    if (index + sizeof(jint) <= m_length) {
        int32_t v = *(int32_t*)(m_instructions.get() + index);
        index += sizeof(jint);
        return v;
    } else {
        return 0;
    }
}
double PainterInstructions::getDouble(unsigned int& index)
{
    if (index + sizeof(jdouble) <= m_length) {
        int32_t v = *(double*)(m_instructions.get() + index);
        index += sizeof(jdouble);
        return v;
    } else {
        return 0;
    }
}

QString PainterInstructions::getString(unsigned int& index)
{
    if (index + sizeof(jint) <= m_length) {
        int32_t length = getInteger(index);
        char* ptr = (char*)(m_instructions.get() + index);
        QString str = QString::fromUtf8(ptr, length);
        index += length;
        return str;
    } else {
        return QString();
    }
}

jbyteArray PainterInstructions::cloneIntoJavaArray(JNIEnv* env) const
{
    jbyteArray javaObject = env->NewByteArray(m_length);
    jbyte* mem = env->GetByteArrayElements(javaObject, nullptr);
    memcpy(mem, m_instructions.get(), m_length);
    env->ReleaseByteArrayElements(javaObject, mem, 0);
    return javaObject;
}
