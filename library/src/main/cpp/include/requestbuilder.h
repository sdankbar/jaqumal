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
#include <QMouseEvent>
#include <jni.h>

class RequestBuilder : public QObject
{
    Q_OBJECT
public:

    explicit RequestBuilder(QObject *parent = nullptr);
    virtual ~RequestBuilder();

    Q_INVOKABLE void addBoolean(bool data);
    Q_INVOKABLE void addInteger(qint32 data);
    Q_INVOKABLE void addLong(qint64 data);
    Q_INVOKABLE void addFloat(float data);
    Q_INVOKABLE void addDouble(double data);
    Q_INVOKABLE void addString(const QString& data);
    Q_INVOKABLE void addColor(const QColor& data);
    Q_INVOKABLE void addRect(const QRect& data);
    Q_INVOKABLE void addSize(const QSize& data);
    Q_INVOKABLE void addDate(const QDateTime& data);
    Q_INVOKABLE void addPoint(const QPoint& data);

protected:

    std::vector<char> m_queuedArguements;
};
