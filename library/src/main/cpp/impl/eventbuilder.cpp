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
#include "eventbuilder.h"
#include "iostream"
#include <jniutilities.h>
#include <applicationfunctions.h>
#include <qmldatatransfer.h>

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
        eventCallbackMethod = env->GetMethodID(eventCallbackClass, "invoke", "(Ljava/lang/String;Ljava/nio/ByteBuffer;)Z");
    }
    EVENT_HANDLER = handler;
}

EventBuilder::EventBuilder(QObject* parent) :
    RequestBuilder(parent)
{
    // Empty Implementation
}

EventBuilder::~EventBuilder()
{
   // Empty Implementation
}

QVariant EventBuilder::fireEvent(const QString& type)
{
	QVariant ret;
    if (EVENT_HANDLER)
    {
        const uint32_t size = m_queuedArguements.size();

        JNIEnv* env = ApplicationFunctions::mainEnv;
        jstring typeStr = JNIUtilities::toJString(env, type);
        jobject buffer = env->NewDirectByteBuffer(m_queuedArguements.data(), size);
        bool result = ApplicationFunctions::mainEnv->CallObjectMethod(EVENT_HANDLER, eventCallbackMethod, typeStr, buffer);
        if (env->ExceptionCheck())
        {
            std::cerr << "Exception when calling event handler" << std::endl;
            env->ExceptionClear();
        }
        env->DeleteLocalRef(typeStr);
        env->DeleteLocalRef(buffer);
        if (result)
        {
            ret = QMLDataTransfer::retrieve(0);
            QMLDataTransfer::clearPendingData();
        }
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
