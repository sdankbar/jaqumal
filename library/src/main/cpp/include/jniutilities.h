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
#include <QFont>

Q_DECL_EXPORT JNINativeMethod JNIUtilities_createJNIMethod(const char* name, const char* sig, void* funcPtr);
Q_DECL_EXPORT jclass JNIUtilities_findClassGlobalReference(JNIEnv* env, const char* name);
Q_DECL_EXPORT QString JNIUtilities_toQString(JNIEnv* env, jstring str);
Q_DECL_EXPORT void JNIUtilities_storeInQString(JNIEnv* env, jstring str, QString& output);
Q_DECL_EXPORT jstring JNIUtilities_toJString(JNIEnv* env, const QString& str);
Q_DECL_EXPORT const QFont& JNIUtilities_getFont(size_t fontIndex);

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
    static void storeInQString(JNIEnv* env, jstring str, QString& output);
    static jstring toJString(JNIEnv* env, const QString& str);

    static JNIEnv* attachThread();
    static void dettachThread();

    static const QFont& getFont(size_t fontIndex);
    static void cacheFont(size_t fontIndex, const QFont& font);

private:
    JNIUtilities();

    static jclass illegalStateExceptionClass;
    static jclass qmlExceptionClass;
    static jclass callbackClass;
    static jmethodID callbackMethod;
    static JavaVM* javaVM;
    static std::vector<QFont> fontCache;
};

