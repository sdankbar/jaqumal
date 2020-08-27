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
#include "fontfunctions.h"

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
#include <QQmlContext>
#include <QQuickWindow>
#include <iostream>
#include <csignal>

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    getBoundingRect
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Ljava/awt/Rectangle;
 */
jobject JNICALL getBoundingRect(JNIEnv* env, jstring, jstring)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    getBoundingRect2
 * Signature: (Ljava/lang/String;IIIIIILjava/lang/String;)Ljava/awt/Rectangle;
 */
jobject JNICALL getBoundingRect2(JNIEnv* env, jclass, jstring, jint, jint, jint, jint, jint, jint, jstring)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    getQFontInfo
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
jstring JNICALL getQFontInfo(JNIEnv *, jclass, jstring)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    getQFontMetrics
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
jstring JNICALL getQFontMetrics(JNIEnv*, jclass, jstring)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    getQFontToString
 * Signature: (Ljava/lang/String;IIZZZZZZZIDDIIIIILjava/lang/String;II)Ljava/lang/String;
 */
jstring JNICALL getQFontToString(JNIEnv *, jclass, jstring, jint, jint, jboolean, jboolean, jboolean,
                                 jboolean, jboolean, jboolean, jboolean, jint, jdouble, jdouble, jint,
                                 jint, jint, jint, jint, jstring, jint, jint)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    getStringWidth
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
jint JNICALL getStringWidth(JNIEnv *, jclass, jstring, jstring)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    getTightBoundingRect
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Ljava/awt/Rectangle;
 */
jobject JNICALL getTightBoundingRect(JNIEnv *, jclass, jstring, jstring)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    inFont
 * Signature: (Ljava/lang/String;I)Z
 */
jboolean JNICALL inFont(JNIEnv *, jclass, jstring, jint)
{

}

void FontFunctions::initialize(JNIEnv* env)
{
    // TODO
    JNINativeMethod methods[] = {

    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/FontFunctions");
    env->RegisterNatives(javaClass, methods, sizeof(methods));
    env->DeleteLocalRef(javaClass);
}

void FontFunctions::uninitialize(JNIEnv*)
{

}
