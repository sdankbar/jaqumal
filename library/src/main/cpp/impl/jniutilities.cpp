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
#include "jniutilities.h"
#include "string.h"
#include "qmldatatransfer.h"
#include "applicationfunctions.h"
#include "eventfunctions.h"
#include "fontfunctions.h"
#include "invokebuilder.h"
#include <iostream>
#include "flattreemodelfunctions.h"
#include "singletonmodelfunctions.h"
#include "listmodelfunctions.h"
#include <math.h>

JNINativeMethod JNIUtilities_createJNIMethod(const char* name, const char* sig, void* funcPtr)
{
    return JNIUtilities::createJNIMethod(name, sig, funcPtr);
}
jclass JNIUtilities_findClassGlobalReference(JNIEnv* env, const char* name)
{
    return JNIUtilities::findClassGlobalReference(env, name);
}
QString JNIUtilities_toQString(JNIEnv* env, jstring str)
{
    return JNIUtilities::toQString(env, str);
}
jstring JNIUtilities_toJString(JNIEnv* env, const QString& str)
{
    return JNIUtilities::toJString(env, str);
}
const QFont& JNIUtilities_getFont(size_t fontIndex)
{
    return JNIUtilities::getFont(fontIndex);
}


jint JNI_OnLoad(JavaVM* vm, void*)
{
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_2) != JNI_OK) {
        std::cout << "  bad version" << std::endl;
        return JNI_ERR;
    }

    JNIUtilities::initialize(vm, env);
    QMLDataTransfer::initialize(env);
    ApplicationFunctions::initialize(env);
    EventFunctions::initialize(env);
    InvokeBuilder::initialize(env);
    FontFunctions::initialize(env);
    FlatTreeModelFunctions::initialize(env);
    ListModelFunctions::initialize(env);
    SingletonModelFunctions::initialize(env);

    // Return the JNI Version as required by method
    return JNI_VERSION_1_2;
}

void JNI_OnUnload(JavaVM* vm, void*)
{
    JNIEnv* env;
    vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_2);

    JNIUtilities::uninitialize(env);
    QMLDataTransfer::uninitialize(env);
    ApplicationFunctions::uninitialize(env);
    EventFunctions::uninitialize(env);
    FontFunctions::uninitialize(env);
}

jclass JNIUtilities::illegalStateExceptionClass;
jclass JNIUtilities::qmlExceptionClass;
jclass JNIUtilities::callbackClass;
jmethodID JNIUtilities::callbackMethod;
JavaVM* JNIUtilities::javaVM;
std::vector<QFont> JNIUtilities::fontCache;

void JNIUtilities::initialize(JavaVM* vm, JNIEnv* env)
{
    javaVM = vm;

    illegalStateExceptionClass = findClassGlobalReference(env, "java/lang/IllegalStateException");
    qmlExceptionClass = findClassGlobalReference(env, "com/github/sdankbar/qml/exceptions/QMLException");
    callbackClass = findClassGlobalReference(env, "com/github/sdankbar/qml/cpp/jni/interfaces/InvokeCallback");
    callbackMethod = env->GetMethodID(callbackClass, "invoke", "()V");
}

void JNIUtilities::uninitialize(JNIEnv* env)
{
    env->DeleteGlobalRef(illegalStateExceptionClass);
    env->DeleteGlobalRef(qmlExceptionClass);
    env->DeleteGlobalRef(callbackClass);
}

JNINativeMethod JNIUtilities::createJNIMethod(const char* name, const char* sig, void* funcPtr)
{
    JNINativeMethod method;
    method.name = strdup(name);
    method.signature = strdup(sig);
    method.fnPtr = funcPtr;
    return method;
}

jclass JNIUtilities::findClassGlobalReference(JNIEnv* env, const char* name)
{
    const jclass tempLocalRef = env->FindClass(name);
    const jclass tempGlobalRef = (jclass) env->NewGlobalRef(tempLocalRef);
    env->DeleteLocalRef(tempLocalRef);
    return tempGlobalRef;
}

void JNIUtilities::throwIllegalStateException(JNIEnv* env, const char* msg)
{
    env->ThrowNew(illegalStateExceptionClass, msg);
}

void JNIUtilities::throwQMLException(JNIEnv* env, const char* msg)
{
    env->ThrowNew(qmlExceptionClass, msg);
}

void JNIUtilities::invokeCallback(JNIEnv* env, jobject callbackObject)
{
    env->CallVoidMethod(callbackObject, callbackMethod, 0);
    if (env->ExceptionCheck()) {
        std::cerr << "Exception while calling invoke()" << std::endl;
        env->ExceptionClear();
    }
}

QString JNIUtilities::toQString(JNIEnv* env, jstring str)
{
    const int32_t strLength = env->GetStringLength(str);
    QString res(strLength, Qt::Uninitialized);
    env->GetStringRegion(str, 0, strLength, reinterpret_cast<jchar*>(res.data()));
    return res;
}

jstring JNIUtilities::toJString(JNIEnv* env, const QString& str)
{
    return env->NewString(reinterpret_cast<const jchar*>(str.constData()), str.length());
}

JNIEnv* JNIUtilities::attachThread()
{
    JNIEnv* threadEnv;
    JavaVMAttachArgs args;
    args.version = JNI_VERSION_1_2;
    args.name = NULL;
    args.group = NULL;
    javaVM->AttachCurrentThread((void **)&threadEnv, &args);

    return threadEnv;
}

void JNIUtilities::dettachThread()
{
    javaVM->DetachCurrentThread();
}

const QFont& JNIUtilities::getFont(size_t fontIndex)
{
   return fontCache[fontIndex];
}

void JNIUtilities::cacheFont(size_t fontIndex, const QFont& font)
{
    if (fontIndex >= fontCache.size())
    {
        size_t initSize = 32;
        fontCache.resize(qMax(initSize, 2 * fontCache.size()));
    }
    fontCache[fontIndex] = font;
}

JNIUtilities::JNIUtilities()
{
    // Empty Implementation
}
