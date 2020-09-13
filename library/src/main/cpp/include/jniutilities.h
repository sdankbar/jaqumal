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
#pragma once
#include "jni.h"
#include <QString>

class JNIUtilities
{
public:
    static void initialize(JavaVM* vm, JNIEnv* env);
    static void uninitialize(JNIEnv* env);

    static JNINativeMethod createJNIMethod(const char* name, const char* sig, void* funcPtr);
    static jclass findClassGlobalReference(JNIEnv* env, const char* name);

    static void throwIllegalStateException(JNIEnv* env, const char* msg);
    static void throwQMLException(JNIEnv* env, const char* msg);

    static void invokeCallback(JNIEnv* env, jobject callbackObject);

    static QString toQString(JNIEnv* env, jstring str);
    static std::string toString(JNIEnv* env, jstring str);

    static JNIEnv* attachThread();
    static void dettachThread();

private:
    JNIUtilities();

    static jclass illegalStateExceptionClass;
    static jclass qmlExceptionClass;
    static jclass callbackClass;
    static jmethodID callbackMethod;
    static JavaVM* javaVM;
};

