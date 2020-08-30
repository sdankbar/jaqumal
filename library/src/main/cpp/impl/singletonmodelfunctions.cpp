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
#include "singletonmodelfunctions.h"

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

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_singleton_SingletonQMLAPIFast
 * Method:    createGenericObjectModel
 * Signature: (Ljava/lang/String;[Ljava/lang/String;)J
 */
jlong JNICALL createGenericObjectModel(JNIEnv *, jclass, jstring, jobjectArray)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_singleton_SingletonQMLAPIFast
 * Method:    clearGenericObjectModel
 * Signature: (J)V
 */
void JNICALL clearGenericObjectModel(JNIEnv *, jclass, jlong)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_singleton_SingletonQMLAPIFast
 * Method:    clearGenericObjectModelRole
 * Signature: (JI)V
 */
void JNICALL clearGenericObjectModelRole(JNIEnv *, jclass, jlong, jint)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_singleton_SingletonQMLAPIFast
 * Method:    getGenericObjectModelData
 * Signature: (JI)Lcom/github/sdankbar/qml/JVariant;
 */
jstring JNICALL getGenericObjectModelData(JNIEnv *, jclass, jlong, jint)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_singleton_SingletonQMLAPIFast
 * Method:    isGenericObjectModelRolePresent
 * Signature: (JI)Z
 */
jboolean JNICALL isGenericObjectModelRolePresent(JNIEnv *, jclass, jlong, jint)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_singleton_SingletonQMLAPIFast
 * Method:    registerValueChangedCallback
 * Signature: (JLcom/github/sdankbar/qml/cpp/jni/singleton/SingletonQMLAPIFast/MapChangeCallback;)V
 */
void JNICALL registerValueChangedCallback(JNIEnv *, jclass, jlong, jobject)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_singleton_SingletonQMLAPIFast
 * Method:    setGenericObjectModelData
 * Signature: (J)V
 */
void JNICALL setGenericObjectModelData(JNIEnv *, jclass, jlong)
{

}

void SingletonModelFunctions::initialize(JNIEnv* env)
{
    // TODO
    JNINativeMethod methods[] = {

    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/singleton/SingletonModelFunctions");
    env->RegisterNatives(javaClass, methods, sizeof(methods)/sizeof(JNINativeMethod));
    env->DeleteLocalRef(javaClass);
}

void SingletonModelFunctions::uninitialize(JNIEnv*)
{

}
