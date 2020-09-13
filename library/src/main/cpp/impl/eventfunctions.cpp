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
#include "eventfunctions.h"

#include "jniutilities.h"

#include <eventbuilder.h>
#include <eventdispatcher.h>
#include <jpolyline.h>
#include <QQmlContext>
#include <QTimer>
#include <QDateTime>
#include <QUuid>
#include <QFont>
#include <QFontInfo>
#include <QFontMetrics>
#include <QScreen>
#include <QApplication>
#include <iostream>
#include <functional>

#include "qmlimageprovider.h"
#include <applicationfunctions.h>
#include <qmldatatransfer.h>

JNICALL void addEventCallback(JNIEnv* env, jclass, jobject eventCallback)
{
    EventBuilder::setEventHandler(env, env->NewGlobalRef(eventCallback));
}

//
JNICALL void sendQMLEvent(JNIEnv* env, jclass, jstring eventName, jobjectArray keys)
{
    if (ApplicationFunctions::check(env))
    {
        QVariantMap map;
        const size_t count = env->GetArrayLength(keys);
        for (size_t i = 0; i < count; ++i)
        {
            jstring jStr = static_cast<jstring>(env->GetObjectArrayElement(keys, i));
            map.insert(JNIUtilities::toQString(env, jStr), QMLDataTransfer::retrieve(i));
        }
        QMLDataTransfer::clearPendingData();
        EventDispatcher::sendToDispatchers(JNIUtilities::toQString(env, eventName), map);
    }
}

void EventFunctions::initialize(JNIEnv* env)
{
    JNINativeMethod methods[] = {
        JNIUtilities::createJNIMethod("sendQMLEvent",    "(Ljava/lang/String;[Ljava/lang/String;)V",    (void *)&sendQMLEvent),
        JNIUtilities::createJNIMethod("addEventCallback",    "(Lcom/github/sdankbar/qml/cpp/jni/interfaces/EventCallback;)V",    (void *)&addEventCallback)
    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/EventFunctions");
    env->RegisterNatives(javaClass, methods, sizeof(methods) / sizeof(JNINativeMethod));
    env->DeleteLocalRef(javaClass);
}

void EventFunctions::uninitialize(JNIEnv*)
{
    // Empty Implementation
}
