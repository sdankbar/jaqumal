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
#include "eventbuilder.h"
#include "iostream"
#include <QColor>
#include <QDateTime>
#include <jniutilities.h>
#include <applicationfunctions.h>

jobject EventBuilder::EVENT_HANDLER = nullptr;
jclass EventBuilder::eventCallbackClass;
jmethodID EventBuilder::eventCallbackMethod;

void EventBuilder::setEventHandler(JNIEnv* env, jobject handler)
{
    if (EVENT_HANDLER)
    {
        env->DeleteGlobalRef(EVENT_HANDLER);
    }
    else
    {
        eventCallbackClass = JNIUtilities::findClassGlobalReference(env, "com/github/sdankbar/qml/cpp/jni/interfaces/EventCallback");
        eventCallbackMethod = env->GetMethodID(eventCallbackClass, "invoke", "(Ljava/lang/String;Ljava/nio/ByteBuffer;)Lcom/github/sdankbar/qml/JVariant;");
    }
    EVENT_HANDLER = handler;
}

EventBuilder::EventBuilder(QObject* parent) :
    QObject(parent)
{
    // Empty Implementation
}

QVariant EventBuilder::fireEvent(const QString& type)
{
	QVariant ret;
    if (EVENT_HANDLER)
    {
        uint32_t size = m_queuedArguements.size();
        char* memory = new char[size];
        for (uint32_t i = 0; i < size; ++i)
        {
            memory[i] = m_queuedArguements[i];
        }

        JNIEnv* env = ApplicationFunctions::mainEnv;
        jstring typeStr = env->NewStringUTF(type.toStdString().c_str());
        jobject buffer = env->NewDirectByteBuffer(memory, size);
        jobject result = ApplicationFunctions::mainEnv->CallObjectMethod(EVENT_HANDLER, eventCallbackMethod, typeStr, buffer);
        if (env->ExceptionCheck())
        {
            std::cerr << "Exception when calling event handler" << std::endl;
            env->ExceptionClear();
        }
        env->DeleteLocalRef(typeStr);
        env->DeleteLocalRef(buffer);
        if (result)
        {
            // TODO
            //ret = toQVariantList(result, 1)[0];
        }

        delete[] memory;
    }
    else
    {
        std::cerr << "No event handler registered" << std::endl;
    }
    m_queuedArguements.clear();
    
    return ret;
}

void EventBuilder::fireEvent(const QString& type, const QString& data)
{
    addString(data);
    fireEvent(type);
}

void EventBuilder::mouseClickEvent(const QString& objectName, int32_t x, int32_t y, int32_t button, int32_t buttons, int32_t modifiers, bool wasHeld)
{
    addString(objectName);
    addInteger(x);
    addInteger(y);
    addInteger(button);
    addInteger(buttons);
    addInteger(modifiers);
    addBoolean(wasHeld);
    fireEvent(QStringLiteral("Builtin-MouseClick"));
}

void EventBuilder::mouseWheelEvent(const QString& objectName, qint32 angleDeltaX, qint32 angleDeltaY,
                                   qint32 buttons, qint32 modifiers, qint32 x, qint32 y)
{
    addString(objectName);
    addInteger(angleDeltaX);
    addInteger(angleDeltaY);
    addInteger(buttons);
    addInteger(modifiers);
    addInteger(x);
    addInteger(y);
    fireEvent(QStringLiteral("Builtin-MouseWheel"));
}

void EventBuilder::textInputAcceptedEvent(const QString& objectName)
{
    addString(objectName);
    fireEvent("Builtin-TextInputAccepted");
}
void EventBuilder::textInputEditingFinishedEvent(const QString& objectName)
{
    addString(objectName);
    fireEvent("Builtin-TextInputEditingFinished");
}

void EventBuilder::perfEvent(PerfEventType t)
{
    addInteger(t);
    fireEvent("Builtin-PerformanceEvent");
}

void EventBuilder::buttonClickEvent(const QString& objectName)
{
    addString(objectName);
    fireEvent(QStringLiteral("Builtin-ButtonClick"));
}

void EventBuilder::addBoolean(bool data)
{
    m_queuedArguements.push_back(data);
}
void EventBuilder::addInteger(qint32 data)
{
    const uint32_t size = sizeof(data);
    const char* ptr = (const char*)(&data);
    for (uint32_t i = 0; i < size; ++i)
    {
        char byte = ptr[i];
        m_queuedArguements.push_back(byte);
    }
}
void EventBuilder::addLong(qint64 data)
{
    const uint32_t size = sizeof(data);
    const char* ptr = (const char*)(&data);
    for (uint32_t i = 0; i < size; ++i)
    {
        char byte = ptr[i];
        m_queuedArguements.push_back(byte);
    }
}
void EventBuilder::addFloat(float data)
{
    const uint32_t size = sizeof(data);
    const char* ptr = (const char*)(&data);
    for (uint32_t i = 0; i < size; ++i)
    {
        char byte = ptr[i];
        m_queuedArguements.push_back(byte);
    }
}
void EventBuilder::addDouble(double data)
{
    const uint32_t size = sizeof(data);
    const char* ptr = (const char*)(&data);
    for (uint32_t i = 0; i < size; ++i)
    {
        char byte = ptr[i];
        m_queuedArguements.push_back(byte);
    }
}
void EventBuilder::addString(const QString& data)
{
    std::string str = data.toStdString();
    for (uint32_t i = 0; i < str.length(); ++i)
    {
        m_queuedArguements.push_back(str[i]);
    }
    m_queuedArguements.push_back(0);
}
void EventBuilder::addColor(const QColor& data)
{
    quint32 argb = data.rgba();
    const uint32_t size = sizeof(argb);
    const char* ptr = (const char*)(&argb);
    for (uint32_t i = 0; i < size; ++i)
    {
        char byte = ptr[i];
        m_queuedArguements.push_back(byte);
    }
}
void EventBuilder::addRect(const QRect& data)
{
    {
        const int32_t x = data.x();
        const char* ptrX = (const char*)(&x);
        for (uint32_t i = 0; i < 4; ++i)
        {
            char byte = ptrX[i];
            m_queuedArguements.push_back(byte);
        }
    }
    {
        const int32_t y = data.y();
        const char* ptrY = (const char*)(&y);
        for (uint32_t i = 0; i < 4; ++i)
        {
            char byte = ptrY[i];
            m_queuedArguements.push_back(byte);
        }
    }
    {
        const int32_t w = data.width();
        const char* ptrW = (const char*)(&w);
        for (uint32_t i = 0; i < 4; ++i)
        {
            char byte = ptrW[i];
            m_queuedArguements.push_back(byte);
        }
    }
    {
        const int32_t h = data.height();
        const char* ptrH = (const char*)(&h);
        for (uint32_t i = 0; i < 4; ++i)
        {
            char byte = ptrH[i];
            m_queuedArguements.push_back(byte);
        }
    }
}
void EventBuilder::addSize(const QSize& data)
{
    {
        const int32_t w = data.width();
        const char* ptrX = (const char*)(&w);
        for (uint32_t i = 0; i < 4; ++i)
        {
            char byte = ptrX[i];
            m_queuedArguements.push_back(byte);
        }
    }
    {
        const int32_t h = data.height();
        const char* ptrY = (const char*)(&h);
        for (uint32_t i = 0; i < 4; ++i)
        {
            char byte = ptrY[i];
            m_queuedArguements.push_back(byte);
        }
    }
}
void EventBuilder::addDate(const QDateTime& data)
{
    {
        const int64_t milli  = data.toMSecsSinceEpoch();
        const char* ptr = (const char*)(&milli);
        for (uint32_t i = 0; i < 8; ++i)
        {
            char byte = ptr[i];
            m_queuedArguements.push_back(byte);
        }
    }
}
void EventBuilder::addPoint(const QPoint& data)
{
    {
        const int32_t x = data.x();
        const char* ptrX = (const char*)(&x);
        for (uint32_t i = 0; i < 4; ++i)
        {
            char byte = ptrX[i];
            m_queuedArguements.push_back(byte);
        }
    }
    {
        const int32_t y = data.y();
        const char* ptrY = (const char*)(&y);
        for (uint32_t i = 0; i < 4; ++i)
        {
            char byte = ptrY[i];
            m_queuedArguements.push_back(byte);
        }
    }
}
