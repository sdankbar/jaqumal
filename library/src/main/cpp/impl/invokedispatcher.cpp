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
#include "invokedispatcher.h"
#include "jniutilities.h"
#include "applicationfunctions.h"
#include <iostream>

JNICALL void setCallback(JNIEnv* env, jclass, jobject callback)
{
    jobject globalRef = env->NewGlobalRef(callback);
    InvokeDispatcher::setInvokable(globalRef);
}

JNICALL void addInvokable(JNIEnv* env, jclass, jstring name)
{
    if (ApplicationFunctions::check(env))
    {
        QString nameStr = JNIUtilities::toQString(env, name);
        InvokeDispatcher* ptr = new InvokeDispatcher(nameStr);
        ApplicationFunctions::get()->addToContext(nameStr, ptr);
    }
}

jclass InvokeDispatcher::invokeClass;
jmethodID InvokeDispatcher::invokeMethod;
jobject InvokeDispatcher::invokableObj;

void InvokeDispatcher::setInvokable(jobject invokable)
{
    invokableObj = invokable;
}

void InvokeDispatcher::initialize(JNIEnv* env)
{
    invokeClass = JNIUtilities::findClassGlobalReference(env, "com/github/sdankbar/qml/invocation/InvokableDispatcher");
    invokeMethod = env->GetMethodID(invokeClass, "invoke", "(Ljava/lang/String;Ljava/lang/String;Ljava/nio/ByteBuffer;)V");


    JNINativeMethod methods[] = {
        JNIUtilities::createJNIMethod("setCallback",    "(Lcom/github/sdankbar/qml/invocation/InvokableDispatcher;)V",    (void *)&setCallback),
        JNIUtilities::createJNIMethod("addInvokable",    "(Ljava/lang/String;)V",    (void *)&addInvokable)
    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/InvokationFunctions");
    env->RegisterNatives(javaClass, methods, sizeof(methods) / sizeof(JNINativeMethod));
    env->DeleteLocalRef(javaClass);
}

void InvokeDispatcher::uninitialize(JNIEnv* env)
{
    env->DeleteGlobalRef(invokeClass);
    env->DeleteGlobalRef(invokableObj);
}

InvokeDispatcher::InvokeDispatcher(const QString& name) :
    RequestBuilder(nullptr),
    m_name(name)
{
    // Empty Implementation
}

InvokeDispatcher::~InvokeDispatcher()
{
    // Empty Implementation
}

void InvokeDispatcher::invoke(const QString& funcName)
{
    QVariant ret;
    if (invokableObj)
    {
        const uint32_t size = m_queuedArguements.size();
        char* memory = new char[size];
        for (uint32_t i = 0; i < size; ++i)
        {
            memory[i] = m_queuedArguements[i];
        }

        JNIEnv* env = ApplicationFunctions::mainEnv;
        jstring functionName = JNIUtilities::toJString(env, funcName);
        jstring invokableName = JNIUtilities::toJString(env, m_name);// TODO
        // TODO reuse this buffer
        jobject buffer = env->NewDirectByteBuffer(memory, size);
        env->CallObjectMethod(invokableObj, invokeMethod, invokableName, functionName, buffer);
        if (env->ExceptionCheck())
        {
            std::cerr << "Exception when calling event handler" << std::endl;
            env->ExceptionClear();
        }
        env->DeleteLocalRef(invokableName);
        env->DeleteLocalRef(functionName);
        env->DeleteLocalRef(buffer);

        delete[] memory;
    }
    else
    {
        std::cerr << "No invokable registered" << std::endl;
    }
    m_queuedArguements.clear();
}
