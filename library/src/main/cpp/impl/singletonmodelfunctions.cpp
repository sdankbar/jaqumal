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

#include <jpolyline.h>
#include <QTimer>
#include <QDateTime>
#include <QUuid>
#include <QFont>
#include <QFontInfo>
#include <QFontMetrics>
#include <QScreen>
#include <QApplication>
#include <functional>
#include <iostream>
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
        modelPtr->clear(env);
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
        modelPtr->clear(env, roleIndex);
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
        return QMLDataTransfer::toJVariant(env, modelPtr->getData(env, roleIndex));
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
        return modelPtr->containsRole(env, roleIndex);
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
        modelPtr->setData(env, QMLDataTransfer::getPendingRoleIndices());
        QMLDataTransfer::clearPendingData();
    }
}

void JNICALL assignGenericObjectModelData(JNIEnv* env, jclass, jlong longPtr)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericObjectModel*>(longPtr);
        modelPtr->assign(env, QMLDataTransfer::getPendingRoleIndices());
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
        JNIUtilities::createJNIMethod("assignGenericObjectModelData",    "(J)V",    (void *)&assignGenericObjectModelData),
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
    QVariant nullVar;
    const size_t size = roles.size();
    for (size_t i = 0; i < size; ++i)
    {
        const QString& role = m_roleMap[static_cast<uint32_t>(i)];
        insert(role, nullVar);
        emit valueChanged(role, nullVar);
    }
    QObject::connect(this, &QQmlPropertyMap::valueChanged, this, &GenericObjectModel::onValueChanged);
}

const QString& GenericObjectModel::modelName() const
{
    return m_modelName;
}

QVariant GenericObjectModel::getData(JNIEnv* env, int32_t roleIndex) const
{
    if (0 <= roleIndex && static_cast<uint32_t>(roleIndex) < m_roleMap.size())
    {
        return value(m_roleMap[static_cast<uint32_t>(roleIndex)]);
    }
    else
    {
        JNIUtilities::throwQMLException(env, "Role does not exist");
        return QVariant();
    }
}

void GenericObjectModel::setData(JNIEnv* env, QVariant& data, size_t roleIndex)
{
    if (roleIndex < m_roleMap.size())
    {
        const QString& role = m_roleMap[roleIndex];
        QVariant& currentValue = (*this)[role];
        if (currentValue != data)
        {
            // Insert() fires the necessary signals for QML to pick up the change.
            insert(role, data);
            emit valueChanged(role, data);
        }
    }
    else
    {
        JNIUtilities::throwQMLException(env, "Role does not exist");
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

void GenericObjectModel::assign(JNIEnv* env, const QVector<int32_t>& roleIndex)
{
    clear(env);
    setData(env, roleIndex);
}

void GenericObjectModel::setData(JNIEnv* env, const QVector<int32_t>& roleIndex)
{
    for (int32_t i = 0; i < roleIndex.size(); ++i)
    {
        setData(env, QMLDataTransfer::retrieve(i), roleIndex[i]);
    }
    QMLDataTransfer::clearPendingData();
}

QVariant GenericObjectModel::getData(const QString& propertyName) const
{
    return value(propertyName);
}

void GenericObjectModel::clear(JNIEnv* env, int32_t roleIndex)
{
    QVariant nullVar;
    setData(env, nullVar, roleIndex);
}

void GenericObjectModel::clear(JNIEnv* env)
{
    for (uint32_t i = 0; i < m_roleMap.size(); ++i)
    {
        clear(env, static_cast<int32_t>(i));
    }
}

bool GenericObjectModel::containsRole(JNIEnv* env, int32_t roleIndex)
{
    return getData(env, roleIndex) != QVariant();
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
        ApplicationFunctions::mainEnv->DeleteLocalRef(jvariantObj);
        ApplicationFunctions::mainEnv->DeleteLocalRef(jKey);
    }
}

