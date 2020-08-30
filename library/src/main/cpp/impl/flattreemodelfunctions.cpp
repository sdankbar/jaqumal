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
#include "flattreemodelfunctions.h"

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
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    createGenericFlatTreeModel
 * Signature: (Ljava/lang/String;[Ljava/lang/String;[I)J
 */
jlong JNICALL createGenericFlatTreeModel(JNIEnv* env, jclass, jstring, jobjectArray, jintArray)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    appendGenericFlatTreeModelData
 * Signature: (J[I)I
 */
jint JNICALL appendGenericFlatTreeModelData(JNIEnv* env, jclass, jlong, jintArray)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    clearAllGenericFlatTreeModelData
 * Signature: (J[I)V
 */
void JNICALL clearAllGenericFlatTreeModelData(JNIEnv* env, jclass, jlong, jintArray)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    clearGenericFlatTreeModelData
 * Signature: (J[II)V
 */
void JNICALL clearGenericFlatTreeModelData(JNIEnv* env, jclass, jlong, jintArray, jint)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    eraseGenericFlatTreeModelData
 * Signature: (J[I)V
 */
void JNICALL eraseGenericFlatTreeModelData(JNIEnv* env, jclass, jlong, jintArray)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    getGenericFlatTreeModelData
 * Signature: (J[II)Lcom/github/sdankbar/qml/JVariant;
 */
jstring JNICALL getGenericFlatTreeModelData(JNIEnv* env, jclass, jlong, jintArray, jint)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    getGenericFlatTreeModelSize
 * Signature: (J[I)I
 */
jint JNICALL getGenericFlatTreeModelSize(JNIEnv* env, jclass, jlong, jintArray)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    insertGenericFlatTreeModelData
 * Signature: (J[I)V
 */
void JNICALL insertGenericFlatTreeModelData(JNIEnv* env, jclass, jlong, jintArray)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    isGenericFlatTreeModelRolePresent
 * Signature: (J[II)Z
 */
jboolean JNICALL isGenericFlatTreeModelRolePresent(JNIEnv* env, jclass, jlong, jintArray, jint)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    reorderGenericFlatTreeModel
 * Signature: (J[I[I)V
 */
void JNICALL reorderGenericFlatTreeModel(JNIEnv* env, jclass, jlong, jintArray, jintArray)
{

}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    setGenericFlatTreeModelData
 * Signature: (J[I)V
 */
void JNICALL setGenericFlatTreeModelData(JNIEnv* env, jclass, jlong, jintArray)
{

}

void FlatTreeModelFunctions::initialize(JNIEnv* env)
{
    // TODO
    JNINativeMethod methods[] = {

    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/flat_tree/FlatTreeModelFunctions");
    env->RegisterNatives(javaClass, methods, sizeof(methods)/sizeof(JNINativeMethod));
    env->DeleteLocalRef(javaClass);
}

void FlatTreeModelFunctions::uninitialize(JNIEnv*)
{

}
