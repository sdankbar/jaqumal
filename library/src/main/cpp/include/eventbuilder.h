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
#ifndef EVENTBUILDER_H
#define EVENTBUILDER_H

#include <QObject>
#include <QMouseEvent>
#include <requestbuilder.h>
#include <jni.h>

class EventBuilder : public RequestBuilder
{
    Q_OBJECT
public:
    enum PerfEventType {
        BEFORE_SYNC,
        BEFORE_RENDER,
        AFTER_RENDER,
        FRAME_SWAP
    };
    Q_ENUMS(PerfEventType)

    static void setEventHandler(JNIEnv* env, jobject handler);

    explicit EventBuilder(QObject *parent = nullptr);
    virtual ~EventBuilder();

    Q_INVOKABLE QVariant fireEvent(const QString& type);
    Q_INVOKABLE void fireEvent(const QString& type, const QString& data);

    Q_INVOKABLE void mouseClickEvent(const QString& objectName, qint32 x, qint32 y, qint32 button, qint32 buttons, qint32 modifiers, bool wasHeld);
    Q_INVOKABLE void mouseWheelEvent(const QString& objectName, qint32 angleDeltaX, qint32 angleDeltaY,
                                     qint32 buttons, qint32 modifiers, qint32 x, qint32 y);
    Q_INVOKABLE void buttonClickEvent(const QString& objectName);
    Q_INVOKABLE void textInputAcceptedEvent(const QString& objectName);
    Q_INVOKABLE void textInputEditingFinishedEvent(const QString& objectName);
    Q_INVOKABLE void perfEvent(PerfEventType t);

private:

    static jobject EVENT_HANDLER;
    static jclass eventCallbackClass;
    static jmethodID eventCallbackMethod;
};

#endif // EVENTBUILDER_H
