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
#include "requestbuilder.h"
#include <QColor>
#include <QDateTime>

RequestBuilder::RequestBuilder(QObject* parent) :
    QObject(parent)
{
    // Empty Implementation
}
RequestBuilder::~RequestBuilder()
{
    // Empty Implementation
}
void RequestBuilder::addBoolean(bool data)
{
    m_queuedArguements.push_back(data);
}
void RequestBuilder::addInteger(qint32 data)
{
    const uint32_t size = sizeof(data);
    const char* ptr = (const char*)(&data);
    for (uint32_t i = 0; i < size; ++i)
    {
        char byte = ptr[i];
        m_queuedArguements.push_back(byte);
    }
}
void RequestBuilder::addLong(qint64 data)
{
    const uint32_t size = sizeof(data);
    const char* ptr = (const char*)(&data);
    for (uint32_t i = 0; i < size; ++i)
    {
        char byte = ptr[i];
        m_queuedArguements.push_back(byte);
    }
}
void RequestBuilder::addFloat(float data)
{
    const uint32_t size = sizeof(data);
    const char* ptr = (const char*)(&data);
    for (uint32_t i = 0; i < size; ++i)
    {
        char byte = ptr[i];
        m_queuedArguements.push_back(byte);
    }
}
void RequestBuilder::addDouble(double data)
{
    const uint32_t size = sizeof(data);
    const char* ptr = (const char*)(&data);
    for (uint32_t i = 0; i < size; ++i)
    {
        char byte = ptr[i];
        m_queuedArguements.push_back(byte);
    }
}
void RequestBuilder::addString(const QString& data)
{
    std::string str = data.toStdString();
    for (uint32_t i = 0; i < str.length(); ++i)
    {
        m_queuedArguements.push_back(str[i]);
    }
    m_queuedArguements.push_back(0);
}
void RequestBuilder::addColor(const QColor& data)
{
    quint32 argb = data.rgba();
    addInteger(argb);
}
void RequestBuilder::addRect(const QRect& data)
{
    addInteger(data.x());
    addInteger(data.y());
    addInteger(data.width());
    addInteger(data.height());
}
void RequestBuilder::addSize(const QSize& data)
{
    addInteger(data.width());
    addInteger(data.height());
}
void RequestBuilder::addDate(const QDateTime& data)
{
    const int64_t milli  = data.toMSecsSinceEpoch();
    addLong(milli);
}
void RequestBuilder::addPoint(const QPoint& data)
{
    addInteger(data.x());
    addInteger(data.y());
}
