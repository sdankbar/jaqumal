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

#include <QObject>
#include <QtQuick/QQuickPaintedItem>
#include <QPen>

class DashedRectangle : public QQuickPaintedItem
{
    Q_OBJECT

    Q_PROPERTY(const QColor& strokeColor READ strokeColor WRITE setStrokeColor NOTIFY strokeColorChanged)
    Q_PROPERTY(qint32 strokeWidth READ strokeWidth WRITE setStrokeWidth NOTIFY strokeWidthChanged)
    Q_PROPERTY(QVariantList dashPattern READ dashPattern WRITE setDashPattern NOTIFY dashPatternChanged)
    Q_PROPERTY(qreal dashOffset READ dashOffset WRITE setDashOffset NOTIFY dashOffsetChanged)
public:

    DashedRectangle(QQuickItem* parent = nullptr);
    virtual ~DashedRectangle();

    virtual void paint(QPainter *painter) override;

    QColor strokeColor() const;
    void setStrokeColor(const QColor& strokeColor);

    qint32 strokeWidth() const;
    void setStrokeWidth(qint32 strokeWidth);

    QVariantList dashPattern() const;
    void setDashPattern(const QVariantList& pattern);

    qreal dashOffset() const;
    void setDashOffset(qreal offset);

signals:

    void strokeColorChanged(const QColor& strokeColor);
    void strokeWidthChanged(qint32 strokeWidth);
    void dashPatternChanged(const QVariantList& pattern);
    void dashOffsetChanged(qreal offset);

private:

    QPen m_stroke;
};

