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
#include "invokebuilder.h"
#include "jniutilities.h"
#include "applicationfunctions.h"
#include "invoketarget.h"
#include <qmldatatransfer.h>
#include <iostream>

JNICALL void setCallback(JNIEnv* env, jclass, jobject callback)
{
    jobject globalRef = env->NewGlobalRef(callback);
    InvokeBuilder::setInvokable(globalRef);
}

JNICALL void addInvokable(JNIEnv* env, jclass, jstring name)
{
    if (ApplicationFunctions::check(env))
    {
        QString nameStr = JNIUtilities::toQString(env, name);
        InvokeBuilder* ptr = new InvokeBuilder(nameStr);
        ApplicationFunctions::get()->addToContext(nameStr, ptr);
    }
}

JNICALL jobject invokeQML(JNIEnv* env, jclass, jstring name, jobjectArray keys)
{
    QString targetName = JNIUtilities::toQString(env, name);

    QVariantMap map;
    const size_t count = env->GetArrayLength(keys);
    for (size_t i = 0; i < count; ++i)
    {
        jstring jStr = static_cast<jstring>(env->GetObjectArrayElement(keys, i));
        map.insert(JNIUtilities::toQString(env, jStr), QMLDataTransfer::retrieve(i));
    }
    QMLDataTransfer::clearPendingData();

    QVariant ret = InvokeTarget::sendToTarget(targetName, map);
    return QMLDataTransfer::toJVariant(env, ret);
}

jclass InvokeBuilder::invokeClass;
jmethodID InvokeBuilder::invokeMethod;
jobject InvokeBuilder::invokableObj;

void InvokeBuilder::setInvokable(jobject invokable)
{
    invokableObj = invokable;
}

void InvokeBuilder::initialize(JNIEnv* env)
{
    invokeClass = JNIUtilities::findClassGlobalReference(env, "com/github/sdankbar/qml/invocation/InvokableDispatcher");
    invokeMethod = env->GetMethodID(invokeClass, "invoke", "(Ljava/lang/String;Ljava/lang/String;Ljava/nio/ByteBuffer;)Z");


    JNINativeMethod methods[] = {
        JNIUtilities::createJNIMethod("setCallback",    "(Lcom/github/sdankbar/qml/invocation/InvokableDispatcher;)V",    (void *)&setCallback),
        JNIUtilities::createJNIMethod("addInvokable",    "(Ljava/lang/String;)V",    (void *)&addInvokable),
        JNIUtilities::createJNIMethod("invokeQML",    "(Ljava/lang/String;[Ljava/lang/String;)Lcom/github/sdankbar/qml/JVariant;",    (void *)&invokeQML)
    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/InvocationFunctions");
    env->RegisterNatives(javaClass, methods, sizeof(methods) / sizeof(JNINativeMethod));
    env->DeleteLocalRef(javaClass);
}

void InvokeBuilder::uninitialize(JNIEnv* env)
{
    env->DeleteGlobalRef(invokeClass);
    env->DeleteGlobalRef(invokableObj);
}

InvokeBuilder::InvokeBuilder(const QString& name) :
    RequestBuilder(nullptr),
    m_name(name)
{
    // Empty Implementation
}

InvokeBuilder::~InvokeBuilder()
{
    // Empty Implementation
}

QVariant InvokeBuilder::invoke(const QString& funcName)
{
    QVariant ret;
    if (invokableObj)
    {
        const uint32_t size = m_queuedArguements.size();

        JNIEnv* env = ApplicationFunctions::mainEnv;
        jstring functionName = JNIUtilities::toJString(env, funcName);
        jstring invokableName = JNIUtilities::toJString(env, m_name);
        jobject buffer = env->NewDirectByteBuffer(m_queuedArguements.data(), size);
        bool result = env->CallObjectMethod(invokableObj, invokeMethod, invokableName, functionName, buffer);
        if (env->ExceptionCheck())
        {
            std::cerr << "Exception when calling invokable" << std::endl;
            env->ExceptionDescribe();
            env->ExceptionClear();
        }
        env->DeleteLocalRef(invokableName);
        env->DeleteLocalRef(functionName);
        env->DeleteLocalRef(buffer);
        if (result)
        {
            ret = QMLDataTransfer::retrieve(0);
            QMLDataTransfer::clearPendingData();
        }
    }
    else
    {
        std::cerr << "No invokable registered" << std::endl;
    }
    m_queuedArguements.clear();

    return ret;
}
