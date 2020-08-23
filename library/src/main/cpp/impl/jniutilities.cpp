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
#include "jniutilities.h"
#include "string.h"
#include "qmldatatransfer.h"
#include "applicationfunctions.h"

jint JNI_OnLoad(JavaVM* vm, void*)
{
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_2) != JNI_OK) {
        return JNI_ERR;
    }

    JNIUtilities::initialize(env);
    QMLDataTransfer::initialize(env);
    ApplicationFunctions::initialize(env);

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
}

jclass JNIUtilities::illegalStateExceptionClass;
jclass JNIUtilities::qmlExceptionClass;
jclass JNIUtilities::callbackClass;
jmethodID JNIUtilities::callbackMethod;

void JNIUtilities::initialize(JNIEnv* env)
{
    illegalStateExceptionClass = findClassGlobalReference(env, "java/lang/IllegalStateException");
    qmlExceptionClass = findClassGlobalReference(env, "com/github/sdankbar/qml/exceptions/QMLException");
    callbackClass = findClassGlobalReference(env, "com/github/sdankbar/jni/CallbackInterface");
    callbackMethod = env->GetMethodID(callbackClass, "callback", "(I)V");
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
}

QString JNIUtilities::toQString(JNIEnv* env, jstring str)
{
    const char* array = env->GetStringUTFChars(str, NULL);
    QString qstr = QString::fromUtf8(array);
    env->ReleaseStringUTFChars(str, array);
    return qstr;
}

JNIUtilities::JNIUtilities()
{
    // Empty Implementation
}
