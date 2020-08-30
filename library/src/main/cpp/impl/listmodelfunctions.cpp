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
#include "listmodelfunctions.h"

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
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    createGenericListModel
 * Signature: (Ljava/lang/String;[Ljava/lang/String;[I)J
 */
jlong JNICALL createGenericListModel(JNIEnv *, jclass, jstring, jobjectArray, jintArray)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    appendGenericListModelData
 * Signature: (J)I
 */
jint JNICALL appendGenericListModelData(JNIEnv *, jclass, jlong)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    clearAllGenericListModelData
 * Signature: (JI)V
 */
void JNICALL clearAllGenericListModelData(JNIEnv *, jclass, jlong, jint)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    clearGenericListModelData
 * Signature: (JII)V
 */
void JNICALL clearGenericListModelData(JNIEnv *, jclass, jlong, jint, jint)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    eraseGenericListModelData
 * Signature: (JI)V
 */
void JNICALL eraseGenericListModelData(JNIEnv *, jclass, jlong, jint)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    getGenericListModelSize
 * Signature: (J)I
 */
jint JNICALL getGenericListModelSize(JNIEnv *, jclass, jlong)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    insertGenericListModelData
 * Signature: (JI)V
 */
void JNICALL insertGenericListModelData(JNIEnv *, jclass, jlong, jint)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    isGenericListModelRolePresent
 * Signature: (JII)Z
 */
jboolean JNICALL isGenericListModelRolePresent(JNIEnv *, jclass, jlong, jint, jint)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    removeRootValueFromListModel
 * Signature: (JLjava/lang/String;)V
 */
void JNICALL removeRootValueFromListModel(JNIEnv *, jclass, jlong, jstring)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    reorderGenericListModel
 * Signature: (J[I)V
 */
void JNICALL reorderGenericListModel(JNIEnv *, jclass, jlong, jintArray)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    setGenericListModelData
 * Signature: (JI)V
 */
void JNICALL setGenericListModelData(JNIEnv *, jclass, jlong, jint)
{

}

void ListModelFunctions::initialize(JNIEnv* env)
{
    // TODO
    JNINativeMethod methods[] = {

    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/list/ListModelFunctions");
    env->RegisterNatives(javaClass, methods, sizeof(methods)/sizeof(JNINativeMethod));
    env->DeleteLocalRef(javaClass);
}

void ListModelFunctions::uninitialize(JNIEnv*)
{

}
