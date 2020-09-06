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
#include <applicationfunctions.h>
#include <qmldatatransfer.h>

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    createGenericListModel
 * Signature: (Ljava/lang/String;[Ljava/lang/String;[I)J
 */
jlong JNICALL createGenericListModel(JNIEnv* env, jclass, jstring modelName, jobjectArray roleStrings, jintArray roleIndices)
{
    if (ApplicationFunctions::check(env))
    {
        QString modelNameStr = JNIUtilities::toQString(env, modelName);

        const size_t length = env->GetArrayLength(roleStrings);
        QHash<int, QByteArray> roleMap;
        jint* roleArray = env->GetIntArrayElements(roleIndices, nullptr);
        for (size_t i = 0; i < length; ++i)
        {
            jstring jStr = static_cast<jstring>(env->GetObjectArrayElement(roleStrings, i));
            QString n = JNIUtilities::toQString(env, jStr);
            int index = roleArray[i];
            roleMap[index] = n.toUtf8();
        }

        GenericListModel* modelPtr = new GenericListModel(modelNameStr, roleMap);
        ApplicationFunctions::get()->addToContext(modelNameStr, modelPtr);
        return reinterpret_cast<jlong>(modelPtr);
    }
    else
    {
        return 0;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    appendGenericListModelData
 * Signature: (J)I
 */
jint JNICALL appendGenericListModelData(JNIEnv* env, jclass, jlong pointer)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericListModel*>(pointer);
        std::vector<QVariant>& vars = QMLDataTransfer::getPendingVariants();
        jint newIndex = modelPtr->appendRowData(vars, QMLDataTransfer::getPendingRoleIndices());
        QMLDataTransfer::clearPendingData();
        return newIndex;
    }
    else
    {
        return -1;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    clearAllGenericListModelData
 * Signature: (JI)V
 */
void JNICALL clearAllGenericListModelData(JNIEnv* env, jclass, jlong pointer, jint row)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericListModel*>(pointer);
        modelPtr->clear(row);
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    clearGenericListModelData
 * Signature: (JII)V
 */
void JNICALL clearGenericListModelData(JNIEnv* env, jclass, jlong pointer, jint row, jint role)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericListModel*>(pointer);
        modelPtr->clear(row, role);
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    eraseGenericListModelData
 * Signature: (JI)V
 */
void JNICALL eraseGenericListModelData(JNIEnv* env, jclass, jlong pointer, jint row)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericListModel*>(pointer);
        modelPtr->erase(row);
    }
}

jobject JNICALL getGenericListModelData(JNIEnv* env, jclass, jlong pointer, jint row, jint roleIndex)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericListModel*>(pointer);
        return QMLDataTransfer::toJVariant(env, modelPtr->getRowData(row, roleIndex));
    }
    else
    {
        return nullptr;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    getGenericListModelSize
 * Signature: (J)I
 */
jint JNICALL getGenericListModelSize(JNIEnv* env, jclass, jlong pointer)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericListModel*>(pointer);
        return modelPtr->rowCount();
    }
    else
    {
        return 0;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    insertGenericListModelData
 * Signature: (JI)V
 */
void JNICALL insertGenericListModelData(JNIEnv* env, jclass, jlong pointer, jint row)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericListModel*>(pointer);
        std::vector<QVariant>& vars = QMLDataTransfer::getPendingVariants();
        modelPtr->insertRowData(row, vars, QMLDataTransfer::getPendingRoleIndices());
        QMLDataTransfer::clearPendingData();
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    isGenericListModelRolePresent
 * Signature: (JII)Z
 */
jboolean JNICALL isGenericListModelRolePresent(JNIEnv* env, jclass, jlong pointer, jint row, jint roleIndex)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericListModel*>(pointer);
        return modelPtr->containsRole(row, roleIndex);
    }
    else
    {
        return false;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    removeRootValueFromListModel
 * Signature: (JLjava/lang/String;)V
 */
void JNICALL removeRootValueFromListModel(JNIEnv* env, jclass, jlong pointer, jstring key)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericListModel*>(pointer);
        QString keyStr = JNIUtilities::toQString(env, key);
        modelPtr->removeRootValue(keyStr);
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    reorderGenericListModel
 * Signature: (J[I)V
 */
void JNICALL reorderGenericListModel(JNIEnv* env, jclass, jlong pointer, jintArray orderArray)
{
    if (ApplicationFunctions::check(env))
    {
        size_t length = env->GetArrayLength(orderArray);
        jint* array = env->GetIntArrayElements(orderArray, nullptr);

        auto modelPtr = reinterpret_cast<GenericListModel*>(pointer);
        std::vector<int32_t> orderingVec;
        for (size_t i = 0; i < length; ++i)
        {
            orderingVec.push_back(array[i]);
        }
        modelPtr->reorder(orderingVec);

        env->ReleaseIntArrayElements(orderArray, array, JNI_ABORT);
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_list_ListModelFunctions
 * Method:    setGenericListModelData
 * Signature: (JI)V
 */
void JNICALL setGenericListModelData(JNIEnv* env, jclass, jlong pointer, jint row)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericListModel*>(pointer);
        std::vector<QVariant>& vars = QMLDataTransfer::getPendingVariants();
        modelPtr->setRowData(row, vars, QMLDataTransfer::getPendingRoleIndices());
        QMLDataTransfer::clearPendingData();
    }
}

void JNICALL putRootValueIntoListModel(JNIEnv* env, jclass, jlong pointer, jstring key)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericListModel*>(pointer);
        QString keyStr = JNIUtilities::toQString(env, key);
        QVariant dataVar = QMLDataTransfer::retrieve(0);
        modelPtr->putRootValue(keyStr, dataVar);
        QMLDataTransfer::clearPendingData();
    }
}


jobject getRootValueFromListModel(JNIEnv* env, jclass, jlong pointer, jstring key)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericListModel*>(pointer);
        QString keyStr = JNIUtilities::toQString(env, key);
        return QMLDataTransfer::toJVariant(env, modelPtr->getRootValue(keyStr));
    }
    else
    {
        return nullptr;
    }
}

void ListModelFunctions::initialize(JNIEnv* env)
{
    JNINativeMethod methods[] = {
        JNIUtilities::createJNIMethod("createGenericListModel",    "(Ljava/lang/String;[Ljava/lang/String;[I)J",    (void *)&createGenericListModel),
        JNIUtilities::createJNIMethod("appendGenericListModelData",    "(J)I",    (void *)&appendGenericListModelData),
        JNIUtilities::createJNIMethod("clearAllGenericListModelData",    "(JI)V",    (void *)&clearAllGenericListModelData),
        JNIUtilities::createJNIMethod("clearGenericListModelData",    "(JII)V",    (void *)&clearGenericListModelData),
        JNIUtilities::createJNIMethod("eraseGenericListModelData",    "(JI)V",    (void *)&eraseGenericListModelData),
        JNIUtilities::createJNIMethod("getGenericListModelData",    "(JII)Lcom/github/sdankbar/qml/JVariant;",    (void *)&getGenericListModelData),
        JNIUtilities::createJNIMethod("getGenericListModelSize",    "(J)I",    (void *)&getGenericListModelSize),
        JNIUtilities::createJNIMethod("insertGenericListModelData",    "(JI)V",    (void *)&insertGenericListModelData),
        JNIUtilities::createJNIMethod("isGenericListModelRolePresent",    "(JII)Z",    (void *)&isGenericListModelRolePresent),
        JNIUtilities::createJNIMethod("removeRootValueFromListModel",    "(JLjava/lang/String;)V",    (void *)&removeRootValueFromListModel),
        JNIUtilities::createJNIMethod("putRootValueIntoListModel",    "(JLjava/lang/String;)V",    (void *)&putRootValueIntoListModel),
        JNIUtilities::createJNIMethod("getRootValueFromListModel",    "(JLjava/lang/String;)Lcom/github/sdankbar/qml/JVariant;",    (void *)&getRootValueFromListModel),
        JNIUtilities::createJNIMethod("reorderGenericListModel",    "(J[I)V",    (void *)&reorderGenericListModel),
        JNIUtilities::createJNIMethod("setGenericListModelData",    "(JI)V",    (void *)&setGenericListModelData),
    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/list/ListModelFunctions");
    env->RegisterNatives(javaClass, methods, sizeof(methods)/sizeof(JNINativeMethod));
    env->DeleteLocalRef(javaClass);
}

void ListModelFunctions::uninitialize(JNIEnv*)
{

}

GenericListModel::GenericListModel(const QString& modelName, const QHash<int, QByteArray>& roleMap)
    : QAbstractListModel(nullptr),
      m_modelName(modelName),
      m_roleNames(roleMap)
{
    auto iter = m_roleNames.constBegin();
    auto end = m_roleNames.constEnd();
    for (; iter != end; ++iter)
    {
        m_stringToIndexRoleMap[iter.value()] = iter.key();
    }
}

const QString& GenericListModel::modelName() const
{
    return m_modelName;
}

const QVariantMap& GenericListModel::root() const
{
    return m_root;
}

void GenericListModel::putRootValue(const QString& key, const QVariant& value)
{
    m_root[key] = value;
    emit rootChanged();
}

void GenericListModel::removeRootValue(const QString& key)
{
    m_root.remove(key);
    emit rootChanged();
}

const QVariant& GenericListModel::getRootValue(const QString& key)
{
    auto iter = m_root.find(key);
    if (iter != m_root.end())
    {
        return iter.value();
    }
    else
    {
        static const QVariant EMPTY;
        return EMPTY;
    }
}

int GenericListModel::rowCount(const QModelIndex &parent) const
{
    // For list models only the root node (an invalid parent) should return the list's size. For all
    // other (valid) parents, rowCount() should return 0 so that it does not become a tree model.
    if (parent.isValid())
    {
        return 0;
    }
    else
    {
        return m_rowData.size();
    }
}

QVariant GenericListModel::data(const QModelIndex &index, int role) const
{
    if (!index.isValid())
    {
        return QVariant();
    }

    if (index.row() >= 0 && index.row() < m_rowData.size())
    {
        const auto& data = m_rowData[index.row()];
        if (data.contains(role))
        {
            return *data.find(role);
        }
        else
        {
            return QVariant();
        }
    }
    else
    {
        return QVariant();
    }
}

bool GenericListModel::setData(const QModelIndex &i, const QVariant &value, int role)
{
    if (!i.isValid())
    {
        return false;
    }

    if (i.row() >= 0 && i.row() < m_rowData.size())
    {
        auto& data = m_rowData[i.row()];
        data.insert(role, value);
        emit dataChanged(index(i.row(), 0), index(i.row(), 0));
        return true;
    }
    else
    {
        return false;
    }
}

QVariantMap GenericListModel::getData(qint32 row) const
{
    QVariantMap map;
    if (0 <= row && row < m_rowData.size())
    {
        const QHash<int, QVariant>& data = m_rowData[row];
        for (const auto& k: m_roleNames.keys())
        {
            const QString roleName = m_roleNames.value(k);
            map.insert(roleName, data.value(k));
        }
    }

    return map;
}

QVariant GenericListModel::getData(qint32 row, const QString& propertyName) const
{
    if (0 <= row && row < m_rowData.size())
    {
        const QHash<int, QVariant>& data = m_rowData[row];
        const int key = m_stringToIndexRoleMap.value(propertyName);
        return data.value(key);
    }
    else
    {
        return QVariant();
    }
}

QVariant GenericListModel::getRowData(qint32 row, int32_t roleIndex) const
{
    if (row < m_rowData.size())
    {
        auto iter = m_rowData[row].find(roleIndex);
        if (iter != m_rowData[row].end())
        {
            return iter.value();
        }
        else
        {
            return QVariant();
        }
    }
    else
    {
        return QVariant();
    }
}

void GenericListModel::setRowData(qint32 row, const std::vector<QVariant>& data, const std::vector<int32_t>& roleIndex)
{
    if (m_rowData.size() <= row)
    {
        beginInsertRows(QModelIndex(), m_rowData.size(), row);
        while (m_rowData.size() <= row)
        {
            QHash<int32_t, QVariant> map;
            m_rowData.push_back(map);
        }

        for (size_t i = 0; i < roleIndex.size(); ++i)
        {
            m_rowData[row].insert(roleIndex[i], data[i]);
        }

        endInsertRows();
        emit sizeChanged();
    }
    else
    {
        QHash<int32_t, QVariant>& entry = m_rowData[row];
        for (size_t i = 0; i < roleIndex.size(); ++i)
        {
            entry.insert(roleIndex[i], data[i]);
        }
        emit dataChanged(index(row, 0), index(row, 0), QVector<int32_t>::fromStdVector(roleIndex));
    }
}

int32_t GenericListModel::appendRowData(const std::vector<QVariant>& data, const std::vector<int32_t>& roleIndex)
{
    int32_t newIndex = m_rowData.size();
    insertRowData(m_rowData.size(), data, roleIndex);
    return newIndex;
}

void GenericListModel::insertRowData(qint32 row, const std::vector<QVariant>& data, std::vector<int32_t> roleIndex)
{
    int32_t actualRow = std::min(row, m_rowData.size());
    beginInsertRows(QModelIndex(), actualRow, actualRow);
    QHash<int32_t, QVariant> map;
    for (size_t i = 0; i < roleIndex.size(); ++i)
    {
       map.insert(roleIndex[i], data[i]);
    }
    m_rowData.insert(actualRow, map);

    endInsertRows();
    emit sizeChanged();
}

void GenericListModel::erase(qint32 row)
{
    beginRemoveRows(QModelIndex(), row, row);
    m_rowData.removeAt(row);
    endRemoveRows();
    emit sizeChanged();
}

QHash<int, QByteArray> GenericListModel::roleNames() const
{
    return m_roleNames;
}

void GenericListModel::clear(qint32 row, int32_t roleIndex)
{
    if (row < m_rowData.size())
    {
        auto iter = m_rowData[row].find(roleIndex);
        if (iter != m_rowData[row].end())
        {
            m_rowData[row].remove(roleIndex);
            emit dataChanged(index(row, 0), index(row, 0));
        }
    }
}
void GenericListModel::clear(qint32 row)
{
    if (row < m_rowData.size())
    {
        if (!m_rowData[row].isEmpty())
        {
            m_rowData[row].clear();
            emit dataChanged(index(row, 0), index(row, 0));
        }
    }
}


bool GenericListModel::containsRole(qint32 row, int32_t roleIndex)
{
    if (row < m_rowData.size())
    {
        return m_rowData[row].contains(roleIndex);
    }
    else
    {
        return false;
    }
}

void GenericListModel::reorder(const std::vector<int32_t>& ordering)
{
    QVector<QHash<int32_t, QVariant> > swapArea;
    for (int32_t i = 0; i < m_rowData.size(); ++i)
    {
        QHash<int32_t, QVariant> temp;
        temp.swap(m_rowData[ordering[i]]);
        swapArea.append(temp);
    }
    m_rowData.swap(swapArea);
}

qint32 GenericListModel::size() const
{
    return m_rowData.size();
}

