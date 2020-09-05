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
#include <applicationfunctions.h>
#include <qmldatatransfer.h>

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_singleton_SingletonQMLAPIFast
 * Method:    createGenericObjectModel
 * Signature: (Ljava/lang/String;[Ljava/lang/String;)J
 */
jlong JNICALL createGenericObjectModel(JNIEnv* env, jclass, jstring modelName, jobjectArray roleStrings)
{
    if (ApplicationFunctions::check(env))
    {
        QString modelNameStr = JNIUtilities::toQString(env, modelName);

        const size_t length = env->GetArrayLength(roleStrings);
        std::vector<QString> roles;
        for (size_t i = 0; i < length; ++i)
        {
            jstring jStr = static_cast<jstring>(env->GetObjectArrayElement(roleStrings, i));
            QString n = JNIUtilities::toQString(env, jStr);
            roles.push_back(n);
        }

        GenericObjectModel* modelPtr = new GenericObjectModel(modelNameStr, roles);
        ApplicationFunctions::get()->addToContext(modelNameStr, modelPtr);
        return reinterpret_cast<jlong>(modelPtr);
    }
    else
    {
        return 0;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_singleton_SingletonQMLAPIFast
 * Method:    clearGenericObjectModel
 * Signature: (J)V
 */
void JNICALL clearGenericObjectModel(JNIEnv* env, jclass, jlong longPtr)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericObjectModel*>(longPtr);
        modelPtr->clear();
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_singleton_SingletonQMLAPIFast
 * Method:    clearGenericObjectModelRole
 * Signature: (JI)V
 */
void JNICALL clearGenericObjectModelRole(JNIEnv* env, jclass, jlong longPtr, jint roleIndex)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericObjectModel*>(longPtr);
        modelPtr->clear(roleIndex);
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_singleton_SingletonQMLAPIFast
 * Method:    getGenericObjectModelData
 * Signature: (JI)Lcom/github/sdankbar/qml/JVariant;
 */
jobject JNICALL getGenericObjectModelData(JNIEnv* env, jclass, jlong longPtr, jint roleIndex)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericObjectModel*>(longPtr);
        return QMLDataTransfer::toJVariant(env, modelPtr->getData(roleIndex));
    }
    else
    {
        return nullptr;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_singleton_SingletonQMLAPIFast
 * Method:    isGenericObjectModelRolePresent
 * Signature: (JI)Z
 */
jboolean JNICALL isGenericObjectModelRolePresent(JNIEnv* env, jclass, jlong longPtr, jint roleIndex)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericObjectModel*>(longPtr);
        return modelPtr->containsRole(roleIndex);
    }
    else
    {
        return false;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_singleton_SingletonQMLAPIFast
 * Method:    registerValueChangedCallback
 * Signature: (JLcom/github/sdankbar/qml/cpp/jni/singleton/SingletonQMLAPIFast/MapChangeCallback;)V
 */
void JNICALL registerValueChangedCallback(JNIEnv* env, jclass, jlong longPtr, jobject c)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericObjectModel*>(longPtr);
        jobject globalC = env->NewGlobalRef(c);
        modelPtr->registerValueChangedCallback(globalC);
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_singleton_SingletonQMLAPIFast
 * Method:    setGenericObjectModelData
 * Signature: (J)V
 */
void JNICALL setGenericObjectModelData(JNIEnv* env, jclass, jlong longPtr)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericObjectModel*>(longPtr);
        modelPtr->setData(QMLDataTransfer::getPendingRoleIndices());
        QMLDataTransfer::clearPendingData();
    }
}

jclass SingletonModelFunctions::mapChangedClass;
jmethodID SingletonModelFunctions::mapChangedMethod;

void SingletonModelFunctions::initialize(JNIEnv* env)
{
    mapChangedClass = JNIUtilities::findClassGlobalReference(env, "com/github/sdankbar/qml/cpp/jni/interfaces/MapChangeCallback");
    mapChangedMethod = env->GetMethodID(mapChangedClass, "invoke", "(Ljava/lang/String;Lcom/github/sdankbar/qml/JVariant;)V");

    JNINativeMethod methods[] = {
        JNIUtilities::createJNIMethod("createGenericObjectModel",    "(Ljava/lang/String;[Ljava/lang/String;)J",    (void *)&createGenericObjectModel),
        JNIUtilities::createJNIMethod("clearGenericObjectModel",    "(J)V",    (void *)&clearGenericObjectModel),
        JNIUtilities::createJNIMethod("clearGenericObjectModelRole",    "(JI)V",    (void *)&clearGenericObjectModelRole),
        JNIUtilities::createJNIMethod("getGenericObjectModelData",    "(JI)Lcom/github/sdankbar/qml/JVariant;",    (void *)&getGenericObjectModelData),
        JNIUtilities::createJNIMethod("isGenericObjectModelRolePresent",    "(JI)Z",    (void *)&isGenericObjectModelRolePresent),
        JNIUtilities::createJNIMethod("registerValueChangedCallback",    "(JLcom/github/sdankbar/qml/cpp/jni/interfaces/MapChangeCallback;)V",  (void *)&registerValueChangedCallback),
        JNIUtilities::createJNIMethod("setGenericObjectModelData",    "(J)V",    (void *)&setGenericObjectModelData),
    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/singleton/SingletonModelFunctions");
    env->RegisterNatives(javaClass, methods, sizeof(methods)/sizeof(JNINativeMethod));
    env->DeleteLocalRef(javaClass);
}

void SingletonModelFunctions::uninitialize(JNIEnv*)
{
    // Empty Implementation
}

void SingletonModelFunctions::invokeMapChangedCallback(jobject callback, jstring key, jobject jvariant)
{
    JNIEnv* env = ApplicationFunctions::mainEnv;
    env->CallVoidMethod(callback, mapChangedMethod, key, jvariant);
    if (env->ExceptionCheck())
    {
        std::cerr << "Exception when calling MapChangedCallback" << std::endl;
        env->ExceptionClear();
    }
}

GenericObjectModel::GenericObjectModel(const QString& modelName, const std::vector<QString>& roles)
    : QQmlPropertyMap(this, nullptr),
      m_modelName(modelName),
      m_roleMap(roles)
{
    const int32_t size = static_cast<int32_t>(roles.size());
    for (int32_t i = 0; i < size; ++i)
    {
        setData(QVariant(), i);
    }
    QObject::connect(this, &QQmlPropertyMap::valueChanged, this, &GenericObjectModel::onValueChanged);
}

const QString& GenericObjectModel::modelName() const
{
    return m_modelName;
}

QVariant GenericObjectModel::getData(int32_t roleIndex) const
{
    if (0 <= roleIndex && static_cast<uint32_t>(roleIndex) < m_roleMap.size())
    {
        return value(m_roleMap[static_cast<uint32_t>(roleIndex)]);
    }
    else
    {
        //exceptionHandler("Role does not exist");
        // TODO throw
        return QVariant();
    }
}

void GenericObjectModel::setData(const QVariant& data, int32_t roleIndex)
{
    if (0 <= roleIndex && static_cast<uint32_t>(roleIndex) < m_roleMap.size())
    {
        const QString& role = m_roleMap[static_cast<uint32_t>(roleIndex)];
        if (value(role) != data)
        {
            insert(role, data);
            emit valueChanged(role, data);
        }
    }
    else
    {
        // TODO throw
        //exceptionHandler("Role does not exist");
    }
}

void GenericObjectModel::setData(const QVariant& data, const QString& propertyName)
{
    if (std::find(m_roleMap.begin(), m_roleMap.end(), propertyName) != m_roleMap.end())
    {
        if (value(propertyName) != data)
        {
            insert(propertyName, data);

            callbackListeners(propertyName, data);
        }
    }
}

void GenericObjectModel::setData(const std::vector<int32_t>& roleIndex)
{
    for (size_t i = 0; i < roleIndex.size(); ++i)
    {
        const int32_t r = roleIndex[i];
        setData(QMLDataTransfer::retrieve(r), r);
    }
    QMLDataTransfer::clearPendingData();
}

QVariant GenericObjectModel::getData(const QString& propertyName) const
{
    return value(propertyName);
}

void GenericObjectModel::clear(int32_t roleIndex)
{
    setData(QVariant(), roleIndex);
}

void GenericObjectModel::clear()
{
    for (uint32_t i = 0; i < m_roleMap.size(); ++i)
    {
        clear(static_cast<int32_t>(i));
    }
}

bool GenericObjectModel::containsRole(int32_t roleIndex)
{
    return getData(roleIndex) != QVariant();
}

void GenericObjectModel::onValueChanged(const QString& key, const QVariant& value)
{
    callbackListeners(key, value);
}

void GenericObjectModel::registerValueChangedCallback(jobject c)
{
    callbacks.push_back(c);
}

void GenericObjectModel::callbackListeners(const QString& key, const QVariant& newValue)
{
    if (!callbacks.empty())
    {
        jobject jvariantObj = QMLDataTransfer::toJVariant(ApplicationFunctions::mainEnv, newValue);
        jstring jKey = ApplicationFunctions::mainEnv->NewStringUTF(qPrintable(key));
        for (const jobject& c: callbacks)
        {
            SingletonModelFunctions::invokeMapChangedCallback(c, jKey, jvariantObj);
        }
    }
}

