/**
 * The MIT License
 * Copyright © 2019 Stephen Dankbar
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
#include "qmlimageprovider.h"
#include "qmlinterface.h"

QMLImageProvider::QMLImageProvider(std::function<void* (const char*, int, int)> callback) :
    QQuickImageProvider(QQuickImageProvider::Image),
    javaImageProviderCallback(callback)
{
    // Empty Implementation
}

QMLImageProvider::~QMLImageProvider()
{
    // Empty Implementation
}

QImage QMLImageProvider::requestImage(const QString& id, QSize* size, const QSize& requestedSize)
{
    int correctedWidth = 50;
    int correctedHeight = 50;
    if (requestedSize.width() > 0)
    {
      correctedWidth = requestedSize.width();
    }
    if (requestedSize.height() > 0)
    {
      correctedHeight = requestedSize.height();
    }
    
    void* data = javaImageProviderCallback(id.toStdString().c_str(), correctedWidth, correctedHeight);
    int dataLength = 0;
    QVariant imageVar = toQVariant(data, dataLength);
    QImage image = imageVar.value<QImage>();
    if (size)
    {
        *size = image.size();
    }
    return image;
}
