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
#include "dashedrectangle.h"
#include <QPainter>

DashedRectangle::DashedRectangle(QQuickItem* parent) :
    QQuickPaintedItem(parent),
    m_stroke(QColor(), 1, Qt::SolidLine, Qt::SquareCap)
{
   // Empty Implementation
}

DashedRectangle::~DashedRectangle()
{
    // Empty Implementation
}

void DashedRectangle::paint(QPainter* painter)
{
    painter->setPen(m_stroke);
    painter->drawRect(x(), y(), width(), height());
}

QColor DashedRectangle::strokeColor() const
{
    return m_stroke.color();
}

void DashedRectangle::setStrokeColor(const QColor& strokeColor)
{
    if (m_stroke.color() != strokeColor)
    {
        m_stroke.setColor(strokeColor);
        emit strokeColorChanged(strokeColor);
        update();
    }
}

qint32 DashedRectangle::strokeWidth() const
{
    return m_stroke.width();
}

void DashedRectangle::setStrokeWidth(qint32 strokeWidth)
{
    if (m_stroke.width() != strokeWidth)
    {
        m_stroke.setWidth(strokeWidth);
        emit strokeWidthChanged(strokeWidth);
        update();
    }
}

QVariantList DashedRectangle::dashPattern() const
{
    QVariantList list;
    for (qreal p: m_stroke.dashPattern())
    {
        list.append(p);
    }
    return list;
}

void DashedRectangle::setDashPattern(const QVariantList& pattern)
{
    QVector<qreal> patternVec;
    for (QVariant v: pattern) {
        patternVec.append(v.toDouble());
    }
    if (m_stroke.dashPattern() != patternVec)
    {
        m_stroke.setDashPattern(patternVec);
        emit dashPatternChanged(pattern);
        update();
    }
}

qreal DashedRectangle::dashOffset() const
{
    return m_stroke.dashOffset();
}

void DashedRectangle::setDashOffset(qreal offset)
{
    if (m_stroke.dashOffset() != offset)
    {
        m_stroke.setDashOffset(offset);
        emit dashOffsetChanged(offset);
        update();
    }
}
