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
    m_instructions(),
    m_end(nullptr)
{
  // Empty Implementation
}

PainterInstructions::PainterInstructions(unsigned int length, unsigned char* instructions) :
    m_length(length),
    m_instructions(instructions),
    m_end(instructions + length)
{
    // Empty Implementation
}

void PainterInstructions::paint(QPainter& p)
{
    unsigned char* ptr = m_instructions.get();
    PainterFunctions nextFunc = getNextFunction(ptr);
    while (nextFunc != none)
    {
        paint(p, nextFunc, ptr);
        nextFunc = getNextFunction(ptr);
    }
}

void PainterInstructions::paint(QPainter& p, PainterFunctions func, unsigned char*& ptr)
{
    switch (func) {
    case drawArcInteger:
        p.drawArc(getInteger(ptr),
                  getInteger(ptr),
                  getInteger(ptr),
                  getInteger(ptr),
                  getInteger(ptr),
                  getInteger(ptr));
        break;
    case drawChordInteger:
        p.drawChord(getInteger(ptr),
                    getInteger(ptr),
                    getInteger(ptr),
                    getInteger(ptr),
                    getInteger(ptr),
                    getInteger(ptr));
        break;
    case drawConvexPolygonInteger: {
        const int32_t length = getInteger(ptr);
        QPoint* points = new QPoint[length];
        for (int32_t i = 0; i < length; ++i)
        {
            points[i] = QPoint(getInteger(ptr), getInteger(ptr));
        }
        p.drawConvexPolygon(points, length);
        delete[] points;
        break;
    }
    case drawEllipseInteger:
        p.drawEllipse(getInteger(ptr),
                      getInteger(ptr),
                      getInteger(ptr),
                      getInteger(ptr));
        break;
    case drawImageInteger: {
        const QRect target(getInteger(ptr),
                     getInteger(ptr),
                     getInteger(ptr),
                     getInteger(ptr));
        const QRect source(getInteger(ptr),
                     getInteger(ptr),
                     getInteger(ptr),
                     getInteger(ptr));
        const int32_t w = getInteger(ptr);
        const int32_t h = getInteger(ptr);
        const int32_t copyLength = 4 * w * h;
        auto iter = m_cachedImages.find(ptr);
        if (iter == m_cachedImages.end())
        {

            unsigned char* copy = new unsigned char[copyLength];
            memcpy(copy, ptr, copyLength);
            QImage image(copy, w, h, QImage::Format_ARGB32, &cleanupMemory);
            m_cachedImages[ptr] = image;
            ptr += copyLength;
            p.drawImage(target, image, source);
        }
        else
        {
            ptr += copyLength;
            p.drawImage(target, iter->second, source);
        }
        break;
    }
    case drawLineInteger:
        p.drawLine(getInteger(ptr),
                   getInteger(ptr),
                   getInteger(ptr),
                   getInteger(ptr));
        break;
    case drawLinesInteger: {
        const int32_t length = getInteger(ptr);
        QPoint* points = new QPoint[length];
        for (int32_t i = 0; i < length; ++i)
        {
            points[i] = QPoint(getInteger(ptr), getInteger(ptr));
        }
        p.drawLines(points, length);
        delete[] points;
        break;
    }
    case drawPieInteger:
        p.drawPie(getInteger(ptr),
                  getInteger(ptr),
                  getInteger(ptr),
                  getInteger(ptr),
                  getInteger(ptr),
                  getInteger(ptr));
        break;
    case drawPointInteger:
        p.drawPoint(getInteger(ptr),
                    getInteger(ptr));
        break;
    case drawPointsInteger: {
        const int32_t length = getInteger(ptr);
        QPoint* points = new QPoint[length];
        for (int32_t i = 0; i < length; ++i)
        {
            points[i] = QPoint(getInteger(ptr), getInteger(ptr));
        }
        p.drawPoints(points, length);
        delete[] points;
        break;
    }
    case drawPolygonInteger: {
        const Qt::FillRule rule = static_cast<Qt::FillRule>(getInteger(ptr));
        const int32_t length = getInteger(ptr);
        QPoint* points = new QPoint[length];
        for (int32_t i = 0; i < length; ++i)
        {
            points[i] = QPoint(getInteger(ptr), getInteger(ptr));
        }
        p.drawPolygon(points, length, rule);
        delete[] points;
        break;
    }
    case drawPolylineInteger: {
        const int32_t length = getInteger(ptr);
        QPolygon poly;
        for (int32_t i = 0; i < length; ++i)
        {
            poly << QPoint(getInteger(ptr), getInteger(ptr));
        }
        p.drawPolyline(poly);
        break;
    }
    case drawRectInteger:
        p.drawRect(getInteger(ptr),
                   getInteger(ptr),
                   getInteger(ptr),
                   getInteger(ptr));
        break;
    case drawRoundedRectInteger:
        p.drawRoundedRect(getInteger(ptr),
                          getInteger(ptr),
                          getInteger(ptr),
                          getInteger(ptr),
                          getInteger(ptr),
                          getInteger(ptr));
        break;
    case drawStaticText: {
        const int32_t x = getInteger(ptr);
        const int32_t y = getInteger(ptr);
        const auto iter = m_cachedStaticText.find(ptr);
        if (iter != m_cachedStaticText.end())
        {
            ptr += getInteger(ptr);
            p.drawStaticText(x,
                             y,
                             iter->second);
        }
        else
        {
            p.drawStaticText(x,
                             y,
                             QStaticText(getString(ptr)));
        }
        break;
    }
    case drawTextSimple:
        p.drawText(getInteger(ptr),
                   getInteger(ptr),
                   getString(ptr));
        break;
    case drawTextComplex:
        p.drawText(getInteger(ptr),
                   getInteger(ptr),
                   getInteger(ptr),
                   getInteger(ptr),
                   getInteger(ptr),
                   getString(ptr));
        break;
    case eraseRect:
        p.eraseRect(getInteger(ptr),
                    getInteger(ptr),
                    getInteger(ptr),
                    getInteger(ptr));
        break;
    case fillRectInteger:
        p.fillRect(getInteger(ptr),
                   getInteger(ptr),
                   getInteger(ptr),
                   getInteger(ptr),
                   QColor::fromRgba(getInteger(ptr)));
        break;
    case resetTransform:
        p.resetTransform();
        break;
    case restore:
        p.restore();
        break;
    case rotate:
        p.rotate(getDouble(ptr));
        break;
    case save:
        p.save();
        break;
    case scale:
        p.scale(getDouble(ptr),
                getDouble(ptr));
        break;
    case setClipRectInteger:
        p.setClipRect(getInteger(ptr),
                      getInteger(ptr),
                      getInteger(ptr),
                      getInteger(ptr));
        break;
    case setClipping:
        p.setClipping(getByte(ptr));
        break;
    case setCompositionMode: {
        const QPainter::CompositionMode mode =
            static_cast<QPainter::CompositionMode>(getInteger(ptr));
        p.setCompositionMode(mode);
        break;
    }
    case setFont: {
        const int32_t fontptr = getInteger(ptr);
        p.setFont(JNIUtilities::getFont(fontptr));
        break;
    }
    case setOpacity:
        p.setOpacity(getDouble(ptr));
        break;
    case setPen: {
        const QPen pen(QBrush(QColor::fromRgba(getInteger(ptr))),
                 getDouble(ptr),
                 static_cast<Qt::PenStyle>(getInteger(ptr)),
                 static_cast<Qt::PenCapStyle>(getInteger(ptr)),
                 static_cast<Qt::PenJoinStyle>(getInteger(ptr)));
        p.setPen(pen);
        break;
    }
    case setPenColor:
        p.setPen(QColor::fromRgba(getInteger(ptr)));
        break;
    case setRenderHint: {
        const QPainter::RenderHint hint =
            static_cast<QPainter::RenderHint>(getInteger(ptr));
        p.setRenderHint(hint, getByte(ptr));
        break;
    }
    case shear:
        p.shear(getDouble(ptr),
                getDouble(ptr));
        break;
    case translate:
         p.translate(getDouble(ptr),
                     getDouble(ptr));
        break;
    case none:
        // Do nothing
        break;
    }
}

PainterInstructions::PainterFunctions PainterInstructions::getNextFunction(unsigned char*& ptr)
{
    unsigned char* next = ptr + sizeof(jint);
    if (next <= m_end) {
        PainterFunctions func;
        memcpy(&func, ptr, sizeof(PainterFunctions));
        ptr = next;
        return func;
    } else {
        return none;
    }
}
unsigned char PainterInstructions::getByte(unsigned char*& ptr)
{
    unsigned char* next = ptr + sizeof(jbyte);
    if (next <= m_end) {
        unsigned char v = *ptr;
        ptr = next;
        return v;
    } else {
        return 0;
    }
}
int32_t PainterInstructions::getInteger(unsigned char*& ptr)
{
    unsigned char* next = ptr + sizeof(jint);
    if (next <= m_end) {
        int32_t v;
        memcpy(&v, ptr, sizeof(jint));
        ptr = next;
        return v;
    } else {
        return 0;
    }
}
double PainterInstructions::getDouble(unsigned char*& ptr)
{
    unsigned char* next = ptr + sizeof(jdouble);
    if (next <= m_end) {
        double v;
        memcpy(&v, ptr, sizeof(jdouble));
        ptr = next;
        return v;
    } else {
        return 0;
    }
}

QString PainterInstructions::getString(unsigned char*& ptr)
{
    int32_t length = getInteger(ptr);
    if (length > 0) {
        QString str = QString::fromUtf8((char*)ptr, length);
        ptr += length;
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
