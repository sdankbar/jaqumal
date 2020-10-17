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

#include <jni.h>
#include <QObject>
#include <QQuickPaintedItem>

class StringPosition : public QObject {
public:
    StringPosition();
    StringPosition(const StringPosition& arg);
    StringPosition(const QString& str, int32_t x, int32_t y);

    const QString& getString() const;
    int32_t getX() const;
    int32_t getY() const;

    StringPosition& operator=(const StringPosition& rhs);

private:
    QString str;
    int32_t x;
    int32_t y;
};

class NewType : public QQuickPaintedItem  {
    Q_OBJECT
    Q_PROPERTY(QVariant data READ data WRITE setData NOTIFY dataChanged)
public:
    NewType(QQuickItem* parent = nullptr);

    virtual void paint(QPainter *painter);

    QVariant data() const;
    void setData(const QVariant& newData);

signals:
    void dataChanged();

private:

    QSharedPointer<StringPosition> m_data;
};




