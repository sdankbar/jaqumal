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

const qreal FPS = 30;

DashedRectangle::DashedRectangle(QQuickItem* parent) :
    QQuickPaintedItem(parent),
    m_stroke(QColor(), 1, Qt::SolidLine, Qt::SquareCap),
    m_offset(0),
    m_animationPeriod(1000),
    m_animationOffset(0),
    m_animationMaxPixelOffset(10),
    m_direction(-1)
{
   m_stroke.setJoinStyle(Qt::MiterJoin);
   m_animationTimer.setSingleShot(false);
   m_animationTimer.setInterval(m_animationPeriod / FPS);
   QObject::connect(&m_animationTimer, &QTimer::timeout, this, &DashedRectangle::animationTimerTriggered);
}

DashedRectangle::~DashedRectangle()
{
    // Empty Implementation
}

void DashedRectangle::paint(QPainter* painter)
{
    qreal sumOffset = m_offset + (m_direction * m_animationOffset);
    m_stroke.setDashOffset(sumOffset);

    painter->setPen(m_stroke);
    // width = 1, offset = 0
    // width = 2, offset = 1
    // width = 3, offset = 1
    // width = 4, offset = 2
    const qint32 topLeftOffset = m_stroke.width() / 2;
    // width = 1, offset = 1
    // width = 2, offset = 2
    // width = 3, offset = 3
    // width = 4, offset = 4
    const qint32 bottomRightOffset = m_stroke.width();
    painter->drawRect(topLeftOffset, topLeftOffset, width() - bottomRightOffset, height() - bottomRightOffset);
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
    return m_offset;
}

void DashedRectangle::setDashOffset(qreal offset)
{
    if (m_offset != offset)
    {
        m_offset = offset;
        emit dashOffsetChanged(offset);
        update();
    }
}

bool DashedRectangle::isAnimated() const
{
    return m_animationTimer.isActive();
}

void DashedRectangle::setAnimated(bool animated)
{
    if (animated != isAnimated())
    {
        m_animationOffset = 0;
        if (animated)
        {
            m_animationTimer.start();
        }
        else
        {
            m_animationTimer.stop();
        }
        emit animatedChanged(animated);
        update();
    }
}

qint32 DashedRectangle::animationPeriodMilliseconds() const
{
    return m_animationPeriod;
}

void DashedRectangle::setAnimationPeriodMilliseconds(qint32 period)
{
    if (m_animationPeriod != period)
    {
        m_animationPeriod = period;
        m_animationTimer.setInterval(m_animationPeriod / FPS);
        m_animationOffset = 0;
        m_animationTimer.start();
        emit animationPeriodMillisecondsChanged(period);
        update();
    }
}

qint32 DashedRectangle::animationMaxPixelOffset() const
{
    return m_animationMaxPixelOffset;
}

void DashedRectangle::setAnimationMaxPixelOffset(qint32 offset)
{
    if (m_animationMaxPixelOffset != offset)
    {
        m_animationMaxPixelOffset = offset;
        m_animationOffset = 0;
        emit animationMaxPixelOffsetChanged(offset);
        update();
    }
}


bool DashedRectangle::animateClockwise() const
{
    return m_direction < 0;
}
void DashedRectangle::setAnimateClockwise(bool clockwise)
{
    if (animateClockwise() != clockwise)
    {
        if (clockwise)
        {
            m_direction = -1;
        }
        else
        {
            m_direction = 1;
        }
        emit animateClockwiseChanged(clockwise);
        update();
    }
}

void DashedRectangle::animationTimerTriggered()
{
    // offset 20 pixels in 1.5 seconds
    // offset 20 pixels in 1.5 * 30 frames = 45 frames.
    // increment = maxPixelOffset / (animationPeriod * FPS)
    qreal secPeriod = m_animationPeriod / 1000.0;
    qreal increment = m_animationMaxPixelOffset / (FPS * secPeriod);
    m_animationOffset += increment;
    if (m_animationOffset > m_animationMaxPixelOffset)
    {
        m_animationOffset = 0;
    }
    update();
}
