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
    case drawArcInteger: {
        const int32_t x = getInteger(ptr);
        const int32_t y = getInteger(ptr);
        const int32_t w = getInteger(ptr);
        const int32_t h = getInteger(ptr);
        const int32_t a = getInteger(ptr);
        const int32_t alen = getInteger(ptr);
        p.drawArc(x, y, w, h, a, alen);
        break;
    }
    case drawChordInteger: {
        const int32_t x = getInteger(ptr);
        const int32_t y = getInteger(ptr);
        const int32_t w = getInteger(ptr);
        const int32_t h = getInteger(ptr);
        const int32_t a = getInteger(ptr);
        const int32_t alen = getInteger(ptr);
        p.drawChord(x, y, w, h, a, alen);
        break;
    }
    case drawConvexPolygonInteger: {
        const int32_t length = getInteger(ptr);
        QPoint* points = new QPoint[length];
        for (int32_t i = 0; i < length; ++i)
        {
            const int32_t x = getInteger(ptr);
            const int32_t y = getInteger(ptr);
            points[i] = QPoint(x, y);
        }
        p.drawConvexPolygon(points, length);
        delete[] points;
        break;
    }
    case drawEllipseInteger: {
        const int32_t x = getInteger(ptr);
        const int32_t y = getInteger(ptr);
        const int32_t w = getInteger(ptr);
        const int32_t h = getInteger(ptr);
        p.drawEllipse(x, y, w, h);
        break;
    }
    case drawImageInteger: {
        const int32_t xTarget = getInteger(ptr);
        const int32_t yTarget = getInteger(ptr);
        const int32_t wTarget = getInteger(ptr);
        const int32_t hTarget = getInteger(ptr);
        const QRect target(xTarget, yTarget, wTarget, hTarget);
        const int32_t xSource = getInteger(ptr);
        const int32_t ySource = getInteger(ptr);
        const int32_t wSource = getInteger(ptr);
        const int32_t hSource = getInteger(ptr);
        const QRect source(xSource, ySource, wSource, hSource);
        const int32_t w = getInteger(ptr);
        const int32_t h = getInteger(ptr);
        const int32_t copyLength = 4 * w * h;
        auto iter = m_cachedImages.find(ptr);
        if (iter != m_cachedImages.end())
        {
            ptr += copyLength;
            p.drawImage(target, iter->second, source);
        }
        else
        {
            unsigned char* copy = new unsigned char[copyLength];
            memcpy(copy, ptr, copyLength);
            const QImage image(copy, w, h, QImage::Format_ARGB32, &cleanupMemory);
            m_cachedImages[ptr] = image;
            ptr += copyLength;
            p.drawImage(target, image, source);
        }
        break;
    }
    case drawLineInteger: {
        const int32_t x1 = getInteger(ptr);
        const int32_t y1 = getInteger(ptr);
        const int32_t x2 = getInteger(ptr);
        const int32_t y2 = getInteger(ptr);
        p.drawLine(x1, y1, x2, y2);
        break;
    }
    case drawLinesInteger: {
        const int32_t length = getInteger(ptr);
        QPoint* points = new QPoint[length];
        for (int32_t i = 0; i < length; ++i)
        {
            const int32_t x = getInteger(ptr);
            const int32_t y = getInteger(ptr);
            points[i] = QPoint(x, y);
        }
        p.drawLines(points, length);
        delete[] points;
        break;
    }
    case drawPieInteger: {
        const int32_t x = getInteger(ptr);
        const int32_t y = getInteger(ptr);
        const int32_t w = getInteger(ptr);
        const int32_t h = getInteger(ptr);
        const int32_t a = getInteger(ptr);
        const int32_t alen = getInteger(ptr);
        p.drawPie(x, y, w, h, a, alen);
        break;
    }
    case drawPointInteger: {
        const int32_t x = getInteger(ptr);
        const int32_t y = getInteger(ptr);
        p.drawPoint(x, y);
        break;
    }
    case drawPointsInteger: {
        const int32_t length = getInteger(ptr);
        QPoint* points = new QPoint[length];
        for (int32_t i = 0; i < length; ++i)
        {
            const int32_t x = getInteger(ptr);
            const int32_t y = getInteger(ptr);
            points[i] = QPoint(x, y);
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
            const int32_t x = getInteger(ptr);
            const int32_t y = getInteger(ptr);
            points[i] = QPoint(x, y);
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
            const int32_t x = getInteger(ptr);
            const int32_t y = getInteger(ptr);
            poly << QPoint(x, y);
        }
        p.drawPolyline(poly);
        break;
    }
    case drawRectInteger: {
        const int32_t x = getInteger(ptr);
        const int32_t y = getInteger(ptr);
        const int32_t w = getInteger(ptr);
        const int32_t h = getInteger(ptr);
        p.drawRect(x, y, w, h);
        break;
    }
    case drawRoundedRectInteger: {
        const int32_t x = getInteger(ptr);
        const int32_t y = getInteger(ptr);
        const int32_t w = getInteger(ptr);
        const int32_t h = getInteger(ptr);
        const double xRadius = getDouble(ptr);
        const double yRadius = getDouble(ptr);
        p.drawRoundedRect(x, y, w, h, xRadius, yRadius);
        break;
    }
    case drawStaticText: {
        const int32_t x = getInteger(ptr);
        const int32_t y = getInteger(ptr);
        const auto iter = m_cachedStaticText.find(ptr);
        if (iter != m_cachedStaticText.end())
        {
            const int32_t length = getInteger(ptr);
            ptr += length;
            p.drawStaticText(x, y, iter->second);
        }
        else
        {
            unsigned char* tempPtr = ptr;
            QStaticText temp(getString(ptr));
            p.drawStaticText(x, y, temp);
            m_cachedStaticText[tempPtr] = temp;
        }
        break;
    }
    case drawTextSimple: {
        int32_t x = getInteger(ptr);
        int32_t y = getInteger(ptr);
        QString text = getString(ptr);
        p.drawText(x, y, text);
        break;
    }
    case drawTextComplex: {
        const int32_t x = getInteger(ptr);
        const int32_t y = getInteger(ptr);
        const int32_t w = getInteger(ptr);
        const int32_t h = getInteger(ptr);
        const int32_t flags = getInteger(ptr);
        QString text = getString(ptr);
        p.drawText(x, y, w, h, flags, text);
        break;
    }
    case eraseRect: {
        const int32_t x = getInteger(ptr);
        const int32_t y = getInteger(ptr);
        const int32_t w = getInteger(ptr);
        const int32_t h = getInteger(ptr);
        p.eraseRect(x, y, w, h);
        break;
    }
    case fillRectInteger: {
        const int32_t x = getInteger(ptr);
        const int32_t y = getInteger(ptr);
        const int32_t w = getInteger(ptr);
        const int32_t h = getInteger(ptr);
        const QColor c = QColor::fromRgba(getInteger(ptr));
        p.fillRect(x, y, w, h, c);
        break;
    }
    case resetTransform:
        p.resetTransform();
        break;
    case restore:
        p.restore();
        break;
    case rotate: {
        const double r = getDouble(ptr);
        p.rotate(r);
        break;
    }
    case save:
        p.save();
        break;
    case scale: {
        double sx = getDouble(ptr);
        double sy = getDouble(ptr);
        p.scale(sx, sy);
        break;
    }
    case setClipRectInteger: {
        const int32_t x = getInteger(ptr);
        const int32_t y = getInteger(ptr);
        const int32_t w = getInteger(ptr);
        const int32_t h = getInteger(ptr);
        Qt::ClipOperation op = static_cast<Qt::ClipOperation>(getInteger(ptr));
        p.setClipRect(x, y, w, h, op);
        break;
    }
    case setClipping: {
        const bool b = getByte(ptr);
        p.setClipping(b);
        break;
    }
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
    case setOpacity: {
        const double opacity = getDouble(ptr);
        p.setOpacity(opacity);
        break;
    }
    case setPen: {
        const QColor c = QColor::fromRgba(getInteger(ptr));
        const double w = getDouble(ptr);
        const Qt::PenStyle ps = static_cast<Qt::PenStyle>(getInteger(ptr));
        const Qt::PenCapStyle pcs = static_cast<Qt::PenCapStyle>(getInteger(ptr));
        const Qt::PenJoinStyle pjs = static_cast<Qt::PenJoinStyle>(getInteger(ptr));
        const QPen pen(QBrush(c),
                       w,
                       ps,
                       pcs,
                       pjs);
        p.setPen(pen);
        break;
    }
    case setPenColor: {
        const QColor c = QColor::fromRgba(getInteger(ptr));
        p.setPen(c);
        break;
    }
    case setRenderHint: {
        const QPainter::RenderHint hint =
            static_cast<QPainter::RenderHint>(getInteger(ptr));
        p.setRenderHint(hint, getByte(ptr));
        break;
    }
    case shear: {
        double sx = getDouble(ptr);
        double sy = getDouble(ptr);
        p.shear(sx, sy);
        break;
    }
    case translate: {
        double tx = getDouble(ptr);
        double ty = getDouble(ptr);
         p.translate(tx, ty);
        break;
    }
    case drawArcDouble: {
        const double x = getDouble(ptr);
        const double y = getDouble(ptr);
        const double w = getDouble(ptr);
        const double h = getDouble(ptr);
        const int32_t a = getInteger(ptr);
        const int32_t alen = getInteger(ptr);
        p.drawArc(QRectF(x, y, w, h), a, alen);
        break;
    }
    case drawChordDouble: {
        const double x = getDouble(ptr);
        const double y = getDouble(ptr);
        const double w = getDouble(ptr);
        const double h = getDouble(ptr);
        const int32_t a = getInteger(ptr);
        const int32_t alen = getInteger(ptr);
        p.drawChord(QRectF(x, y, w, h), a, alen);
        break;
    }
    case drawConvexPolygonDouble: {
        const int32_t length = getInteger(ptr);
        QPointF* points = new QPointF[length];
        for (int32_t i = 0; i < length; ++i)
        {
            const double x = getDouble(ptr);
            const double y = getDouble(ptr);
            points[i] = QPointF(x, y);
        }
        p.drawConvexPolygon(points, length);
        delete[] points;
        break;
    }
    case drawEllipseDouble: {
        const double x = getDouble(ptr);
        const double y = getDouble(ptr);
        const double w = getDouble(ptr);
        const double h = getDouble(ptr);
        p.drawEllipse(QRectF(x, y, w, h));
        break;
    }
    case drawImageDouble: {
        const double xTarget = getDouble(ptr);
        const double yTarget = getDouble(ptr);
        const double wTarget = getDouble(ptr);
        const double hTarget = getDouble(ptr);
        const QRectF target(xTarget, yTarget, wTarget, hTarget);
        const double xSource = getDouble(ptr);
        const double ySource = getDouble(ptr);
        const double wSource = getDouble(ptr);
        const double hSource = getDouble(ptr);
        const QRectF source(xSource, ySource, wSource, hSource);
        const int32_t w = getInteger(ptr);
        const int32_t h = getInteger(ptr);
        const int32_t copyLength = 4 * w * h;
        auto iter = m_cachedImages.find(ptr);
        if (iter != m_cachedImages.end())
        {
            ptr += copyLength;
            p.drawImage(target, iter->second, source);
        }
        else
        {
            unsigned char* copy = new unsigned char[copyLength];
            memcpy(copy, ptr, copyLength);
            const QImage image(copy, w, h, QImage::Format_ARGB32, &cleanupMemory);
            m_cachedImages[ptr] = image;
            ptr += copyLength;
            p.drawImage(target, image, source);
        }
        break;
    }
    case drawLineDouble: {
        const double x1 = getDouble(ptr);
        const double y1 = getDouble(ptr);
        const double x2 = getDouble(ptr);
        const int32_t y2 = getDouble(ptr);
        p.drawLine(QLineF(x1, y1, x2, y2));
        break;
    }
    case drawLinesDouble: {
        const int32_t length = getInteger(ptr);
        QPointF* points = new QPointF[length];
        for (int32_t i = 0; i < length; ++i)
        {
            const double x = getDouble(ptr);
            const double y = getDouble(ptr);
            points[i] = QPointF(x, y);
        }
        p.drawLines(points, length);
        delete[] points;
        break;
    }
    case drawPieDouble: {
        const double x = getDouble(ptr);
        const double y = getDouble(ptr);
        const double w = getDouble(ptr);
        const double h = getDouble(ptr);
        const int32_t a = getInteger(ptr);
        const int32_t alen = getInteger(ptr);
        p.drawPie(QRectF(x, y, w, h), a, alen);
        break;
    }
    case drawPointDouble: {
        const double x = getDouble(ptr);
        const double y = getDouble(ptr);
        p.drawPoint(QPointF(x, y));
        break;
    }
    case drawPointsDouble: {
        const int32_t length = getInteger(ptr);
        QPointF* points = new QPointF[length];
        for (int32_t i = 0; i < length; ++i)
        {
            const double x = getDouble(ptr);
            const double y = getDouble(ptr);
            points[i] = QPointF(x, y);
        }
        p.drawPoints(points, length);
        delete[] points;
        break;
    }
    case drawPolygonDouble: {
        const Qt::FillRule rule = static_cast<Qt::FillRule>(getInteger(ptr));
        const int32_t length = getInteger(ptr);
        QPointF* points = new QPointF[length];
        for (int32_t i = 0; i < length; ++i)
        {
            const double x = getDouble(ptr);
            const double y = getDouble(ptr);
            points[i] = QPointF(x, y);
        }
        p.drawPolygon(points, length, rule);
        delete[] points;
        break;
    }
    case drawPolylineDouble: {
        const int32_t length = getInteger(ptr);
        QPolygonF poly;
        for (int32_t i = 0; i < length; ++i)
        {
            const double x = getDouble(ptr);
            const double y = getDouble(ptr);
            poly << QPointF(x, y);
        }
        p.drawPolyline(poly);
        break;
    }
    case drawRectDouble: {
        const double x = getDouble(ptr);
        const double y = getDouble(ptr);
        const double w = getDouble(ptr);
        const double h = getDouble(ptr);
        p.drawRect(x, y, w, h);
        break;
    }
    case drawRoundedRectDouble: {
        const double x = getDouble(ptr);
        const double y = getDouble(ptr);
        const double w = getDouble(ptr);
        const double h = getDouble(ptr);
        const double xRadius = getDouble(ptr);
        const double yRadius = getDouble(ptr);
        p.drawRoundedRect(QRectF(x, y, w, h), xRadius, yRadius);
        break;
    }
    case drawStaticTextDouble: {
        const double x = getDouble(ptr);
        const double y = getDouble(ptr);
        const auto iter = m_cachedStaticText.find(ptr);
        if (iter != m_cachedStaticText.end())
        {
            const int32_t length = getInteger(ptr);
            ptr += length;
            p.drawStaticText(QPointF(x, y), iter->second);
        }
        else
        {
            unsigned char* tempPtr = ptr;
            QStaticText temp(getString(ptr));
            p.drawStaticText(QPointF(x, y), temp);
            m_cachedStaticText[tempPtr] = temp;
        }
        break;
    }
    case drawTextSimpleDouble: {
        double x = getDouble(ptr);
        double y = getDouble(ptr);
        QString text = getString(ptr);
        p.drawText(QPointF(x, y), text);
        break;
    }
    case drawTextComplexDouble: {
        const double x = getDouble(ptr);
        const double y = getDouble(ptr);
        const double w = getDouble(ptr);
        const double h = getDouble(ptr);
        const int32_t flags = getInteger(ptr);
        QString text = getString(ptr);
        p.drawText(QRectF(x, y, w, h), flags, text);
        break;
    }
    case fillRectDouble: {
        const double x = getDouble(ptr);
        const double y = getDouble(ptr);
        const double w = getDouble(ptr);
        const double h = getDouble(ptr);
        const QColor c = QColor::fromRgba(getInteger(ptr));
        p.fillRect(QRectF(x, y, w, h), c);
        break;
    }
    case setClipRectDouble: {
        const double x = getDouble(ptr);
        const double y = getDouble(ptr);
        const double w = getDouble(ptr);
        const double h = getDouble(ptr);
        Qt::ClipOperation op = static_cast<Qt::ClipOperation>(getInteger(ptr));
        p.setClipRect(QRectF(x, y, w, h), op);
        break;
    }
    case none:
        // Do nothing
        break;
    }
}

PainterInstructions::PainterFunctions PainterInstructions::getNextFunction(unsigned char*& ptr) const
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
unsigned char PainterInstructions::getByte(unsigned char*& ptr) const
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
int32_t PainterInstructions::getInteger(unsigned char*& ptr) const
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
double PainterInstructions::getDouble(unsigned char*& ptr) const
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

QString PainterInstructions::getString(unsigned char*& ptr) const
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
